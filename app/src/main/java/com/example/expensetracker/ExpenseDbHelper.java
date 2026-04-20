package com.example.expensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
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
}
