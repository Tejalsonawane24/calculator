package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class CurrencyPage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.currency_page);
        Spinner spinner1=findViewById(R.id.sp1);
        Spinner spinner2=findViewById(R.id.sp2);
        EditText edittext1=findViewById(R.id.edcc1);
        EditText editText2=findViewById(R.id.edcc2);
        final String[] valuesArray= getResources().getStringArray(R.array.currency_value);
        ArrayAdapter<CharSequence> adapter1=ArrayAdapter.createFromResource(this, R.array.ccsp1, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> adapter2=ArrayAdapter.createFromResource(this,R.array.ccsp1, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter1);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selectedItem1 = valuesArray[position];
                edittext1.setText(selectedItem1);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                edittext1.setText("0");
            }
        });
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id){
                String selectedItem2 = valuesArray[position];
                try {
                    // Use double to handle decimal values from strings.xml
                    double ch1 = Double.parseDouble(selectedItem2);
                    int pos = spinner1.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        double ch2 = Double.parseDouble(valuesArray[pos]);
                        editText2.setText(String.valueOf(ch1 * ch2));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent){
                Toast.makeText(CurrencyPage.this,"Selected:Not Yet",Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method signature must include View view to be compatible with android:onClick
    public void swap(View view){
        Spinner sp1 = findViewById(R.id.sp1);
        Spinner sp2 = findViewById(R.id.sp2);
        EditText ed1 = findViewById(R.id.edcc1);
        EditText ed2 = findViewById(R.id.edcc2);

        int se1 = sp1.getSelectedItemPosition();
        int se2 = sp2.getSelectedItemPosition();

        sp1.setSelection(se2);
        sp2.setSelection(se1);

        String str1 = ed1.getText().toString();
        String str2 = ed2.getText().toString();

        ed1.setText(str2);
        ed2.setText(str1);
    }

    public void showMeasurements(View view) {
        Intent i=new Intent(getApplicationContext(), Measurements.class);
        startActivity(i);
    }

}
