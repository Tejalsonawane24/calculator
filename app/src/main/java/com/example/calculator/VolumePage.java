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

public class VolumePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_page);
        Spinner vcsp1 = findViewById(R.id.spv1);
        Spinner vcsp2 = findViewById(R.id.spvc2);
        EditText vced1 = findViewById(R.id.edvc1);
        EditText vced2 = findViewById(R.id.edvc2);
        final String[] valuesVolume = getResources().getStringArray(R.array.volume_value);
        ArrayAdapter<CharSequence> adaptervc1 = ArrayAdapter.createFromResource(this, R.array.spvc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adaptervc2 = ArrayAdapter.createFromResource(this, R.array.spvc, android.R.layout.simple_spinner_item);
        adaptervc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vcsp1.setAdapter(adaptervc1);
        vcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesVolume[position];
                vced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vced1.setText("0");
            }
        });
        adaptervc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vcsp2.setAdapter(adaptervc2);
        vcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesVolume[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = vcsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesVolume[pos]);
                        vced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(
                        VolumePage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
