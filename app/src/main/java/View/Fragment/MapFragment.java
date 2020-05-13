package View.Fragment;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import Controller.AccidentController;
import Controller.IncidentController;
import Interface.IAccidentModelView;
import Interface.IGPSActivity;
import Interface.IIncidentModelView;
import Model.Accident;
import Model.Alert;
import View.Activity.AlertDetails;
import View.Activity.ChannelNotification;
import Interface.IButtonMapListener;

import com.example.ihmproject.R;

import Model.Incident;
import View.Activity.MainActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

public class MapFragment extends Fragment implements View.OnClickListener, LocationListener, IIncidentModelView, IAccidentModelView {
    private ArrayList<Alert> allAlerts;
    private IncidentController incidentController;
    private AccidentController accidentController;

    private FloatingActionButton eventAdder, incidentButton, twitterButton, accidentButton, centerMapButton, saveLocationButton, callEmergencyButton;
    private TextView incidentButtonText, accidentButtonText, saveLocationButtonText;
    private Button reminder;

    private Animation fabOpenAnim, fabCloseAnim, floatButtonOpen, floatButtonClose, centerButtonOpen, centerButtonClose;

    private boolean isAddEventsOpen;
    private int notificationId = 0;

    private IButtonMapListener mCallBack;
    private Location currentLocation, savedLocation;

    private SharedPreferences pref = null;
    private SharedPreferences.Editor editor = null;

    private MapView map;
    private IMapController mapController;

    private int compteurIncidentProche=0;
    private int compteurAccidentProche=0;

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
        centerMapButton = (FloatingActionButton) view.findViewById(R.id.centerPosition);
        saveLocationButton = (FloatingActionButton) view.findViewById(R.id.saveLocation);

        callEmergencyButton = (FloatingActionButton) view.findViewById(R.id.callEmergency);


        incidentButtonText = (TextView) view.findViewById(R.id.incidentTextView);
        accidentButtonText = (TextView) view.findViewById(R.id.accidentTextView);
        saveLocationButtonText = (TextView) view.findViewById(R.id.fixPositionTextView);

        eventAdder.setOnClickListener(this);
        incidentButton.setOnClickListener(this);
        accidentButton.setOnClickListener(this);
        centerMapButton.setOnClickListener(this);
        saveLocationButton.setOnClickListener(this);




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
            savedLocation = currentLocation;
        }

        allAlerts = new ArrayList<>();

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
                                            if(getContext()!=null){
                                                compteurAccidentProche=incidentController.getNumberOfAlert();
                                                compteurIncidentProche=accidentController.getNumberOfAlert();
                                                if(compteurIncidentProche!=0||compteurAccidentProche!=0){
                                                    sendDanger("channel1", NotificationCompat.PRIORITY_DEFAULT);
                                                }
                                            }

                                        }
                                    },
                                    3000);
                        }
                    },
                    1000);
            incidentController = new IncidentController(this, getContext());
            accidentController = new AccidentController(this, getContext());
            refreshMarkers();
            //ArrayList<OverlayItem> list = new ArrayList<OverlayItem>(items.values());
            /*ArrayList<OverlayItem> list = new ArrayList<OverlayItem>();
            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(this, map.getOverlays(),
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            //Intent intent=new Intent(getContext(), ShowDetailActivity.class);
                            //intent.putExtra("code", getKey(items,item));
                            //startActivity(intent);

                            return true;
                        }
                        @Override
                        public boolean onItemLongPress(final int index, final OverlayItem item) {
                            return false;
                        }
                    });
            mOverlay.setFocusItemsOnTap(true);
            map.getOverlays().add(mOverlay);*/

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
            saveLocationButtonText.setVisibility(View.INVISIBLE);
            incidentButton.setAnimation(fabCloseAnim);
            accidentButton.setAnimation(fabCloseAnim);
            saveLocationButton.setAnimation(fabCloseAnim);
            //centerMapButton.setAnimation(centerButtonClose);
            isAddEventsOpen = false;
        }
    }
    public void openEventAdder(){
        if(!isAddEventsOpen){
            eventAdder.setAnimation(floatButtonOpen);
            incidentButtonText.setVisibility(View.VISIBLE);
            accidentButtonText.setVisibility(View.VISIBLE);
            saveLocationButtonText.setVisibility(View.VISIBLE);
            incidentButton.setAnimation(fabOpenAnim);
            accidentButton.setAnimation(fabOpenAnim);
            saveLocationButton.setAnimation(fabOpenAnim);
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
//                     sendNotificationOnChannel("Confirmation de publication","Nous vous informons que votre incident a bien été publié.","channel1", NotificationCompat.PRIORITY_DEFAULT);
                    break;
                case R.id.accidentButton:
                    closeEventAdder();
                    mCallBack.mapIntentButtonClicked(v);
                Snackbar.make(v, "Button d'accident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                break;
            case R.id.centerPosition:
                if(getView()!=null)
                    Snackbar.make(getView(), "Position courante ... ", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                centerMapToCurrentPosition();
                break;
            case R.id.saveLocation:
                Snackbar.make(getView(), "Position courante enregistrée ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                savedLocation = currentLocation;
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
    private String getAlertNotification(){
        return "Attention nous avons détecté "+(compteurAccidentProche>0&&compteurIncidentProche>0?
                compteurAccidentProche+" accidents "+" et "+compteurIncidentProche+"incidents":
                compteurAccidentProche>0? compteurAccidentProche+" accidents": compteurIncidentProche+" incidents");
    }
    private void sendDanger(String channelId, int priority){
        Bitmap icon = BitmapFactory.decodeResource(requireActivity().getApplicationContext().getResources(),
                R.drawable.alert);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(requireActivity().getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Attention !")
                .setContentText("Danger dans votre périmètre")
                .setLargeIcon(icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getAlertNotification()))
                .setPriority(priority);
        ChannelNotification.getNotificationManager().notify(++notificationId ,notification.build());
    }
    public void createNotification(String channelId){

        NotificationCompat.Builder notification = new NotificationCompat.Builder(requireActivity().getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Attention!")
                .setContentText("Il y a un nouveau incident!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
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
        try {
            if(getActivity()!=null){
                addMaker(new GeoPoint(getUserCurrentLatitude(), getUserCurrentLongitude()), "Position Actuelle", requireActivity().getResources().getDrawable(R.drawable.ic_location_on_blue_24dp),true);
            }else{
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                try {
                                    addMaker(new GeoPoint(getUserCurrentLatitude(), getUserCurrentLongitude()), "Position Actuelle", requireActivity().getResources().getDrawable(R.drawable.ic_location_on_blue_24dp), true);
                                }catch (Exception e){
                                        e.printStackTrace();
                                    }
                            }
                        }, 2000);
            };
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public Incident getIncidentToPublish() {
        return null;
    }
    private void cleanOtherMarkers(){
        allAlerts.clear();
        map.getOverlays().clear();
        resetCurrentPostionMarker();
    }
    public void refreshMarkers(){
        cleanOtherMarkers();
        allAlerts.addAll(incidentController.get());
        allAlerts.addAll(accidentController.get());
        addAllOverlayItems();
    }
    private void addAllOverlayItems(){
        if(getContext()!=null){
            ArrayList<OverlayItem> list = new ArrayList<OverlayItem>();
            for (Alert alert:allAlerts){
                list.add(new OverlayItem(alert.getTitle(),alert.getDescription(),new GeoPoint(alert.getLatitude(),alert.getLongitude()) ));
            }
            ItemizedOverlayWithFocus<OverlayItem> mOverlay = new ItemizedOverlayWithFocus<OverlayItem>(getContext(), list,
                    new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                        @Override
                        public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                            Intent intent=new Intent(getContext(), AlertDetails.class);
                            // intent.putExtra("code", getKey(items,item));
                            startActivity(intent);
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
    public Accident getAccidentToPublish() {
        return null;
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
            if(getActivity()!=null)
            if(requireActivity().getSharedPreferences("setting", 0).getBoolean("followedGpsCamera",true))
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

    public double getSavedLatitude(){
        if (savedLocation != null){
            return savedLocation.getLatitude();}
        else{return 0;}
    }
    public double getSavedLongitude() {
        if (savedLocation != null) {
            return savedLocation.getLongitude();
        }
        else{return 0;}
    }

    public void resetSavedLocation(){savedLocation = null;}
}
