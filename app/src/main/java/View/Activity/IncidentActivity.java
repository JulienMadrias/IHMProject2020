package View.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihmproject.R;

import Controller.AlertController;
import Controller.IncidentController;
import Interface.IActivitiesCodeResult;
import Interface.IGPSActivity;
import Interface.IIncidentModelView;

import com.google.android.material.snackbar.Snackbar;

import org.osmdroid.util.GeoPoint;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.Alert;
import Model.Incident;
import Model.PostImage;
import View.Fragment.AddPhotoDialogFragment;
import View.Fragment.PictureFragment;
import View.Fragment.PostImageListFragment;
import View.Fragment.StorageFragment;
import Interface.IButtonIncidentListener;
import Interface.IDescriptionListener;
import Interface.IPhotoDialogListener;
import Interface.IPictureActivity;
import Interface.IPostImageClickListener;
import Interface.IStorageActivity;
import Model.ListOfImages;

public class IncidentActivity extends AppCompatActivity implements IButtonIncidentListener, View.OnClickListener, IPhotoDialogListener, IPictureActivity, IStorageActivity, IPostImageClickListener, IDescriptionListener, IGPSActivity, IIncidentModelView {
    Intent intent;

    AlertDialog alertDialog;
    private Bitmap picture;
    private PostImageListFragment postImageListFragment;
    private PictureFragment pictureFragment;
    private StorageFragment storageFragment;
    private AddPhotoDialogFragment addPhotoDialogFragment;
    private TextView pictureTotalShower;
    private EditText description;
    private SharedPreferences pref=null;
    private SharedPreferences prefBouton=null;
    private SharedPreferences.Editor editor=null;
    private SharedPreferences.Editor editorBouton=null;
    private double latitude = 43.615102;
    private double longitude= 7.080124;
    private double savedLongitude = 0;
    private double savedLatitude = 0;
    private IncidentController incidentController;

    private Button auto,motard,cycliste,camion,pieton,bus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        addPhotoDialogFragment = new AddPhotoDialogFragment(this);
        //createPictureFragment();
        createPostImageListFragment();
        // createStorageFragment();
        pictureTotalShower = (TextView) findViewById(R.id.picturesCountShower);

        ((ImageButton)findViewById(R.id.add_incident_photo)).setOnClickListener(this);
        ((Button)findViewById(R.id.publishIncidentButton)).setOnClickListener(this);
        ((Button)findViewById(R.id.cancelIncidentButton)).setOnClickListener(this);

        description= (EditText)findViewById(R.id.editText);
        pref = getApplication().getApplicationContext().getSharedPreferences(AlertController.INCIDENT_PREF, 0); // 0 - for private mode
        incidentController = new IncidentController(this, this);
        prefBouton = getApplication().getApplicationContext().getSharedPreferences("MyPrefBouton", 0); // 0 - for private mode
        editor = pref.edit();

        editorBouton = prefBouton.edit();

        auto = ((Button) findViewById(R.id.voitureButton));
        auto.setOnClickListener(this);

        motard = ((Button) findViewById(R.id.motoButton));
        motard.setOnClickListener(this);

        cycliste = ((Button) findViewById(R.id.veloButton));
        cycliste.setOnClickListener(this);

        camion = ((Button) findViewById(R.id.camionButton));
        camion.setOnClickListener(this);

        pieton = ((Button) findViewById(R.id.pietonButton));
        pieton.setOnClickListener(this);

        bus = ((Button) findViewById(R.id.busButton));
        bus.setOnClickListener(this);

        if(prefBouton.getString("valeurBoutonVehicule","automobile")=="automobile")
            editorBouton.putString("valeurBoutonVehicule", "automobile");
        editorBouton.apply();
        initialiseMode();

        longitude= getIntent().getDoubleExtra("longitude",0);
        latitude= getIntent().getDoubleExtra("latitude",0);
        savedLatitude=getIntent().getDoubleExtra("saveLatitude", 0);
        savedLongitude=getIntent().getDoubleExtra("saveLongitude", 0);


        Objects.requireNonNull(getSupportActionBar()).setTitle("Déclaration d'incident");
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

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
    private void createPostImageListFragment(){
        postImageListFragment = (PostImageListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_image_list);
        if(postImageListFragment == null){
            postImageListFragment = new PostImageListFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_picture_container,postImageListFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    private void sendDanger(String channelId, int priority){
        Bitmap icon = BitmapFactory.decodeResource(this.getApplicationContext().getResources(),
                R.drawable.ic_traffic_jam_svgrepo_com_marker);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this.getApplicationContext(),channelId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Inforamtion !")
                .setContentText("Publication d'incident effectuée")
                .setLargeIcon(icon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Incident publié avc succès"))
                .setPriority(priority);
        ChannelNotification.getNotificationManager().notify(2,notification.build());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.add_incident_photo:
                Snackbar.make(v, "Ajout de photo", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                photoImportChoiceDialog();
                break;
            case R.id.publishIncidentButton:
                incidentController.post();
                editorBouton.clear();
                editorBouton.commit();
                Toast.makeText(this,"Publication de l'incident en cour...",Toast.LENGTH_SHORT).show();
                sendDanger("channel1", NotificationCompat.PRIORITY_DEFAULT);
                finish();
                //ChannelNotification notification = new ChannelNotification();
                //notification.createNotification(getApplicationContext(),getClass());
                break;
            case R.id.cancelIncidentButton:
                finish();
                break;
            case R.id.voitureButton:
                editorBouton.putString("valeurBoutonVehicule", "automobile");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.busButton:
                editorBouton.putString("valeurBoutonVehicule", "bus");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.pietonButton:
                editorBouton.putString("valeurBoutonVehicule", "pieton");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.motoButton:
                editorBouton.putString("valeurBoutonVehicule", "motard");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.camionButton:
                editorBouton.putString("valeurBoutonVehicule", "camion");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.veloButton:
                editorBouton.putString("valeurBoutonVehicule", "cycliste");
                editorBouton.commit();
                initialiseMode();
                break;
            case R.id.editText:
                startActivityForResult(new Intent(this, DescriptionActivity.class),COMMENT_RESULT_CODE);
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void finish() {
        setResult(IActivitiesCodeResult.INCIDENT_RESULT_CODE, intent);
        super.finish();
    }

    public void updateMap() {

    }
    private void photoImportChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(addPhotoDialogFragment.getThisView(getLayoutInflater()))
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getWindow().setLayout(1000, 700);
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onPhotoClik() {
        Snackbar.make(findViewById(R.id.incidentLayout), "Prise de photo via camera", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        if(ContextCompat.checkSelfPermission( getBaseContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, IPictureActivity.REQUEST_CAMERA);
        }else {
            takePicture();
        }
    }

    @Override
    public void onImportPhotoClick() {
        Snackbar.make(findViewById(R.id.incidentLayout), "Import de photo cliqué", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_IMPORT);
        /*intent = new Intent();
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);*/
    }

    @Override
    public void onCancel() {
        Snackbar.make(findViewById(R.id.incidentLayout), "Ajout de photo annulé", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    /**
     *callback from requestPermission
     * @param requestCode
     * @param permissions
     * @param grantResults*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults){
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Autorisation Camera acceptée",Toast.LENGTH_SHORT).show();
                    takePicture();
                }else{
                    Toast.makeText(this,"Autorisation Camera refusée",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_MEDIA_WRITE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    storageFragment.saveToInternalStorage(picture);
                    Toast.makeText(this,"Permission d'écriture acceptée",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission d'écriture refusée",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_MEDIA_READ:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission de lecture acceptée",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission de lecture refusée",Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_GPS_CODE:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission d'accès Gps acceptée",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this,"Permission d'accès lecture refusée",Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    /**
     *callback from startActivity
     * @param requestCode
     * @param resultCode
     * @param data*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // storageFragment.setEnableSaveButton();
        if(alertDialog!=null)
        alertDialog.dismiss();
        // if(resultCode == RESULT_OK) storageFragment.setEnableSaveButton();
        switch (requestCode){
            case COMMENT_RESULT_CODE:
                assert data != null;
                ((EditText)findViewById(R.id.editText)).setText(data.getStringExtra(COMMENT_RETURN_ID));
                break;
            case REQUEST_CAMERA:
                if(resultCode == RESULT_OK){
                    picture = (Bitmap) data.getExtras().get("data");
                    //setImage(picture);
                    alertDialog.dismiss();
                    postImageListFragment.addNewPostImage(picture);
                    //pictureFragment.setImage(picture);
                } else if (resultCode == RESULT_CANCELED){
                    Toast.makeText(this,"Prise de photo annulée!",Toast.LENGTH_SHORT).show();
                    takePicture();
                }else{
                    Toast.makeText(this,"Echec de la prise de photo!",Toast.LENGTH_SHORT).show();
                    takePicture();
                }
                break;
            //pictureFragment.setImage(picture);break;
            case REQUEST_IMPORT:
                if(resultCode == RESULT_OK){
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        picture = BitmapFactory.decodeStream(imageStream);
                        postImageListFragment.addNewPostImage(picture);
                        //pictureFragment.setImage(picture);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Une erreur s'est produite",Toast.LENGTH_LONG).show();

                    }
                }else{
                    Toast.makeText(this,"Vous n'avez pas choisi de photo!",Toast.LENGTH_SHORT).show();
                    takePicture();
                } break;
        }
    }

    @Override
    public void onPictureLoad(Bitmap bitmap) {
    }

    @Override
    public Bitmap getPictureToSave() {
        return picture;
    }

    @Override
    public void onPostImageClicked(int position) {
        Toast.makeText(this,"une photo a été cliquée!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void incrementImageTotal() {
        pictureTotalShower.setText(ListOfImages.listOfPostImages.size()+"");
    }

    @Override
    public void moveCamera() {

    }

    @Override
    public Incident getIncidentToPublish() {

        String description= this.description.getText().toString();
        String title= "incident " + prefBouton.getString("valeurBoutonVehicule","");
        //double longitude=43.622448;
        //ListOfImages.listOfPostImages
        ArrayList<String> images = new ArrayList<>();
        for (PostImage postImage:ListOfImages.listOfPostImages){
            images.add(PostImage.encode(postImage.getPicture()));
        }
        if(savedLatitude != 0 && savedLongitude != 0){
            return new Incident(savedLongitude,savedLatitude,title,description, images);
        }
        else{ return new Incident(longitude,latitude,title,description, images);}
    }

    @Override
    public void addMaker(GeoPoint startPoint, String title, Drawable icon, boolean userPosition) {

    }

}
