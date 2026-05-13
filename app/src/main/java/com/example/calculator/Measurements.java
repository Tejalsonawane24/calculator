package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class Measurements extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.measurments);
    }
    public void showHome(View view){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_in_right);
    }
    public void showCurrency(View view){
        Intent intent = new Intent(getApplicationContext(), CurrencyPage.class);
        startActivity(intent);
    }
    public void showLength(View view){
        Intent intent = new Intent(getApplicationContext(), LengthPage.class);
        startActivity(intent);
    }
    public void showArea(View view){
        Intent intent = new Intent(getApplicationContext(), AreaPage.class);
        startActivity(intent);
    }
    public void showVolume(View view){
        Intent intent = new Intent(getApplicationContext(), VolumePage.class);
        startActivity(intent);
    }
    public void showWeight(View view){
        Intent intent = new Intent(getApplicationContext(), WeightPage.class);
        startActivity(intent);
    }
    public void showTemprature(View view){
        Intent intent = new Intent(getApplicationContext(), TempraturePage.class);
        startActivity(intent);
    }
    public void showSpeed(View view){
        Intent intent = new Intent(getApplicationContext(), SpeedPage.class);
        startActivity(intent);
    }
    public void showPressure(View view){
        Intent intent = new Intent(getApplicationContext(), PressurePage.class);
        startActivity(intent);
    }
    public void showPower(View view){
        Intent intent = new Intent(getApplicationContext(), PowerPage.class);
        startActivity(intent);
    }
    public void showNumberSys(View view){
        Intent intent = new Intent(getApplicationContext(), NumberSystemPage.class);
        startActivity(intent);
    }
}
