package com.mys3soft.mys3chat;


import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import com.mys3soft.mys3chat.Services.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

public class ActivityChat extends AppCompatActivity {


    DataContext db = new DataContext(this, null, null, 1);
    EditText messageArea;
    ScrollView scrollView;
    LinearLayout layout;
    Firebase reference1, reference2, refNotMess, refFriend;
    User user;
    String friendEmail;
    Firebase refUser;

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
        refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
        refNotMess = new Firebase("https://mys3chat.firebaseio.com/messagenotificatins/" + friendEmail);
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Map map = dataSnapshot.getValue(Map.class);
                String mess = map.get("Message").toString();
                String senderEmail = map.get("SenderEmail").toString();
                String sentDate = map.get("SentDate").toString();
                if (senderEmail.equals(user.Email)) {
                    // login user
                    appendMessage(mess, sentDate, 1);
                } else {
                    appendMessage(mess, sentDate, 2);
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
        refFriend.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if (dataSnapshot.getKey().equals("Status")){
                String friendStatus = dataSnapshot.getValue().toString();
                if (!friendStatus.equals("Online")){
                    friendStatus = Tools.lastSeenProper(friendStatus);
                }
                getSupportActionBar().setSubtitle(friendStatus);

            }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String friendStatus = dataSnapshot.getValue().toString();
                if (!friendStatus.equals("Online")){
                    friendStatus = Tools.lastSeenProper(friendStatus);
                }
                getSupportActionBar().setSubtitle(friendStatus);
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
        StaticInfo.UserCurrentChatFriendEmail = friendEmail;
        refUser = new Firebase(StaticInfo.UsersURL + "/" + user.Email);

//        messageArea.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                scrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.fullScroll(View.FOCUS_DOWN);
//                    }
//                });
//            }
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
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
        // update status to online
        refUser.child("Status").setValue("Online");
    }

    public void btn_SendMessageClick(View view) {

        String message = messageArea.getText().toString().trim();
        messageArea.setText("");
        if (!message.equals("")) {
            Map<String, String> map = new HashMap<>();
            map.put("Message", message);
            map.put("SenderEmail", user.Email);
            map.put("FirstName", user.FirstName);
            map.put("LastName", user.LastName);

            // DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
            Date date = new Date();

            map.put("SentDate", dateFormat.format(date));
            reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);
        }
    }

    public void appendMessage(String mess, String sentDate, int messType) {

        TextView textView = new TextView(ActivityChat.this);

        Calendar cal = Calendar.getInstance();
        Date todayDate = new Date();
        cal.setTime(todayDate);

        int todayMonth = cal.get(Calendar.MONTH) + 1;
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);

        String[] date = sentDate.split(" ");
        if (todayMonth == Integer.parseInt(date[1]) && todayDay == Integer.parseInt(date[0])) {
            sentDate = "Today" + " " + date[3] + " " + date[4];
            // 06 11 17 12:28 AM
        } else if (todayMonth == Integer.parseInt(date[1]) && (todayDay - 1) == Integer.parseInt(date[0])) {
            sentDate = "Yesterday" + " " + date[3] + " " + date[4];
        } else {
            sentDate = date[0] + " " + Tools.toCharacterMonth(Integer.parseInt(date[1])) + " " + date[2] + " " + date[3] + " " + date[4];
        }

        SpannableString dateString = new SpannableString(sentDate);
        dateString.setSpan(new RelativeSizeSpan(0.7f), 0, sentDate.length(), 0);
        dateString.setSpan(new ForegroundColorSpan(Color.GRAY), 0, sentDate.length(), 0);

        textView.setText(mess + "\n");
        textView.append(dateString);
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
    protected void onDestroy() {
        super.onDestroy();
        StaticInfo.UserCurrentChatFriendEmail = "";
        // set last seen
        DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
        Date date = new Date();
        refUser.child("Status").setValue(dateFormat.format(date));
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
