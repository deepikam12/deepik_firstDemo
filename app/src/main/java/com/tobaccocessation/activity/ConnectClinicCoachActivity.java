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

public class ConnectClinicCoachActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_connect, tv_yes, tv_no;
    ImageView iv_back_arrow, iv_video_ic, iv_chat_ic;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;
    Utils utils;
    String errorType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.input_type_yes_no);
        intializeids();
        utils = new Utils(this);
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(ConnectClinicCoachActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

    }

    private void intializeids() {
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_chat_ic = findViewById(R.id.iv_chat);
        iv_video_ic = findViewById(R.id.iv_video);
        tv_connect = findViewById(R.id.tv_question_clinic_paitent);
        tv_yes = findViewById(R.id.tv_yes);
        tv_no = findViewById(R.id.tv_no);
        tv_connect.setText(getResources().getString(R.string.coach_connect));

        tv_connect.setOnClickListener(this);
        tv_no.setOnClickListener(this);
        tv_yes.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
        iv_video_ic.setOnClickListener(this);
        iv_chat_ic.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_yes:
                Intent i = new Intent(ConnectClinicCoachActivity.this, ContactToCoachActivity.class);
                startActivity(i);

                break;

            case R.id.tv_no:
                i = new Intent(ConnectClinicCoachActivity.this, ContactToCoachActivity.class);
                startActivity(i);

                break;

            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.iv_video:
                i = new Intent(ConnectClinicCoachActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }

    }
}
