package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;


public class ActivityFriendProfile extends AppCompatActivity {

    String friendEmail;
    TextView tv_FriendFullName;
    User user, f;
    ProgressDialog pd;
    Button btn_AddFriend;
    DataContext db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_profile);
        Firebase.setAndroidContext(this);
        btn_AddFriend = (Button) findViewById(R.id.btn_AddFriend);
        pd = new ProgressDialog(this);

        pd.setMessage("Loading...");
        f = new User();
        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("Email");
        tv_FriendFullName = (TextView) findViewById(R.id.tv_FriendFullName_L_FriendProfile);

        user = LocalUserService.getLocalUserFromPreferences(this);

        db = new DataContext(this, null, null, 1);

        // check if already friends otherwise get info from server
        User friend = db.getFriendByEmailFromLocalDB(friendEmail);
        if ( friend.Email == null){
            FindFriendsTask t = new FindFriendsTask();
            t.execute();
        }else {
            tv_FriendFullName.setText(Tools.toProperName(friend.FirstName) + " " + Tools.toProperName(friend.LastName));
            btn_AddFriend.setEnabled(false);
            btn_AddFriend.setText("Friends");

        }


    }


    public class FindFriendsTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllUsersAsJsonString();

            try {
                return call.execute().body();
            } catch (IOException e) {
                e.printStackTrace();
                pd.hide();
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
                tv_FriendFullName.setText(Tools.toProperName(f.FirstName) + " " + Tools.toProperName(f.LastName));
                pd.hide();
            } catch (JSONException e1) {
                e1.printStackTrace();
                pd.hide();
            }
        }
    }
    public void btn_SendFriendRequestClick(View view) {
        Firebase firebase = new Firebase("https://mys3chat.firebaseio.com/friendrequests");
        Map<String, String> map = new HashMap<>();
        map.put("Email", user.Email);
        map.put("FirstName", user.FirstName);
        map.put("LastName", user.LastName);
        firebase.child(Tools.encodeString(f.Email)).child(Tools.encodeString(user.Email)).setValue(map);
        btn_AddFriend.setEnabled(false);
        btn_AddFriend.setText("Request Sent");
    }


}
