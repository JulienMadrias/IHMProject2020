package com.example.ihmproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

public class IncidentActivity extends AppCompatActivity implements IButtonIncidentListener, View.OnClickListener, IPhotoDialogListener, IPictureActivity {
    private Intent intent;
    ImageView imageView;
    private Bitmap picture;
    private PictureFragment pictureFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        createPictureFragment();
        ((Button)findViewById(R.id.incidentToMain)).setOnClickListener(this);
        ((Button)findViewById(R.id.add_incident_photo)).setOnClickListener(this);
    }
    private void createPictureFragment(){
        pictureFragment = (PictureFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_picture);
        if(pictureFragment == null){
            pictureFragment = new PictureFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_picture_container,pictureFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.incidentToMain: finish(); break;
            case R.id.add_incident_photo:
                Snackbar.make(v, "Button d'incident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                photoImportChoiceDialog();
                break;
            case R.id.picture_from_camera:
                Snackbar.make(findViewById(R.id.incidentLayout), "Ajout de photo annulé", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if(ContextCompat.checkSelfPermission( getBaseContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, IPictureActivity.REQUEST_CAMERA);
                }else {
                    takePicture();
                }
                break;
            case R.id.import_pictures:

                break;
        }
    }
    private void photoImportChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        /*
        * tout ceci sera déplacé plus tard pour question de reponsabilité*/
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_photo_dialog,null);
        View viewFragmentPicture = inflater.inflate(R.layout.fragment_picture, null);
        imageView = viewFragmentPicture.findViewById(R.id.imageTaken);

        builder.setView(view)
                // Add action buttons
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // LoginDialogFragment.this.getDialog().cancel();
                    }
                });

         AlertDialog alertDialog = builder.create();
        ((ImageButton)view.findViewById(R.id.picture_from_camera)).setOnClickListener(this);
        ((ImageButton)view.findViewById(R.id.import_pictures)).setOnClickListener(this);
        alertDialog.show();
    }

    public void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IPictureActivity.REQUEST_CAMERA);
    }

    @Override
    public void onPhotoClik(DialogFragment dialog) {
        Snackbar.make(findViewById(R.id.incidentLayout), "Prise de photo via camera", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        if(ContextCompat.checkSelfPermission( getBaseContext(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, IPictureActivity.REQUEST_CAMERA);
        }else {
            takePicture();
        }
    }

    @Override
    public void onImportPhotoClick(DialogFragment dialog) {
        Snackbar.make(findViewById(R.id.incidentLayout), "Import de photo clické", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onCancel(DialogFragment dialog) {
        Snackbar.make(findViewById(R.id.incidentLayout), "Ajout de photo annulé", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    public void setImage(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        imageView.setImageResource(R.drawable.twitter);
        Log.d("jiv",bitmap.toString());
        /*Toast.makeText(this,"Prise effectuée  avec succès!",Toast.LENGTH_SHORT).show();
        takePicture();*/
    }
    /**
     *callback from requestPermission
     * @param requestCode
     * @param permissions
     * @param grantResults*/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case REQUEST_CAMERA:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"Permission acceptée",Toast.LENGTH_SHORT).show();
                    takePicture();
                }else{
                    Toast.makeText(this,"Permission refusée",Toast.LENGTH_SHORT).show();
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
        if( requestCode == REQUEST_CAMERA){
            if(resultCode == RESULT_OK){
                picture = (Bitmap) data.getExtras().get("data");
                //setImage(picture);
                pictureFragment.setImage(picture);
            } else if (resultCode == RESULT_CANCELED){
                Toast.makeText(this,"Prise de photo annulée!",Toast.LENGTH_SHORT).show();
                takePicture();
            }else{
                Toast.makeText(this,"Echec de la prise de photo!",Toast.LENGTH_SHORT).show();
                takePicture();
            }
            //pictureFragment.setImage(picture);
        }
    }
}
