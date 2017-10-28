package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {


    private DataContext db;

    ListView lv_FriendList;
    User user;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
        pd = new ProgressDialog(this);
        pd.setMessage("Refreshing...");

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivity(intent);
        } else {
            startService(new Intent(this, AppService.class));
            // set last msgs
            db = new DataContext(this, null, null, 1);
            ListAdapter adp = new FriendListAdapter(MainActivity.this, db.getUserFriendList());
            lv_FriendList.setAdapter(adp);

            // listener for item click
            lv_FriendList.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            TextView email = (TextView) view.findViewById(R.id.tv_HiddenEmail);
                            TextView tv_Name = (TextView) view.findViewById(R.id.tv_FriendFullName);
                            Intent intend = new Intent(getApplicationContext(), ActivityChat.class);
                            intend.putExtra("FriendEmail", email.getText().toString());
                            intend.putExtra("FriendFullName", tv_Name.getText().toString());
                            startActivity(intend);
                        }
                    }
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            if (LocalUserService.deleteLocalUserFromPreferences(this)) {
                db.deleteAllFriendsFromLocalDB();
                System.exit(1);
            } else {
                System.exit(1);
            }
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this, ActivityProfile.class));

        }

        if (id == R.id.menu_addContacts) {
            startActivity(new Intent(this, ActivityAddContact.class));
            return true;
        }

        if (id == R.id.menu_notification) {
            startActivity(new Intent(this, ActivityNotifications.class));
            return true;
        }

        if (id == R.id.menu_refresh) {
            FriendListTask t = new FriendListTask();
            t.execute();
        }
        return true;// super.onOptionsItemSelected(item);
    }


    public class FriendListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pd.show();
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllFriendListAsJsonString();
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
                user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                JSONObject jsonObjectList = new JSONObject(jsonListString);
                List<User> friendList = new ArrayList<>();
                JSONObject userFriendTree = jsonObjectList.getJSONObject(user.Email);
                for (Iterator iterator = userFriendTree.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    User friend = new User();
                    JSONObject friendJson = userFriendTree.getJSONObject(key);
                    friend.Email = friendJson.getString("Email");
                    friend.FirstName = friendJson.getString("FirstName");
                    friend.LastName = friendJson.getString("LastName");
                    friendList.add(friend);
                }

                // refresh local database
                db = new DataContext(getApplicationContext(), null, null, 1);
                db.refreshUserFriendList(friendList);
                // set to adapter
                ListAdapter adp = new FriendListAdapter(MainActivity.this, friendList);
                lv_FriendList.setAdapter(adp);
                pd.hide();
            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }
        }
    }

}
