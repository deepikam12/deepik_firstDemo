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

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.model.NotificationModel;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ContactVideoChatActivity extends AppCompatActivity {
    CircularProgressBar circularProgressBar;
    ImageView iv_call, iv_email, iv_chat, iv_chat_ic, iv_video_ic, iv_video, iv_back_arrow;
    TextView tv_cancel, tv_wait, tv_message;
    String connect_type;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_through_call_email);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);

        LocalBroadcastManager.getInstance(ContactVideoChatActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();
        utils = new Utils(this);
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


    private void initializeIds() {
        circularProgressBar = findViewById(R.id.homeloader);
        iv_call = findViewById(R.id.iv_call);
        iv_email = findViewById(R.id.iv_email);
        iv_chat = findViewById(R.id.iv_chat);
        iv_video = findViewById(R.id.iv_video);
        iv_chat_ic = findViewById(R.id.iv_chat_ic);
        iv_video_ic = findViewById(R.id.iv_video_ic);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_message = findViewById(R.id.tv_empty);
        tv_wait = findViewById(R.id.tv_wait);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        connect_type = getIntent().getStringExtra("connect_type");
        if (connect_type.equalsIgnoreCase("chat")) {
            tv_wait.setText(getResources().getString(R.string.chat_conv));
            iv_chat_ic.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_video.setVisibility(View.GONE);

        } else if (connect_type.equalsIgnoreCase("video")) {
            tv_wait.setText(getResources().getString(R.string.video_call));
            iv_video_ic.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_video.setVisibility(View.GONE);

        }


        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactVideoChatActivity.this, FinalConnectingCoachActivity.class);
                startActivity(i);
            }
        });
        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactVideoChatActivity.this, VideoStatusActivity.class);
                startActivity(i);
            }
        });
    }
}
