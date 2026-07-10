package com.example.calculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Defining layout components
    private EditText ed1, ed2;
    private GridLayout gridLayout;
    private ToggleButton mrbtn;
    private View btnmrop; // Changed to view base to handle ImageButton / Button interchangeably safely

    // Track the active string formula input
    private String currentExpression = "";

    // Button sets for grid state transformations
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

        // Binding UI element identifiers
        ed1 = findViewById(R.id.text1);
        ed2 = findViewById(R.id.text2);
        gridLayout = findViewById(R.id.l3);
        mrbtn = findViewById(R.id.more_button);
        btnmrop = findViewById(R.id.measures);

        // Fail-fast safety checks for layout integrity
        if (ed2 == null || ed1 == null) {
            throw new IllegalStateException("EditText views not found in layout configuration");
        }
        if (gridLayout == null) {
            throw new IllegalStateException("GridLayout with id l3 not found in layout");
        }

        // Set initial alpha state for the toggle button to indicate unchecked status
        if (mrbtn != null) {
            mrbtn.setAlpha(0.6f);
        }

        // Initialize the basic 4x5 calculator interface layout view state
        collapseGrid();

        // Listener handling grid state shifts between basic and scientific views
        mrbtn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) {
                    mrbtn.setAlpha(1.0f); // Bright highlight when active
                    expandGrid();
                } else {
                    mrbtn.setAlpha(0.6f); // Slightly faded when scientific mode is off
                    collapseGrid();
                }
            } catch (Exception e) {
                Log.e("CalculatorError", "Error toggling grid: " + e.getMessage());
            }
        });
    }

    /**
     * Standard click intent mapping targeting external measurements dashboard activity
     */
    public void showMeasurements(View view) {
        Intent intent = new Intent(getApplicationContext(), Measurements.class);
        startActivity(intent);
    }

    /**
     * Programmatically generates uniform grid buttons matching custom layouts
     */
    private Button createStyledButton(String text) {
        if (gridLayout == null) {
            throw new IllegalStateException("gridLayout component context missing references");
        }

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

    /**
     * Shifts layout to Expanded Scientific Mode (7 Rows x 5 Columns)
     */
    private void expandGrid() {
        if (gridLayout == null || ed2 == null) return;

        gridLayout.removeAllViews();
        gridLayout.setRowCount(7);
        gridLayout.setColumnCount(5);

        for (String key : expandKeys) {
            Button button = createStyledButton(key);
            button.setTextSize(13); // Scale down font sizes to match higher grid density elements
            gridLayout.addView(button);
        }
    }

    /**
     * Shifts layout to standard Simple Mode (5 Rows x 4 Columns)
     */
    private void collapseGrid() {
        if (gridLayout == null || ed2 == null) return;

        gridLayout.removeAllViews();
        gridLayout.setRowCount(5);
        gridLayout.setColumnCount(4);

        for (String key : originalKeys) {
            Button button = createStyledButton(key);
            button.setTextSize(24);
            gridLayout.addView(button);
        }
    }

    /**
     * Core state controller capturing, handling, and parsing button events
     */
    public void onCalculatorButtonPressed(String key) {
        if (ed2 == null || ed1 == null) return;

        switch (key) {
            case "AC":
                // Completely resets evaluation workspaces
                currentExpression = "";
                ed1.setText("");
                ed2.setText("0");
                break;

            case "Er":
                // Single-character string backspace routine
                if (currentExpression.length() > 0) {
                    // Check if we are deleting an entire functional token like "sin(" to keep backspacing clean
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
                // Evaluates formula models and safely manages outputs
                if (!currentExpression.isEmpty()) {
                    try {
                        ed1.setText(currentExpression);
                        double result = evaluateExpression(currentExpression);

                        // Clean display formats: strips floating decimals off whole integer values
                        if (result == (long) result) {
                            currentExpression = String.valueOf((long) result);
                        } else {
                            currentExpression = String.valueOf(result);
                        }
                        ed2.setText(currentExpression);
                    } catch (Exception e) {
                        ed2.setText("Error");
                        currentExpression = "";
                    }
                }
                break;

            case "X":
                // Standardize layout view symbols to native mathematical evaluation tokens
                currentExpression += "*";
                ed2.setText(currentExpression);
                break;

            // Automatically open parameters parenthesis for nested expressions to prevent runtime engine crash
            case "sin":
            case "cos":
            case "tan":
            case "log":
            case "Ln":
                if (currentExpression.equals("0")) {
                    currentExpression = key + "(";
                } else {
                    currentExpression += key + "(";
                }
                ed2.setText(currentExpression);
                break;

            default:
                // Handles continuous numerical digit array streams dynamically
                if (currentExpression.equals("0") && !key.equals(".")) {
                    currentExpression = key;
                } else {
                    currentExpression += key;
                }
                ed2.setText(currentExpression);
                break;
        }
    }

    /**
     * Mathematical compilation engine optimizing high, mid, and low priority operator rules
     */
    private double evaluateExpression(String expression) {
        // Drop any spaces or hidden layout fragments
        final String cleanExpression = expression.replaceAll("\\s+", "");

        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < cleanExpression.length()) ? cleanExpression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < cleanExpression.length()) throw new RuntimeException("Unexpected character sequence");
                return x;
            }

            // Low-Priority Phase: Addition (+) and Subtraction (-)
            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if      (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            // Mid-Priority Phase: Multiplication (*) and Division (/)
            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if      (eat('*')) x *= parseFactor();
                    else if (eat('/')) {
                        double divisor = parseFactor();
                        if (divisor == 0) throw new ArithmeticException("Divide by zero exception");
                        x /= divisor;
                    }
                    else return x;
                }
            }

            // High-Priority Phase: Values, explicit signs, brackets, string functions, and exponents
            double parseFactor() {
                if (eat('-')) return -parseFactor(); // Unary negation support
                if (eat('+')) return parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) { // Bracket sub-expressions resolution
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // Text number extraction loops
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(cleanExpression.substring(startPos, this.pos));
                } else if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) { // Functional key identity monitoring
                    while ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z')) nextChar();
                    String func = cleanExpression.substring(startPos, this.pos);

                    // Run a inner parsing cycle on everything inside the parameter boundary brackets
                    x = parseFactor();

                    // Structural mapping routing text strings directly to strict arithmetic operations
                    switch (func) {
                        case "sin":
                            x = Math.sin(Math.toRadians(x)); // Java assumes input is in Radians, we explicitly pass Degrees
                            break;
                        case "cos":
                            x = Math.cos(Math.toRadians(x));
                            break;
                        case "tan":
                            x = Math.tan(Math.toRadians(x));
                            break;
                        case "log":
                            x = Math.log10(x);
                            break;
                        case "Ln":
                            x = Math.log(x);
                            break;
                        default:
                            throw new RuntimeException("Unknown function execution target: " + func);
                    }
                } else {
                    throw new RuntimeException("Syntax validation mismatch");
                }

                // Power/Exponent math parsing phase rules implementation
                if (eat('^')) x = Math.pow(x, parseFactor());

                return x;
            }
        }.parse();
    }
}