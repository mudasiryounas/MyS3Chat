package com.mys3soft.mys3chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.mys3soft.mys3chat.Services.DataContext;

public class MainActivity extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // check if user exists in local db
        if (!db.doesUserExistsInLocalDB()) {
            // send to activitylogin
            Intent intent = new Intent(this, ActivityLogin.class);
            startActivity(intent);
        } else {
            // set last msgs
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
            if(db.deleteUserInLocalDB()){
                System.exit(1);
            }else {
                System.exit(1);
            }
            return true;
        }
        if (id == R.id.menu_profile) {
            startActivity(new Intent(this,ActivityProfile.class));

        }

        if (id == R.id.menu_contacts) {
            startActivity(new Intent(this,ActivityFriendList.class));

        }

        if (id == R.id.menu_notification) {
            startActivity(new Intent(this,ActivityNotifications.class));
            return true;
        }
        return true;// super.onOptionsItemSelected(item);
    }


}
