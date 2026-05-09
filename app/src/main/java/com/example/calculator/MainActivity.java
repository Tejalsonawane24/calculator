package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ToggleButton;


public class MainActivity extends AppCompatActivity {

    //Defining objects
    Button btnp,btnd,btn7,btn8,btn9,btnx,btn4,btn5,btn6,btnm,btn1,btn2,btn3,btna,btnzz,btnz,btnf,btnmrop;
    ToggleButton mrbtn;
    EditText ed1,ed2;
    GridLayout gridLayout;

    private String[] expandKeys={"sin","cos","tan","rad","deg","log","In",
                                "(",")","inv","!","AC","%","Er","/", "^",
                                "7","8","9","X","Sq","4","5","6","-","Pi",
                                "1","2","3","+","e","00","0",".","="};
    private String[] originalKeys={"AC","%","Er","/","7","8","9","X","4",
                                    "5","6","-","1","2","3","+","00","0",
                                    ".","="};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //defining the edittext
        ed1=findViewById(R.id.text1);
        ed2= findViewById(R.id.text2);
        gridLayout=findViewById(R.id.l3);
        mrbtn=findViewById(R.id.more_button);
        btnmrop=findViewById(R.id.measures);
        if(ed2==null){
            throw new IllegalStateException("Views not found");
        }
        if(gridLayout==null){
            throw new IllegalStateException("GridLayout with id l3 found in layout");
        }
        collapseGrid();
        //defining the buttons
//        btnp=findViewById(R.id.percent);
//        btnd=findViewById(R.id.divide);
//        btn7=findViewById(R.id.seven);
//        btn8=findViewById(R.id.eight);
//        btn9=findViewById(R.id.nine);
//        btnx=findViewById(R.id.multiply);
//        btn4=findViewById(R.id.four);
//        btn5=findViewById(R.id.five);
//        btn6=findViewById(R.id.six);
//        btnm=findViewById(R.id.subtract);
//        btn1=findViewById(R.id.one);
//        btn2=findViewById(R.id.two);
//        btn3=findViewById(R.id.three);
//        btna=findViewById(R.id.plus);
//        btnzz=findViewById(R.id.twozero);
//        btnz=findViewById(R.id.zero);
//        btnf=findViewById(R.id.point);




        // hendling the button event
//        btnp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("%");
//            }
//        });
//        btnd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("/");
//            }
//        });
//        btn7.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("7");
//            }
//        });
//        btn8.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("8");
//            }
//        });
//        btn9.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("9");
//            }
//        });
//        btnx.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("x");
//            }
//        });
//        btn4.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("4");
//            }
//        });
//        btn5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("5");
//            }
//        });
//        btn6.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("6");
//            }
//        });
//        btnm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("-");
//            }
//        });
//        btn1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("1");
//            }
//        });
//        btn2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("2");
//            }
//        });
//        btn3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("3");
//            }
//        });
//        btna.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("+");
//            }
//        });
//        btnzz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("00");
//            }
//        });
//        btnz.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText("0");
//            }
//        });
//        btnf.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ed2.setText(".");
//            }
//        });

        mrbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
              if(isChecked){
                  expandGrid();
              }
              else {
                  collapseGrid();
              }
            }
            catch (Exception e){
                Log.e("CalculatorError","Error toggling grid:"+e.getMessage() );
            }
        });
    }
    public void showMeasurements(View view) {
        Intent intent = new Intent(getApplicationContext(), Measurements.class);
        startActivity(intent);
    }
    private Button createStyledButton(String text)
    {
        if (gridLayout==null){
            throw new IllegalStateException("gridLayout is null");
        }
        Button button=(Button) getLayoutInflater().
                inflate(R.layout.item_calc_button, gridLayout,
                        false);
        button.setText(text);
        GridLayout.LayoutParams params=new GridLayout.LayoutParams();
        params.width=0;
        params.height=0;
        params.columnSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);
        params.rowSpec=GridLayout.spec(GridLayout.UNDEFINED,1f);

        button.setLayoutParams(params);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalculatorButtonPressed (((Button) v).getText().toString());
            }
        });
        return button;
    }
    private void expandGrid(){
        //int originalRows=5;
       // int originalCols=4;
if(gridLayout==null||ed2==null)return;
        //2 extra rows 1 extra column
        int newRows=7;
        int newCols=5;

        gridLayout.setRowCount(newRows);
        gridLayout.setColumnCount(newCols);

        gridLayout.removeAllViews();

        //add all buttons for the expanded grid
        String[] keys=expandKeys;
        int totalButtons=keys.length;

        for(int i=0;i<totalButtons;i++){
            Button button= createStyledButton(keys[i]);
            button.setTextSize(13);
            gridLayout.addView(button);
        }
    }

    private void collapseGrid(){
        if(gridLayout==null||ed2==null)return;
        gridLayout.removeAllViews();
        int originalRows=5;
        int originalCols=4;
        gridLayout.setRowCount(originalRows);
        gridLayout.setColumnCount(originalCols);

        String[] keys=originalKeys;
        int totalButtons= keys.length;

        for(int i=0;i<totalButtons;i++){
            Button button=createStyledButton(keys[i]);
            button.setTextSize(24);
            gridLayout.addView(button);
        }
    }
    public void onCalculatorButtonPressed(String key){
        if(ed2!=null)
        {
            ed2.setText(key);
        }
    }
}