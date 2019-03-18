package com.tobaccocessation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.tobaccocessation.model.Options;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class Terms_and_CondActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tc, tv_tc, tv_i_agree, tv_message, tv_contact_us;
    ArrayList<QuestionAnswer> qnaList;
    ArrayList<Answers> answersList;
    CircularProgressBar circularProgressBar;
    Answers answers;
    Boolean isDataLoaded = false;
    ImageView iv_close;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_and_c);
        utils= new Utils(this);
        initViews();
        set_Font();

    }

    public void initViews() {
        qnaList = new ArrayList<>();

        circularProgressBar = findViewById(R.id.homeloader);
        iv_close = findViewById(R.id.iv_close);
        tc = (TextView) findViewById(R.id.tc);
        tv_tc = (TextView) findViewById(R.id.tv_tc);
        tv_i_agree = (TextView) findViewById(R.id.tv_i_agree);
        tv_contact_us = (TextView) findViewById(R.id.tv_contact_us);
        tv_message=findViewById(R.id.tv_empty);
        tv_tc.setMovementMethod(LinkMovementMethod.getInstance());

        tv_contact_us.setOnClickListener(this);
        tv_i_agree.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_i_agree:
                //  getMessagingDetailData();
                // isDataLoaded= true;
                Intent intent = new Intent(Terms_and_CondActivity.this, Question1Activity.class);
                intent.putExtra("questionList", qnaList);
                startActivity(intent);
                break;

            case R.id.tv_contact_us:
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@clevelandandcleaningcoach.com"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "support from the app");
                intent.setPackage("com.google.android.gm");
                if (intent.resolveActivity(getPackageManager()) != null)
                    startActivity(intent);
                else
                    utils.showAlertText(Terms_and_CondActivity.this,tv_message, getResources().getString(R.string.gmail_package));

                    //Toast.makeText(Terms_and_CondActivity.this, "Gmail App is not installed", Toast.LENGTH_SHORT).show();
                break;


            case R.id.iv_close:
                finish();

        }

    }

    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");
        tc.setTypeface(tf);
        tv_i_agree.setTypeface(tf);
        tv_contact_us.setTypeface(tf);

        Typeface tyf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        tv_tc.setTypeface(tyf);
    }


}
