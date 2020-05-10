package View.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import Interface.IGPSActivity;
import Interface.IStorageActivity;
import View.Activity.ChannelNotification;
import Interface.IButtonMapListener;

import com.example.ihmproject.R;

import Model.Incident;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements View.OnClickListener, LocationListener {


    private FloatingActionButton eventAdder, incidentButton, twitterButton, accidentButton, centerMapButton;
    private TextView incidentButtonText, accidentButtonText;

    private Animation fabOpenAnim, fabCloseAnim, floatButtonOpen, floatButtonClose, centerButtonOpen, centerButtonClose;

    private boolean isAddEventsOpen;
    private int notificationId = 0;

    private IButtonMapListener mCallBack;
    private Location currentLocation;

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    private MapView map;
    private IMapController mapController;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IButtonMapListener) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
        eventAdder = (FloatingActionButton) view.findViewById(R.id.addAnEvent);
        incidentButton = (FloatingActionButton) view.findViewById(R.id.incidentButton);
        accidentButton = (FloatingActionButton) view.findViewById(R.id.accidentButton);
        twitterButton = (FloatingActionButton) view.findViewById(R.id.twitterButton);
        centerMapButton = (FloatingActionButton) view.findViewById(R.id.centerPosition);

        incidentButtonText = (TextView) view.findViewById(R.id.incidentTextView);
        accidentButtonText = (TextView) view.findViewById(R.id.accidentTextView);

        eventAdder.setOnClickListener(this);
        incidentButton.setOnClickListener(this);
        accidentButton.setOnClickListener(this);
        twitterButton.setOnClickListener(this);
        centerMapButton.setOnClickListener(this);

        pref = getContext().getSharedPreferences("MyPref", 0);
        editor = pref.edit();

        isAddEventsOpen = false;

        fabOpenAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);
        floatButtonOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_open);
        floatButtonClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_close);
        centerButtonOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.center_button_event_adder_opened);
        centerButtonClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.center_button_event_adder_closed);

        boolean permissionDenied = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED;
        if (permissionDenied) {
            askGpsPermission();
        } else {
            resetCurrentPostion();
        }

        assert container != null;
        map = view.findViewById(R.id.map);
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            mapController = map.getController();
            mapController.setZoom(18.0);
            GeoPoint startPoint;
            /*if (!permissionDenied && currentLocation != null) {
                startPoint = new GeoPoint(getLatitude(), getLongitude());
            } else {
                startPoint = new GeoPoint(43.615102, 7.080124);
            }
            mapController.setCenter(startPoint);*/
            //currentLocation = new Location(LocationManager.GPS_PROVIDER);

            map.setExpectedCenter(new GeoPoint(43.615102, 7.080124));

            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            centerMapToCurrentPosition();
                        }
                    },
                    1000);
            // centerMapToCurrentPosition();
            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            Gson gson = new Gson();
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                String json = pref.getString(entry.getKey(), "");
                if (json.contains("{")) {
                    Incident incident = gson.fromJson(json, Incident.class);
                    OverlayItem alert = new OverlayItem(incident.getTitle(), incident.getDescription(), new GeoPoint(incident.getLongitude(), incident.getLatitude()));
                    items.add(alert);
                }


            }

            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(inflater.getContext(), items,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            return true;
                        }

                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    });


            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);
            // askGpsPermission();
        }
        return view;
    }

    private void resetCurrentPostion() {
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                //moveCamera();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }


        };
        LocationManager locationManager = (LocationManager) (getActivity().getSystemService(LOCATION_SERVICE));
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);
    }

    public void closeEventAdder(){
        if(isAddEventsOpen){
            eventAdder.setAnimation(floatButtonClose);
            incidentButtonText.setVisibility(View.INVISIBLE);
            accidentButtonText.setVisibility(View.INVISIBLE);
            incidentButton.setAnimation(fabCloseAnim);
            accidentButton.setAnimation(fabCloseAnim);
            //centerMapButton.setAnimation(centerButtonClose);
            isAddEventsOpen = false;
        }
    }
    public void openEventAdder(){
        if(!isAddEventsOpen){
            eventAdder.setAnimation(floatButtonOpen);
            incidentButtonText.setVisibility(View.VISIBLE);
            accidentButtonText.setVisibility(View.VISIBLE);
            incidentButton.setAnimation(fabOpenAnim);
            accidentButton.setAnimation(fabOpenAnim);
            //centerMapButton.setAnimation(centerButtonOpen);
            isAddEventsOpen = true;
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addAnEvent:
                if(isAddEventsOpen){
                    closeEventAdder();
                }else openEventAdder();
                break;
                case R.id.incidentButton:
                    closeEventAdder();
                    mCallBack.mapIntentButtonClicked(v);
                    // sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre incident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
                    break;
                case R.id.accidentButton:
                    closeEventAdder();
                    mCallBack.mapIntentButtonClicked(v);
                Snackbar.make(v, "Button d'accident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre accident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
                break;
                case R.id.twitterButton:

                    closeEventAdder();
                    Intent intent = null;
                    try {
                        // get the Twitter app if possible
                        getActivity().getPackageManager().getPackageInfo("com.twitter.android", 0);
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=EmmanuelMacron"));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    } catch (Exception e) {
                        // no Twitter app, revert to browser
                        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/EmmanuelMacron"));
                    }
                    getActivity().startActivity(intent);
                break;
            case R.id.centerPosition:
                closeEventAdder();
                centerMapToCurrentPosition();
                break;
        }
    }
    private void sendNotificationOnChannel(String title, String message, String channelId, int priority){
        NotificationCompat.Builder notification = new NotificationCompat.Builder(requireActivity().getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        ChannelNotification.getNotificationManager().notify(++notificationId ,notification.build());
    }

    private void askGpsPermission(){

        ActivityCompat.requestPermissions(requireActivity(),
                new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                IGPSActivity.REQUEST_GPS_CODE);
        /*LocationManager locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);*/
    }
    @Override
    public void onLocationChanged(Location location) {
            GeoPoint center = new GeoPoint(location.getLatitude(), location.getLongitude());
            mapController.animateTo(center);
            addMaker(center);
    }
    private void addMaker(GeoPoint startPoint) {
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(getResources().getDrawable(R.drawable.ic_location_on_blue_24dp));
        startMarker.setTitle("Position Actuelle");
        map.getOverlays().remove(0);
        map.getOverlays().add(0,startMarker);
        map.invalidate();
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public double getLatitude() {
        return currentLocation.getLatitude();
    }

    public String getLocationName(){
        return currentLocation.toString();
    }

    public double getLongitude(){
        return currentLocation.getLongitude();
    }
    public void centerMapToCurrentPosition(){
        if(currentLocation==null)
            currentLocation = new Location(LocationManager.GPS_PROVIDER);

            if(getView()!=null)
            Snackbar.make(getView(), "Position courante ... ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mapController.setZoom(20.0);
            addMaker(new GeoPoint(getLatitude(),getLongitude()));
        map.setExpectedCenter(new GeoPoint(getLatitude(),getLongitude()));
            // mapController.animateTo(new GeoPoint(getLatitude(),getLongitude()));
       /* }
        else {
            if(getView()!=null)
            Snackbar.make(getView(), "Désolé une erreur s'est produite avec la permission Gps veuillez redmarrer l'application", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            map.setExpectedCenter(new GeoPoint(43.615102, 7.080124));
            askGpsPermission();
        }*/
    }
}
