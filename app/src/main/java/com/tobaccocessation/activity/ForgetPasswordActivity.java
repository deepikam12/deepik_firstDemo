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
import com.tobaccocessation.Utils.Constant;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ForgetPasswordActivity extends AppCompatActivity {
    EditText et_email;
    TextView tv_next, tv_message;
    private Matcher matcher;
    private Pattern pattern;
    String email = "";
    CircularProgressBar circularProgressBar;
    ArrayList qnaList;
    ImageView iv_video, iv_chat, iv_back_arrow;
    Utils utils;SharedPreferences pref;
    SharedPreferences.Editor editor;NotificationModel notificationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        pattern = Pattern.compile(Constant.EMAIL_PATTERN);
        utils = new Utils(this);
        initializeIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
       // LocalBroadcastManager.getInstance(ForgetPasswordActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();
    }

    private void initializeIds() {
        circularProgressBar = findViewById(R.id.homeloader);
        et_email = findViewById(R.id.et_email);
        tv_next = findViewById(R.id.tv_next);
        tv_message = findViewById(R.id.tv_empty);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_video.setVisibility(View.GONE);
        iv_chat.setVisibility(View.GONE);
        qnaList = new ArrayList<>();
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }
        tv_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate())
                    forgetPassword();
            }
        });
        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }




    private boolean validate() {
        email = et_email.getText().toString().trim();
        matcher = pattern.matcher(email);
        boolean result = true;
        if (et_email.getText().toString().equalsIgnoreCase("")) {
            // Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.email_empty_val), Toast.LENGTH_SHORT).show();
            utils.showAlertText(ForgetPasswordActivity.this, tv_message, getResources().getString(R.string.email_empty_val));
            result = false;
        } else if (!matcher.matches()) {
            //Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.email_val), Toast.LENGTH_SHORT).show();
            utils.showAlertText(ForgetPasswordActivity.this, tv_message, getResources().getString(R.string.email_val));
            result = false;
        }
        return result;
    }

    public void forgetPassword() {
        if (Utils.isConnectingToInternet(this)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", email);


               // circularProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.reset_password, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());

                                    circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                        utils.showAlertText(ForgetPasswordActivity.this, tv_message, obj.getString("message"));

                                        //Toast.makeText(ForgetPasswordActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
                                        i.putExtra("questionList", qnaList);
                                        startActivity(i);
                                        finish();

                                    } else {
                                        utils.showAlertText(ForgetPasswordActivity.this, tv_message, obj.getString("message"));

                                        //Toast.makeText(ForgetPasswordActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);
                                    utils.showAlertText(ForgetPasswordActivity.this, tv_message, e.getMessage());

                                    e.printStackTrace();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RCA", "error----" + error.getMessage());
//                        Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
                        circularProgressBar.setVisibility(View.GONE);
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        utils.showAlertText(ForgetPasswordActivity.this, tv_message, error.getMessage());

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                            }
                        }
                    }
                });

                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dashboardRequest.setRetryPolicy(policy);

                TwilioChatApplication.getInstance().addToRequestQueue(dashboardRequest);

            } catch (Exception e) {
                e.printStackTrace();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }

        } else {
            utils.showAlertText(ForgetPasswordActivity.this, tv_message, getResources().getString(R.string.no_internet));

            //Toast.makeText(ForgetPasswordActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }

}
