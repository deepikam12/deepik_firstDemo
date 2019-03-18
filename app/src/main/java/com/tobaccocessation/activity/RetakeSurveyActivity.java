package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RetakeSurveyActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_retake, tv_contact_coach;
    ArrayList<QuestionAnswer> qnaList;
    static List<AnwerQuestionList> ans_id;
    ImageView iv_video, iv_chat, iv_back_arrow;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_retake_survey);
        initializeIds();
        eventListner();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(RetakeSurveyActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

    }

    private void initializeIds() {

        tv_retake = findViewById(R.id.tv_retake_survey);
        tv_contact_coach = findViewById(R.id.tv_contact_coach);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        qnaList = new ArrayList<>();
        ans_id = new ArrayList<>();
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");
            //ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void eventListner() {
        tv_retake.setOnClickListener(this);
        tv_contact_coach.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
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

            case R.id.tv_retake_survey:
                String ques_id = qnaList.get(0).getId();
                qnaList.remove(0);
                Intent i = new Intent(RetakeSurveyActivity.this, Question2Activity.class);
                i.putExtra("questionList", qnaList);
                i.putExtra("ques_id", ques_id);
                // i.putExtra("comeFrom", "retake");
                i.putExtra("ansList", (Serializable) ans_id);
                i.putExtra("name", pref.getString("first_name", ""));
                startActivity(i);
                break;

            case R.id.tv_contact_coach:
                i = new Intent(RetakeSurveyActivity.this, ContactToCoachActivity.class);
                startActivity(i);
                break;

            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.iv_video:
                i = new Intent(RetakeSurveyActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }
    }


}