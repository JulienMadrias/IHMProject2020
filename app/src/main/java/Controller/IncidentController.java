package Controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import Interface.IIncidentModelView;
import Model.Incident;
import View.Activity.ChannelNotification;

public class IncidentController{

    private SharedPreferences pref=null;
    private SharedPreferences.Editor editor=null;
    private IIncidentModelView iIncidentModelView;
    public IncidentController(IIncidentModelView iIncidentModelView, SharedPreferences sharedPreferences){
        this.iIncidentModelView = iIncidentModelView;
        pref = sharedPreferences;
        editor = pref.edit();
    }
    public void publishIncident(){
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
            //ChannelNotification notification = new ChannelNotification();
            //notification.createNotification(getBaseContext(),getClass());
        }
    }
}
