package View.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowInsets;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.config.Configuration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.ihmproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Objects;

import Interface.IActivitiesCodeResult;
import Interface.IGPSActivity;
import View.Fragment.MapFragment;
import View.Fragment.ModeDeDeplacementFragment;
import Interface.IButtonDrawerClickListener;
import Interface.IButtonMapListener;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, IButtonDrawerClickListener, View.OnClickListener, IButtonMapListener, IActivitiesCodeResult, IGPSActivity {
    private Intent intent;
    private DrawerLayout drawerLayout;

    private MapFragment mapFragment;
    private boolean permissionGranted;


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

        if(savedInstanceState == null)
            startMapFragment();
        //Log.d("jiv",savedInstanceState==null?"il est null":savedInstanceState.toString());
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
        }*/
        else if(Objects.equals(Objects.requireNonNull(getSupportActionBar()).getTitle(), "Mode de déplacement")){
            startMapFragment();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.twitterButton:
                Intent intent = null;
                try {
                    // get the Twitter app if possible
                    getPackageManager().getPackageInfo("com.twitter.android", 0);
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=ProjectIhm"));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } catch (Exception e) {
                    // no Twitter app, revert to browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ProjectIhm"));
                }
                startActivity(intent);
                break;
            case R.id.callEmergency:
                intent = new Intent(this, EmergencyCallActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_transport_mode:
                getSupportActionBar().setTitle("Mode de déplacement");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ModeDeDeplacementFragment()).commit();
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            //case R.id.SwitchGPS:
            case R.id.switchGpsMain:
                //((Switch)findViewById(R.id.switchGpsMain)).setEnabled(((Switch)findViewById(R.id.switchGpsMain)).isChecked());
                Toast.makeText(this, ((Switch)findViewById(R.id.switchGpsMain)).isChecked()?"La caméra viendra automatiquement à votre position actuellemnt une fois actuelisée":"Vous pourrez naviguez sans être dérangé par l'actualisation de votre position",Toast.LENGTH_SHORT).show();
                getSharedPreferences("setting", 0).edit().putBoolean("followedGpsCamera",((Switch)findViewById(R.id.switchGpsMain)).isChecked()).apply();
                break;
            case R.id.shareAppli:
                String messageToSend = getString(R.string.app_share_message);
                intent = new Intent(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT,messageToSend);
                intent.setType("text/plain");
                startActivity(intent);
                break;
            case R.id.stopAppli:
                finish();
                break;
        }
        return true;
    }
    private void startMapFragment(){
        getSupportActionBar().setTitle(R.string.app_name);
        if(mapFragment==null)mapFragment = new MapFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                mapFragment).commit();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_GPS_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Permission d'accès Gps acceptée",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"Permission d'accès lecture refusée",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case INCIDENT_RESULT_CODE:
            case ACCIDENT_RESULT_CODE:
                mapFragment.refreshMarkers();
                break;
        }
    }
    private void inititateIntent(){
        if(intent!=null){
            intent.putExtra("longitude",mapFragment.getUserCurrentLongitude());
            intent.putExtra("latitude",mapFragment.getUserCurrentLatitude());
            intent.putExtra("saveLatitude", mapFragment.getSavedLatitude());
            intent.putExtra("saveLongitude", mapFragment.getSavedLongitude());
        }
    }
    @Override
    public void mapIntentButtonClicked(View v) {
        int resultCode = 0;
        switch(v.getId()){
            case R.id.incidentButton :
                resultCode = INCIDENT_RESULT_CODE;
                intent = new Intent(this, IncidentActivity.class);
                inititateIntent();
                break;
            case R.id.accidentButton:
                resultCode = ACCIDENT_RESULT_CODE;
                intent = new Intent(this, AccidentActivity.class);
                inititateIntent();
                break;
        }
        if(intent != null){
            inititateIntent();
            // intent.putExtra("name",mapFragment.getLocationName());
        startActivityForResult(intent,resultCode);
        mapFragment.resetSavedLocation();
        }
    }

    @Override
    public void moveCamera() {

    }
}
