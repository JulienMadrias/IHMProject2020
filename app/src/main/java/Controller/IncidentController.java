package Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ihmproject.R;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Map;

import Interface.IAlertController;
import Interface.IIncidentModelView;
import Model.Alert;
import Model.Incident;

public class IncidentController extends AlertController implements IAlertController<Incident> {
    private IIncidentModelView iIncidentModelView;
    public IncidentController(IIncidentModelView iIncidentModelView, Context context){
        super(context,INCIDENT_PREF);
        this.iIncidentModelView = iIncidentModelView;
    }
    public ArrayList<Incident> get() {
        Log.d("jiv","récupération  des incidents...");
        ArrayList<Incident> incidents = new ArrayList<>();
        try{
            Gson gson = new Gson();
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String json = pref.getString(entry.getKey(), "");
                assert json != null;
                Log.d("jiv",json);
                if(json.startsWith("{")){
                    Incident incident = gson.fromJson(json, Incident.class);
                    incidents.add(incident);

                    iIncidentModelView.addMaker(new GeoPoint(incident.getLatitude(), incident.getLongitude()), incident.getDescription(), Alert.getIcon(incident.getType(),context),false);
                    //OverlayItem alert = new OverlayItem(incident.getTitle(), incident.getDescription(), new GeoPoint(incident.getLongitude(), incident.getLatitude()));
                    //items.add(alert);
                }
            }

        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return incidents;
    }
    public Incident getByOverlayItem(OverlayItem overlayItem){
        return null;
    }

    public void post(){
        //set variables of 'myObject', etc.
        Incident incident = iIncidentModelView.getIncidentToPublish();
        if(incident!=null){
            Gson gson = new Gson();
            String json = gson.toJson(incident);
            editor.putString(incident.getTitle()+incident.getLongitude(), json);
            editor.commit();
            json = pref.getString(incident.getTitle()+incident.getLongitude(), "");
            Incident obj = gson.fromJson(json, Incident.class);
            Log.d("jiv",obj.getDescription());
            Log.d("jiv",obj.toString());
            //ChannelNotification notification = new ChannelNotification();
            //notification.createNotification(getBaseContext(),getClass());
        }
    }
}
