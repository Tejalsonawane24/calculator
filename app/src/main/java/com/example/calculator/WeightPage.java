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

public class WeightPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_page);
        Spinner wcsp1 = findViewById(R.id.spw1);
        Spinner wcsp2 = findViewById(R.id.spwc2);
        EditText wced1 = findViewById(R.id.edwc1);
        EditText wced2 = findViewById(R.id.edwc2);
        final String[] valuesWeight = getResources().getStringArray(R.array.weight_value);
        ArrayAdapter<CharSequence> adapterwc1 = ArrayAdapter.createFromResource(this, R.array.spwc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterwc2 = ArrayAdapter.createFromResource(this, R.array.spwc, android.R.layout.simple_spinner_item);
        adapterwc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wcsp1.setAdapter(adapterwc1);
        wcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesWeight[position];
                wced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                wced1.setText("0");
            }
        });
        adapterwc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wcsp2.setAdapter(adapterwc2);
        wcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesWeight[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = wcsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesWeight[pos]);
                        wced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(WeightPage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}

