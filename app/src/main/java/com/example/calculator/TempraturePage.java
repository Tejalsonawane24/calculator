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

public class TempraturePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temprature_page);
        Spinner tcsp1 = findViewById(R.id.spt1);
        Spinner tcsp2 = findViewById(R.id.sptc2);
        EditText tced1 = findViewById(R.id.edtc1);
        EditText tced2 = findViewById(R.id.edtc2);
        final String[] valuesWeight = getResources().getStringArray(R.array.weight_value);
        ArrayAdapter<CharSequence> adapterwc1 = ArrayAdapter.createFromResource(this, R.array.spwc, android.R.layout.simple_spinner_item);
        adapterwc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tcsp1.setAdapter(adapterwc1);
        tcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesWeight[position];
                tced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tced1.setText("0");
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
    }

