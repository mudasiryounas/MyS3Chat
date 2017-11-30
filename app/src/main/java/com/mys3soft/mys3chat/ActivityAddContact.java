package com.mys3soft.mys3chat;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.mys3soft.mys3chat.Models.User;
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

public class ActivityAddContact extends AppCompatActivity {


    ListView lv_SerachList;
    EditText searchKey;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        pd = new ProgressDialog(this);
        pd.setMessage("Seraching...");
        lv_SerachList = (ListView) findViewById(R.id.lv_AddContactList);
        searchKey = (EditText) findViewById(R.id.et_SearchKey);
        // listener for item click
        lv_SerachList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TextView email = (TextView) view.findViewById(R.id.tv_HiddenEmail);
                        // start FriendProfileFull
                        Intent intent = new Intent(ActivityAddContact.this, ActivityFriendProfile.class);
                        intent.putExtra("Email", email.getText().toString());
                        startActivity(intent);
                    }
                }
        );
    }

    public void btn_SearchClick(View view) {
        if (!searchKey.getText().toString().trim().equals("") && searchKey.getText().toString().length() > 2) {

            if (Tools.isNetworkAvailable(this)){
                FindFriendsTask t = new FindFriendsTask();
                t.execute();
            }else {
                Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                }

        } else {
            searchKey.setText("");
            Toast.makeText(this, "Input at least 3 characters", Toast.LENGTH_SHORT).show();
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
                pd.hide();
                Toast.makeText(ActivityAddContact.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String jsonListString) {

            try {
                User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
                JSONObject jsonObjectList = new JSONObject(jsonListString);
                List<User> friendList = new ArrayList<>();
                for (Iterator iterator = jsonObjectList.keys(); iterator.hasNext(); ) {
                    try {
                        String key = (String) iterator.next();
                        JSONObject item = jsonObjectList.getJSONObject(key);
                        User f = new User();
                        f.Email = item.getString("Email");
                        f.FirstName = item.getString("FirstName");
                        f.LastName = item.getString("LastName");
                        String serKey = Tools.encodeString(searchKey.getText().toString()).toLowerCase().trim();
                        String fullName = f.FirstName.toLowerCase() + " " + f.LastName.toLowerCase();
                        if (f.Email.toLowerCase().contains(serKey) || fullName.contains(serKey)) {
                            if (!f.Email.equals(user.Email)) {
                                friendList.add(f);
                            }
                        }
                    } catch (Exception exx) {
                        continue;
                    }
                }
                ListAdapter adp = new FriendListAdapter(ActivityAddContact.this, friendList);
                lv_SerachList.setAdapter(adp);
                pd.hide();

            } catch (JSONException e) {
                pd.hide();
                e.printStackTrace();
            }


        }
    }



}
