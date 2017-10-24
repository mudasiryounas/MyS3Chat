package com.mys3soft.mys3chat;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.ListFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.mys3soft.mys3chat.Models.NotificationModel;
import com.mys3soft.mys3chat.Models.StaticInfo;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.DataContext;

import java.util.List;

public class NotficationListAdapter extends ArrayAdapter<NotificationModel> {

    DataContext db = new DataContext(getContext(), null, null, 1);



    public NotficationListAdapter(@NonNull Context context, List<NotificationModel> list) {
        super(context, R.layout.custom_notication_row, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_notication_row,parent,false);
        NotificationModel model = getItem(position);
        // get layout
        LinearLayout layout = (LinearLayout) customView.findViewById(R.id.layout_CustomNotificationRow);

        // make components according to model and append to layout

        TextView tv_NotficationMessage = (TextView) customView.findViewById(R.id.tv_NotificationMessage);
        tv_NotficationMessage.setText(model.NotificationMessage);

        // friend request
        if (model.NotificationType == 1){
            // make button and append
            Button acceptBtn = new Button(getContext());
            acceptBtn.setText("Accept");

            setCustomOnClick(acceptBtn,model.EmailFrom);
            // set layout params
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );

            acceptBtn.setLayoutParams(layoutParams);
            layout.addView(acceptBtn);
        }
        return customView;
    }



    private void setCustomOnClick(final Button btn, final String email){

        btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = db.getLocalUser();
                        // add to friends and remove from requests
                        Firebase fireBase = new Firebase(StaticInfo.FriendsURL);
                        // set each other friends
                        fireBase.child(user.Email).child(email).setValue("");
                        fireBase.child(email).child(user.Email).setValue("");
                    }
                }
        );


    }

}
