package View.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihmproject.R;
import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Objects;

import Controller.AccidentController;
import Controller.AlertController;
import Interface.IAccidentModelView;
import Interface.IActivitiesCodeResult;
import Model.Accident;
import Model.Alert;
import Model.Incident;
import Model.ListOfImages;
import Model.PostImage;
import View.Fragment.AddPhotoDialogFragment;
import View.Fragment.PictureFragment;
import View.Fragment.PostImageListFragment;
import View.Fragment.StorageFragment;

public class AccidentActivity extends AppCompatActivity implements View.OnClickListener, IAccidentModelView {
    Intent intent;

    AlertDialog alertDialog;
    private Bitmap picture;
    private PostImageListFragment postImageListFragment;
    private PictureFragment pictureFragment;
    private StorageFragment storageFragment;
    private AddPhotoDialogFragment addPhotoDialogFragment;
    private TextView pictureTotalShower;
    private EditText description, casualtyNumberEdit;
    private SharedPreferences pref=null;
    private SharedPreferences prefBouton=null;
    private SharedPreferences.Editor editor=null;
    private SharedPreferences.Editor editorBouton=null;
    private double latitude = 43.615102;
    private double longitude= 7.080124;
    private double savedLongitude = 0;
    private double savedLatitude = 0;
    private AccidentController accidentController;

    private Button auto,motard,cycliste,camion,pieton,bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Déclaration d'accident");
        ((Button)findViewById(R.id.publishAccidentButton)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancelAccidentButton)).setOnClickListener(this);
        accidentController = new AccidentController(this,this);

        description= (EditText)findViewById(R.id.editTextAccident);
        casualtyNumberEdit= (EditText)findViewById(R.id.casualtyNumber);

        pref = getApplication().getApplicationContext().getSharedPreferences(AlertController.ACCIDENT_PREF, 0); // 0 - for private mode

        prefBouton = getApplication().getApplicationContext().getSharedPreferences("MyPrefBouton", 0); // 0 - for private mode

        editor = pref.edit();

        editorBouton = prefBouton.edit();

        auto = ((Button) findViewById(R.id.voitureButtonAccident));
        auto.setOnClickListener(this);

        motard = ((Button) findViewById(R.id.motoButtonAccident));
        motard.setOnClickListener(this);

        cycliste = ((Button) findViewById(R.id.veloButtonAccident));
        cycliste.setOnClickListener(this);

        camion = ((Button) findViewById(R.id.camionButtonAccident));
        camion.setOnClickListener(this);

        pieton = ((Button) findViewById(R.id.pietonButtonAccident));
        pieton.setOnClickListener(this);

        bus = ((Button) findViewById(R.id.busButtonAccident));
        bus.setOnClickListener(this);

        if(prefBouton.getString("valeurBoutonVehicule","automobile")=="automobile")
            editorBouton.putString("valeurBoutonVehicule", "automobile");
        editorBouton.apply();
        initialiseMode();

        longitude= getIntent().getDoubleExtra("longitude",0);
        latitude= getIntent().getDoubleExtra("latitude",0);
        savedLatitude=getIntent().getDoubleExtra("saveLatitude", 0);
        savedLongitude=getIntent().getDoubleExtra("saveLongitude", 0);


    }

    private void initialiseMode(){
        auto.setBackgroundColor(Color.WHITE);
        motard.setBackgroundColor(Color.WHITE);
        cycliste.setBackgroundColor(Color.WHITE);
        camion.setBackgroundColor(Color.WHITE);
        pieton.setBackgroundColor(Color.WHITE);
        bus.setBackgroundColor(Color.WHITE);

        auto.setTextColor(Color.BLACK);
        motard.setTextColor(Color.BLACK);
        cycliste.setTextColor(Color.BLACK);
        camion.setTextColor(Color.BLACK);
        pieton.setTextColor(Color.BLACK);
        bus.setTextColor(Color.BLACK);
        switch (Objects.requireNonNull(prefBouton.getString("valeurBoutonVehicule", "automobile"))){
            case "automobile":
                auto.setTextColor(Color.WHITE);
                auto.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
            case "motard":
                motard.setTextColor(Color.WHITE);
                motard.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
            case "cycliste":
                cycliste.setTextColor(Color.WHITE);
                cycliste.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
            case "camion":
                camion.setTextColor(Color.WHITE);
                camion.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
            case "pieton":
                pieton.setTextColor(Color.WHITE);
                pieton.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
            case "bus":
                bus.setTextColor(Color.WHITE);
                bus.setBackgroundColor(Color.rgb(98, 0, 238));
                break;
        }
    }

    private void sendDanger(String channelId, int priority){
        Bitmap icon = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),
                R.drawable.ic_accident_svgrepo_com_marker);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Information !")
                .setContentText("Publication d'accident éffectuée")
                .setLargeIcon(icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Accident publié avc succès"))
                .setPriority(priority);
        ChannelNotification.getNotificationManager().notify(1,notification.build());
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.publishAccidentButton:
                accidentController.post();
                editorBouton.clear();
                editorBouton.commit();
                Toast.makeText(this,"Publication de l'accident en cour...",Toast.LENGTH_SHORT).show();
                setResult(IActivitiesCodeResult.INCIDENT_RESULT_CODE, intent); //The data you want to send back
                sendDanger("channel1", NotificationCompat.PRIORITY_DEFAULT);
                finish();
                break;
            case R.id.voitureButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "automobile");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.cancelAccidentButton:
                finish();
            case R.id.busButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "bus");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.pietonButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "pieton");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.motoButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "motard");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.camionButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "camion");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.veloButtonAccident:
                editorBouton.putString("valeurBoutonVehicule", "cycliste");
                editorBouton.commit();
                initialiseMode();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void addMaker(GeoPoint startPoint, String title, Drawable icon, boolean userPosition) {

    }

    @Override
    public Accident getAccidentToPublish() {

        String description= this.description.getText().toString();
        String title= "accident " + prefBouton.getString("valeurBoutonVehicule","");
        int casualtyNumber =  casualtyNumberEdit.getText().toString().length()>0? Integer.parseInt(casualtyNumberEdit.getText().toString()):1;
        casualtyNumber = casualtyNumber>0?casualtyNumber:1;
        //double longitude=43.622448;
        //ListOfImages.listOfPostImages
        ArrayList<String> images = new ArrayList<>();
        for (PostImage postImage: ListOfImages.listOfPostImages){
            images.add(PostImage.encode(postImage.getPicture()));
        }
        if(savedLatitude != 0 && savedLongitude != 0){
            return new Accident(savedLongitude,savedLatitude,title,description, casualtyNumber);
        }
        else{ return new Accident(longitude,latitude,title,description, casualtyNumber);}
    }
}
