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

public class WeightPage extends AppCompatActivity {

    private EditText wced1, wced2;
    private Spinner wcsp1, wcsp2;
    private String[] valuesWeight;
    private boolean isUpdating = false; // Flag to prevent multi-trigger infinite recursion loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_page);

        // Bind layouts safely
        wcsp1 = findViewById(R.id.spw1);
        wcsp2 = findViewById(R.id.spwc2);
        wced1 = findViewById(R.id.edwc1);
        wced2 = findViewById(R.id.edwc2);

        // Turn off system keyboard popping up automatically when EditText gains focus
        wced1.setShowSoftInputOnFocus(false);
        wced2.setShowSoftInputOnFocus(false);

        // Extract raw decimal multiplier conversion points
        valuesWeight = getResources().getStringArray(R.array.weight_value);

        // Initialize array adapter elements
        ArrayAdapter<CharSequence> adapterwc = ArrayAdapter.createFromResource(
                this, R.array.spwc, android.R.layout.simple_spinner_item);
        adapterwc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        wcsp1.setAdapter(adapterwc);
        wcsp2.setAdapter(adapterwc);

        // Set non-overlapping index selections on initial layout setup view load
        wcsp1.setSelection(6); // Kilogram (kg)
        wcsp2.setSelection(0); // Gram (g)

        // Spinner 1 Selection Logic Engine Link
        wcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion: If items match, offset the other spinner over by 1 position
                if (position == wcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == wcsp2.getCount() - 1) ? 0 : position + 1;
                    wcsp2.setSelection(newPos);
                    Toast.makeText(WeightPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selection Logic Engine Link
        wcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion: If items match, offset the other spinner over by 1 position
                if (position == wcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == wcsp1.getCount() - 1) ? 0 : position + 1;
                    wcsp1.setSelection(newPos);
                    Toast.makeText(WeightPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Loop over custom UI grid layout keyboard indices
        setKeypadListeners();
    }

    // Weight Unit Conversion Multiplier Execution
    private void performConversion() {
        String inputStr = wced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            wced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = wcsp1.getSelectedItemPosition();
            int pos2 = wcsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Get scalar relationships proportioned against 1.0 Gram (g) base unit anchor point
                double factor1 = Double.parseDouble(valuesWeight[pos1]);
                double factor2 = Double.parseDouble(valuesWeight[pos2]);

                // Perform core linear mathematical conversion scale equation
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    wced2.setText(String.valueOf((long) result));
                } else {
                    wced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            wced2.setText("Error");
        }
    }

    // Custom Calculator Keypad Grid Button Listeners Interface Configuration
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.wcbtn0, R.id.wcbtn1, R.id.wcbtn2, R.id.wcbtn3, R.id.wcbtn4,
                R.id.wcbtn5, R.id.wcbtn6, R.id.wcbtn7, R.id.wcbtn8, R.id.wcbtn9,
                R.id.wcbtn00, R.id.wcbtndot, R.id.wcbtnAC, R.id.wcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = wced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.wcbtnAC) {
                    wced1.setText("0");
                } else if (id == R.id.wcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    wced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.wcbtndot) {
                    if (!currentText.contains(".")) {
                        wced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    wced1.setText(currentText + b.getText().toString());
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