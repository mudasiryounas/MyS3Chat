package com.mys3soft.mys3chat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;


public class ActivityFriendProfile extends AppCompatActivity {

    ProgressBar pb;
    String friendEmail;
    TextView tv_FriendFullName;
    User user, f;

    DataContext db = new DataContext(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);

        Firebase.setAndroidContext(this);

        f = new User();

        Bundle extras = getIntent().getExtras();

        friendEmail = extras.getString("Email");

        pb = (ProgressBar) findViewById(R.id.pb_Loading_L_FriendProfile);
        pb.setVisibility(View.INVISIBLE);
        tv_FriendFullName = (TextView) findViewById(R.id.tv_FriendFullName_L_FriendProfile);

        user = db.getLocalUser();


        FindFriendsTask t = new FindFriendsTask();
        t.execute();


    }


    public class FindFriendsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

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
                JSONObject item = jsonObjectList.getJSONObject(friendEmail);

                f.Email = item.getString("Email");
                f.FirstName = item.getString("FirstName");
                f.LastName = item.getString("LastName");
                tv_FriendFullName.setText(f.FirstName + " " + f.LastName);

                // check if are friends then remove add button
                // if not firiends then remove send message button
                pb.setVisibility(View.INVISIBLE);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }


        }
    }


    public void btn_SendFriendRequestClick(View view){
        Firebase firebase = new Firebase("https://mys3chat.firebaseio.com/friendrequests");
        firebase.child(Tools.encodeString(f.Email)).child(Tools.encodeString(user.Email)).setValue("1");

    }


}
