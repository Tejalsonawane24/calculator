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

public class NumberSystemPage extends AppCompatActivity {

    private EditText nced1, nced2;
    private Spinner ncsp1, ncsp2;
    private boolean isUpdating = false; // Prevents infinite mutual spinner trigger loops

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.number_system_page);

        // Initialize Views
        ncsp1 = findViewById(R.id.spn1);
        ncsp2 = findViewById(R.id.spnc2);
        nced1 = findViewById(R.id.ednc1);
        nced2 = findViewById(R.id.ednc2);

        // Block soft keyboard inputs since we use a custom keypad grid layout
        nced1.setShowSoftInputOnFocus(false);
        nced2.setShowSoftInputOnFocus(false);

        // Setup Spinner Resource Adapters
        ArrayAdapter<CharSequence> adapternc = ArrayAdapter.createFromResource(
                this, R.array.spnc, android.R.layout.simple_spinner_item);
        adapternc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ncsp1.setAdapter(adapternc);
        ncsp2.setAdapter(adapternc);

        // Set different initial default selection indices so they don't clash on launch
        ncsp1.setSelection(2); // Decimal (DEC)
        ncsp2.setSelection(0); // Binary (BIN)

        // Spinner 1 Selection Listener
        ncsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion validation check
                if (position == ncsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == ncsp2.getCount() - 1) ? 0 : position + 1;
                    ncsp2.setSelection(newPos);
                    Toast.makeText(NumberSystemPage.this, "Bases cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 Selection Listener
        ncsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion validation check
                if (position == ncsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == ncsp1.getCount() - 1) ? 0 : position + 1;
                    ncsp1.setSelection(newPos);
                    Toast.makeText(NumberSystemPage.this, "Bases cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup custom calculator keypad grid clicks
        setKeypadListeners();
    }

    // Number System Radix Conversion Core Logic
    private void performConversion() {
        String inputStr = nced1.getText().toString().trim();
        if (inputStr.isEmpty()) {
            nced2.setText("0");
            return;
        }

        int pos1 = ncsp1.getSelectedItemPosition();
        int pos2 = ncsp2.getSelectedItemPosition();

        if (pos1 == AdapterView.INVALID_POSITION || pos2 == AdapterView.INVALID_POSITION) {
            return;
        }

        // Map positions to base values: 0 -> Binary(2), 1 -> Octal(8), 2 -> Decimal(10), 3 -> Hexadecimal(16)
        int baseFrom = getRadixFromPosition(pos1);
        int baseTo = getRadixFromPosition(pos2);

        try {
            // Step 1: Parse the string safely into a base-10 Long integer value
            long decimalValue = Long.parseLong(inputStr, baseFrom);

            // Step 2: Format the base-10 decimal value out to the targeted base format string
            String outputResult = Long.toString(decimalValue, baseTo);

            // Normalize Hex representation text strings to display as neat Uppercase text
            if (baseTo == 16) {
                outputResult = outputResult.toUpperCase();
            }

            nced2.setText(outputResult);
        } catch (NumberFormatException e) {
            // Fired if characters out-of-bounds for selected base are entered (e.g. '9' in Binary)
            nced2.setText("Invalid Input");
        }
    }

    // Maps positional spinner indexing options directly to math base radices
    private int getRadixFromPosition(int position) {
        switch (position) {
            case 0: return 2;  // Binary
            case 1: return 8;  // Octal
            case 2: return 10; // Decimal
            case 3: return 16; // Hexadecimal
            default: return 10;
        }
    }

    // Bind layout button IDs to calculation engine hooks
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.ncbtn0, R.id.ncbtn1, R.id.ncbtn2, R.id.ncbtn3, R.id.ncbtn4,
                R.id.ncbtn5, R.id.ncbtn6, R.id.ncbtn7, R.id.ncbtn8, R.id.ncbtn9,
                R.id.ncbtn00, R.id.ncbtndot, R.id.ncbtnAC, R.id.ncbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = nced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.ncbtnAC) {
                    nced1.setText("0");
                } else if (id == R.id.ncbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    nced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.ncbtndot) {
                    // Note: Standard number system conversion operations do not typically evaluate fractions
                    if (!currentText.contains(".")) {
                        nced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    nced1.setText(currentText + b.getText().toString());
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