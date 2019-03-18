package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.model.NotificationModel;

public class NoCoachAvailableScreen extends AppCompatActivity {
    ImageView iv_back_arrow, iv_video, iv_chat;
    SharedPreferences pref;
    NotificationModel notificationModel;
    SharedPreferences.Editor editor;
    String connectType;
    TextView tv_video_chat_status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_coach_available_screen);
        initializeIds();
    }

    private void initializeIds() {
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        connectType = getIntent().getStringExtra("connect_type");
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        tv_video_chat_status = findViewById(R.id.tv_video_chat_status);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        if (connectType.equalsIgnoreCase("chat")) {
            tv_video_chat_status.setText(getResources().getString(R.string.chat_status_no_coach));
        } else
            tv_video_chat_status.setText(getResources().getString(R.string.video_status_no_coach));


        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NoCoachAvailableScreen.this, Landing_3Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();
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
        //  super.onBackPressed();
        Intent intent = new Intent(NoCoachAvailableScreen.this, Landing_3Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
