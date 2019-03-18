package com.tobaccocessation.activity;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ConnectingThroughCallEmail extends AppCompatActivity {
    CircularProgressBar circularProgressBar;
    ImageView iv_call, iv_email, iv_chat, iv_chat_ic, iv_video_ic, iv_video, iv_back_arrow;
    TextView tv_cancel, tv_wait, tv_message;
    String connect_type, mobileEmail;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    NotificationModel notificationModel;
    Utils utils;
    String accesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting_through_call_email);
        initializIds();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        accesstoken = pref.getString("accessToken", null);

        LocalBroadcastManager.getInstance(ConnectingThroughCallEmail.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();
        utils = new Utils(this);
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
        if (connect_type.equalsIgnoreCase("call") || connect_type.equalsIgnoreCase("email")) {
            Intent i = new Intent(ConnectingThroughCallEmail.this, FinalConnectingCoachActivity.class);
            startActivity(i);

        } else if (connect_type.equalsIgnoreCase("chat")) {
            callServiceForVideoChat(connect_type);
        } else if (connect_type.equalsIgnoreCase("video")) {
            callServiceForVideoChat(connect_type);
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


    private void initializIds() {
        circularProgressBar = findViewById(R.id.homeloader);
        iv_call = findViewById(R.id.iv_call);
        iv_email = findViewById(R.id.iv_email);
        iv_chat = findViewById(R.id.iv_chat);
        iv_video = findViewById(R.id.iv_video);
        iv_chat_ic = findViewById(R.id.iv_chat_ic);
        iv_video_ic = findViewById(R.id.iv_video_ic);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_message = findViewById(R.id.tv_empty);
        tv_wait = findViewById(R.id.tv_wait);
        iv_back_arrow = findViewById(R.id.iv_back_arrow);
        iv_video.setVisibility(View.GONE);
        iv_chat.setVisibility(View.GONE);

        connect_type = getIntent().getStringExtra("connect_type");
        // mobileEmail = getIntent().getStringExtra("mobile_email");
        if (connect_type.equalsIgnoreCase("call")) {
            // iv_callEmail.setImageResource(R.drawable.smilie_ic);
            // iv_callEmail.setImageDrawable(getResources().getDrawable(R.drawable.phone_black));
            tv_wait.setText(getResources().getString(R.string.phone_contact));
            iv_call.setVisibility(View.VISIBLE);
            iv_email.setVisibility(View.GONE);
        } else if (connect_type.equalsIgnoreCase("email")) {
            tv_wait.setText(getResources().getString(R.string.email_contact));
            iv_email.setVisibility(View.VISIBLE);
            iv_call.setVisibility(View.GONE);

        } else if (connect_type.equalsIgnoreCase("chat")) {
            tv_wait.setText(getResources().getString(R.string.connect_to));
            iv_chat_ic.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_video.setVisibility(View.GONE);

        } else if (connect_type.equalsIgnoreCase("video")) {
            tv_wait.setText(getResources().getString(R.string.connect_to));
            iv_video_ic.setVisibility(View.VISIBLE);
            iv_chat.setVisibility(View.GONE);
            iv_video.setVisibility(View.GONE);

        }
        tv_cancel.setClickable(false);
//        if (connect_type.equalsIgnoreCase("call") || connect_type.equalsIgnoreCase("email")) {
//            Intent i = new Intent(ConnectingThroughCallEmail.this, FinalConnectingCoachActivity.class);
//            startActivity(i);
//
//        } else if (connect_type.equalsIgnoreCase("chat")) {
//            callServiceForVideoChat(connect_type);
//        } else if (connect_type.equalsIgnoreCase("video")) {
//            callServiceForVideoChat(connect_type);
//        }

        tv_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (connect_type.equalsIgnoreCase("call") || connect_type.equalsIgnoreCase("email")) {
//                    Intent i = new Intent(ConnectingThroughCallEmail.this, FinalConnectingCoachActivity.class);
//                    startActivity(i);
//
//                } else if (connect_type.equalsIgnoreCase("chat")) {
//                   // callServiceForVideoChat(connect_type);
//                    Intent i = new Intent(ConnectingThroughCallEmail.this, ContactVideoChatActivity.class);
//                    i.putExtra("connect_type", connect_type);
//                    startActivity(i);
//                } else if (connect_type.equalsIgnoreCase("video")) {
//                    Intent i = new Intent(ConnectingThroughCallEmail.this, ContactVideoChatActivity.class);
//                    i.putExtra("connect_type", connect_type);
//                    startActivity(i);
//                   // callServiceForVideoChat(connect_type);
//                }

            }
        });
        iv_back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        iv_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ConnectingThroughCallEmail.this, VideoStatusActivity.class);
                startActivity(i);
            }
        });

    }


    private void callServiceForVideoChat(final String connect_type) {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("contact_type", connect_type);
                jsonObject.put("contact_value", "");
                jsonObject.put("device_id", Settings.System.getString(this.getContentResolver(),
                        Settings.Secure.ANDROID_ID));

                //  circularProgressBar.setVisibility(View.VISIBLE);

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

                                        editor = pref.edit();
                                        editor.putString("connect_type", connect_type);
                                        editor.commit();
                                        if (!isMyServiceRunning(CallingIntentService.class)) {
                                            tv_cancel.setClickable(true);
                                            Intent serviceIntent = new Intent(getApplicationContext(), CallingIntentService.class);
                                            startService(serviceIntent);
                                        }
                                        //   Toast.makeText(ConnectingThroughCallEmail.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        // utils.showAlertText(ConnectingThroughCallEmail.this, tv_message, obj.getString("message"));

                                        Intent i = new Intent(ConnectingThroughCallEmail.this, ContactVideoChatActivity.class);
                                        i.putExtra("connect_type", connect_type);
                                        startActivity(i);
                                        finish();
                                    } else if (obj.getString("status").equalsIgnoreCase("Failed") && obj.getString("code").equalsIgnoreCase("1")) {
                                        editor = pref.edit();
                                        editor.putString("connect_type", connect_type);
                                        editor.commit();
                                        if (!isMyServiceRunning(CallingIntentService.class)) {
                                            Intent serviceIntent = new Intent(getApplicationContext(), CallingIntentService.class);
                                            startService(serviceIntent);
                                        }

                                        //   utils.showAlertText(ConnectingThroughCallEmail.this, tv_message, obj.getString("message"));
                                        Intent i = new Intent(ConnectingThroughCallEmail.this, NoCoachAvailableScreen.class);
                                        i.putExtra("connect_type",connect_type);
                                        startActivity(i);
                                        finish();
                                        // Toast.makeText(ConnectingThroughCallEmail.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);
                                    utils.showAlertText(ConnectingThroughCallEmail.this, tv_message, e.getMessage());

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
                })

                {
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
            utils.showAlertText(ConnectingThroughCallEmail.this, tv_message, getResources().getString(R.string.no_internet));

            //Toast.makeText(ConnectingThroughCallEmail.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
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
