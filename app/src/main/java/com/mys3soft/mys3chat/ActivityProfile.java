package com.mys3soft.mys3chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import org.w3c.dom.Text;

public class ActivityProfile extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        User user = LocalUserService.getLocalUserFromPreferences(this);
        TextView tv_UserFullName = (TextView) findViewById(R.id.tv_UserFullName);
        tv_UserFullName.setText(Tools.toProperName(user.FirstName) + " " + Tools.toProperName(user.LastName));
    }
}
