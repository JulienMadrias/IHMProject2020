package View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.ihmproject.R;

import java.util.Objects;

public class AccidentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accident);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Déclaration d'accident");
    }
}
