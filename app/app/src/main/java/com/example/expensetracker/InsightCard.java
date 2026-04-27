package com.example.expensetracker;

public class InsightCard {
    private String title;
    private String value;
    private String subtitle;
    private int    colorResHex; // e.g. 0xFF6200EE
    private String emoji;

    public InsightCard(String title, String value,
                       String subtitle, int colorResHex, String emoji) {
        this.title       = title;
        this.value       = value;
        this.subtitle    = subtitle;
        this.colorResHex = colorResHex;
        this.emoji       = emoji;
    }

    public String getTitle()      { return title; }
    public String getValue()      { return value; }
    public String getSubtitle()   { return subtitle; }
    public int    getColor()      { return colorResHex; }
    public String getEmoji()      { return emoji; }
}
