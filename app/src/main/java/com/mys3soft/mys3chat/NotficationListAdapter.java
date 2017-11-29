package com.mys3soft.mys3chat;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.NotificationModel;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;
import com.mys3soft.mys3chat.Services.LocalUserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotficationListAdapter extends ArrayAdapter<NotificationModel> {

    private Context con;
    private ImageButton acceptBtn;
    private ImageButton rejectBtn;


    public NotficationListAdapter(@NonNull Context context, List<NotificationModel> list) {
        super(context, R.layout.custom_notication_row, list);
        con = context;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_notication_row, parent, false);
        NotificationModel model = getItem(position);
        // get layout
        LinearLayout layout = (LinearLayout) customView.findViewById(R.id.layout_CustomNotificationRow);

        // make components according to model and append to layout

        TextView tv_NotficationMessage = (TextView) customView.findViewById(R.id.tv_NotificationMessage);
        tv_NotficationMessage.setText(model.NotificationMessage);

        // friend request
        if (model.NotificationType == 1) {
            // make button and append
//            acceptBtn = new Button(getContext());
//            rejectBtn = new Button(getContext());

            acceptBtn = new ImageButton(getContext());
            rejectBtn = new ImageButton(getContext());

            acceptBtn.setBackgroundColor(Color.TRANSPARENT);
            rejectBtn.setBackgroundColor(Color.TRANSPARENT);

            acceptBtn.setImageResource(R.drawable.emoji_2705);
            rejectBtn.setImageResource(R.drawable.emoji_274c);

            setCustomOnClick(acceptBtn, model.EmailFrom, model.FirstName, model.LastName);
            onRejectClick(rejectBtn, position, model.FirstName + " " + model.LastName);
            // set layout params
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.gravity = Gravity.CENTER;

            acceptBtn.setLayoutParams(layoutParams);
            rejectBtn.setLayoutParams(layoutParams);
            acceptBtn.setPadding(4, 4, 4, 4);
            rejectBtn.setPadding(4, 4, 4, 4);
            layout.addView(acceptBtn);
            layout.addView(rejectBtn);
        }
        return customView;
    }


    private void setCustomOnClick(final ImageButton btn, final String friendEmail, final String friendFirstName, final String friendLastName) {

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = LocalUserService.getLocalUserFromPreferences(con);
                        // add to friends and remove from requests
                        Firebase fireBase = new Firebase(StaticInfo.FriendsURL);
                        // set each other friends

                        Map<String, String> map1 = new HashMap<>();
                        map1.put("Email", friendEmail);
                        map1.put("FirstName", friendFirstName);
                        map1.put("LastName", friendLastName);
                        fireBase.child(user.Email).child(friendEmail).setValue(map1);

                        Map<String, String> map2 = new HashMap<>();
                        map2.put("Email", user.Email);
                        map2.put("FirstName", user.FirstName);
                        map2.put("LastName", user.LastName);
                        fireBase.child(friendEmail).child(user.Email).setValue(map2);

                        Firebase frRequ = new Firebase(StaticInfo.EndPoint + "/friendrequests");
                        frRequ.child(user.Email).child(friendEmail).removeValue();
                        acceptBtn.setEnabled(false);

                        Toast.makeText(con, "Accepted", Toast.LENGTH_SHORT).show();
                        rejectBtn.setEnabled(false);

                        Map<String, String> notMap = new HashMap<String, String>();
                        notMap.put("SenderEmail", user.Email);
                        notMap.put("FirstName", user.FirstName);
                        notMap.put("LastName", user.LastName);
                        notMap.put("Message", "Contact request accepted start chating... ");
                        // accepted contact reques
                        notMap.put("NotificationType", "3");
                        Firebase notRef = new Firebase(StaticInfo.NotificationEndPoint + "/" + friendEmail);
                        notRef.push().setValue(notMap);
                    }
                }
        );


    }

    private void onRejectClick(final ImageButton btn, final int modelPosition, final String friedFullName) {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(con)
                        .setTitle(friedFullName)
                        .setMessage("Are you sure to reject this contact request?")
                        .setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                User user = LocalUserService.getLocalUserFromPreferences(con);
                                Firebase fireBase = new Firebase(StaticInfo.FriendRequestsEndPoint + "/" + user.Email + "/" + getItem(modelPosition).FriendRequestFireBaseKey);
                                fireBase.removeValue();
                                rejectBtn.setEnabled(false);
                                acceptBtn.setEnabled(false);
                                Toast.makeText(con, "Rejected", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });

    }
}
