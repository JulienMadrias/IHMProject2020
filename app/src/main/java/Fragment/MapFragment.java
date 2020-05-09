package Fragment;

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
import androidx.fragment.app.Fragment;

import com.example.ihmproject.ChannelNotification;
import Interface.IButtonMapListener;
import com.example.ihmproject.R;
import com.example.ihmproject.alertfactory.Incident;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class MapFragment extends Fragment implements View.OnClickListener {



    private FloatingActionButton eventAdder,incidentButton,twitterButton,accidentButton,centerMapButton;
    private TextView incidentButtonText, accidentButtonText;

    private Animation fabOpenAnim, fabCloseAnim, floatButtonOpen, floatButtonClose;

    private boolean isAddEventsOpen;
    private int notificationId = 0;

    private IButtonMapListener mCallBack;
    private Location currentLocation;
    MapView map;
    private SharedPreferences pref=null;
    private SharedPreferences.Editor editor=null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mCallBack = (IButtonMapListener)getActivity();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);
        IMapController mapController;
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

        pref = getActivity().getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        editor = pref.edit();
        
        isAddEventsOpen = false;

        Context context;
        fabOpenAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.fab_close);
        floatButtonOpen = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_open);
        floatButtonClose = AnimationUtils.loadAnimation(view.getContext(), R.anim.float_button_close);

        boolean permissionGranted = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (permissionGranted) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 1, listener);

        }

        assert container != null;
        map = view.findViewById(R.id.map);
        if(map != null){
            map.setTileSource(TileSourceFactory.MAPNIK);
            map.setBuiltInZoomControls(true);
            map.setMultiTouchControls(true);

            mapController = map.getController();
            mapController.setZoom(18.0);
            GeoPoint startPoint;
            if(permissionGranted && currentLocation!= null){startPoint = new GeoPoint(getLatitude(),getLongitude());}
            else{startPoint = new GeoPoint(43.615102, 7.080124);}
            mapController.setCenter(startPoint);

            ArrayList<OverlayItem> items = new ArrayList<OverlayItem>();
            Gson gson = new Gson();
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String json = pref.getString(entry.getKey(), "");
                Incident incident = gson.fromJson(json, Incident.class);
                OverlayItem alert = new OverlayItem(incident.getTitle(),incident.getDescription(), new GeoPoint(incident.getLongitude(),incident.getLatitude()));
                items.add(alert);

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
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.addAnEvent:
                if(isAddEventsOpen){
                    eventAdder.setAnimation(floatButtonClose);
                    incidentButtonText.setVisibility(View.INVISIBLE);
                    accidentButtonText.setVisibility(View.INVISIBLE);
                    incidentButton.setAnimation(fabCloseAnim);
                    accidentButton.setAnimation(fabCloseAnim);
                    isAddEventsOpen = false;
                }else {
                    eventAdder.setAnimation(floatButtonOpen);
                    incidentButtonText.setVisibility(View.VISIBLE);
                    accidentButtonText.setVisibility(View.VISIBLE);
                    incidentButton.setAnimation(fabOpenAnim);
                    accidentButton.setAnimation(fabOpenAnim);
                    isAddEventsOpen = true;
                }
                break;
                case R.id.incidentButton:
                /* Snackbar.make(v, "Button d'incident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); */
                mCallBack.mapIntentButtonClicked(v);
                // sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre incident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
                break;
                case R.id.accidentButton:
                Snackbar.make(v, "Button d'accident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre accident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
                break;
                case R.id.twitterButton:
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
                if(currentLocation!= null){
                map.setExpectedCenter(new GeoPoint(getLatitude(),getLongitude()));}
                break;
        }
    }
    public void sendNotificationOnChannel(String title,String message,String channelId, int priority){
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getActivity().getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(priority);
        ChannelNotification.getNotificationManager().notify(++notificationId ,notification.build());
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getContext().getAssets().open("alerts.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    double getLatitude() {
        return currentLocation.getLatitude();
    }

    double getLongitude(){
        return currentLocation.getLongitude();
    }
}
