package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ExpenseDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "expense_tracker.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and columns
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CATEGORY = "category";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_DESCRIPTION = "description";

    // Create table SQL
    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_TRANSACTIONS + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_AMOUNT + " REAL NOT NULL, " +
            COLUMN_CATEGORY + " TEXT NOT NULL, " +
            COLUMN_TYPE + " TEXT NOT NULL, " +
            COLUMN_DATE + " TEXT NOT NULL, " +
            COLUMN_DESCRIPTION + " TEXT);";

    public ExpenseDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRANSACTIONS);
        onCreate(db);
    }

    // Add a new transaction
    public long addTransaction(Transaction transaction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, transaction.getAmount());
        values.put(COLUMN_CATEGORY, transaction.getCategory());
        values.put(COLUMN_TYPE, transaction.getType());
        values.put(COLUMN_DATE, transaction.getDate());
        values.put(COLUMN_DESCRIPTION, transaction.getDescription());

        long id = db.insert(TABLE_TRANSACTIONS, null, values);
        db.close();
        return id;
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSACTIONS,
                new String[]{COLUMN_ID, COLUMN_AMOUNT, COLUMN_CATEGORY, COLUMN_TYPE, COLUMN_DATE, COLUMN_DESCRIPTION},
                null, null, null, null, COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Transaction transaction = new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                );
                transactions.add(transaction);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    // Delete a transaction
    public void deleteTransaction(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRANSACTIONS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    // Get total income
    public double getTotalIncome() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Income'", null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Get total expense
    public double getTotalExpense() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(" + COLUMN_AMOUNT + ") FROM " + TABLE_TRANSACTIONS +
                " WHERE " + COLUMN_TYPE + " = 'Expense'", null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }
    // Get transactions filtered by type ("Income" or "Expense")
    public List<Transaction> getTransactionsByType(String type) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSACTIONS,
                new String[]{COLUMN_ID, COLUMN_AMOUNT, COLUMN_CATEGORY,
                        COLUMN_TYPE, COLUMN_DATE, COLUMN_DESCRIPTION},
                COLUMN_TYPE + " = ?",
                new String[]{type},
                null, null, COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                transactions.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }

    // Get transactions filtered by month (format: "YYYY-MM")
    public List<Transaction> getTransactionsByMonth(String yearMonth) {
        List<Transaction> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRANSACTIONS,
                new String[]{COLUMN_ID, COLUMN_AMOUNT, COLUMN_CATEGORY,
                        COLUMN_TYPE, COLUMN_DATE, COLUMN_DESCRIPTION},
                COLUMN_DATE + " LIKE ?",
                new String[]{yearMonth + "%"},
                null, null, COLUMN_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                transactions.add(new Transaction(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                        cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TYPE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION))
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }
    // Get spending grouped by category (Expense only)
    public List<String[]> getCategoryWiseExpense() {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT " + COLUMN_CATEGORY + ", SUM(" + COLUMN_AMOUNT + ") as total " +
                        "FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COLUMN_TYPE + " = 'Expense' " +
                        "GROUP BY " + COLUMN_CATEGORY +
                        " ORDER BY total DESC", null);

        if (cursor.moveToFirst()) {
            do {
                String category = cursor.getString(0);
                String total    = String.valueOf(cursor.getDouble(1));
                result.add(new String[]{category, total});
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    // Get monthly totals for last 6 months
    public List<String[]> getMonthlyTotals() {
        List<String[]> result = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT substr(" + COLUMN_DATE + ", 1, 7) as month, " +
                        COLUMN_TYPE + ", SUM(" + COLUMN_AMOUNT + ") as total " +
                        "FROM " + TABLE_TRANSACTIONS +
                        " GROUP BY month, " + COLUMN_TYPE +
                        " ORDER BY month DESC LIMIT 12", null);

        if (cursor.moveToFirst()) {
            do {
                result.add(new String[]{
                        cursor.getString(0), // month  e.g. "2025-04"
                        cursor.getString(1), // type
                        String.valueOf(cursor.getDouble(2)) // total
                });
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    // Get transaction count for the current month
    public int getThisMonthTransactionCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar cal = Calendar.getInstance();
        String yearMonth = String.format("%d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1);

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_TRANSACTIONS +
                        " WHERE " + COLUMN_DATE + " LIKE ?",
                new String[]{yearMonth + "%"});

        int count = 0;
        if (cursor.moveToFirst()) count = cursor.getInt(0);
        cursor.close();
        db.close();
        return count;
    }

    // Get distinct active months (for streak calculation)
    public List<String> getDistinctActiveMonths() {
        List<String> months = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT DISTINCT substr(" + COLUMN_DATE + ", 1, 7) " +
                        "FROM " + TABLE_TRANSACTIONS +
                        " ORDER BY 1 DESC", null);

        if (cursor.moveToFirst()) {
            do {
                months.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return months;
    }

}
