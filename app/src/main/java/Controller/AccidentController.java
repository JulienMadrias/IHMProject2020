package Controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Map;

import Interface.IAccidentModelView;
import Interface.IAlertController;
import Model.Alert;
import Model.Accident;

public class AccidentController extends AlertController implements IAlertController<Accident> {
    private IAccidentModelView iAccidentModelView;
    public AccidentController(IAccidentModelView iAccidentModelView, Context context){
        super(context,ACCIDENT_PREF);
        this.iAccidentModelView = iAccidentModelView;
    }
    public ArrayList<Accident> get() {
        Log.d("jiv","récupération  des accidents...");
        ArrayList<Accident> Accidents = new ArrayList<>();
        try{
            Gson gson = new Gson();
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                String json = pref.getString(entry.getKey(), "");
                assert json != null;
                Log.d("jiv",json);
                if(json.startsWith("{")){
                    Accident Accident = gson.fromJson(json, Accident.class);
                    Accidents.add(Accident);
                    iAccidentModelView.addMaker(new GeoPoint(Accident.getLatitude(), Accident.getLongitude()), Accident.getDescription(), Alert.getIcon(Accident.getType(),context),false);
                    //OverlayItem alert = new OverlayItem(Accident.getTitle(), Accident.getDescription(), new GeoPoint(Accident.getLongitude(), Accident.getLatitude()));
                    //items.add(alert);
                }
            }

        }catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return Accidents;
    }
    public Accident getByOverlayItem(OverlayItem overlayItem){
        return null;
    }

    public void post(){
        //set variables of 'myObject', etc.
        Accident Accident = iAccidentModelView.getAccidentToPublish();
        if(Accident!=null){
            Gson gson = new Gson();
            String json = gson.toJson(Accident);
            editor.putString(Accident.getTitle()+Accident.getLongitude(), json);
            editor.commit();
            json = pref.getString(Accident.getTitle()+Accident.getLongitude(), "");
            Accident obj = gson.fromJson(json, Accident.class);
            Log.d("jiv",obj.getDescription());
            Log.d("jiv",obj.toString());
            //ChannelNotification notification = new ChannelNotification();
            //notification.createNotification(getBaseContext(),getClass());
        }
    }
}
