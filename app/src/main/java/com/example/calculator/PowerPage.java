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

public class PowerPage extends AppCompatActivity {

    private EditText pced1, pced2;
    private Spinner pcsp1, pcsp2;
    private String[] valuesPower;
    private boolean isUpdating = false; // Flags and prevents recursive loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.power_page);

        // UI Binding
        pcsp1 = findViewById(R.id.spp1);
        pcsp2 = findViewById(R.id.sppc2);
        pced1 = findViewById(R.id.edpc1);
        pced2 = findViewById(R.id.edpc2);

        // Hide virtual soft keyboard to use your custom XML layout grid keypad exclusively
        pced1.setShowSoftInputOnFocus(false);
        pced2.setShowSoftInputOnFocus(false);

        // Load the string conversion multiplier reference values
        valuesPower = getResources().getStringArray(R.array.power_value);

        // Set up Spinner Adapters
        ArrayAdapter<CharSequence> adapterpc = ArrayAdapter.createFromResource(
                this, R.array.sppc, android.R.layout.simple_spinner_item);
        adapterpc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        pcsp1.setAdapter(adapterpc);
        pcsp2.setAdapter(adapterpc);

        // Initialize spinners at different positions so they don't clash on screen launch
        pcsp1.setSelection(5); // Watt(W)
        pcsp2.setSelection(9); // Kilowatt(kW)

        // Spinner 1 select callback configuration
        pcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion validation check: shifts selection down if matching index occurs
                if (position == pcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == pcsp2.getCount() - 1) ? 0 : position + 1;
                    pcsp2.setSelection(newPos);
                    Toast.makeText(PowerPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 select callback configuration
        pcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion validation check: shifts selection down if matching index occurs
                if (position == pcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == pcsp1.getCount() - 1) ? 0 : position + 1;
                    pcsp1.setSelection(newPos);
                    Toast.makeText(PowerPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Bind layout calculator button arrays to system actions
        setKeypadListeners();
    }

    // Power Scale Conversion Calculation Mechanism
    private void performConversion() {
        String inputStr = pced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            pced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = pcsp1.getSelectedItemPosition();
            int pos2 = pcsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Fetch power unit values evaluated relative to a single baseline reference anchor (e.g., 1 Watt)
                double factor1 = Double.parseDouble(valuesPower[pos1]);
                double factor2 = Double.parseDouble(valuesPower[pos2]);

                // Core execution math calculation
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    pced2.setText(String.valueOf((long) result));
                } else {
                    pced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            pced2.setText("Error");
        }
    }

    // Set custom button listeners to change values inside input view text targets
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.pcbtn0, R.id.pcbtn1, R.id.pcbtn2, R.id.pcbtn3, R.id.pcbtn4,
                R.id.pcbtn5, R.id.pcbtn6, R.id.pcbtn7, R.id.pcbtn8, R.id.pcbtn9,
                R.id.pcbtn00, R.id.pcbtndot, R.id.pcbtnAC, R.id.pcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = pced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.pcbtnAC) {
                    pced1.setText("0");
                } else if (id == R.id.pcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    pced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.pcbtndot) {
                    if (!currentText.contains(".")) {
                        pced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    pced1.setText(currentText + b.getText().toString());
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