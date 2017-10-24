package com.mys3soft.mys3chat;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.IFireBaseAPI;
import com.mys3soft.mys3chat.Services.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;

public class ActivityAddContact extends AppCompatActivity {

    ProgressBar pb;
    ListView lv_SerachList;
    EditText searchKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        pb =(ProgressBar) findViewById(R.id.pd_LoadingAddContact);
        pb.setVisibility(View.INVISIBLE);
        lv_SerachList = (ListView) findViewById(R.id.lv_AddContactList);
        searchKey = (EditText) findViewById(R.id.et_SearchKey);
        // listener for item click
        lv_SerachList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView email = (TextView) view.findViewById(R.id.tv_HiddenEmail);
                       // start FriendProfileFull
                        Intent intent = new Intent(ActivityAddContact.this,ActivityFriendProfile.class);
                        intent.putExtra("Email",email.getText().toString());
                        startActivity(intent);
                    }
                }
        );
    }

    public void btn_SearchClick(View view){
        if (!searchKey.getText().toString().equals("")){
            FindFriendsTask t = new FindFriendsTask();
            t.execute();
        }
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
                List<User> friendList = new ArrayList<>();
                for (Iterator iterator = jsonObjectList.keys(); iterator.hasNext(); ) {
                    String key = (String) iterator.next();
                    JSONObject item = jsonObjectList.getJSONObject(key);
                    User f = new User();
                    f.Email = item.getString("Email");
                    f.FirstName = item.getString("FirstName");
                    f.LastName = item.getString("LastName");
                    String serKey = Tools.encodeString(searchKey.getText().toString()).toLowerCase();

                    if (f.Email.toLowerCase().contains(serKey) || f.FirstName.toLowerCase().contains(serKey) ||f.LastName.toLowerCase().contains(serKey)  ){
                        friendList.add(f);
                    }

                }
                ListAdapter adp = new FriendListAdapter(ActivityAddContact.this, friendList);
                lv_SerachList.setAdapter(adp);
                pb.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }



}
