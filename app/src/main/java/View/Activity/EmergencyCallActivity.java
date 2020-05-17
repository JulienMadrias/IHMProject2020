package View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.ihmproject.R;

import java.util.Objects;

public class EmergencyCallActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Appel des urgence");
        ((Button)findViewById(R.id.general_call)).setOnClickListener(this);
        ((Button)findViewById(R.id.pompier)).setOnClickListener(this);
        ((Button)findViewById(R.id.police_secour)).setOnClickListener(this);
        ((Button)findViewById(R.id.samu)).setOnClickListener(this);
        ((Button)findViewById(R.id.returnFromEmergencyActivity)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()){
            case R.id.general_call:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:112"));
                break;
            case R.id.pompier:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:18"));
                break;
            case R.id.police_secour:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:17"));
                break;
            case R.id.samu:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:15"));
                break;
            case R.id.returnFromEmergencyActivity:
                finish();
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
        if(intent!=null)
        startActivity(intent);
    }
}
