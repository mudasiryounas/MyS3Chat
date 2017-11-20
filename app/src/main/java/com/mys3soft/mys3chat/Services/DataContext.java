package com.mys3soft.mys3chat.Services;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mys3soft.mys3chat.Models.Message;
import com.mys3soft.mys3chat.Models.User;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


/*
Tables:
    1) LocalUser
    2) Friends -> contains local user friend list
    3) Messages

 */

public class DataContext extends SQLiteOpenHelper {

    public DataContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, "mys3chat.db", factory, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String tblLocalUser = "create table if not exists LocalUser (ID integer ,Email text, FirstName text, LastName text); ";
        String tblFriends = "create table if not exists Friends (Email text, FirstName text, LastName text);";
        String tblMessages = "create table if not exists Messages (FromMail text, ToMail text, Message text, SentDate text);";
        //db.execSQL(tblLocalUser);
        db.execSQL(tblFriends);
        db.execSQL(tblMessages);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //String dropLocalUser = "drop table if exists LocalUser;";
        String dropFriends = "drop table if exists Friends; ";
        String dropMessages = "drop table if exists Messages;";
        // db.execSQL(dropLocalUser);
        db.execSQL(dropFriends);
        db.execSQL(dropMessages);
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
        } finally {
            if (c != null) {
                c.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }

    public void deleteAllFriendsFromLocalDB() {
        String query = "delete from Friends";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(query);
    }

    public void deleteFriendByEmailFromLocalDB(String email) {
        String query = "delete from Friends where Email = '" + email + "';";
        getWritableDatabase().execSQL(query);
    }

    public User getFriendByEmailFromLocalDB(String friendEmail) {
        String query = "select * from Friends where Email = '" + friendEmail + "';";
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        User friend = new User();

        if (c.getCount() > 0) {
            friend.Email = c.getString(c.getColumnIndex("Email"));
            friend.FirstName = c.getString(c.getColumnIndex("FirstName"));
            friend.LastName = c.getString(c.getColumnIndex("LastName"));
        }
        return friend;
    }

    public void saveMessageOnLocakDB(String from, String to, String message, String sentDate) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "insert into Messages (FromMail, ToMail, Message, SentDate) values('" + from + "', '" + to + "', '" + message.replace("'","\"") + "','" + sentDate + "');";
        db.execSQL(query);
    }

    public List<Message> getChat(String userMail, String friendMail) {
        List<Message> messageList = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try {
            String query = "select * from Messages where (FromMail = '"+userMail+"' and ToMail='"+friendMail+"') or (ToMail = '"+userMail+"' and FromMail='"+friendMail+"')  ";
            Cursor c = db.rawQuery(query, null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                Message mess = new Message();
                mess.FromMail = c.getString(c.getColumnIndex("FromMail"));
                mess.ToMail = c.getString(c.getColumnIndex("ToMail"));
                mess.Message = c.getString(c.getColumnIndex("Message"));
                mess.SentDate = c.getString(c.getColumnIndex("SentDate"));
                messageList.add(mess);
                c.moveToNext();
            }
            c.close();
            return messageList;
        } catch (Exception e) {
            e.printStackTrace();
            return messageList;
        }

    }

    public void  deleteChat(String userMail, String friendMail){
        String deleteQuery = "delete from  Messages where (FromMail = '"+userMail+"' and ToMail='"+friendMail+"') or (ToMail = '"+userMail+"' and FromMail='"+friendMail+"')  ";
        getWritableDatabase().execSQL(deleteQuery);

    }

}
