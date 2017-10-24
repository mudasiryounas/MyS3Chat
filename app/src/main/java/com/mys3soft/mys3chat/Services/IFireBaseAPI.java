package com.mys3soft.mys3chat.Services;


import android.widget.ListView;

import com.mys3soft.mys3chat.Models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.GET;
import java.util.List;

public interface IFireBaseAPI {

    // change later to get only single user
    @GET("/users.json")
    Call<List<User>> getAllUsers();
   // public void getAllUsers(Callback<List<User>> response);

    @GET("/users.json")
    Call<String> getAllUsersAsJsonString();


    @GET("/friendrequests.json")
    Call<String> getAllFriendRequestsAsJsonString();


    @GET("/friends.json")
    Call<String> getAllFriendListAsJsonString();

}
