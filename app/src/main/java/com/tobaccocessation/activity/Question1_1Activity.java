package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.Options;
import com.tobaccocessation.model.QuestionAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question1_1Activity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_head, tv_msg, tv_next;
    ImageView iv_navi_pills, iv_chat, iv_video;
    String name, id;
    ArrayList<QuestionAnswer> qna;
    ArrayList<Answers> options;
    static List<AnwerQuestionList> ans_id;
    NotificationModel notificationModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question1_1);
        findviewIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(Question1_1Activity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

        eventListener();
        set_Font();

    }

    private void eventListener() {
        iv_navi_pills.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
    }

    private void findviewIds() {
        iv_chat = findViewById(R.id.iv_chat);
        iv_video = findViewById(R.id.iv_video);
        qna = new ArrayList<>();
        ans_id = new ArrayList<>();


        try {
            qna = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");

            //  options = ( ArrayList<Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }
        iv_navi_pills = (ImageView) findViewById(R.id.iv_back_arrow);
        tv_head = (TextView) findViewById(R.id.tv_head);
        tv_msg = (TextView) findViewById(R.id.tv_msg);
        tv_next = (TextView) findViewById(R.id.tv_next);
        name = getIntent().getStringExtra("name");
        id = getIntent().getStringExtra("ques_id");


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


    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        tv_head.setTypeface(tf);
        tv_msg.setTypeface(tf);

        Typeface tyf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");
        tv_next.setTypeface(tyf);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back_arrow:
                finish();
                break;

            case R.id.tv_next:
                Intent i = new Intent(Question1_1Activity.this, Question2Activity.class);
                i.putExtra("questionList", qna);
                i.putExtra("name", name);
                i.putExtra("ques_id", id);
                i.putExtra("isQNA", "previousQA");
                i.putExtra("ansList", (Serializable) ans_id);
                //  i.putExtra("selectionList", options);
                startActivity(i);
                break;

            case R.id.iv_video:
                i = new Intent(Question1_1Activity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }
    }
}
