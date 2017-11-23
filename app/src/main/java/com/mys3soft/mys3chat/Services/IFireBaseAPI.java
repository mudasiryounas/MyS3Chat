package com.mys3soft.mys3chat.Services;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

import java.util.List;

public interface IFireBaseAPI {

    @GET("/users.json")
    Call<String> getAllUsersAsJsonString();

    @GET
    Call<String> getUserFriendsListAsJsonString(@Url String url);

    @GET
    Call<String> getSingleUserByEmail(@Url String url);

}
