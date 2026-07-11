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

public class TempraturePage extends AppCompatActivity {

    private EditText tced1, tced2;
    private Spinner tcsp1, tcsp2;
    private boolean isUpdating = false; // Prevents recursive selection listener loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temprature_page);

        // Bind Views
        tcsp1 = findViewById(R.id.spt1);
        tcsp2 = findViewById(R.id.sptc2);
        tced1 = findViewById(R.id.edtc1);
        tced2 = findViewById(R.id.edtc2);

        // Disable standard system soft-keyboard popups to rely entirely on your XML keypad layout
        tced1.setShowSoftInputOnFocus(false);
        tced2.setShowSoftInputOnFocus(false);

        // Set up Adapters (using string array resource 'sptc' for temperature parameters)
        ArrayAdapter<CharSequence> adaptertc = ArrayAdapter.createFromResource(
                this, R.array.sptc, android.R.layout.simple_spinner_item);
        adaptertc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        tcsp1.setAdapter(adaptertc);
        tcsp2.setAdapter(adaptertc);

        // Select distinct starting indices so they don't match on creation
        tcsp1.setSelection(1); // Degree Celsius(C)
        tcsp2.setSelection(3); // Degree Fahrenheit(F)

        // Spinner 1 Selection Callback Configuration
        tcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes opposite spinner over if matching index occurs
                if (position == tcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == tcsp2.getCount() - 1) ? 0 : position + 1;
                    tcsp2.setSelection(newPos);
                    Toast.makeText(TempraturePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selection Callback Configuration
        tcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes opposite spinner over if matching index occurs
                if (position == tcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == tcsp1.getCount() - 1) ? 0 : position + 1;
                    tcsp1.setSelection(newPos);
                    Toast.makeText(TempraturePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
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

    // Temperature Scale Conversion Engine
    private void performConversion() {
        String inputStr = tced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            tced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int fromUnit = tcsp1.getSelectedItemPosition();
            int toUnit = tcsp2.getSelectedItemPosition();

            // First step: Convert everything to a universal base unit (Celsius)
            double celsius = 0.0;
            switch (fromUnit) {
                case 0: // Rankine
                    celsius = (inputValue - 491.67) * 5.0 / 9.0;
                    break;
                case 1: // Celsius
                    celsius = inputValue;
                    break;
                case 2: // Reaumur
                    celsius = inputValue * 1.25;
                    break;
                case 3: // Fahrenheit
                    celsius = (inputValue - 32.0) * 5.0 / 9.0;
                    break;
                case 4: // Kelvin
                    celsius = inputValue - 273.15;
                    break;
            }

            // Second step: Convert from the Celsius base to the target unit
            double result = 0.0;
            switch (toUnit) {
                case 0: // Rankine
                    result = (celsius * 9.0 / 5.0) + 491.67;
                    break;
                case 1: // Celsius
                    result = celsius;
                    break;
                case 2: // Reaumur
                    result = celsius * 0.8;
                    break;
                case 3: // Fahrenheit
                    result = (celsius * 9.0 / 5.0) + 32.0;
                    break;
                case 4: // Kelvin
                    result = celsius + 273.15;
                    break;
            }

            // Formatting output layout values cleanly
            if (result == (long) result) {
                tced2.setText(String.valueOf((long) result));
            } else {
                // Rounding up to 4 decimal spots cleanly to handle long double remainders
                result = Math.round(result * 10000.0) / 10000.0;
                tced2.setText(String.valueOf(result));
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            tced2.setText("Error");
        }
    }

    // Connect array grids to input window text adjustments
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.tcbtn0, R.id.tcbtn1, R.id.tcbtn2, R.id.tcbtn3, R.id.tcbtn4,
                R.id.tcbtn5, R.id.tcbtn6, R.id.tcbtn7, R.id.tcbtn8, R.id.tcbtn9,
                R.id.tcbtn00, R.id.tcbtndot, R.id.tcbtnAC, R.id.tcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = tced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.tcbtnAC) {
                    tced1.setText("0");
                } else if (id == R.id.tcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    tced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.tcbtndot) {
                    if (!currentText.contains(".")) {
                        tced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    tced1.setText(currentText + b.getText().toString());
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