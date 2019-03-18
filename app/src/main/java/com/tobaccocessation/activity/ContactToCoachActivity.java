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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.model.NotificationModel;

public class ContactToCoachActivity extends AppCompatActivity implements View.OnClickListener {
    RadioGroup rg_option;
    String selectedText = "";
    ImageView iv_back_arrow, iv_video, iv_chat;
    TextView tv_next, tv_message;
    Utils utils;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_to_coach);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(ContactToCoachActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
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

        rg_option = findViewById(R.id.rg_option);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_chat = findViewById(R.id.iv_chat);
        iv_video = findViewById(R.id.iv_video);
        tv_next = findViewById(R.id.tv_next);
        tv_message = findViewById(R.id.tv_empty);
        rg_option.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
        selectContactOptionToCoach();


    }

    private void selectContactOptionToCoach() {
        rg_option.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(id);

                selectedText = rb.getText().toString();


            }


        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_next:
                if (!selectedText.equalsIgnoreCase("")) {
//                    editor = pref.edit();
//                    editor.putString("connect_type", selectedText);

                    if (selectedText.equalsIgnoreCase("Call You")) {
                        Intent intent = new Intent(ContactToCoachActivity.this, EmailPhoneNumberInputActivity.class);

                        intent.putExtra("type", "call");
                        // intent.putExtra("call",preferences.getString(""))
                        startActivity(intent);

                    } else if (selectedText.equalsIgnoreCase("Email You")) {
                        Intent intent = new Intent(ContactToCoachActivity.this, EmailPhoneNumberInputActivity.class);
                        intent.putExtra("type", "email");
                        startActivity(intent);

                    } else if (selectedText.equalsIgnoreCase("Chat from this app")) {
                        // Toast.makeText(ContactToCoachActivity.this, getResources().getString(R.string.feature_val), Toast.LENGTH_SHORT).show();
                        // utils.showAlertText(ContactToCoachActivity.this,tv_message, getResources().getString(R.string.feature_val));
                        Intent intent = new Intent(ContactToCoachActivity.this, ConnectingThroughCallEmail.class);
                        intent.putExtra("connect_type", "chat");
                        startActivity(intent);

                    } else if (selectedText.equalsIgnoreCase("Video Chat")) {
                        //  utils.showAlertText(ContactToCoachActivity.this,tv_message, getResources().getString(R.string.feature_val));
                        Intent intent = new Intent(ContactToCoachActivity.this, ConnectingThroughCallEmail.class);
                        intent.putExtra("connect_type", "video");
                        startActivity(intent);
                        //  Toast.makeText(ContactToCoachActivity.this, getResources().getString(R.string.feature_val), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Toast.makeText(ContactToCoachActivity.this, getResources().getString(R.string.select_val), Toast.LENGTH_SHORT).show();
                    utils.showAlertText(ContactToCoachActivity.this, tv_message, getResources().getString(R.string.select_val));

                }
                break;

            case R.id.iv_back_arrow:
                Intent i = new Intent(ContactToCoachActivity.this, Landing_3Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();
                break;

            case R.id.iv_video:
                i = new Intent(ContactToCoachActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent intent = new Intent(ContactToCoachActivity.this, Landing_3Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
