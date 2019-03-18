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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.Options;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Question1Activity extends AppCompatActivity implements View.OnClickListener {
    private static final int EDIT_TRAININGDAY_RESPONSE = 1;
    TextView tv_name, tv_next, tv_madantory, tv_empty_name;
    ImageView iv_navi_drawer, iv_smileyType, iv_video_ic, iv_chat_ic;
    EditText et_name;
    Answers answers;
    public static String name = "", name_id = "";
    ArrayList<Answers> answersList;
    Boolean isFirstTymLoaded = false;
    List<AnwerQuestionList> answerList;
    static List<AnwerQuestionList> ans_id;
    static ArrayList<QuestionAnswer> qnaList;
    Utils utils;
    public static String token;
    NotificationModel notificationModel;
    SharedPreferences.Editor editor;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question1);
        findviewIds();
        set_Font();
        initList();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        token = pref.getString("regId", null);
        utils = new Utils(this);
        editor = pref.edit();
        //registerReceiver(receiver, new IntentFilter("com.example.INCOMING_CALL"));
        LocalBroadcastManager.getInstance(Question1Activity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));

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


    private void findviewIds() {
        iv_smileyType = findViewById(R.id.iv_smileyType);
        iv_video_ic = findViewById(R.id.iv_video);
        iv_chat_ic = findViewById(R.id.iv_chat);
        tv_empty_name = findViewById(R.id.tv_empty);
        ans_id = new ArrayList<>();
        qnaList = new ArrayList<>();
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }


        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_madantory = (TextView) findViewById(R.id.tv_madantory);
        tv_next = (TextView) findViewById(R.id.tv_next);
        et_name = (EditText) findViewById(R.id.et_name);
        et_name.setInputType(InputType.TYPE_CLASS_TEXT);
        iv_navi_drawer = (ImageView) findViewById(R.id.iv_navi_drawer);
        setSmiley(qnaList.get(0).getIcon(), iv_smileyType);
        if (qnaList.get(0).getMandatory().equalsIgnoreCase("yes"))
            tv_madantory.setVisibility(View.VISIBLE);
        tv_name.setText(qnaList.get(0).getQuestion());
        name = et_name.getText().toString().trim();
        name_id = qnaList.get(0).getId();
    }

    public void setSmiley(String icon, ImageView iv_smileyType) {
        if (icon.equalsIgnoreCase("naviLaughing")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_laughing);
        } else if (icon.equalsIgnoreCase("naviSmile")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_smile);

        } else if (icon.equalsIgnoreCase("naviSmileTeeth")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_smile_teeth);

        } else if (icon.equalsIgnoreCase("naviTalking")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_talking);

        } else if (icon.equalsIgnoreCase("naviThinking")) {
            iv_smileyType.setBackgroundResource(R.drawable.navi_thinking);

        }
    }

    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        tv_name.setTypeface(tf);
        et_name.setTypeface(tf);
    }

    public void initList() {
        tv_next.setOnClickListener(this);
        iv_navi_drawer.setOnClickListener(this);
        iv_video_ic.setOnClickListener(this);
        iv_chat_ic.setOnClickListener(this);

    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == EDIT_TRAININGDAY_RESPONSE) {
//            if (resultCode == RESULT_OK) {
//
//                Intent intent = getIntent();
//                answerList = (List<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
//
//                String currentDay = intent.getStringExtra("day");
//                // Other code ...
//            }
//        }
//    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_next:
                if (validate()) {
                    if (isFirstTymLoaded == false) {
                        qnaList.remove(0);
                    } else {

                    }
                    Intent i = new Intent(Question1Activity.this, Question2Activity.class);
                    i.putExtra("name", et_name.getText().toString().trim());
                    i.putExtra("ques_id", name_id);
                    i.putExtra("questionList", qnaList);
                    i.putExtra("isQNA", "previousQA");
                    i.putExtra("ansList", (Serializable) ans_id);


                    //  i.putExtra("selectionList", options);
                    isFirstTymLoaded = true;

                    startActivity(i);
                }
                break;

            case R.id.iv_navi_drawer:
                finish();
//                Intent intent = new Intent(Question1Activity.this, Question1_DrawerActivity.class);
//
//                startActivity(intent);
                break;
            case R.id.iv_video:
                Intent i = new Intent(Question1Activity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

    private boolean validate() {
        boolean result = true;
        if (et_name.getText().toString().trim().equalsIgnoreCase("")) {

            Timer t = new Timer(false);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            tv_empty_name.setVisibility(View.GONE);
                        }
                    });
                }
            }, 2000);
            tv_empty_name.setVisibility(View.VISIBLE);
            //  Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            result = false;
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(Question1Activity.this).unregisterReceiver(receiver);

        //unregisterReceiver(receiver);

    }


}
