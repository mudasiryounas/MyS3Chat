package com.mys3soft.mys3chat.Services;

import android.os.AsyncTask;
import com.mys3soft.mys3chat.Models.CatcheUserList;
import com.mys3soft.mys3chat.Models.User;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import retrofit2.Call;

public class FillUserListTask extends AsyncTask<Void, Void, String> {


    @Override
    protected String doInBackground(Void... params) {

        IFireBaseAPI api = Tools.makeRetroFitApi();
        Call<String> call = api.getAllUsersAsJsonString();

        try {
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(String jsonListString) {

        try {
            JSONObject jsonObjectList = new JSONObject(jsonListString);

            List<User> userList = new ArrayList<>();
            for (Iterator iterator = jsonObjectList.keys(); iterator.hasNext(); ) {
                String key = (String) iterator.next();
                JSONObject item = jsonObjectList.getJSONObject(key);

                User f = new User();
                f.Email = item.getString("Email");
                f.FirstName = item.getString("FirstName");
                f.LastName = item.getString("LastName");
                userList.add(f);
            }
            CatcheUserList.CatchedUserList = userList;
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
