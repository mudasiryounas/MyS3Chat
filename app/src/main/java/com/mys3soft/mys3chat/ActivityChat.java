package com.mys3soft.mys3chat;


import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.Message;
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
import java.util.List;
import java.util.Map;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


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
        List<Message> chatList = db.getChat(user.Email, friendEmail);
        for (Message item : chatList) {
            int messageType = item.FromMail.equals(user.Email) ? 1 : 2;
            appendMessage(item.Message, item.SentDate, messageType);
        }

        this.setTitle(extras.getString("FriendFullName"));
        final String ENDPOINT = "https://mys3chat.firebaseio.com/messages/";
//        reference1 = new Firebase(ENDPOINT + user.Email + "_" + friendEmail);
//        reference2 = new Firebase(ENDPOINT + friendEmail + "_" + user.Email);
        reference1 = new Firebase(ENDPOINT + user.Email + "-@@-" + friendEmail);
        reference2 = new Firebase(ENDPOINT + friendEmail + "-@@-" + user.Email);
        refFriend = new Firebase(StaticInfo.UsersURL + "/" + friendEmail);
        refNotMess = new Firebase("https://mys3chat.firebaseio.com/messagenotificatins/" + friendEmail);
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                if (!dataSnapshot.getKey().equals(StaticInfo.TypingStatus)) {
                    Map map = dataSnapshot.getValue(Map.class);
                    String mess = map.get("Message").toString();
                    String senderEmail = map.get("SenderEmail").toString();
                    String sentDate = map.get("SentDate").toString();
                    try {
                        // remove from server
                        reference1.child(dataSnapshot.getKey()).removeValue();
                        // save message on local db
                        db.saveMessageOnLocakDB(senderEmail, user.Email, mess, sentDate);
                        if (senderEmail.equals(user.Email)) {
                            // login user
                            appendMessage(mess, sentDate, 1);
                        } else {
                            appendMessage(mess, sentDate, 2);
                        }
                    } catch (Exception e) {

                    }
                } else {
                    // show typing status
                    String typingStatus = dataSnapshot.getValue().toString();
                    if (typingStatus.equals("Typing")) {
                        getSupportActionBar().setSubtitle(typingStatus + "...");
                    }
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String typingStatus = dataSnapshot.getValue().toString();
                if (typingStatus.equals("Typing")) {
                    getSupportActionBar().setSubtitle(typingStatus + "...");
                } else {
                    // check if online
                    getSupportActionBar().setSubtitle("Online");
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //layout.removeAllViews();
                if (dataSnapshot.getKey().equals("TypingStatus")) {
                    getSupportActionBar().setSubtitle("Online");

                }
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
                if (dataSnapshot.getKey().equals("Status")) {
                    // check if subtitle is not Typing
                    CharSequence subTitle = getSupportActionBar().getSubtitle();
                    if (subTitle != null) {
                        if (!subTitle.equals("Typing...")) {
                            String friendStatus = dataSnapshot.getValue().toString();
                            if (!friendStatus.equals("Online")) {
                                friendStatus = Tools.lastSeenProper(friendStatus);
                            }
                            getSupportActionBar().setSubtitle(friendStatus);
                        }
                    } else {
                        String friendStatus = dataSnapshot.getValue().toString();
                        if (!friendStatus.equals("Online")) {
                            friendStatus = Tools.lastSeenProper(friendStatus);
                        }
                        getSupportActionBar().setSubtitle(friendStatus);
                    }


                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                String friendStatus = dataSnapshot.getValue().toString();
                if (!friendStatus.equals("Online")) {
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

        messageArea.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (messageArea.getText().toString().length() == 0) {
                    reference2.child(StaticInfo.TypingStatus).setValue("");
                } else if (messageArea.getText().toString().length() == 1) {
                    reference2.child(StaticInfo.TypingStatus).setValue("Typing");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        View rootView = findViewById(R.id.rootLayout);
        EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.et_Message);
        ImageView emojiImageView = (ImageView) findViewById(R.id.emoji_btn);

        EmojIconActions emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView,"#1c2764","#e8e8e8","#f4f4f4");
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onKeyboardClose() {

            }
        });


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

            DateFormat dateFormat = new SimpleDateFormat("dd MM yy hh:mm a");
            Date date = new Date();
            String sentDate = dateFormat.format(date);

            map.put("SentDate", sentDate);
            //reference1.push().setValue(map);
            reference2.push().setValue(map);
            refNotMess.push().setValue(map);

            // save in local db
            db.saveMessageOnLocakDB(user.Email, friendEmail, message, sentDate);

            // appendmessage
            appendMessage(message, sentDate, 1);

        }
    }

    public void appendMessage(String mess, String sentDate, int messType) {

        EmojiconTextView textView = new EmojiconTextView(this);
        textView.setEmojiconSize(35);


        // TextView textView = new TextView(ActivityChat.this);

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
        // 1 user
        if (messType == 1) {
            textView.setBackgroundResource(R.drawable.messagebg1);
            textView.setPadding(18, 18, 18, 18);
            lp.gravity = Gravity.RIGHT;

        }
        //  2 friend
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
        reference1 = null;

    }

    @Override
    protected void onStop() {
        super.onStop();
        StaticInfo.UserCurrentChatFriendEmail = "";
        reference1 = null;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_deleteConservation) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure to permanently delete this chat this cannot be undone?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            db.deleteChat(user.Email, friendEmail);
                            layout.removeAllViews();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        if (id == R.id.menu_deleteContact) {
            new AlertDialog.Builder(this)
                    .setMessage("Are you sure to permanently delete this contact this cannot be undone?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Firebase ref = new Firebase(StaticInfo.EndPoint + "/friends/" + user.Email + "/" + friendEmail);
                            ref.removeValue();
                            // delete from local database
                            db.deleteFriendByEmailFromLocalDB(friendEmail);
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            return true;
        }
        return true;
    }


}
