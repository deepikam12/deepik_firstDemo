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

public class VideoStatusActivity extends AppCompatActivity {
    String isLogin, errorTyp, connectType, coach_avail, connectionType;
    TextView tv_setStatus;
    SharedPreferences pref;
    Utils utils;
    SharedPreferences.Editor editor;
    ImageView iv_back_arrow, iv_video_ic, iv_chat_ic, iv_smiley;
    NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_status);

        utils = new Utils(this);
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(VideoStatusActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

        initializeIds();
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = pref.getBoolean("isbooleanVideo", false);
        boolean isChat = pref.getBoolean("isbooleanChat", false);
        if (isVideo == true) {
            iv_video_ic.setImageResource(R.drawable.video_icon);
        } else if (isChat == true) {
            iv_chat_ic.setImageResource(R.drawable.chat_icon);
        }

    }

    //broadcast receiver for video chat calling
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                notificationModel = (NotificationModel) intent.getSerializableExtra("notificationData");
                if (notificationModel.getMethod().equalsIgnoreCase("video")) {
                    iv_video_ic.setImageResource(R.drawable.video_icon);
                    editor.putBoolean("isbooleanVideo", true);
                    editor.commit();
                } else if (notificationModel.getMethod().equalsIgnoreCase("chat")) {
                    iv_chat_ic.setImageResource(R.drawable.chat_icon);
                    editor.putBoolean("isbooleanChat", true);
                    editor.commit();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    private void initializeIds() {
        tv_setStatus = findViewById(R.id.tv_video_chat_status);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_chat_ic = findViewById(R.id.iv_chat);
        iv_video_ic = findViewById(R.id.iv_video);
        iv_smiley = findViewById(R.id.iv_smiley);


        isLogin = String.valueOf(pref.getBoolean("isLogin", false));
        connectionType = pref.getString("connection_type", null);
        connectType = pref.getString("connect_type", null);
        coach_avail = String.valueOf(pref.getBoolean("isbooleanVideo", false));
        callVideoChatIcon(connectType, "video");

    }


    private void callVideoChatIcon(String connectType, String type) {
        if (!isLogin.equalsIgnoreCase("true")) {
            tv_setStatus.setText(getResources().getString(R.string.video_status_islogin));
        } else if (!connectType.equalsIgnoreCase("video")) {
            tv_setStatus.setText(getResources().getString(R.string.video_status_wrong_mode)+" "+connectType);

        } else if (connectType.equalsIgnoreCase(""))
            tv_setStatus.setText(getResources().getString(R.string.video_status_no_mode_selected));

        else if (!coach_avail.equalsIgnoreCase("true")) {
//            tv_setStatus.setText(getResources().getString(R.string.video_status_no_coach));
//            iv_smiley.setBackgroundResource(R.drawable.navi_talking);
            Intent i = new Intent(VideoStatusActivity.this, NoCoachAvailableScreen.class);
            i.putExtra("connect_type", type);
            startActivity(i);
            finish();
        } else {
            Intent i = new Intent(VideoStatusActivity.this, ContactVideoChatActivity.class);
            i.putExtra("connect_type", type);
            startActivity(i);
            finish();
        }
        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
