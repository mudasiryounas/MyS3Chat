package com.mys3soft.mys3chat.Services;

import android.content.Context;
import android.content.SharedPreferences;

import com.mys3soft.mys3chat.Models.User;


public class LocalUserService {
    public static User getLocalUserFromPreferences(Context context){
        SharedPreferences pref = context.getSharedPreferences("LocalUser",0);
        User user = new User();
        user.Email = pref.getString("Email",null);
        user.FirstName = pref.getString("FirstName",null);
        user.LastName = pref.getString("LastName",null);
        return user;
    }

    public static boolean deleteLocalUserFromPreferences(Context context){
        try {
            SharedPreferences pref = context.getSharedPreferences("LocalUser",0);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }



}
