package Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.ihmproject.R;
import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Map;

import Interface.IIncidentModelView;
import Model.Incident;

public class IncidentController{
    private Context context;
    private SharedPreferences pref=null;
    private SharedPreferences.Editor editor=null;
    private IIncidentModelView iIncidentModelView;
    public IncidentController(IIncidentModelView iIncidentModelView, Context context){
        this.iIncidentModelView = iIncidentModelView;
        this.context = context;
        recupPref();
    }
    private void recupPref(){
        pref = context.getSharedPreferences("MyPref", 0);
        editor = pref.edit();
    }
    public ArrayList<Incident> get(){
        recupPref();
        Log.d("jiv","récupération  des donnée...");
        ArrayList<Incident> incidents = new ArrayList<>();
        Gson gson = new Gson();
        Map<String, ?> allEntries = pref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String json = pref.getString(entry.getKey(), "");
            assert json != null;
            Log.d("jiv",json);
            if(json.startsWith("{")){
                Incident incident = gson.fromJson(json, Incident.class);
                incidents.add(incident);
                iIncidentModelView.addMaker(new GeoPoint( incident.getLatitude(),incident.getLongitude()), incident.getDescription(), context.getResources().getDrawable(R.drawable.ic_traffic_jam_svgrepo_com_marker),false);
                //OverlayItem alert = new OverlayItem(incident.getTitle(), incident.getDescription(), new GeoPoint(incident.getLongitude(), incident.getLatitude()));
                //items.add(alert);
            }
        }

        return incidents;
    }
    public Incident getById(){
        return null;
    }

    public void postIncident(){
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
