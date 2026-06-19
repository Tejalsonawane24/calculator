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

public class PowerPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_page);
        Spinner pcsp1 = findViewById(R.id.spp1);
        Spinner pcsp2 = findViewById(R.id.sppc2);
        EditText pced1 = findViewById(R.id.edpc1);
        EditText pced2 = findViewById(R.id.edpc2);
        final String[] valuesPower = getResources().getStringArray(R.array.power_value);
        ArrayAdapter<CharSequence> adapterpc1 = ArrayAdapter.createFromResource(this, R.array.sppc, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterpc2 = ArrayAdapter.createFromResource(this, R.array.sppc, android.R.layout.simple_spinner_item);
        adapterpc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pcsp1.setAdapter(adapterpc1);
        pcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesPower[position];
                pced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                pced1.setText("0");
            }
        });
        adapterpc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pcsp2.setAdapter(adapterpc2);
        pcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesPower[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = pcsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesPower[pos]);
                        pced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(PowerPage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
