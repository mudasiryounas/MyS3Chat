package com.mys3soft.mys3chat;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.CatcheUserList;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.FillUserListTask;
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

    ListView lv_FriendList;
    ProgressBar pb;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
        pb = (ProgressBar) findViewById(R.id.pb_Loading);

        // check if user exists in local db
        user = LocalUserService.getLocalUserFromPreferences(this);
        if (user.Email == null) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivity(intent);
        } else {
            // set last msgs

            if (CatcheUserList.CatchedUserList.size() < 1) {
                FillUserListTask task = new FillUserListTask();
                task.execute();
            }

            FriendListTask t = new FriendListTask();
            t.execute();

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
            //start add contact activity
            startActivity(new Intent(this, ActivityAddContact.class));

            return true;
        }


        if (id == R.id.menu_notification) {
            startActivity(new Intent(this, ActivityNotifications.class));
            return true;
        }
        return true;// super.onOptionsItemSelected(item);
    }


    public class FriendListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllFriendListAsJsonString();

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
                List<User> friendList = new ArrayList<>();
                JSONObject userFriendTree = jsonObjectList.getJSONObject(user.Email);
                for (Iterator iterator = userFriendTree.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();

                    User f = new User();
                    // get userinfo from catched if available
                    for (User item : CatcheUserList.CatchedUserList) {
                        if (item.Email.equals(key)) {
                            f = item;
                            break;
                        }
                    }
                    friendList.add(f);
                }
                ListAdapter adp = new FriendListAdapter(MainActivity.this, friendList);
                lv_FriendList.setAdapter(adp);
                pb.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                pb.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }


        }
    }


}
