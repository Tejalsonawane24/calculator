package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Imports
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText ed1, ed2;
    private GridLayout gridLayout;
    private ToggleButton mrbtn;
    private View btnmrop;
    private ImageButton moreOptionBtn;

    private String currentExpression = "";

    // Firebase Database Reference
    private DatabaseReference mDatabase;

    private final String[] expandKeys = {
            "sin", "cos", "tan", "rad", "deg", "log", "Ln",
            "(", ")", "inv", "!", "AC", "%", "Er", "/", "^",
            "7", "8", "9", "X", "Sq", "4", "5", "6", "-", "Pi",
            "1", "2", "3", "+", "e", "00", "0", ".", "="
    };

    private final String[] originalKeys = {
            "AC", "%", "Er", "/",
            "7", "8", "9", "X",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "00", "0", ".", "="
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Replace the URL with the exact one shown at the top of your Firebase Console
        mDatabase = FirebaseDatabase.getInstance("https://calculator-271ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("history");
        ed1 = findViewById(R.id.text1);
        ed2 = findViewById(R.id.text2);
        gridLayout = findViewById(R.id.l3);
        mrbtn = findViewById(R.id.more_button);
        btnmrop = findViewById(R.id.measures);
        moreOptionBtn = findViewById(R.id.moreoption);

        if (ed2 == null || ed1 == null || gridLayout == null) {
            throw new IllegalStateException("Required UI references missing from layout configuration");
        }

        if (mrbtn != null) mrbtn.setAlpha(0.6f);

        collapseGrid();

        mrbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) {
                    mrbtn.setAlpha(1.0f);
                    expandGrid();
                } else {
                    mrbtn.setAlpha(0.6f);
                    collapseGrid();
                }
            } catch (Exception e) {
                Log.e("CalculatorError", "Error toggling grid: " + e.getMessage());
            }
        });

        if (moreOptionBtn != null) {
            moreOptionBtn.setOnClickListener(this::showPopupMenu);
        }
    }

    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(MainActivity.this, view);
        popup.getMenuInflater().inflate(R.menu.main_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_history) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
        popup.show();
    }

    /**
     * Pushes a completed calculation item directly into the Firebase database
     */
    private void saveCalculationToFirebase(String expression, String result) {
        String timestamp = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        Map<String, Object> historyItem = new HashMap<>();
        historyItem.put("expression", expression);
        historyItem.put("result", result);
        historyItem.put("date", timestamp);

        mDatabase.push().setValue(historyItem)
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirebaseSuccess", "Data successfully saved to cloud!");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseError", "Write failed deeply: " + e.getMessage());
                    e.printStackTrace();
                });
    }

    public void showMeasurements(View view) {
        Intent intent = new Intent(getApplicationContext(), Measurements.class);
        startActivity(intent);
    }

    private Button createStyledButton(String text) {
        Button button = (Button) getLayoutInflater().inflate(R.layout.item_calc_button, gridLayout, false);
        button.setText(text);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = 0;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
        button.setLayoutParams(params);

        button.setOnClickListener(v -> onCalculatorButtonPressed(((Button) v).getText().toString()));
        return button;
    }

    private void expandGrid() {
        gridLayout.removeAllViews();
        gridLayout.setRowCount(7);
        gridLayout.setColumnCount(5);
        for (String key : expandKeys) {
            Button button = createStyledButton(key);
            button.setTextSize(13);
            gridLayout.addView(button);
        }
    }

    private void collapseGrid() {
        gridLayout.removeAllViews();
        gridLayout.setRowCount(5);
        gridLayout.setColumnCount(4);
        for (String key : originalKeys) {
            Button button = createStyledButton(key);
            button.setTextSize(24);
            gridLayout.addView(button);
        }
    }

    public void onCalculatorButtonPressed(String key) {
        switch (key) {
            case "AC":
                currentExpression = "";
                ed1.setText("");
                ed2.setText("0");
                break;

            case "Er":
                if (currentExpression.length() > 0) {
                    if (currentExpression.endsWith("sin(") || currentExpression.endsWith("cos(") || currentExpression.endsWith("tan(") || currentExpression.endsWith("log(")) {
                        currentExpression = currentExpression.substring(0, currentExpression.length() - 4);
                    } else if (currentExpression.endsWith("Ln(")) {
                        currentExpression = currentExpression.substring(0, currentExpression.length() - 3);
                    } else {
                        currentExpression = currentExpression.substring(0, currentExpression.length() - 1);
                    }
                    ed2.setText(currentExpression.isEmpty() ? "0" : currentExpression);
                }
                break;

            case "=":
                if (!currentExpression.isEmpty()) {
                    try {
                        ed1.setText(currentExpression);
                        double result = evaluateExpression(currentExpression);

                        String finalResult = (result == (long) result) ? String.valueOf((long) result) : String.valueOf(result);

                        // Push to Firebase Realtime Database
                        saveCalculationToFirebase(currentExpression, finalResult);

                        currentExpression = finalResult;
                        ed2.setText(currentExpression);
                    } catch (Exception e) {
                        ed2.setText("Error");
                        currentExpression = "";
                    }
                }
                break;

            case "X":
                currentExpression += "*";
                ed2.setText(currentExpression);
                break;

            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "Ln":
                currentExpression = currentExpression.equals("0") ? key + "(" : currentExpression + (key + "(");
                ed2.setText(currentExpression);
                break;

            default:
                currentExpression = (currentExpression.equals("0") && !key.equals(".")) ? key : currentExpression + key;
                ed2.setText(currentExpression);
                break;
        }
    }

    private double evaluateExpression(String expression) {
        final String cleanExpression = expression.replaceAll("\\s+", "");
        return new Object() {
            int pos = -1, ch;
            void nextChar() { ch = (++pos < cleanExpression.length()) ? cleanExpression.charAt(pos) : -1; }
            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) { nextChar(); return true; }
                return false;
            }
            double parse() { nextChar(); double x = parseExpression(); return x; }
            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }
            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException();
                        x /= divisor;
                    }
                    else return x;
                }
            }
            double parseFactor() {
                if (eat('-')) return -parseFactor();
                if (eat('+')) return parseFactor();
                double x; int startPos = this.pos;
                if (eat('(')) { x = parseExpression(); eat(')'); }
                else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(cleanExpression.substring(startPos, this.pos));
                } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) {
                    while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                    String func = cleanExpression.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func) {
                        case "sin": x = Math.sin(Math.toRadians(x)); break;
                        case "cos": x = Math.cos(Math.toRadians(x)); break;
                        case "tan": x = Math.tan(Math.toRadians(x)); break;
                        case "log": x = Math.log10(x); break;
                        case "Ln": x = Math.log(x); break;
                    }
                } else { throw new RuntimeException(); }
                if (eat('^')) x = Math.pow(x, parseFactor());
                return x;
            }
        }.parse();
    }
}