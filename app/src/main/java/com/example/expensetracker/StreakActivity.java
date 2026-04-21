package com.example.expensetracker;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class StreakActivity extends AppCompatActivity {

    private TextView tvCurrentStreak, tvStreakMessage;
    private TextView tvLongestStreak, tvTotalMonths;
    private RecyclerView rvStreakTimeline;
    private ExpenseDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streak);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarStreak);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Views
        tvCurrentStreak  = findViewById(R.id.tvCurrentStreak);
        tvStreakMessage  = findViewById(R.id.tvStreakMessage);
        tvLongestStreak  = findViewById(R.id.tvLongestStreak);
        tvTotalMonths    = findViewById(R.id.tvTotalMonths);
        rvStreakTimeline = findViewById(R.id.rvStreakTimeline);

        dbHelper = new ExpenseDbHelper(this);

        loadStreakData();
    }

    private void loadStreakData() {
        List<String> activeMonths = dbHelper.getDistinctActiveMonths();

        // ── Calculate Stats ──────────────────────────────────────────────
        int currentStreak = StreakCalculator.calculateStreak(activeMonths);
        int longestStreak = StreakCalculator.calculateLongestStreak(activeMonths);
        int totalActive   = activeMonths.size();

        // ── Update UI ────────────────────────────────────────────────────
        tvCurrentStreak.setText(String.valueOf(currentStreak));
        tvLongestStreak.setText(String.valueOf(longestStreak));
        tvTotalMonths  .setText(String.valueOf(totalActive));

        // Dynamic motivational message
        tvStreakMessage.setText(getStreakMessage(currentStreak));

        // ── Timeline RecyclerView ─────────────────────────────────────────
        List<StreakMonth> timeline = StreakCalculator.buildStreakTimeline(activeMonths);
        StreakAdapter adapter = new StreakAdapter(timeline);
        rvStreakTimeline.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvStreakTimeline.setAdapter(adapter);

        // Scroll to the end (most recent month)
        rvStreakTimeline.scrollToPosition(timeline.size() - 1);
    }

    private String getStreakMessage(int streak) {
        if (streak == 0)  return "Start tracking today to build your streak!";
        if (streak == 1)  return "Great start! Come back next month! 💪";
        if (streak < 3)   return "You're on a roll! Keep going! 🚀";
        if (streak < 6)   return "Impressive consistency! 🌟";
        if (streak < 12)  return "You're a financial tracking pro! 🏆";
        return "Legendary streak! Absolutely outstanding! 👑";
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
