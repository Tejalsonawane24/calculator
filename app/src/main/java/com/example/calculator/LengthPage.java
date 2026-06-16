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

public class LengthPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.length_page);
        Spinner lcsp1 = findViewById(R.id.spl1);
        Spinner lcsp2 = findViewById(R.id.splc2);
        EditText lced1 = findViewById(R.id.edlc1);
        EditText lced2 = findViewById(R.id.edlc2);
        final String[] valuesLength = getResources().getStringArray(R.array.length_value);
        ArrayAdapter<CharSequence> adapterlc1 = ArrayAdapter.createFromResource(this, R.array.lcsp1, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapterlc2 = ArrayAdapter.createFromResource(this, R.array.lcsp1, android.R.layout.simple_spinner_item);
        adapterlc1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lcsp1.setAdapter(adapterlc1);
        lcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem1 = valuesLength[position];
                lced1.setText(selectedItem1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                lced1.setText("0");
            }
        });
        adapterlc2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lcsp2.setAdapter(adapterlc2);
        lcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String selectedItem2 = valuesLength[position];
                try {
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = lcsp1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesLength[pos]);
                        lced2.setText(String.valueOf(ch1 / ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(LengthPage.this, "Selected: Not Yet", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void showMeasurements(View view){
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}
