package Controller;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import Interface.IAlertController;
import Model.Alert;

public class AlertController {

    public static final String ACCIDENT_PREF = "prefAccident";
    public static final String INCIDENT_PREF = "prefIncident";
    protected Context context;
    protected SharedPreferences pref=null;
    protected SharedPreferences.Editor editor=null;
    public AlertController(Context context, String prefString){
        this.context = context;
        pref=context.getSharedPreferences(prefString, 0);
        editor = pref.edit();
    }
    public int getNumberOfAlert() {
        return pref.getAll().size();
    }
}
