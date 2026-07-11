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

public class AreaPage extends AppCompatActivity {

    private EditText aced1, aced2;
    private Spinner acsp1, acsp2;
    private String[] valuesArea;
    private boolean isUpdating = false; // Prevents infinite loop triggers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area_page);

        acsp1 = findViewById(R.id.spa1);
        acsp2 = findViewById(R.id.spac2);
        aced1 = findViewById(R.id.edac1);
        aced2 = findViewById(R.id.edac2);

        aced1.setShowSoftInputOnFocus(false);
        aced2.setShowSoftInputOnFocus(false);

        valuesArea = getResources().getStringArray(R.array.Area_value);

        ArrayAdapter<CharSequence> adapterac = ArrayAdapter.createFromResource(
                this, R.array.spac, android.R.layout.simple_spinner_item);
        adapterac.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        acsp1.setAdapter(adapterac);
        acsp2.setAdapter(adapterac);

        // Set default selection to different positions so they don't clash at startup
        acsp1.setSelection(0); // Square meter
        acsp2.setSelection(1); // Square decimeter

        // Recalculate when unit 1 changes
        acsp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // If same item is selected, shift the second spinner down
                if (position == acsp2.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == acsp2.getCount() - 1) ? 0 : position + 1;
                    acsp2.setSelection(newPos);
                    Toast.makeText(AreaPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Recalculate when unit 2 changes
        acsp2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (isUpdating) return;

                // If same item is selected, shift the first spinner down
                if (position == acsp1.getSelectedItemPosition()) {
                    isUpdating = true;
                    int newPos = (position == acsp1.getCount() - 1) ? 0 : position + 1;
                    acsp1.setSelection(newPos);
                    Toast.makeText(AreaPage.this, "Units cannot be identical", Toast.LENGTH_SHORT).show();
                    isUpdating = false;
                }
                performConversion();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        setKeypadListeners();
    }

    private void performConversion() {
        String inputStr = aced1.getText().toString().trim();
        if (inputStr.isEmpty() || inputStr.equals(".")) {
            aced2.setText("0");
            return;
        }

        try {
            double inputValue = Double.parseDouble(inputStr);
            int pos1 = acsp1.getSelectedItemPosition();
            int pos2 = acsp2.getSelectedItemPosition();

            if (pos1 != AdapterView.INVALID_POSITION && pos2 != AdapterView.INVALID_POSITION) {
                double factor1 = Double.parseDouble(valuesArea[pos1]);
                double factor2 = Double.parseDouble(valuesArea[pos2]);

                double result = inputValue * (factor1 / factor2);

                if (result == (long) result) {
                    aced2.setText(String.valueOf((long) result));
                } else {
                    aced2.setText(String.valueOf(result));
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            aced2.setText("Error");
        }
    }

    private void setKeypadListeners() {
        int[] buttonIds = {
                R.id.acbtn0, R.id.acbtn1, R.id.acbtn2, R.id.acbtn3, R.id.acbtn4,
                R.id.acbtn5, R.id.acbtn6, R.id.acbtn7, R.id.acbtn8, R.id.acbtn9,
                R.id.acbtn00, R.id.acbtndot, R.id.acbtnAC, R.id.acbtndelete
        };

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = aced1.getText().toString();
                if (currentText.equals("100") || currentText.equals("0")) {
                    currentText = "";
                }

                int id = v.getId();
                if (id == R.id.acbtnAC) {
                    aced1.setText("0");
                } else if (id == R.id.acbtndelete) {
                    if (currentText.length() > 0) {
                        currentText = currentText.substring(0, currentText.length() - 1);
                    }
                    aced1.setText(currentText.isEmpty() ? "0" : currentText);
                } else if (id == R.id.acbtndot) {
                    if (!currentText.contains(".")) {
                        aced1.setText(currentText.isEmpty() ? "0." : currentText + ".");
                    }
                } else {
                    Button b = (Button) v;
                    aced1.setText(currentText + b.getText().toString());
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