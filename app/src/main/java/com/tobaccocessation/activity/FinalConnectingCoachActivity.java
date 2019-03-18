package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.model.NotificationModel;

public class FinalConnectingCoachActivity extends AppCompatActivity {
    ImageView iv_contact_us, iv_navi_drawer, iv_navi, iv_video, iv_chat;
    TextView tv_message;
    Utils utils;
    NotificationModel notificationModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_connecting_coach);
        utils = new Utils(this);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(FinalConnectingCoachActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();
    }

    private void initializeIds() {
        iv_contact_us = findViewById(R.id.iv_contact_us_final);
        tv_message = findViewById(R.id.tv_empty);
        iv_navi = findViewById(R.id.iv_navi);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
//        if(getIntent().getStringExtra("ask_create_account").equalsIgnoreCase("create_account")){
//        }
        iv_contact_us.setVisibility(View.GONE);

        iv_contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);

                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@clevelandandcleaningcoach.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "support from the app");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                else
                    utils.showAlertText(FinalConnectingCoachActivity.this, tv_message, getResources().getString(R.string.gmail_package));

            }
        });

        iv_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinalConnectingCoachActivity.this, Landing_3Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();
            }
        });
        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FinalConnectingCoachActivity.this, VideoStatusActivity.class);
                startActivity(i);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = pref.getBoolean("isbooleanVideo", false);
        boolean isChat = pref.getBoolean("isbooleanChat", false);
        if (isVideo == true) {
            iv_video.setImageResource(R.drawable.video_icon);
        } else if (isChat == true) {
            iv_chat.setImageResource(R.drawable.chat_icon);
        }

    }

    //broadcast receiver for video chat calling
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {

                notificationModel = (NotificationModel) intent.getSerializableExtra("notificationData");
                if (notificationModel.getMethod().equalsIgnoreCase("video")) {
                    iv_video.setImageResource(R.drawable.video_icon);
                    editor.putBoolean("isbooleanVideo", true);
                    editor.commit();

                } else if (notificationModel.getMethod().equalsIgnoreCase("chat")) {
                    iv_chat.setImageResource(R.drawable.chat_icon);
                    editor.putBoolean("isbooleanChat", true);
                    editor.commit();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent intent = new Intent(FinalConnectingCoachActivity.this, Landing_3Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
