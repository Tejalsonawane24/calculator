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

public class PressurePage extends AppCompatActivity {

    private EditText prced1, prced2;
    private Spinner prcsp1, prcsp2;
    private String[] valuesPressure;
    private boolean isUpdating = false; // Prevents recursive selection listener loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pressure_page);

        // Bind Views
        prcsp1 = findViewById(R.id.sppr1);
        prcsp2 = findViewById(R.id.spprc2);
        prced1 = findViewById(R.id.edprc1);
        prced2 = findViewById(R.id.edprc2);

        // Disable soft input layout popping up so your custom keypad handles text input entry
        prced1.setShowSoftInputOnFocus(false);
        prced2.setShowSoftInputOnFocus(false);

        // Load conversion scaling factors from resources
        valuesPressure = getResources().getStringArray(R.array.pressure_value);

        // Set up Adapters
        ArrayAdapter<CharSequence> adapterprc = ArrayAdapter.createFromResource(
                this, R.array.spprc, android.R.layout.simple_spinner_item);
        adapterprc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        prcsp1.setAdapter(adapterprc);
        prcsp2.setAdapter(adapterprc);

        // Default initial layouts to distinct positions to avoid overlap conflicts on creation
        prcsp1.setSelection(5); // Bar
        prcsp2.setSelection(10); // Kilopascal(kPa)

        // Spinner 1 Selector Listener
        prcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: forces adjacent selection shift if items match
                if (position == prcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == prcsp2.getCount() - 1) ? 0 : position + 1;
                    prcsp2.setSelection(newPos);
                    Toast.makeText(PressurePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selector Listener
        prcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: forces adjacent selection shift if items match
                if (position == prcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == prcsp1.getCount() - 1) ? 0 : position + 1;
                    prcsp1.setSelection(newPos);
                    Toast.makeText(PressurePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Bind keypad array items
        setKeypadListeners();
    }

    // Pressure Scale Factor Calculation Logic Engine
    private void performConversion() {
        String inputStr = prced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            prced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = prcsp1.getSelectedItemPosition();
            int pos2 = prcsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Fetch pressure scaling factors calculated relative to a single baseline reference anchor (e.g., 1 Pascal)
                double factor1 = Double.parseDouble(valuesPressure[pos1]);
                double factor2 = Double.parseDouble(valuesPressure[pos2]);

                // Mathematical conversion calculation ratio equation
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    prced2.setText(String.valueOf((long) result));
                } else {
                    prced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            prced2.setText("Error");
        }
    }

    // Set custom click interfaces to handle screen button edits
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.prcbtn0, R.id.prcbtn1, R.id.prcbtn2, R.id.prcbtn3, R.id.prcbtn4,
                R.id.prcbtn5, R.id.prcbtn6, R.id.prcbtn7, R.id.prcbtn8, R.id.prcbtn9,
                R.id.prcbtn00, R.id.prcbtndot, R.id.prcbtnAC, R.id.prcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = prced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.prcbtnAC) {
                    prced1.setText("0");
                } else if (id == R.id.prcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    prced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.prcbtndot) {
                    if (!currentText.contains(".")) {
                        prced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    prced1.setText(currentText + b.getText().toString());
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