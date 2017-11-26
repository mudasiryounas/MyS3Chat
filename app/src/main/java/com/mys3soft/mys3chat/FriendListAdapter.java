package com.mys3soft.mys3chat;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mys3soft.mys3chat.Models.User;
import com.mys3soft.mys3chat.Services.Tools;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<User> {


    public FriendListAdapter(@NonNull Context context, List<User> contactList) {
        super(context, R.layout.custom_friend_list_row, contactList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_friend_list_row, parent, false);
        User user = getItem(position);
        TextView hiddenEmail = (TextView) customView.findViewById(R.id.tv_HiddenEmail);
        TextView tv_Name = (TextView) customView.findViewById(R.id.tv_FriendFullName);
        hiddenEmail.setText(String.valueOf(user.Email));
        tv_Name.setText(Tools.toProperName(user.FirstName) + " " + Tools.toProperName(user.LastName));
        return customView;
    }

}
