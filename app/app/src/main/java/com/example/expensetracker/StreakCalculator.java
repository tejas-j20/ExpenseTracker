package com.example.expensetracker;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class StreakCalculator {

    /**
     * Returns the current consecutive month streak count.
     * A streak is counted from the most recent month backwards.
     */
    public static int calculateStreak(List<String> activeMonths) {
        if (activeMonths == null || activeMonths.isEmpty()) return 0;

        Set<String> monthSet = new HashSet<>(activeMonths);
        Calendar cal = Calendar.getInstance();
        int streak = 0;

        // Walk backwards month by month
        for (int i = 0; i < 24; i++) {
            String key = String.format("%d-%02d",
                    cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);
            if (monthSet.contains(key)) {
                streak++;
                cal.add(Calendar.MONTH, -1);
            } else {
                break; // streak broken
            }
        }
        return streak;
    }

    /**
     * Returns the longest ever consecutive streak found in activeMonths.
     */
    public static int calculateLongestStreak(List<String> activeMonths) {
        if (activeMonths == null || activeMonths.isEmpty()) return 0;

        Set<String>  monthSet  = new HashSet<>(activeMonths);
        List<String> allMonths = getAllMonthsBetween(activeMonths);

        int longest = 0, current = 0;
        for (String m : allMonths) {
            if (monthSet.contains(m)) {
                current++;
                longest = Math.max(longest, current);
            } else {
                current = 0;
            }
        }
        return longest;
    }

    /**
     * Builds a visual list of last 12 months with streak state flags.
     */
    public static List<StreakMonth> buildStreakTimeline(List<String> activeMonths) {
        Set<String>   monthSet = new HashSet<>(activeMonths);
        List<StreakMonth> timeline = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        // Compute which months are part of the current streak
        Set<String> currentStreakMonths = new HashSet<>();
        Calendar tmp = Calendar.getInstance();
        for (int i = 0; i < 24; i++) {
            String key = String.format("%d-%02d",
                    tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH) + 1);
            if (monthSet.contains(key)) {
                currentStreakMonths.add(key);
                tmp.add(Calendar.MONTH, -1);
            } else { break; }
        }

        // Build last 12 months (oldest → newest for display)
        List<String[]> last12 = new ArrayList<>();
        Calendar build = Calendar.getInstance();
        for (int i = 11; i >= 0; i--) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_MONTH, 1);
            c.add(Calendar.MONTH, -i);
            String key = String.format("%d-%02d",
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
            String display = new java.text.SimpleDateFormat("MMM yyyy",
                    java.util.Locale.getDefault()).format(c.getTime());
            last12.add(new String[]{key, display});
        }

        for (String[] entry : last12) {
            timeline.add(new StreakMonth(
                    entry[0],
                    entry[1],
                    monthSet.contains(entry[0]),
                    currentStreakMonths.contains(entry[0])
            ));
        }
        return timeline;
    }

    // Helper: generate all months from earliest to latest
    private static List<String> getAllMonthsBetween(List<String> months) {
        List<String> sorted = new ArrayList<>(months);
        java.util.Collections.sort(sorted);
        List<String> all = new ArrayList<>();
        if (sorted.isEmpty()) return all;

        String[] startParts = sorted.get(0).split("-");
        String[] endParts   = sorted.get(sorted.size() - 1).split("-");

        Calendar c = Calendar.getInstance();
        c.set(Integer.parseInt(startParts[0]),
                Integer.parseInt(startParts[1]) - 1, 1);

        Calendar end = Calendar.getInstance();
        end.set(Integer.parseInt(endParts[0]),
                Integer.parseInt(endParts[1]) - 1, 1);

        while (!c.after(end)) {
            all.add(String.format("%d-%02d",
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1));
            c.add(Calendar.MONTH, 1);
        }
        return all;
    }
}
