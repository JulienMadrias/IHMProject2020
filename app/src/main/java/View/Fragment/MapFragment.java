package View.Fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import Controller.IncidentController;
import Interface.IGPSActivity;
import Interface.IIncidentModelView;
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

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements View.OnClickListener, LocationListener, IIncidentModelView {
    private IncidentController incidentController;

    private FloatingActionButton eventAdder, incidentButton, twitterButton, accidentButton, centerMapButton;
    private TextView incidentButtonText, accidentButtonText;
    private Button reminder;

    private Animation fabOpenAnim, fabCloseAnim, floatButtonOpen, floatButtonClose, centerButtonOpen, centerButtonClose;

    private boolean isAddEventsOpen;
    private int notificationId = 0;

    private IButtonMapListener mCallBack;
    private Location currentLocation;

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    private MapView map;
    private IMapController mapController;

    private int compteurIncidentProche=0;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IButtonMapListener) getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Log.d("jiv","passe encore par ici");
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ItemizedOverlayWithFocus<OverlayItem> mMyLocationOverlay;
        eventAdder = (FloatingActionButton) view.findViewById(R.id.addAnEvent);
        incidentButton = (FloatingActionButton) view.findViewById(R.id.incidentButton);
        accidentButton = (FloatingActionButton) view.findViewById(R.id.accidentButton);
        twitterButton = (FloatingActionButton) view.findViewById(R.id.twitterButton);
        centerMapButton = (FloatingActionButton) view.findViewById(R.id.centerPosition);
        reminder = view.findViewById(R.id.reminder);

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
            currentPositionListener();
        }

        assert container != null;
        map = view.findViewById(R.id.map);
        if (map != null) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);
            mapController = map.getController();
            mapController.setZoom(18.0);
            map.setExpectedCenter(new GeoPoint(43.615102, 7.080124));
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            centerMapToCurrentPosition();
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {

                                            Gson gson = new Gson();
                                            Map<String, ?> allEntries = pref.getAll();
                                            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                                                System.out.println(entry.getValue());

                                                String json = pref.getString(entry.getKey(), "");
                                                if(json.contains("{")){
                                                    Incident incident = gson.fromJson(json, Incident.class);
                                                    if(calculateDistance(incident.getLatitude(),incident.getLongitude(),getUserCurrentLatitude(),getUserCurrentLongitude())<1)
                                                        compteurIncidentProche+=1;


                                                }

                                            }
                                            if(compteurIncidentProche!=0){
                                                sendNotificationOnChannel("title","message","channel1", NotificationCompat.PRIORITY_DEFAULT);
                                            }
                                        }
                                    },
                                    3000);
                        }
                    },
                    1000);
            incidentController = new IncidentController(this, getContext());
            incidentController.get();

        }
        return view;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2){

        double pi = Math.PI/180;    // Math.PI / 180

            double a = 0.5 - Math.cos((lat2 - lat1) * pi)/2 +
                    Math.cos(lat1 * pi) * Math.cos(lat2 * pi) *
                            (1 - Math.cos((lon2 - lon1) * pi))/2;

            return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km

    }
    private void currentPositionListener() {
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                currentLocation = location;
                centerMapToCurrentPosition();
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
            case R.id.reminder:
                sendNotificationOnChannel("Attention","Nous vous informons que il y a eu un nouveau incident.","channel1", NotificationCompat.PRIORITY_DEFAULT);
            case R.id.addAnEvent:
                if(isAddEventsOpen){
                    closeEventAdder();
                }else openEventAdder();
                break;
                case R.id.incidentButton:
                    closeEventAdder();
                    mCallBack.mapIntentButtonClicked(v);
//                     sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre incident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
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
                if(getView()!=null)
                    Snackbar.make(getView(), "Position courante ... ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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

        Snackbar.make(getView(), "changement position 1", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            GeoPoint center = new GeoPoint(location.getLatitude(), location.getLongitude());
            //mapController.animateTo(center);
            resetCurrentPostionMarker();
    }
    private void resetCurrentPostionMarker(){
        addMaker(new GeoPoint(getUserCurrentLatitude(), getUserCurrentLongitude()), "Position Actuelle", getResources().getDrawable(R.drawable.ic_location_on_blue_24dp),true);
    }

    @Override
    public Incident getIncidentToPublish() {
        return null;
    }
    private void cleanOtherMarkers(){
        map.getOverlays().clear();
        resetCurrentPostionMarker();
    }
    public void refreshMarkers(){
        cleanOtherMarkers();
        incidentController.get();
        Log.d("jiv","current Long: "+getUserCurrentLongitude()+" Lat: "+getUserCurrentLatitude());
    }
    @Override
    public void addMaker(GeoPoint startPoint, String title, Drawable icon, boolean userPosition){
        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(icon);
        startMarker.setTitle(title);
        if(userPosition){
            if(map.getOverlays().size()>0)
            map.getOverlays().remove(0);
        map.getOverlays().add(0,startMarker);
        }else
            map.getOverlays().add(startMarker);
        if(currentLocation!=null)
        Log.d("jiv","current Long: "+getUserCurrentLongitude()+" Lat: "+getUserCurrentLatitude());
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

    public double getUserCurrentLatitude() {
        return currentLocation.getLatitude();
    }

    public String getLocationName(){
        return currentLocation.toString();
    }

    public double getUserCurrentLongitude(){
        return currentLocation.getLongitude();
    }
    public void centerMapToCurrentPosition(){
        if(currentLocation==null)
            currentLocation = new Location(LocationManager.GPS_PROVIDER);

            mapController.setZoom(20.0);
            resetCurrentPostionMarker();
            map.setExpectedCenter(new GeoPoint(getUserCurrentLatitude(), getUserCurrentLongitude()));
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
