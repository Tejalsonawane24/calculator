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

public class AreaPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_page);
        Spinner acsp1 = findViewById(R.id.spa1);
        Spinner acsp2 = findViewById(R.id.spac2);
        EditText aced1 = findViewById(R.id.edac1);
        EditText aced2 = findViewById(R.id.edac2);
        final String[] valuesArea = getResources().getStringArray(R.array.Area_value);
        ArrayAdapter<CharSequence> adapterac1 = ArrayAdapter.createFromResource(this, R.array.spac, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterac2 = ArrayAdapter.createFromResource(this, R.array.spac, android.R.layout.simple_spinner_item);
        adapterac1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acsp1.setAdapter(adapterac1);
        acsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesArea[position];
                aced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                aced1.setText("0");
            }
        });
        adapterac2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        acsp2.setAdapter(adapterac2);
        acsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesArea[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = acsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesArea[pos]);
                        aced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AreaPage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
