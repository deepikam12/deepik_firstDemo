package com.tobaccocessation.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
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
import com.tobaccocessation.BroadcastReceiver.CallingIntentService;
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Constant;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class EmailPhoneNumberInputActivity extends AppCompatActivity implements View.OnClickListener {
    EditText et_phone_email;
    TextView tv_heading, tv_message, tv_plusOne, tv_next;
    String type, accesstoken;
    CircularProgressBar circularProgressBar;
    private Pattern pattern;
    private Matcher matcher;
    ImageView iv_back_arrow, iv_video, iv_chat;
    SharedPreferences preferences;
    Utils utils;
    NotificationModel notificationModel;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_phone_number_input);
        preferences = getSharedPreferences(Config.SHARED_PREF, 0);
        accesstoken = preferences.getString("accessToken", "");
        LocalBroadcastManager.getInstance(EmailPhoneNumberInputActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = preferences.edit();
        initializeId();
        pattern = Pattern.compile(Constant.EMAIL_PATTERN);
        utils = new Utils(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isVideo = preferences.getBoolean("isbooleanVideo", false);
        boolean isChat = preferences.getBoolean("isbooleanChat", false);
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

    private void initializeId() {
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_chat = findViewById(R.id.iv_chat);
        iv_video = findViewById(R.id.iv_video);

        circularProgressBar = findViewById(R.id.homeloader);
        tv_plusOne = findViewById(R.id.tv_plusOne);
        tv_message = findViewById(R.id.tv_empty);
        et_phone_email = findViewById(R.id.et_phone_email);
        tv_heading = findViewById(R.id.tv_heading);
        tv_next = findViewById(R.id.tv_next);
        iv_back_arrow.setOnClickListener(this);
        tv_next.setOnClickListener(this);
        //  iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
        type = getIntent().getStringExtra("type");
        if (type.equalsIgnoreCase("call")) {
            tv_plusOne.setVisibility(View.VISIBLE);
            tv_heading.setText("What # should I have a \ncoach call you at?");
            et_phone_email.setText(preferences.getString("mobile", ""));
            et_phone_email.setInputType(InputType.TYPE_CLASS_NUMBER);
            setEditTextMaxLength(10);
        } else if (type.equalsIgnoreCase("Email")) {
            tv_plusOne.setVisibility(View.GONE);
            tv_heading.setText("What email should I have a \ncoach contact you at?");
            et_phone_email.setInputType(InputType.TYPE_CLASS_TEXT);
            et_phone_email.setText(preferences.getString("email", ""));

        }

    }

    public void setEditTextMaxLength(int length) {
        InputFilter[] filterArray = new InputFilter[1];
        filterArray[0] = new InputFilter.LengthFilter(length);
        et_phone_email.setFilters(filterArray);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:

                if (type.equalsIgnoreCase("call")) {
                    if (!et_phone_email.getText().toString().equalsIgnoreCase("")) {

//                        Intent i = new Intent(EmailPhoneNumberInputActivity.this, ConnectingThroughCallEmail.class);
//                        i.putExtra("connect_type", "call");
//                        i.putExtra("mobile_email", "+1" + et_phone_email.getText().toString().trim());
//                        startActivity(i);
                        postcallEmalService("call", "+1" + et_phone_email.getText().toString().trim());
                    } else {
                        utils.showAlertText(EmailPhoneNumberInputActivity.this, tv_message, getResources().getString(R.string.phone_val));

                        // Toast.makeText(EmailPhoneNumberInputActivity.this, getResources().getString(R.string.phone_val), Toast.LENGTH_SHORT).show();

                    }
                } else {
                    if (!et_phone_email.getText().toString().equalsIgnoreCase("")) {

                        matcher = pattern.matcher(et_phone_email.getText().toString().trim());
                        if (!matcher.matches()) {
                            // Toast.makeText(EmailPhoneNumberInputActivity.this, getResources().getString(R.string.email_val), Toast.LENGTH_SHORT).show();
                            utils.showAlertText(EmailPhoneNumberInputActivity.this, tv_message, getResources().getString(R.string.email_val));

                        } else {
//                            Intent i = new Intent(EmailPhoneNumberInputActivity.this, ConnectingThroughCallEmail.class);
//                            i.putExtra("connect_type", "Email");
//                            i.putExtra("mobile_email",  et_phone_email.getText().toString().trim());
//                            startActivity(i);
                            postcallEmalService("email", et_phone_email.getText().toString().trim());

                        }
                    } else {
                        utils.showAlertText(EmailPhoneNumberInputActivity.this, tv_message, getResources().getString(R.string.email_empty_val));

                        //  Toast.makeText(EmailPhoneNumberInputActivity.this, getResources().getString(R.string.email_empty_val), Toast.LENGTH_SHORT).show();

                    }
                }


                break;
            case R.id.iv_back_arrow:
                finish();
                break;

            case R.id.iv_video:
                Intent i = new Intent(EmailPhoneNumberInputActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }
    }

    private void postcallEmalService(final String call, String phone_email) {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contact_type", call);
                jsonObject.put("contact_value", phone_email);
                jsonObject.put("device_id", Settings.System.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID));

                //circularProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.BASE_URL + Urls.ContactDetail, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {

                                        if(!isMyServiceRunning(CallingIntentService.class)) {
                                            Intent serviceIntent = new Intent(getApplicationContext(), CallingIntentService.class);
                                            startService(serviceIntent);
                                        }
                                        // Toast.makeText(EmailPhoneNumberInputActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                      //  utils.showAlertText(EmailPhoneNumberInputActivity.this, tv_message, obj.getString("message"));
                                        editor = preferences.edit();
                                        editor.putString("connect_type", call);
                                        editor.commit();

//                                        Intent i = new Intent(EmailPhoneNumberInputActivity.this, FinalConnectingCoachActivity.class);
//                                        startActivity(i);
                                        Intent i = new Intent(EmailPhoneNumberInputActivity.this, ConnectingThroughCallEmail.class);
                                        i.putExtra("connect_type", call);
                                        startActivity(i);
                                    } else {
                                        utils.showAlertText(EmailPhoneNumberInputActivity.this, tv_message, obj.getString("message"));

                                        //Toast.makeText(EmailPhoneNumberInputActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);

                                    e.printStackTrace();

//                                    Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
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
                        //  params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + accesstoken);

                        return params;
                    }
                };

                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dashboardRequest.setRetryPolicy(policy);

                TwilioChatApplication.getInstance().addToRequestQueue(dashboardRequest);

            } catch (Exception e) {
                circularProgressBar.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                e.printStackTrace();
            }

        } else {
            Toast.makeText(EmailPhoneNumberInputActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
