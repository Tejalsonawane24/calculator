package com.example.calculator;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    //Defining objects
    Button btnp,btnd;
    EditText ed1,ed2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //defining the edittext
        ed1=findViewById(R.id.text1);
        ed2= findViewById(R.id.text2);

        //defining the buttons
        btnp=findViewById(R.id.percent);
        btnd=findViewById(R.id.divide);

    }
}