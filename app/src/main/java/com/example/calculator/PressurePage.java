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

public class PressurePage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pressure_page);
        Spinner prcsp1 = findViewById(R.id.sppr1);
        Spinner prcsp2 = findViewById(R.id.spprc2);
        EditText prced1 = findViewById(R.id.edprc1);
        EditText prced2 = findViewById(R.id.edprc2);
        final String[] valuesPressure = getResources().getStringArray(R.array.pressure_value);
        ArrayAdapter<CharSequence> adapterprc1 = ArrayAdapter.createFromResource(this, R.array.spprc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterprc2 = ArrayAdapter.createFromResource(this, R.array.spprc, android.R.layout.simple_spinner_item);
        adapterprc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prcsp1.setAdapter(adapterprc1);
        prcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesPressure[position];
                prced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                prced1.setText("0");
            }
        });
        adapterprc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prcsp2.setAdapter(adapterprc2);
        prcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesPressure[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = prcsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesPressure[pos]);
                        prced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PressurePage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
