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
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;

import java.io.Serializable;
import java.util.ArrayList;

public class AskCreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_yes, tv_no;
    ArrayList<AnwerQuestionList> ans_id;
    String id, name;
    ImageView iv_back_arrow, iv_video_ic, iv_chat_ic;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_create_account);
        ans_id = new ArrayList<AnwerQuestionList>();
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(AskCreateAccountActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

        eventListner();

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
        try {
            iv_back_arrow = findViewById(R.id.iv_back_arrow);
            iv_video_ic = findViewById(R.id.iv_video);
            iv_chat_ic = findViewById(R.id.iv_chat);
            id = getIntent().getStringExtra("ques_id");
            name = getIntent().getStringExtra("name");
            ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");

        } catch (Exception e) {
            e.printStackTrace();
        }

        tv_yes = findViewById(R.id.tv_yes);
        tv_no = findViewById(R.id.tv_no);
    }

    private void eventListner() {
        tv_yes.setOnClickListener(this);
        tv_no.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
        iv_chat_ic.setOnClickListener(this);
        iv_video_ic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_yes:
                Intent i = new Intent(AskCreateAccountActivity.this, CreateAccountActivity.class);
                i.putExtra("ansList", (Serializable) ans_id);
                i.putExtra("name", name);
                i.putExtra("ques_id", id);
                startActivity(i);
                break;

            case R.id.tv_no:

                i = new Intent(AskCreateAccountActivity.this, FinalConnectingCoachActivity.class);
                i.putExtra("ask_create_account", "create_account");
                startActivity(i);
                break;
            case R.id.iv_back_arrow:
                finish();
                break;
        }
    }
}
