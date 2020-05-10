package View.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ihmproject.R;

import Interface.IDescriptionListener;

public class DescriptionActivity extends AppCompatActivity implements IDescriptionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);

        findViewById(R.id.cancelComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.okComment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(DescriptionActivity.this,"validé",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.putExtra(COMMENT_RETURN_ID, ((EditText)findViewById(R.id.descriptionText)).getText()); //value should be your string from the edittext
                setResult(RESULT_OK, intent); //The data you want to send back
                Toast.makeText(DescriptionActivity.this, "départ: "+((EditText)findViewById(R.id.descriptionText)).getText(),Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
