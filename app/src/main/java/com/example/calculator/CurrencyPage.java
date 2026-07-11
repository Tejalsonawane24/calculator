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

public class CurrencyPage extends AppCompatActivity {

    private EditText edcc1, edcc2;
    private Spinner sp1, sp2;
    private String[] valuesArray;
    private boolean isUpdating = false; // Prevents infinite listeners trigger loop

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_page);

        // Initialize views
        sp1 = findViewById(R.id.sp1);
        sp2 = findViewById(R.id.sp2);
        edcc1 = findViewById(R.id.edcc1);
        edcc2 = findViewById(R.id.edcc2);

        // Block soft keyboard since we have a custom grid layout keypad
        edcc1.setShowSoftInputOnFocus(false);
        edcc2.setShowSoftInputOnFocus(false);

        // Load the math exchange factors relative to a base currency
        valuesArray = getResources().getStringArray(R.array.currency_value);

        // Setup Spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.ccsp1, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sp1.setAdapter(adapter);
        sp2.setAdapter(adapter);

        // Set different initial items so they don't clash at startup
        sp1.setSelection(67); // US Dollar (USD) position reference
        sp2.setSelection(21); // Indian Rupee (INR) position reference

        // Spinner 1 Selection Listener
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                if (position == sp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == sp2.getCount() - 1) ? 0 : position + 1;
                    sp2.setSelection(newPos);
                    Toast.makeText(CurrencyPage.this, "Currencies cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selection Listener
        sp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                if (position == sp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == sp1.getCount() - 1) ? 0 : position + 1;
                    sp1.setSelection(newPos);
                    Toast.makeText(CurrencyPage.this, "Currencies cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup keypad layout mapping
        setKeypadListeners();
    }

    // Mathematical conversion calculation logic
    private void performConversion() {
        String inputStr = edcc1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            edcc2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = sp1.getSelectedItemPosition();
            int pos2 = sp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // If factors are defined as: 1 Base Unit = X Currency Units
                double factor1 = Double.parseDouble(valuesArray[pos1]);
                double factor2 = Double.parseDouble(valuesArray[pos2]);

                // Exchange formula calculation
                double result = inputValue * (factor2 / factor1);

                if (result == (long) result) {
                    edcc2.setText(String.valueOf((long) result));
                } else {
                    // Limits long decimals for clean view layout matching your style
                    edcc2.setText(String.format("%.4f", result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            edcc2.setText("Error");
        }
    }

    // Set up click listeners for the custom layout layout keyboard items
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9,
                R.id.btn00, R.id.btnDot, R.id.btnAC, R.id.btnDelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = edcc1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.btnAC) {
                    edcc1.setText("0");
                } else if (id == R.id.btnDelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    edcc1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.btnDot) {
                    if (!currentText.contains(".")) {
                        edcc1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    edcc1.setText(currentText + b.getText().toString());
                }

                performConversion();
            }
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(listener);
        }
    }

    // Layout XML Action Click handler for swapping rows
    public void swap(View view) {
        int se1 = sp1.getSelectedItemPosition();
        int se2 = sp2.getSelectedItemPosition();

        // Use flag blocker so swap routine doesn't throw target constraints violation
        isUpdating = true;
        sp1.setSelection(se2);
        sp2.setSelection(se1);
        isUpdating = false;

        // Take whatever text is currently calculated in 2 and make it the input for 1
        String calculatedOutput = edcc2.getText().toString();
        if (calculatedOutput.equals("Error") || calculatedOutput.isEmpty()) {
            calculatedOutput = "0";
        }

        edcc1.setText(calculatedOutput);
        performConversion();
    }

    public void showMeasurements(View view) {
        Intent i = new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }
}