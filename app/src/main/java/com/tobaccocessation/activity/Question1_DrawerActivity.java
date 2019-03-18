package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.tobaccocessation.model.QuestionAnswer;

import java.util.ArrayList;


public class Question1_DrawerActivity extends AppCompatActivity implements View.OnClickListener {
    TextView msg, tv_next, tv_message;
    ImageView img_cancel, iv_contact_us, iv_video, iv_chat;
    ArrayList<QuestionAnswer> qna;
    Utils utils;
    NotificationModel notificationModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1__drawer);
        utils = new Utils(this);
        findviewIds();
        set_Font();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(Question1_DrawerActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

    }

    private void findviewIds() {
        tv_message = findViewById(R.id.tv_empty);
        msg = (TextView) findViewById(R.id.msg);
        tv_next = (TextView) findViewById(R.id.tv_next);
        img_cancel = (ImageView) findViewById(R.id.img_cancel);
        iv_contact_us = findViewById(R.id.iv_contact_us);
//        iv_video = findViewById(R.id.iv_video);
//        iv_chat = findViewById(R.id.iv_chat);
        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        iv_contact_us.setOnClickListener(this);
        tv_next.setOnClickListener(this);
//        iv_chat.setOnClickListener(this);
//        iv_video.setOnClickListener(this);
    }

    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        Typeface tf1 = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");

        msg.setTypeface(tf);
        tv_next.setTypeface(tf1);
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

            case R.id.iv_contact_us:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@clevelandandcleaningcoach.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "support from the app");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                else
                    utils.showAlertText(Question1_DrawerActivity.this, tv_message, getResources().getString(R.string.gmail_package));

                // Toast.makeText(Question1_DrawerActivity.this, "Gmail App is not installed", Toast.LENGTH_SHORT).show();
                break;

            case R.id.tv_next:
                finish();
                break;
            case R.id.iv_video:
                Intent i = new Intent(Question1_DrawerActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }


    }
}

