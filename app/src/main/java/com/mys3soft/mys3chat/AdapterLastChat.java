package com.mys3soft.mys3chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.Message;
import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.Tools;

import java.util.List;



public class AdapterLastChat extends ArrayAdapter<Message> {

    public AdapterLastChat(@NonNull Context context, List<Message> messageList) {
        super(context, R.layout.custom_lastchat_row, messageList);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_lastchat_row, parent, false);
        Message message = getItem(position);
        TextView hiddenEmail = (TextView) customView.findViewById(R.id.tv_lastChat_HiddenEmail);
        TextView tv_Name = (TextView) customView.findViewById(R.id.tv_lastChat_FriendFullName);
        TextView tv_MessageDate = (TextView) customView.findViewById(R.id.tv_lastChat_MessageDate);
        TextView tv_Message = (TextView) customView.findViewById(R.id.tv_lastChat_Message);
        hiddenEmail.setText(String.valueOf(message.FromMail));
        tv_Name.setText(message.FriendFullName);
        String properDate = Tools.messageSentDateProper(message.SentDate);
        tv_MessageDate.setText(properDate);
        if (message.Message.length() > 20){
            message.Message = message.Message.substring(0,20);
        }
        tv_Message.setText(message.Message);
        return customView;
    }

}
