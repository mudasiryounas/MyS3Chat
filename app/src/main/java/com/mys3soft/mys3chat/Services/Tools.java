package com.mys3soft.mys3chat.Services;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.mys3soft.mys3chat.Models.User;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Tools {

    public static final String ENDPOINT = "https://mys3chat.firebaseio.com";

    public static String encodeString(String string) {
        return string.replace(".", ",");
    }

    public static String decodeString(String string) {
        return string.replace(",", ".");
    }


    public static IFireBaseAPI makeRetroFitApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit.create(IFireBaseAPI.class);
    }


    public static String toProperName(String s) {
        if (s.length() <= 1)
            return s.toUpperCase();
        return  s.substring(0,1).toUpperCase() + s.substring(1).toLowerCase();
    }






}
