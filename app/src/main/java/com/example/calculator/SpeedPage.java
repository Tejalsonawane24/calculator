package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SpeedPage extends AppCompatActivity {

    private EditText sced1, sced2;
    private Spinner scsp1, scsp2;
    private String[] valuesSpeed;
    private boolean isUpdating = false; // Prevents recursive selection listener loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.speed_page);

        // Bind Views
        scsp1 = findViewById(R.id.sps1);
        scsp2 = findViewById(R.id.spsc2);
        sced1 = findViewById(R.id.edsc1);
        sced2 = findViewById(R.id.edsc2);

        // Disable standard system soft-keyboard popups to rely entirely on your XML keypad layout
        sced1.setShowSoftInputOnFocus(false);
        sced2.setShowSoftInputOnFocus(false);

        // Load the array conversion scale factor numbers from resources
        valuesSpeed = getResources().getStringArray(R.array.speed_value);

        // Set up Adapters
        ArrayAdapter<CharSequence> adaptersc = ArrayAdapter.createFromResource(
                this, R.array.spsc, android.R.layout.simple_spinner_item);
        adaptersc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        scsp1.setAdapter(adaptersc);
        scsp2.setAdapter(adaptersc);

        // Select distinct starting indices so they don't match on creation
        scsp1.setSelection(5); // Meter/second(m/s)
        scsp2.setSelection(6); // Kilometer/hour(km/h)

        // Spinner 1 Selection Callback Configuration
        scsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes the opposite spinner over if matching index occurs
                if (position == scsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == scsp2.getCount() - 1) ? 0 : position + 1;
                    scsp2.setSelection(newPos);
                    Toast.makeText(SpeedPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selection Callback Configuration
        scsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes the opposite spinner over if matching index occurs
                if (position == scsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == scsp1.getCount() - 1) ? 0 : position + 1;
                    scsp1.setSelection(newPos);
                    Toast.makeText(SpeedPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Bind layout calculator button click handlers
        setKeypadListeners();
    }

    // Speed Scale Conversion Arithmetic Method
    private void performConversion() {
        String inputStr = sced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            sced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = scsp1.getSelectedItemPosition();
            int pos2 = scsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Fetch scaling reference points mapped uniformly against 1 Meter per Second (m/s)
                double factor1 = Double.parseDouble(valuesSpeed[pos1]);
                double factor2 = Double.parseDouble(valuesSpeed[pos2]);

                // Direct conversion calculations
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    sced2.setText(String.valueOf((long) result));
                } else {
                    sced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            sced2.setText("Error");
        }
    }

    // Connect array grids to input window text adjustments
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.scbtn0, R.id.scbtn1, R.id.scbtn2, R.id.scbtn3, R.id.scbtn4,
                R.id.scbtn5, R.id.scbtn6, R.id.scbtn7, R.id.scbtn8, R.id.scbtn9,
                R.id.scbtn00, R.id.scbtndot, R.id.scbtnAC, R.id.scbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = sced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.scbtnAC) {
                    sced1.setText("0");
                } else if (id == R.id.scbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    sced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.scbtndot) {
                    if (!currentText.contains(".")) {
                        sced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    sced1.setText(currentText + b.getText().toString());
                }

                performConversion();
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    public void showMeasurements(View view) {
        Intent i = new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}