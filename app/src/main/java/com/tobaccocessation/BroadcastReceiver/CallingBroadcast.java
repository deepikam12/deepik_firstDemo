package com.tobaccocessation.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.activity.IncomingCallViewActivity;

/**
 * Created by Deepika.Mishra on 1/7/2019.
 */

public class CallingBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            /*context.sendBroadcast(new Intent("com.example.INCOMING_CALL"));

            if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                // notification received
                Intent i=new Intent(context, IncomingCallViewActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }*/

            Intent i=new Intent(context, IncomingCallViewActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
    }
}
