package com.mys3soft.mys3chat;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.CatcheUserList;
import com.mys3soft.mys3chat.Models.NotificationModel;
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
import java.util.stream.Collectors;

import retrofit2.Call;

public class ActivityNotifications extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);


    ListView lv_NotificationList;
    ProgressBar pb;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        Firebase.setAndroidContext(this);


        //pb_Loading_L_Notitications
        pb = (ProgressBar) findViewById(R.id.pb_Loading_L_Notitications);
        lv_NotificationList = (ListView) findViewById(R.id.lv_NoticicationList);
        user = db.getLocalUser();

        // fill CathedUserList if empty
        if (CatcheUserList.CatchedUserList.size() < 1){
            FillUserListTask task = new FillUserListTask();
            task.execute();
        }

        NotificationListTask t = new NotificationListTask();
        t.execute();


    }


    public class NotificationListTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(Void... params) {

            IFireBaseAPI api = Tools.makeRetroFitApi();
            Call<String> call = api.getAllFriendRequestsAsJsonString();

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

                List<NotificationModel> notificationList = new ArrayList<>();

                JSONObject userFriendRequests = jsonObjectList.getJSONObject(user.Email);
                for (Iterator iterator = userFriendRequests.keys(); iterator.hasNext(); ) {
                    final String key = (String) iterator.next();
                    NotificationModel not = new NotificationModel();
                    not.EmailFrom = key;
                    not.NotificationType = 1;
                    User fr = null;

                   for (User item:CatcheUserList.CatchedUserList){
                       if (item.Email.equals(key)){
                           fr = item;
                           break;
                       }
                   }
                   if (fr != null){
                       not.NotificationMessage = fr.FirstName + " " + fr.LastName;
                       notificationList.add(not);
                   }

                }

                ListAdapter adp = new NotficationListAdapter(ActivityNotifications.this,notificationList);
                lv_NotificationList.setAdapter(adp);
                pb.setVisibility(View.INVISIBLE);

            } catch (JSONException e) {
                pb.setVisibility(View.INVISIBLE);
                e.printStackTrace();
            }


        }
    }







}
