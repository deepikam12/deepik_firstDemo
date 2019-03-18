package com.tobaccocessation.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.provider.Settings;
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

import com.android.volley.AuthFailureError;
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
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class CreateAccountActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_message, create_account, tv_first_name, tv_last_name, tv_phone_no, tv_email, tv_password, tv_confirm_pass, tv_next;
    EditText et_first_name, et_last_name, et_phone_no, et_email, et_password, et_confirm_pass;
    private Pattern pattern;
    private Matcher matcher;
    CircularProgressBar circularProgressBar;
    String email = "", password = "", response;
    ArrayList<AnwerQuestionList> ans_id;
    String id, name, device_id, accesstoken="";
    ImageView iv_video, iv_chat, iv_back_arrow;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;
    Utils utils;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ans_id = new ArrayList<AnwerQuestionList>();
        activity = this;
        try {
            id = getIntent().getStringExtra("ques_id");
            name = getIntent().getStringExtra("name");
            ans_id = (ArrayList<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initializeIds();
        set_Font();
        utils = new Utils(this);
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        editor = pref.edit();
        pattern = Pattern.compile(Constant.EMAIL_PATTERN);
        device_id = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(CreateAccountActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));

        accesstoken = pref.getString("accessToken", "");

    }

    private void initializeIds() {

        circularProgressBar = findViewById(R.id.homeloader);
        create_account = (TextView) findViewById(R.id.create_account);
        tv_first_name = (TextView) findViewById(R.id.tv_firstName);
        tv_last_name = (TextView) findViewById(R.id.tv_lastName);
        tv_phone_no = (TextView) findViewById(R.id.tv_phone);
        tv_password = (TextView) findViewById(R.id.tv_password);
        tv_confirm_pass = (TextView) findViewById(R.id.tv_confirm_pass);
        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_message = findViewById(R.id.tv_empty);

        et_first_name = (EditText) findViewById(R.id.et_first_name);
        et_last_name = (EditText) findViewById(R.id.et_last_name);
        et_phone_no = (EditText) findViewById(R.id.et_phone_no);
        et_email = (EditText) findViewById(R.id.et_email);
        et_password = (EditText) findViewById(R.id.et_password);
        et_confirm_pass = (EditText) findViewById(R.id.et_confirm_pass);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_video.setVisibility(View.GONE);
        iv_chat.setVisibility(View.GONE);
        iv_back_arrow.setOnClickListener(this);
        tv_next.setOnClickListener(this);
//        name = getIntent().getStringExtra("name");
//        id = getIntent().getStringExtra("ques_id");


    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = pref.getBoolean("isbooleanVideo", false);
        boolean isChat = pref.getBoolean("isbooleanChat", false);
        if (isVideo == true) {
            iv_video.setImageResource(R.drawable.video_icon);
        } else if (isChat == true) {
            iv_video.setImageResource(R.drawable.chat_icon);
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
                    iv_video.setImageResource(R.drawable.chat_icon);
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
        tv_first_name.setTypeface(tf);
        tv_last_name.setTypeface(tf);
        tv_phone_no.setTypeface(tf);
        tv_email.setTypeface(tf);
        tv_password.setTypeface(tf);
        tv_confirm_pass.setTypeface(tf);
        et_first_name.setTypeface(tf);
        et_last_name.setTypeface(tf);
        et_phone_no.setTypeface(tf);
        et_email.setTypeface(tf);
        et_password.setTypeface(tf);
        et_confirm_pass.setTypeface(tf);


        Typeface tyf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");
        create_account.setTypeface(tyf);
        tv_next.setTypeface(tf);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                if (validate())
                    submitAnswerandUserProfile();

                break;
            case R.id.iv_back_arrow:
                finish();
                break;

        }
    }

    boolean validate() {
        email = et_email.getText().toString().trim();
        password = et_password.getText().toString().trim();
        matcher = pattern.matcher(email);
        boolean result = true;
        try {
            if (et_first_name.getText().toString().isEmpty()) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.firstname));
                et_first_name.requestFocus();
                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.firstname), Toast.LENGTH_LONG).show();
                result = false;
            }
//            else if (!et_first_name.getText().toString().matches("[a-zA-Z ]+")) {
//                et_first_name.requestFocus();
//                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.name_val));
//
//                //  Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.name_val), Toast.LENGTH_LONG).show();
//                result = false;
//
//            }
            else if (et_last_name.getText().toString().isEmpty()) {
                et_last_name.requestFocus();
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.lastname));
                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.lastname), Toast.LENGTH_LONG).show();
                et_last_name.requestFocus();
                result = false;

            }
//            else if (!et_last_name.getText().toString().matches("[a-zA-Z ]+")) {
//                et_last_name.requestFocus();
//                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.name_val));
//
//                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.name_val), Toast.LENGTH_LONG).show();
//                result = false;
//
//            }
            else if (et_phone_no.getText().toString().isEmpty()) {
                et_phone_no.requestFocus();
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.phone_val));

                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.phone_val), Toast.LENGTH_LONG).show();

                et_phone_no.requestFocus();
                return false;

            } else if (et_phone_no.getText().length() != 10) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.phone));

                //  Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.phone), Toast.LENGTH_LONG).show();
                et_phone_no.requestFocus();
                result = false;

            } else if (et_email.getText().toString().isEmpty()) {
                et_email.requestFocus();
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.email_empty_val));
                et_email.requestFocus();
                //  Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.email_empty_val), Toast.LENGTH_LONG).show();
                return false;

            } else if (!matcher.matches()) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.email_val));

                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.email_val), Toast.LENGTH_SHORT).show();
                et_email.requestFocus();
                result = false;

            } else if (et_password.getText().toString().equalsIgnoreCase("")) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.pass));

                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.pass), Toast.LENGTH_SHORT).show();
                et_password.requestFocus();
                result = false;

            } else if (et_password.getText().toString().length() < 8 || et_password.getText().toString().length() > 17) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.val_pass));

                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.val_pass), Toast.LENGTH_LONG).show();
                et_password.requestFocus();
                result = false;
            }
//            else if (!utils.validatePassword(password)) {
//                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.val_pass));
//
//                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.val_pass), Toast.LENGTH_LONG).show();
//                et_password.requestFocus();
//                result = false;
//            }
            else if (et_confirm_pass.getText().toString().isEmpty()) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.confirmPass));
                //Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.confirmPass), Toast.LENGTH_LONG).show();
                et_confirm_pass.requestFocus();
                result = false;

            } else if (!et_password.getText().toString().trim().equals(et_confirm_pass.getText().toString().trim())) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.confirmPass_match));

                // Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.confirmPass_match), Toast.LENGTH_SHORT).show();
                et_confirm_pass.requestFocus();
                result = false;

            }


        } catch (Exception e) {
            e.printStackTrace();
            Log.i("RCA", "error----" + e.getMessage());
        }
        return result;
    }

    private boolean isStrong(String password) {
        return password.matches("\"^[a-zA-Z0-9!@#$&()\\-`.+,/\"]*$\"");

    }

    public void submitAnswerandUserProfile() {
        if (Utils.isConnectingToInternet(this)) {
            try {
                JSONArray array = new JSONArray();
                for (int i = 0; i < ans_id.size(); i++) {
                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("ques_id", ans_id.get(i).getQues_id());
                        obj.put("ans_id", ans_id.get(i).getAns_id());
                        obj.put("ans", ans_id.get(i).getAns());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    array.put(obj);
                }
                JSONObject profileObj = new JSONObject();
                profileObj.put("firstName", et_first_name.getText().toString().trim());
                profileObj.put("lastName", et_last_name.getText().toString().trim());
                profileObj.put("email", et_email.getText().toString().trim());
                profileObj.put("phone", et_phone_no.getText().toString().trim());
                profileObj.put("password", et_password.getText().toString().trim());


                JSONObject answerObj = new JSONObject();
                answerObj.put("device_id", device_id);
                answerObj.put("device_type", "android");
                answerObj.put("device_token", Question1Activity.token);
                answerObj.put("answerList", array);
                answerObj.put("userProfile", profileObj);
                // circularProgressBar.setVisibility(View.VISIBLE);

                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.BASE_URL + Urls.AnswerList, answerObj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());

                                    circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                        //   if(obj.getString("code").equalsIgnoreCase("10"))
                                        getUserDetail(response.toString());
//                                        else if(obj.getString("code").equalsIgnoreCase("20")){
//
//                                        }

//                                        if (!obj.getString("message").equalsIgnoreCase("This email address already has an account created, enter password")) {
//
//                                            getUserDetail(response.toString());
//
//                                        } else {
//                                            et_password.setText("");
//                                            et_confirm_pass.setText("");
//
//                                        }

                                    } else {
                                        if (obj.getString("code").equalsIgnoreCase("2"))
                                            utils.showAlertText(CreateAccountActivity.this, tv_message, obj.getString("message"));
                                        et_password.setText("");
                                        et_confirm_pass.setText("");
                                        //Toast.makeText(CreateAccountActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);

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
                }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + accesstoken);

                        return params;
                    }
                };
                ;

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
            utils.showAlertText(CreateAccountActivity.this, tv_message, getResources().getString(R.string.no_internet));

            //Toast.makeText(CreateAccountActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
                  //  editor.putString("connect_type", jsonObject.getString("connection_type"));
                    editor.putBoolean("isLogin", true);
                    editor.commit();
                    // editor.apply();
                }
                //  utils.showAlertText(CreateAccountActivity.this, tv_message, json.getString("message"));

                // Toast.makeText(CreateAccountActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(CreateAccountActivity.this, ContactToCoachActivity.class);
                startActivity(i);
                finish();

            } else if (resultObj.equalsIgnoreCase("20")) {
                utils.showAlertText(CreateAccountActivity.this, tv_message, json.getString("message"));
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
                     showAlertText();
                }


            } else {
                utils.showAlertText(CreateAccountActivity.this, tv_message, json.getString("message"));
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                //  Toast.makeText(CreateAccountActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                circularProgressBar.setVisibility(View.GONE);
            }

        } catch (JSONException je) {
            circularProgressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        }
    }

    public void showAlertText() {
        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent i = new Intent(CreateAccountActivity.this, ContactToCoachActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }, 5000);
//
    }
}
