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

public class LengthPage extends AppCompatActivity {

    private EditText lced1, lced2;
    private Spinner lcsp1, lcsp2;
    private String[] valuesLength;
    private boolean isUpdating = false; // Prevents recursive spinner loop events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.length_page);

        // Initialize view components
        lcsp1 = findViewById(R.id.spl1);
        lcsp2 = findViewById(R.id.splc2);
        lced1 = findViewById(R.id.edlc1);
        lced2 = findViewById(R.id.edlc2);

        // Block soft keyboard since the layout provides a custom keypad grid
        lced1.setShowSoftInputOnFocus(false);
        lced2.setShowSoftInputOnFocus(false);

        // Load the math multiplier array values
        valuesLength = getResources().getStringArray(R.array.length_value);

        // Setup Spinner resource adapters
        ArrayAdapter<CharSequence> adapterlc = ArrayAdapter.createFromResource(
                this, R.array.lcsp1, android.R.layout.simple_spinner_item);
        adapterlc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        lcsp1.setAdapter(adapterlc);
        lcsp2.setAdapter(adapterlc);

        // Explicitly set different default initial items to avoid conflict on load
        lcsp1.setSelection(5); // Meter (m)
        lcsp2.setSelection(4); // Centimeter (cm)

        // Spinner 1 select trigger logic
        lcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: offset opposite selection on item duplicate match
                if (position == lcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == lcsp2.getCount() - 1) ? 0 : position + 1;
                    lcsp2.setSelection(newPos);
                    Toast.makeText(LengthPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 select trigger logic
        lcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: offset opposite selection on item duplicate match
                if (position == lcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == lcsp1.getCount() - 1) ? 0 : position + 1;
                    lcsp1.setSelection(newPos);
                    Toast.makeText(LengthPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Setup custom button array clicks mapping
        setKeypadListeners();
    }

    // Mathematical unit processing execution core block
    private void performConversion() {
        String inputStr = lced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            lced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = lcsp1.getSelectedItemPosition();
            int pos2 = lcsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Fetch conversion ratio properties relative to a baseline standard unit (e.g., 1 Meter)
                double factor1 = Double.parseDouble(valuesLength[pos1]);
                double factor2 = Double.parseDouble(valuesLength[pos2]);

                // Mathematical conversion ratio equation
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    lced2.setText(String.valueOf((long) result));
                } else {
                    lced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            lced2.setText("Error");
        }
    }

    // Setup custom calculator grid map interaction listeners
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.lcbtn0, R.id.lcbtn1, R.id.lcbtn2, R.id.lcbtn3, R.id.lcbtn4,
                R.id.lcbtn5, R.id.lcbtn6, R.id.lcbtn7, R.id.lcbtn8, R.id.lcbtn9,
                R.id.lcbtn00, R.id.lcbtndot, R.id.lcbtnAC, R.id.lcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = lced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.lcbtnAC) {
                    lced1.setText("0");
                } else if (id == R.id.lcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    lced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.lcbtndot) {
                    if (!currentText.contains(".")) {
                        lced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    lced1.setText(currentText + b.getText().toString());
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