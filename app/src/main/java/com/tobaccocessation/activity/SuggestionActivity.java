package com.tobaccocessation.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.adapter.SelectionListAdapter;
import com.tobaccocessation.adapter.SuggestionAdapter;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.SuggestionList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SuggestionActivity extends AppCompatActivity {
    RadioGroup rg_suggest;
    String selectedText = "";
    Boolean isLogin;
    TextView tv_next;
    ImageView iv_back_arrow, iv_navi, iv_video, iv_chat;
    SharedPreferences preferences;
    ArrayList<AnwerQuestionList> ans_id;
    ArrayList<SuggestionList> suggestions;
    String id, name;
    SuggestionAdapter suggestionAdapter;
    RecyclerView rv_suggestion;
    NotificationModel notificationModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggestion);
        preferences = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        ans_id = new ArrayList<AnwerQuestionList>();
        suggestions = new ArrayList<SuggestionList>();
        initializeIds();
        setData();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(SuggestionActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();

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

    private void setData() {
        suggestions = (ArrayList<SuggestionList>) getIntent().getSerializableExtra("suggestionList");

        rv_suggestion.setLayoutManager(new LinearLayoutManager(SuggestionActivity.this));
        suggestionAdapter = new SuggestionAdapter(SuggestionActivity.this, suggestions);
        rv_suggestion.setAdapter(suggestionAdapter);
    }

    private void initializeIds() {
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_navi = findViewById(R.id.iv_navi);

        rv_suggestion = findViewById(R.id.rv_suggestion);
        id = getIntent().getStringExtra("ques_id");
        name = getIntent().getStringExtra("name");
        ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
        rg_suggest = findViewById(R.id.rg_option);
        tv_next = findViewById(R.id.tv_next);
        //  iv_back_arrow = findViewById(R.id.iv_back_arrow);
        isLogin = preferences.getBoolean("isLogin", false);

        //  selectsugestion();

//        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        iv_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestionActivity.this, Landing_3Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);
                finish();
            }
        });
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (!selectedText.equalsIgnoreCase("")) {
                if (isLogin != true) {

                    Intent i = new Intent(SuggestionActivity.this, AskCreateAccountActivity.class);
                    i.putExtra("ansList", (Serializable) ans_id);
                    i.putExtra("name", name);
                    i.putExtra("ques_id", id);
                    startActivity(i);
                } else {
                    Intent i = new Intent(SuggestionActivity.this, TalkToCoachActivity.class);
                    startActivity(i);
                }

            }
//                else
//                    Toast.makeText(SuggestionActivity.this, getResources().getString(R.string.select_val), Toast.LENGTH_SHORT)
//                            .show();
//
//            }
        });
        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SuggestionActivity.this, VideoStatusActivity.class);
                startActivity(i);
            }
        });


    }

    private void selectsugestion() {
        rg_suggest.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) findViewById(id);

                selectedText = rb.getText().toString();


            }


        });

    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed();
        Intent i = new Intent(SuggestionActivity.this, Landing_3Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }
}
