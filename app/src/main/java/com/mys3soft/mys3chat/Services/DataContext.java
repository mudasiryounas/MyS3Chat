package com.mys3soft.mys3chat.Services;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mys3soft.mys3chat.Models.User;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;




/*

Tables:
    1) LocalUser
    2) Friends -> contains local user friend list

 */

public class DataContext extends SQLiteOpenHelper {

    public DataContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "mys3chat.db", factory, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tblLocalUser = "create table LocalUser (ID integer ,Email text, FirstName text, LastName text); ";
        String tblFriends = "create table Friends (Email text, FirstName text, LastName text);";
        db.execSQL(tblLocalUser);
        db.execSQL(tblFriends);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public List<User> getUserFriendList() {
        List<User> friendList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            String query = "select * from Friends";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                User friend = new User();
                friend.Email = c.getString(c.getColumnIndex("Email"));
                friend.FirstName = c.getString(c.getColumnIndex("FirstName"));
                friend.LastName = c.getString(c.getColumnIndex("LastName"));
                friendList.add(friend);
                c.moveToNext();
            }
            c.close();
            return friendList;
        } catch (Exception e) {
            e.printStackTrace();
            return friendList;
        }

    }

    public void refreshUserFriendList(List<User> friendList) {

        for (User item : friendList) {
            // check if user already exists
            if (checkFriendAlreadyExists(item.Email) == 0) {
                // insert
                SQLiteDatabase db = getWritableDatabase();
                String query = "insert into Friends (Email,FirstName,LastName) values('" + item.Email + "', '" + item.FirstName + "', '" + item.LastName + "');";
                db.execSQL(query);
              // db.close();
            }
        }
    }

    public int checkFriendAlreadyExists(String email) {
        Cursor c = null;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            String query = "select count(*) from Friends where Email = '" + email + "'";
            c = db.rawQuery(query, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
            return 0;
        }
        finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void deleteAllFriendsFromLocalDB(){
        String query = "delete from Friends";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }



}
