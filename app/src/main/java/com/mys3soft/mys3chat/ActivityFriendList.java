package com.mys3soft.mys3chat;


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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.CatcheUserList;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.FillUserListTask;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class ActivityFriendList extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);

    ListView lv_FriendList;
    ProgressBar pb;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        lv_FriendList = (ListView) findViewById(R.id.lv_FriendList);
        pb = (ProgressBar) findViewById(R.id.pb_Loading);
        user = db.getLocalUser();

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
                        Intent intend = new Intent(getApplicationContext(), ActivityChat.class);
                         intend.putExtra("FriendEmail", email.getText().toString());
                        startActivity(intend);
                    }
                }

        );


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
                ListAdapter adp = new FriendListAdapter(ActivityFriendList.this, friendList);
                lv_FriendList.setAdapter(adp);
                pb.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                pb.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }


        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_addContacts) {
            //start add contact activity
            startActivity(new Intent(this, ActivityAddContact.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
