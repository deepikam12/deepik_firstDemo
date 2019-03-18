package com.tobaccocessation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.QuestionAnswer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class HaveAccountActivity extends AppCompatActivity  implements View.OnClickListener{
 TextView tv_yes, tv_no;  Boolean isLogin;SharedPreferences preferences;
   // static List<AnwerQuestionList> ans_id;
    static ArrayList<QuestionAnswer> qnaList;ImageView iv_video, iv_chat,iv_back_arrow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_accounrt);
        initializeIds();
        eventListner();
        preferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);

    }

    private void initializeIds() {
        tv_yes= findViewById(R.id.tv_yes);
        tv_no=findViewById(R.id. tv_no);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_back_arrow.setOnClickListener(this);
        iv_video.setVisibility(View.GONE);
        iv_chat.setVisibility(View.GONE);
//        if (isLogin == true) {
//            Intent i= new Intent(HaveAccountActivity.this, RetakeSurveyActivity.class);
//            startActivity(i);
//
//        } else {
//
//        }
     //   ans_id = new ArrayList<>();
        qnaList = new ArrayList<>();
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void eventListner() {
        tv_yes.setOnClickListener(this);
        tv_no.setOnClickListener(this);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_yes:
                Intent i = new Intent(HaveAccountActivity.this, LoginActivity.class);
                i.putExtra("questionList", qnaList);
              //  i.putExtra("isQNA", "previousQA");
               // i.putExtra("ansList", (Serializable) ans_id);
                startActivity(i);
                finish();
                break;

            case R.id.tv_no:

                i = new Intent(HaveAccountActivity.this, Terms_and_CondActivity.class);
                i.putExtra("questionList", qnaList);
              //  i.putExtra("isQNA", "previousQA");
               // i.putExtra("ansList", (Serializable) ans_id);
                startActivity(i);
                break;
            case R.id.iv_back_arrow:
                finish();
                break;

            case R.id.iv_video:
                i = new Intent(HaveAccountActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;

        }
    }
}
