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
import com.tobaccocessation.model.NotificationModel;

public class TalkToCoachActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_yes, tv_no;
    ImageView iv_video, iv_chat;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk_to_coach);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(TalkToCoachActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

    }

    private void initializeIds() {
        tv_yes = findViewById(R.id.tv_yes);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        tv_no = findViewById(R.id.tv_no);
        tv_no.setOnClickListener(this);
        tv_yes.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_yes:
                Intent i = new Intent(TalkToCoachActivity.this, ContactToCoachActivity.class);
                startActivity(i);
                break;

            case R.id.tv_no:
                i = new Intent(TalkToCoachActivity.this, FinalConnectingCoachActivity.class);
                startActivity(i);
                break;
            case R.id.iv_video:
                i = new Intent(TalkToCoachActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }
    }
}
