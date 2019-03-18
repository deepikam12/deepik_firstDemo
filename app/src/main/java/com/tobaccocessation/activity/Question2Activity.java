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
import android.widget.ImageView;
import android.widget.TextView;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Question2Activity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_name, tv_next, tv_description;
    ImageView iv_back_arrow, iv_video, iv_chat;
    Utils utils;
    String name, id;
    public static boolean isQNA = true;
    ArrayList<QuestionAnswer> qna;
    ArrayList<Answers> options;
    static List<AnwerQuestionList> ans_id;
    NotificationModel notificationModel;
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question2);
        utils = new Utils(this);
        initializeIds();
        set_Font();
        eventListner();
        sharedPreferences = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(Question2Activity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = sharedPreferences.edit();


    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = sharedPreferences.getBoolean("isbooleanVideo", false);
        boolean isChat = sharedPreferences.getBoolean("isbooleanChat", false);
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

    private void eventListner() {
        tv_next.setOnClickListener(this);
        iv_back_arrow.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
    }

    private void initializeIds() {
        ans_id = new ArrayList<>();
        qna = new ArrayList<>();
        try {
            qna = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList<Answers>) getIntent().getSerializableExtra("questionList");
            ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");

            name = getIntent().getStringExtra("name");
            id = getIntent().getStringExtra("ques_id");


        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_name = findViewById(R.id.tv_name);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        tv_next = findViewById(R.id.tv_next);
        tv_description = findViewById(R.id.tv_description);
        tv_name.setText("Hi" + " " + name + "!");
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);


    }

    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        tv_name.setTypeface(tf);
        tv_description.setTypeface(tf);

        Typeface tyf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");
        tv_next.setTypeface(tyf);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_next:
                Intent i = new Intent(Question2Activity.this, QuestionAnswerActivity.class);
                i.putExtra("questionList", qna);
                i.putExtra("isQNA", "previousQA");
                i.putExtra("name", name);
                i.putExtra("ques_id", id);
                i.putExtra("ansList", (Serializable) ans_id);

                //i.putExtra("comeFrom","QuestionList");


                startActivity(i);
                break;

            case R.id.iv_back_arrow:
                finish();
                break;
            case R.id.iv_video:
                i = new Intent(Question2Activity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }

    }
}
