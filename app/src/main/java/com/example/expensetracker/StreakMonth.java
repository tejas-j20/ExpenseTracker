package com.example.expensetracker;

public class StreakMonth {
    private String yearMonth;   // "2025-04"
    private String displayName; // "Apr 2025"
    private boolean hasActivity;
    private boolean isCurrentStreak;

    public StreakMonth(String yearMonth, String displayName,
                       boolean hasActivity, boolean isCurrentStreak) {
        this.yearMonth      = yearMonth;
        this.displayName    = displayName;
        this.hasActivity    = hasActivity;
        this.isCurrentStreak= isCurrentStreak;
    }

    public String  getYearMonth()      { return yearMonth; }
    public String  getDisplayName()    { return displayName; }
    public boolean hasActivity()       { return hasActivity; }
    public boolean isCurrentStreak()   { return isCurrentStreak; }
}
