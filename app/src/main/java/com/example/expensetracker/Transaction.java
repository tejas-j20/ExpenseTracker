package com.example.expensetracker;

public class Transaction {
    private int id;
    private double amount;
    private String category;
    private String type;
    private String date;
    private String description;

    // Constructor without ID (for new transactions)
    public Transaction(double amount, String category, String type, String date, String description) {
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    // Constructor with ID (for loading from DB)
    public Transaction(int id, double amount, String category, String type, String date, String description) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.type = type;
        this.date = date;
        this.description = description;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}