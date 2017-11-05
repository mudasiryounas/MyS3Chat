package com.mys3soft.mys3chat;


import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;

import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class ActivityChat extends AppCompatActivity {


    DataContext db = new DataContext(this, null, null, 1);
    EditText messageArea;
    ScrollView scrollView;
    LinearLayout layout;
    Firebase reference1, reference2, refNotMess;
    User user;
    String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageArea = (EditText) findViewById(R.id.et_Message);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (LinearLayout) findViewById(R.id.layout1);
        user = LocalUserService.getLocalUserFromPreferences(this);
        Firebase.setAndroidContext(this);
        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("FriendEmail");
        this.setTitle(extras.getString("FriendFullName"));
        final String ENDPOINT = "https://mys3chat.firebaseio.com/messages/";
        reference1 = new Firebase(ENDPOINT + user.Email + "_" + friendEmail);
        reference2 = new Firebase(ENDPOINT + friendEmail + "_" + user.Email);
        refNotMess = new Firebase("https://mys3chat.firebaseio.com/messagenotificatins/" + friendEmail);
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                String mess = map.get("Message").toString();
                String senderEmail = map.get("SenderEmail").toString();
                if (senderEmail.equals(user.Email)) {
                    // login user
                    appendMessage(mess, 1);
                } else {
                    appendMessage(mess, 2);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

                layout.removeAllViews();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
    }


    @Override
    protected void onStart() {

        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("FriendEmail");
        this.setTitle(extras.getString("FriendFullName"));



        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        super.onStart();
    }

    public void btn_SendMessageClick(View view) {

        String message = messageArea.getText().toString();
        messageArea.setText("");
        if (!message.equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("Message", message);
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);
        }
    }

    public void appendMessage(String mess, int messType) {

        TextView textView = new TextView(ActivityChat.this);

        textView.setText(mess);
        textView.setTextColor(Color.parseColor("#000000"));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                6f
        );
        lp.setMargins(0, 0, 0, 10);
        // green
        if (messType == 1) {
            textView.setBackgroundResource(R.drawable.messagebg1);
            textView.setPadding(18, 18, 18, 18);
            lp.gravity = Gravity.RIGHT;

        }
        //  white
        else {
            textView.setBackgroundResource(R.drawable.messagebg2);
            textView.setPadding(18, 18, 18, 18);

            lp.gravity = Gravity.LEFT;
        }

        textView.setLayoutParams(lp);
        layout.addView(textView);
        //scrollView.fullScroll(View.FOCUS_DOWN);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        StaticInfo.UserCurrentChatFriendEmail = "";

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StaticInfo.UserCurrentChatFriendEmail = "";
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.menu_deleteConservation) {
            reference1.removeValue();
            return true;
        }
        if (id == R.id.menu_deleteContact) {
            Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + friendEmail);
            ref.removeValue();
            // delete from local database
            db.deleteFriendByEmailFromLocalDB(friendEmail);
            finish();
        }
        return true;
    }
}
