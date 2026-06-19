package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SpeedPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_page);
        Spinner scsp1 = findViewById(R.id.sps1);
        Spinner scsp2 = findViewById(R.id.spsc2);
        EditText sced1 = findViewById(R.id.edsc1);
        EditText sced2 = findViewById(R.id.edsc2);

        ArrayAdapter<CharSequence> adaptersc1 = ArrayAdapter.createFromResource(this, R.array.spsc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adaptersc2 = ArrayAdapter.createFromResource(this, R.array.spsc, android.R.layout.simple_spinner_item);
        adaptersc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        scsp1.setAdapter(adaptersc1);
        scsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sced1.setText("0");
            }
        });

    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }

}
