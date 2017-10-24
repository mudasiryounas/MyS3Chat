package com.mys3soft.mys3chat.Services;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mys3soft.mys3chat.Models.User;

public class DataContext extends SQLiteOpenHelper {

    public DataContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "mys3chat.db", factory, 1);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "create table LocalUser (ID integer ,Email text, FirstName text, LastName text);";
        db.execSQL(query);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if LocalUser Contacts ");
        onCreate(db);
    }


    public boolean doesUserExistsInLocalDB() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from LocalUser where ID = 1 limit 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();

        try {
            if (c.getString(c.getColumnIndex("Email")) != "") {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    public boolean saveUserInLocalDB(String email, String firstName, String lastName) {
        try {
            String query = "INSERT INTO LocalUser (ID, Email, FirstName, LastName) values (1,'" + email + "', '" + firstName + "', '" + lastName + "');  ";
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUserInLocalDB() {
        try {
            SQLiteDatabase db = getWritableDatabase();
            db.execSQL("delete from LocalUser where ID = 1;");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getLocalUser() {
        SQLiteDatabase db = getReadableDatabase();
        String query = "select * from LocalUser where ID = 1 limit 1;";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        User user = new User();
        user.ID = c.getLong(c.getColumnIndex("ID"));
        user.Email = c.getString(c.getColumnIndex("Email"));
        user.FirstName = c.getString(c.getColumnIndex("FirstName"));
        user.LastName = c.getString(c.getColumnIndex("LastName"));
        return user;

    }


}
