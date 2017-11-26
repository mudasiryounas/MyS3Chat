package com.mys3soft.mys3chat;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.LocalUserService;
import com.mys3soft.mys3chat.Services.Tools;

import java.util.Map;

public class AppService extends Service {
    public AppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Firebase.setAndroidContext(getApplicationContext());
        User user = LocalUserService.getLocalUserFromPreferences(getApplicationContext());
        final Firebase reference = new Firebase("https://mys3chat.firebaseio.com/messagenotificatins/" + user.Email);
        reference.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Map map = dataSnapshot.getValue(Map.class);
                        String mess = map.get("Message").toString();
                        String senderEmail = map.get("SenderEmail").toString();
                        String friendFullName = Tools.toProperName(map.get("FirstName").toString()) + " " + Tools.toProperName(
                                map.get("LastName").toString());

                        // check if user is on chat activity with senderEmail
                        if (!StaticInfo.UserCurrentChatFriendEmail.equals(senderEmail)) {
                            notifyMessage(senderEmail, friendFullName, mess);
                            // remove notification
                            reference.child(dataSnapshot.getKey()).removeValue();
                        } else {
                            reference.child(dataSnapshot.getKey()).removeValue();
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
                }
        );
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        sendBroadcast(new Intent("com.mys3soft.mys3chat.restartservice"));
    }

    private void notifyMessage(String friendEmail, String friendFullName, String mess) {
        NotificationCompat.Builder not = new NotificationCompat.Builder(getApplicationContext());
        not.setAutoCancel(true);
        not.setSmallIcon(R.mipmap.ic_launcher_round);
        not.setTicker("New Message");
        not.setWhen(System.currentTimeMillis());
        not.setContentTitle(friendFullName);
        not.setContentText(mess);
        Intent i = new Intent(getApplicationContext(), ActivityChat.class);
        i.putExtra("FriendEmail", friendEmail);
        i.putExtra("FriendFullName", friendFullName);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, i, PendingIntent.FLAG_ONE_SHOT);
        not.setContentIntent(pendingIntent);
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        not.setDefaults(Notification.DEFAULT_ALL);
        nm.notify(Tools.createUniqueIdPerUser(friendEmail), not.build());

    }
}
