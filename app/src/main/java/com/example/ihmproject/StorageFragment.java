package com.example.ihmproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StorageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StorageFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private IStorageActivity activity;

    private Button buttonSave;
    private Button buttonLoad;
    private String pictureName;
    private String directoryName;

    public StorageFragment() {
        // Required empty public constructor
    }
    public StorageFragment(IStorageActivity activity) {
        this.activity = activity;
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StorageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StorageFragment newInstance(String param1, String param2) {
        StorageFragment fragment = new StorageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_storage, container, false);
        pictureName = "_test.jpg";
        Context base;
        ContextWrapper contextWrapper = new ContextWrapper(getContext());
        directoryName = contextWrapper.getDir("ImageDir",ContextWrapper.MODE_PRIVATE).getPath(); //path to /data/user/0/etu.demo.camera/app_imageDir
        buttonLoad = rootView.findViewById(R.id.button_load_picture);
        buttonSave = rootView.findViewById(R.id.button_save_picture);
        setDisableSaveButton();
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap picture = activity.getPictureToSave();
                if(picture!=null){
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                IStorageActivity.REQUEST_MEDIA_WRITE);
                    } else { //permission is still granted
                        saveToInternalStorage(picture);
                        setDisableSaveButton();
                    }
                }
            }
        });
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                                IStorageActivity.REQUEST_MEDIA_WRITE);
                    } else { //permission is still granted
                        activity.onPictureLoad(loadImageFromStorage());
                    }
            }
        });
        return rootView;
    }

    public Bitmap loadImageFromStorage() {
        try {
            File file = new File(directoryName, pictureName);
            Toast.makeText(getContext(), "Image chargée avec succès", Toast.LENGTH_LONG).show();
            return BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveToInternalStorage(Bitmap picture) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, pictureName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/*");
        // contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, directoryName);
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,contentValues);
        File file = new File(directoryName,pictureName);
        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(file);
            Toast.makeText(getContext(), "Image sauvegardée", Toast.LENGTH_LONG).show();
            picture.compress(Bitmap.CompressFormat.PNG, 90, fos);
        } catch (FileNotFoundException e) {
            Toast.makeText(getContext(), "Désolé une erreur s'est produite", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    public void setDisableSaveButton() {
        buttonSave.setEnabled(false);
    }

    public void setEnableSaveButton() {
        buttonSave.setEnabled(true);
    }

    public void setDisableLoadButton() {
        buttonLoad.setEnabled(false);
    }

    public void setEnableLoadButton() {
        buttonLoad.setEnabled(true);
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
