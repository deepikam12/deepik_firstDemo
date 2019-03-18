package com.tobaccocessation.FCM;

/**
 * Created by HP on 09-09-2017.
 */

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tobaccocessation.BroadcastReceiver.CallingIntentService;
import com.tobaccocessation.R;
import com.tobaccocessation.activity.IncomingCallViewActivity;
import com.tobaccocessation.chat.MainChatActivity;
import com.tobaccocessation.model.NotificationModel;


import org.json.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";
    private static int currentNotificationID = 0;
    SharedPreferences sharedPreferences;
    Context mcontext;
    Intent intent;
    NotificationModel notificationModel;
    PendingIntent pendingIntent;
    SharedPreferences.Editor editor;
    AudioAttributes audioAttributes;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("dataChat", remoteMessage.toString());
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF, 0);
        editor = sharedPreferences.edit();
        try {
            Map<String, String> data = remoteMessage.getData();
            notificationModel = new NotificationModel();

            if (remoteMessage.getData().containsKey("asset")) {
                JSONObject jsonObject = new JSONObject(data.get("asset"));
                if (jsonObject.has("method") && jsonObject.getString("method").equalsIgnoreCase("video")) {
                    notificationModel.setAccessToken_video(jsonObject.getString("accessToken"));
                    notificationModel.setMethod(jsonObject.getString("method"));
                    notificationModel.setRoomName(jsonObject.getString("roomName"));
                    notificationModel.setCoach_id(jsonObject.getString("coach_id"));
                    notificationModel.setTitle(jsonObject.getString("title"));


//                    if (notificationModel.getMethod().equalsIgnoreCase("video")) {
//                        editor.putString("VideoJson", jsonObject.toString());

                    //For broadcast to update UI.
                    Intent Broadcastintent = new Intent();
                    Broadcastintent.setAction("com.INCOMING_CALL.CHAT");
                    Broadcastintent.putExtra("notificationData", (Serializable) notificationModel);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(Broadcastintent);


                    intent = new Intent(this, IncomingCallViewActivity.class);
                    intent.putExtra("notificationData", (Serializable) notificationModel);
                    intent.putExtra("comeFrom", (Serializable) "OnMessage");
                    //startService(intent);

                    //For background service.
                    Intent serviceIntent = new Intent(this, CallingIntentService.class);
                    serviceIntent.putExtra("notificationData", (Serializable) notificationModel);
                    startService(serviceIntent);
                    // }
                }
//                else if (jsonObject.has("method") && jsonObject.getString("method").equalsIgnoreCase("chat")) {
//                    notificationModel.setAccessToken_chat(jsonObject.getString("accessToken"));
//                    notificationModel.setMethod(jsonObject.getString("method"));
//                    notificationModel.setChannel_name(jsonObject.getString("GENERAL_CHANNEL_UNIQUE_NAME"));
////                    notificationModel.setTitle(jsonObject.getString("title"));
//                    editor.putString("chat_accessToken", jsonObject.getString("accessToken"));
//                    editor.putString("channel_name", jsonObject.getString("GENERAL_CHANNEL_UNIQUE_NAME"));
//                    editor.commit();
////                    if (notificationModel.getMethod().equalsIgnoreCase("video")) {
////                        editor.putString("VideoJson", jsonObject.toString());
//
//                    //For broadcast to update UI.
//                    Intent Broadcastintent = new Intent();
//                    Broadcastintent.setAction("com.INCOMING_CALL.CHAT");
//                    Broadcastintent.putExtra("notificationData", (Serializable) notificationModel);
//                    LocalBroadcastManager.getInstance(this).sendBroadcast(Broadcastintent);
//
//
//                    intent = new Intent(this, MainChatActivity.class);
//                    intent.putExtra("notificationData", (Serializable) notificationModel);
//                    intent.putExtra("comeFrom", (Serializable) "OnMessage");
//                    //startService(intent);
////
////                    //For background service.
//                    Intent serviceIntent = new Intent(this, CallingIntentService.class);
//                    serviceIntent.putExtra("notificationData", (Serializable) notificationModel);
//                    startService(serviceIntent);
//               }
                else {
                    intent = new Intent();
                }
            }


            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            String channelId = "Default";
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            //  Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
            //  r.play();

            NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel mChannel;
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////                mChannel = new NotificationChannel("1", "abc", NotificationManager.IMPORTANCE_HIGH);
////                //   mChannel.setLightColor(Color.GRAY);
////                //  mChannel.enableLights(true);
////                // mChannel.setDescription(Utils.CHANNEL_SIREN_DESCRIPTION);
////                AudioAttributes audioAttributes = new AudioAttributes.Builder()
////                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
////                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
////                        .build();
////                mChannel.setSound(soundUri, audioAttributes);
////
////                if (mNotificationManager != null) {
////                    mNotificationManager.createNotificationChannel(mChannel);
////                }
////            }
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//
//                if(soundUri != null) {
//                    // Changing Default mode of notification
//                    //notificationCompatBuilder .setDefaults(Notification.DEFAULT_VIBRATE);
//
//                    // Creating an Audio Attribute
//                   audioAttributes = new AudioAttributes.Builder()
//                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                            .setUsage(AudioAttributes.USAGE_ALARM)
//                            .build();
//
////                    // Creating Channel
////                    NotificationChannel notificationChannel = new NotificationChannel("1", "abc", NotificationManager.IMPORTANCE_HIGH);
////                    notificationChannel.setSound(soundUri, audioAttributes);
////                    mNotificationManager.createNotificationChannel(notificationChannel);
//
//                }}


            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Accept", pendingIntent).build();
            NotificationCompat.Action action1 = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Reject", pendingIntent).build();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)

                    .setSmallIcon(R.drawable.app_icon)
//                    .addAction(action) // #0
//                    .addAction(action1)  // #1

                    .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.app_icon))
                    .setContentTitle(remoteMessage.getNotification().getTitle())
                    .setContentText(remoteMessage.getNotification().getBody())
                  //  .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true).setContentIntent(pendingIntent);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setSound(soundUri);
            }

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (soundUri != null) {
                    // Changing Default mode of notification
                    //notificationCompatBuilder .setDefaults(Notification.DEFAULT_VIBRATE);

                    // Creating an Audio Attribute
                    audioAttributes = new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build();

                    NotificationChannel channel = new NotificationChannel(channelId, "Default channel", NotificationManager.IMPORTANCE_DEFAULT);
                    if(manager.areNotificationsEnabled() && channel.getImportance() != NotificationManager.IMPORTANCE_NONE) {
                        try {
                            Ringtone r = RingtoneManager.getRingtone(this, soundUri);
                            r.play();
                        } catch (Exception e) {
                        }
                    }
                  //  channel.setSound(soundUri, audioAttributes);
                    manager.createNotificationChannel(channel);
                }
            }
            currentNotificationID++;
            int notificationId = currentNotificationID;
            if (notificationId == Integer.MAX_VALUE - 1) {
                notificationId = 0;
            }
//            stopForeground( false );
//            manager.cancel(notificationId);
            manager.notify(notificationId, builder.build());
        } catch (Exception e) {
            Log.e("FIREBASE", "onMessageReceived: " + e.getMessage());
        }
    }
}
