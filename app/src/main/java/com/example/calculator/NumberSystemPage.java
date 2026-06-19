package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class NumberSystemPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_system_page);
        Spinner ncsp1 = findViewById(R.id.spn1);
        Spinner ncsp2 = findViewById(R.id.spnc2);
        EditText nced1 = findViewById(R.id.ednc1);
        EditText nced2 = findViewById(R.id.ednc2);
        ArrayAdapter<CharSequence> adapternc1 = ArrayAdapter.createFromResource(this, R.array.spnc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapternc2 = ArrayAdapter.createFromResource(this, R.array.spnc, android.R.layout.simple_spinner_item);
        adapternc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ncsp1.setAdapter(adapternc1);
        ncsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                nced1.setText("0");
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
