package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

// Firebase Realtime Database Imports
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout historyContainer;
    private ImageButton backBtn, deleteBtn;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        historyContainer = findViewById(R.id.history_container);
        backBtn = findViewById(R.id.back_button);
        deleteBtn = findViewById(R.id.delete_button);

        // Bind reference node target
        // Replace your old mDatabase line with this:
        mDatabase = FirebaseDatabase.getInstance("https://calculator-271ea-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference()
                .child("history");

        backBtn.setOnClickListener(v -> finish());

        // Clears data from Firebase with one click
        deleteBtn.setOnClickListener(v -> mDatabase.removeValue());

        // Setup real-time UI synchronization listener
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                historyContainer.removeAllViews(); // Reset layout view pool

                // Read snapshots in reverse chronologically order
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String expr = snapshot.child("expression").getValue(String.class);
                    String res = snapshot.child("result").getValue(String.class);
                    String date = snapshot.child("date").getValue(String.class);

                    if (expr != null && res != null) {
                        // Inflate and prepend calculation view cards dynamically to top
                        addHistoryCardToView(expr, res, date);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle read errors gracefully here
            }
        });
    }

    private void addHistoryCardToView(String expr, String res, String dateStr) {
        View card = getLayoutInflater().inflate(R.layout.item_history_card, historyContainer, false);

        TextView expressionTv = card.findViewById(R.id.card_expression);
        TextView resultTv = card.findViewById(R.id.card_result);
        TextView dateTv = card.findViewById(R.id.card_date);

        expressionTv.setText(expr);
        resultTv.setText(res);
        dateTv.setText(dateStr);

        // Adds view elements at index 0 so newest inputs show at the very top of layout
        historyContainer.addView(card, 0);
    }
}