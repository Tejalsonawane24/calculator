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

public class VolumePage extends AppCompatActivity {

    private EditText vced1, vced2;
    private Spinner vcsp1, vcsp2;
    private String[] valuesVolume;
    private boolean isUpdating = false; // Prevents recursive listener loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_page);

        // Bind Views
        vcsp1 = findViewById(R.id.spv1);
        vcsp2 = findViewById(R.id.spvc2);
        vced1 = findViewById(R.id.edvc1);
        vced2 = findViewById(R.id.edvc2);

        // Hide soft input keyboard to let your custom XML layout keypad exclusively manage inputs
        vced1.setShowSoftInputOnFocus(false);
        vced2.setShowSoftInputOnFocus(false);

        // Load conversion multiplier scale values
        valuesVolume = getResources().getStringArray(R.array.volume_value);

        // Set up Spinner Adapters
        ArrayAdapter<CharSequence> adaptervc = ArrayAdapter.createFromResource(
                this, R.array.spvc, android.R.layout.simple_spinner_item);
        adaptervc.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        vcsp1.setAdapter(adaptervc);
        vcsp2.setAdapter(adaptervc);

        // Pre-select distinct positions so they do not conflict on load
        vcsp1.setSelection(6); // Litre(l)
        vcsp2.setSelection(7); // Millilitre(ml)

        // Spinner 1 select callback
        vcsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes secondary spinner position if identical item selected
                if (position == vcsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == vcsp2.getCount() - 1) ? 0 : position + 1;
                    vcsp2.setSelection(newPos);
                    Toast.makeText(VolumePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Spinner 2 select callback
        vcsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // Mutual exclusion check: pushes primary spinner position if identical item selected
                if (position == vcsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == vcsp1.getCount() - 1) ? 0 : position + 1;
                    vcsp1.setSelection(newPos);
                    Toast.makeText(VolumePage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Wire up custom calculator keypad listeners
        setKeypadListeners();
    }

    // Universal Ratio Scaling Volume Conversion Logic
    private void performConversion() {
        String inputStr = vced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            vced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = vcsp1.getSelectedItemPosition();
            int pos2 = vcsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                // Fetch conversion ratio factors calibrated to 1.0 Litre baseline
                double factor1 = Double.parseDouble(valuesVolume[pos1]);
                double factor2 = Double.parseDouble(valuesVolume[pos2]);

                // Standard conversion arithmetic formula execution
                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    vced2.setText(String.valueOf((long) result));
                } else {
                    vced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            vced2.setText("Error");
        }
    }

    // Keypad grid mapping click configurations
    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.vcbtn0, R.id.vcbtn1, R.id.vcbtn2, R.id.vcbtn3, R.id.vcbtn4,
                R.id.vcbtn5, R.id.vcbtn6, R.id.vcbtn7, R.id.vcbtn8, R.id.vcbtn9,
                R.id.vcbtn00, R.id.vcbtndot, R.id.vcbtnAC, R.id.vcbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = vced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.vcbtnAC) {
                    vced1.setText("0");
                } else if (id == R.id.vcbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    vced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.vcbtndot) {
                    if (!currentText.contains(".")) {
                        vced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    vced1.setText(currentText + b.getText().toString());
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