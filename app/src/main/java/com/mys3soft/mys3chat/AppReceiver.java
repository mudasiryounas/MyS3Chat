package com.mys3soft.mys3chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mys3soft.mys3chat.Services.LocalUserService;

public class AppReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       context.startService(new Intent(context,AppService.class));
    }
}
