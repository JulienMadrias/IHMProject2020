package com.example.ihmproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.snackbar.Snackbar;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import Fragment.AddPhotoDialogFragment;
import Fragment.PictureFragment;
import Fragment.PostImageListFragment;
import Fragment.StorageFragment;
import Interface.IButtonIncidentListener;
import Interface.IPhotoDialogListener;
import Interface.IPictureActivity;
import Interface.IPostImageClickListener;
import Interface.IStorageActivity;
import PostImage.ListOfImages;

public class IncidentActivity extends AppCompatActivity implements IButtonIncidentListener, View.OnClickListener, IPhotoDialogListener, IPictureActivity, IStorageActivity, IPostImageClickListener {
    Intent intent;
    AlertDialog alertDialog;
    ImageView imageView;
    private Bitmap picture;
    private PostImageListFragment postImageListFragment;
    private PictureFragment pictureFragment;
    private StorageFragment storageFragment;
    private AddPhotoDialogFragment addPhotoDialogFragment;
    private TextView pictureTotalShower;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        addPhotoDialogFragment = new AddPhotoDialogFragment(this);
        //createPictureFragment();
        createPostImageListFragment();
        // createStorageFragment();
        //Objects.requireNonNull(getActionBar()).setTitle("Incident");
        //Objects.requireNonNull(getSupportActionBar()).setTitle("Incident");
        pictureTotalShower = (TextView) findViewById(R.id.picturesCountShower);
        ((Button)findViewById(R.id.add_incident_photo)).setOnClickListener(this);
        ((Button)findViewById(R.id.publishIncidentButton)).setOnClickListener(this);
        getSupportActionBar().setTitle("Incident");
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
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
    private void createStorageFragment(){
        storageFragment = (StorageFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_storage_control);
        if(storageFragment == null){
            storageFragment = new StorageFragment(this);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // transaction.replace(R.id.fragment_storage_control_container,storageFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_incident_photo:
                Snackbar.make(v, "Boutton d'incident cliqué", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                photoImportChoiceDialog();
                break;
            case R.id.publishIncidentButton:
                Toast.makeText(this,"l'incident sera très bientôt publié.",Toast.LENGTH_SHORT).show();
                finish();
        }
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
        alertDialog.dismiss();
        // if(resultCode == RESULT_OK) storageFragment.setEnableSaveButton();
        switch (requestCode){
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
        pictureFragment.setImage(bitmap);
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
    /*@Override
    public void setActionBar(String heading) {
        // TODO Auto-generated method stub

        com.actionbarsherlock.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.title_bar_gray)));
        actionBar.setTitle(heading);
        actionBar.show();

    }*/
}
