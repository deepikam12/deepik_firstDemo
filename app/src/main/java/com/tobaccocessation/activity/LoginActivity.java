package com.tobaccocessation.activity;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_email, et_password;
    TextView tv_forgetpassword, tv_next, tv_message;
    CircularProgressBar circularProgressBar;
    private Matcher matcher;
    private Pattern pattern;
    String email = "", token, response;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    ImageView iv_back_arrow, iv_video, iv_chat;
    // static List<AnwerQuestionList> ans_id;
    static ArrayList<QuestionAnswer> qnaList;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pattern = Pattern.compile(Constant.EMAIL_PATTERN);
        intializIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        utils = new Utils(this);
        token = pref.getString("regId", null);
        editor = pref.edit();
        //ans_id = new ArrayList<>();
        qnaList = new ArrayList<>();
        try {
            qnaList = (ArrayList<QuestionAnswer>) getIntent().getSerializableExtra("questionList");
            //  options = (ArrayList< Answers>) getIntent().getSerializableExtra("selectionList");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void intializIds() {
        tv_message = findViewById(R.id.tv_empty);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        circularProgressBar = findViewById(R.id.homeloader);
        tv_forgetpassword = findViewById(R.id.tv_forgetpassword);
        tv_next = findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        tv_forgetpassword.setOnClickListener(this);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_back_arrow.setOnClickListener(this);
        iv_video.setVisibility(View.GONE);
        iv_chat.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                if (validate())
                    userLogin();

                break;

            case R.id.tv_forgetpassword:
                Intent i = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                i.putExtra("questionList", qnaList);

                startActivity(i);
                break;
            case R.id.iv_back_arrow:
                finish();

        }


    }

    private boolean validate() {
        email = et_email.getText().toString().trim();
        matcher = pattern.matcher(email);
        boolean result = true;

        if (et_email.getText().toString().equalsIgnoreCase("")) {
            // Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_empty_val), Toast.LENGTH_SHORT).show();
            utils.showAlertText(LoginActivity.this, tv_message, getResources().getString(R.string.email_empty_val));
            result = false;
        } else if (!matcher.matches()) {
            utils.showAlertText(LoginActivity.this, tv_message, getResources().getString(R.string.email_val));
            //Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_val), Toast.LENGTH_SHORT).show();
            result = false;
        } else if (et_password.getText().toString().equalsIgnoreCase("")) {
            //  Toast.makeText(LoginActivity.this, getResources().getString(R.string.pass_empty_val), Toast.LENGTH_SHORT).show();
            utils.showAlertText(LoginActivity.this, tv_message, getResources().getString(R.string.pass_empty_val));
            result = false;
        }
        return result;
    }


    public void userLogin() {
        if (Utils.isConnectingToInternet(this)) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("email", et_email.getText().toString().trim());
                jsonObject.put("password", et_password.getText().toString().trim());
                jsonObject.put("device_token", token);
                jsonObject.put("device_type", "android");

              //  circularProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.BASE_URL + Urls.Login, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());

                                    circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());
                                    getUserDetail(response.toString());

//                                    if (obj.getString("status").equalsIgnoreCase("success")) {
//
//                                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//
//
//                                    } else {
//
//                                        Toast.makeText(LoginActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
//
//                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);
                                    utils.showAlertText(LoginActivity.this, tv_message, e.getMessage());

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
            // Toast.makeText(LoginActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            utils.showAlertText(LoginActivity.this, tv_message, getResources().getString(R.string.no_internet));

        }

    }

    void getUserDetail(String result) {
        try {
            JSONObject json = new JSONObject(result);

            String resultObj = json.getString("code");
            if (resultObj.equals("10")) {
                response = json.getString("message");
                JSONArray jsonArray = json.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    editor.putString("first_name", jsonObject.getString("first_name"));
                    editor.putString("last_name", jsonObject.getString("last_name"));
                    editor.putString("mobile", jsonObject.getString("mobile"));
                    editor.putString("email", jsonObject.getString("email"));
                    editor.putString("accessToken", jsonObject.getString("accessToken"));
                    editor.putString("token", jsonObject.getString("token"));
                    editor.putString("connect_type", jsonObject.getString("connection_type"));

                    editor.putBoolean("isLogin", true);
                    editor.commit();
                    // editor.apply();
                }

             //   utils.showAlertText(LoginActivity.this, tv_message, response);
                // Toast.makeText(LoginActivity.this, response, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(LoginActivity.this, RetakeSurveyActivity.class);
                i.putExtra("questionList", qnaList);
                startActivity(i);
                finish();

            } else {
                utils.showAlertText(LoginActivity.this, tv_message, json.getString("message"));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                //  Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                circularProgressBar.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            circularProgressBar.setVisibility(View.GONE);
            utils.showAlertText(LoginActivity.this, tv_message, e.getMessage());
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }


}
