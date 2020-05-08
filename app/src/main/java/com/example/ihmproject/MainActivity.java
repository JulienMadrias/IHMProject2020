package com.example.ihmproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

import Fragment.MapFragment;
import Fragment.ModeDeDeplacementFragment;
import Interface.IButtonDrawerClickListener;
import Interface.IButtonMapListener;
import PostImage.PostImage;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IButtonDrawerClickListener, View.OnClickListener, IButtonMapListener {
    private Intent intent;
    private DrawerLayout drawerLayout;
    private boolean permissionGranted;
    private Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(   getApplicationContext(),
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()) );

        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout, toolbar,
                R.string.naviguation_drawer_open, R.string.naviguation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView mainNavigationView = (NavigationView) findViewById(R.id.main_nav_view);
        mainNavigationView.setNavigationItemSelectedListener(this);
        mainNavigationView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                initiateLanguageSpinner();
                return insets;
            }
        });

        if(savedInstanceState == null) startMapFragment();
    }

    @Override
    public void onResume(){
        super.onResume();
        // map.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        // map.onPause();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }/*else if(Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).getTargetFragment() instanceof ModeDeDeplacementFragment){
            startMapFragment();
        }*/else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.nav_transport_mode) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ModeDeDeplacementFragment()).commit();
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        if(menuItem.getItemId() == R.id.SwitchGPS) {
            permissionGranted = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!permissionGranted) {

            }
            if (permissionGranted) {
            }
        }
        return true;
    }
    private void startMapFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MapFragment()).commit();
    }
    private void initiateLanguageSpinner(){
        Spinner languageSpinner = (Spinner) findViewById(R.id.languageSpinner);
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.languages));
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if(languageSpinner != null && languageSpinner.getAdapter()==null)
            languageSpinner.setAdapter(languagesAdapter);
    }
    @Override
    public void onCloseModeTransportButtonClicked(View v) {
        startMapFragment();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void mapIntentButtonClicked(View v) {
        switch(v.getId()){
            case R.id.incidentButton : intent = new Intent(this, IncidentActivity.class); break;
            case R.id.accidentButton: intent = new Intent(this, AccidentActivity.class); break;
        }
        if(intent != null)
        startActivity(intent);
    }
}
