package com.example.ihmproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;

public class IncidentActivity extends AppCompatActivity implements IButtonIncidentListener, View.OnClickListener, IPhotoDialogListener {
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incident);
        ((Button)findViewById(R.id.incidentToMain)).setOnClickListener(this);
        ((Button)findViewById(R.id.add_incident_photo)).setOnClickListener(this);
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

                break;
            case R.id.import_pictures:

                break;
        }
    }
    private void photoImportChoiceDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Get the layout inflater
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_photo_dialog,null);
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

    @Override
    public void onPhotoClik(DialogFragment dialog) {
        Snackbar.make(findViewById(R.id.incidentLayout), "Prise de photo via camera", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
}
