package com.mys3soft.mys3chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;

import java.util.HashMap;
import java.util.Map;

public class ActivityChat extends AppCompatActivity {

    DataContext db = new DataContext(this, null, null, 1);


    EditText messageArea;
    ScrollView scrollView;
    LinearLayout layout;
    Firebase reference1, reference2;
    User user;
    String friendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        messageArea = (EditText) findViewById(R.id.et_Message);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        layout = (LinearLayout) findViewById(R.id.layout1);
        user = db.getLocalUser();

        Bundle extras = getIntent().getExtras();
        friendEmail = extras.getString("FriendEmail");

        Firebase.setAndroidContext(this);

        final String ENDPOINT = "https://mys3chat.firebaseio.com/messages/";

        reference1 = new Firebase(ENDPOINT + user.Email + "_" + friendEmail);
        reference2 = new Firebase(ENDPOINT + friendEmail + "_" + user.Email);
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                String mess = map.get("message").toString();
                String email = map.get("sender").toString();
                if (email.equals(user.Email)) {
                    // you
                    appendMessage("You: " + mess, 1);
                } else {
                    //  CurrentChatDetail.ChatWithEmail
                    appendMessage(mess, 2);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    public void btn_SendMessageClick(View view) {

        String message = messageArea.getText().toString();
        messageArea.setText("");

        if (!message.equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("message", message);
            map.put("sender",user.Email);
            reference1.push().setValue(map);
            reference2.push().setValue(map);

        }


    }


    public void appendMessage(String mess, int messType) {

        TextView textView = new TextView(ActivityChat.this);

        textView.setText(mess);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 0, 0, 10);
        lp.gravity = Gravity.BOTTOM;
        textView.setLayoutParams(lp);

        // you
//        if (messType == 1){
//            textView.setBackgroundResource(R.drawable.rounded_corner1);
//        }
//        // other
//        else{
//            textView.setBackgroundResource(R.drawable.rounded_corner2);
//        }

        layout.addView(textView);
        scrollView.fullScroll(View.FOCUS_DOWN);

    }

}
