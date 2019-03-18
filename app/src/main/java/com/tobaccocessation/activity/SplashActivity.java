package com.tobaccocessation.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.tobaccocessation.BroadcastReceiver.CallingIntentService;
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.chat.MainChatActivity;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    SharedPreferences pref;
    Activity activity;
    String loginStatus = "", connectType;
    LinearLayout ll_header;
    Button btn_start;
    NotificationModel notificationModel;
    Utils utils;
    JSONObject json;
    Boolean isLogin;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        activity = SplashActivity.this;
        utils = new Utils(activity);
        btn_start = findViewById(R.id.btn_start);
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        editor = sharedPreferences.edit();
        //For background service.


        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SplashActivity.this, Landing_3Activity.class);
                startActivity(i);

            }
        });
        pref = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        isLogin = pref.getBoolean("isLogin", false);
        if (isLogin == true) {
            if (!isMyServiceRunning(CallingIntentService.class)) {
                pref = getSharedPreferences(Config.SHARED_PREF, 0);

                connectType = pref.getString("connect_type", "");
                if (connectType.equalsIgnoreCase("video")) {
//                    if (pref.contains("VideoJson")) {
                    String VideoJson = pref.getString("VideoJson", "");
                    Intent serviceIntent = new Intent(getApplicationContext(), CallingIntentService.class);
                    serviceIntent.putExtra("VideoJson", VideoJson);
                    startService(serviceIntent);

                }

            }
        } else {

        }

        if (getIntent().getExtras() != null) {
                       /* ArrayList<String> ar1=new ArrayList<>();
                        for (String key : getIntent().getExtras().keySet()) {
                            ar1.add(key);
                        }
                        System.out.print(ar1);
                        if(ar1.contains())*/
            if (getIntent().getExtras().containsKey("asset")) {
                //JSONObject jsonObject = new JSONObject(data.get("asset"));
                try {
                    json = new JSONObject(getIntent().getExtras().getString("asset"));
                    notificationModel = new NotificationModel();
                    if (json.has("accessToken") && json.getString("method").equalsIgnoreCase("video")) {
                        notificationModel.setAccessToken_video(json.getString("accessToken"));
                        notificationModel.setMethod(json.getString("method"));
                        notificationModel.setRoomName(json.getString("roomName"));
                        notificationModel.setCoach_id(json.getString("coach_id"));
                        notificationModel.setTitle(json.getString("title"));


                        Intent i = new Intent(SplashActivity.this, IncomingCallViewActivity.class);
                        i.putExtra("notificationData", (Serializable) notificationModel);
                        i.putExtra("comeFrom", (Serializable) "Splash");


                        startActivity(i);
                        finish();
                    }
//                    else if (json.has("method") && json.getString("method").equalsIgnoreCase("chat")) {
//                        notificationModel.setAccessToken_chat(json.getString("accessToken"));
//                        notificationModel.setMethod(json.getString("method"));
//                        notificationModel.setChannel_name(json.getString("GENERAL_CHANNEL_UNIQUE_NAME"));
//                        editor.putString("chat_accessToken", json.getString("accessToken"));
//                        editor.putString("channel_name", json.getString("GENERAL_CHANNEL_UNIQUE_NAME"));
//                        editor.commit();
//
//                        Intent intent = new Intent(this, MainChatActivity.class);
//                        intent.putExtra("notificationData", (Serializable) notificationModel);
//                        intent.putExtra("comeFrom", (Serializable) "Splash");
//                        startActivity(intent);
//                        finish();


                   // }
                    else {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
//                Intent i = new Intent(SplashActivity.this, DashboardActivity.class);
//                startActivity(i);
//                finish();
            }


        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}

//    private BroadcastReceiver receiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            Intent intent2 = new Intent(SplashActivity.this, VideoActivity.class);
//            intent.putExtra("notificationData", (Serializable) notificationModel);
//
//            startActivity(intent2);
//        }
//    };
