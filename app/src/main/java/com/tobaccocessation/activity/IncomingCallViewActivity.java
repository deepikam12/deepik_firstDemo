package com.tobaccocessation.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class IncomingCallViewActivity extends AppCompatActivity implements View.OnClickListener {
    NotificationModel notificationModel;
    TextView tv_accept, tv_reject;
    ImageView iv_accept, iv_reject;
    CircularProgressBar circularProgressBar;
    Utils utils;
    TextView tv_message,tv_notification_title;
    String accesstoken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call_view);
        notificationModel = (NotificationModel) getIntent().getSerializableExtra("notificationData");
        utils = new Utils(this);
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);

        accesstoken = pref.getString("accessToken", null);
        initializeIds();


    }

    private void initializeIds() {
        circularProgressBar = findViewById(R.id.homeloader);
//        tv_accept = findViewById(R.id.tv_accept);
//        tv_reject = findViewById(R.id.tv_reject);
        tv_accept= findViewById(R.id.tv_accept);
        tv_reject= findViewById(R.id.tv_reject);

        tv_message = findViewById(R.id.tv_empty);
        tv_notification_title = findViewById(R.id.tv_notification_title);
        tv_accept.setOnClickListener(this);
        tv_reject.setOnClickListener(this);
        tv_notification_title.setText(notificationModel.getTitle());
        // set its background to our AnimationDrawable XML resource.
//        @SuppressLint("ResourceType") Animation blink = AnimationUtils.loadAnimation(this,R.animator.blink_image);
//        tv_accept.startAnimation(blink);
//        tv_reject.startAnimation(blink);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_accept:
                //  serviceCallForVideo(notificationModel.getRoomName());
//                Intent i = new Intent(IncomingCallViewActivity.this, VideoActivity.class);
//                i.putExtra("notificationData", notificationModel);
//                startActivity(i);
//                finish();
                serviceCallForVideo("accepted");
                break;

            case R.id.tv_reject:
                serviceCallForVideo("rejected");

                break;
        }

    }

    @Override
    public void onBackPressed() {

    }

    private void serviceCallForVideo(final String status) {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("room_id", notificationModel.getRoomName());
                jsonObject.put("call_status", status);
                jsonObject.put("coach_id", notificationModel.getCoach_id());

                // circularProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.BASE_URL + Urls.NotifyAcceptReject, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());

                                    ///  circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {


                                        Intent i = new Intent(IncomingCallViewActivity.this, VideoActivity.class);
                                        i.putExtra("notificationData", notificationModel);
                                        startActivity(i);
                                        finish();
                                    }
                                     else{

                                        callActivity(status);

                                        utils.showAlertText(IncomingCallViewActivity.this, tv_message, obj.getString("message"));

                                        }


                                    } catch(Exception e){
                                        Log.i("RCA", "error----" + e.getMessage());
                                        circularProgressBar.setVisibility(View.GONE);

                                        e.printStackTrace();

//                                    Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
                                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    }


                                }
                            },new Response.ErrorListener()

                            {
                                @Override
                                public void onErrorResponse (VolleyError error){
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
                                public Map<String, String> getHeaders () throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("Content-Type", "application/json");
                                params.put("Authorization", "Bearer " + accesstoken);

                                return params;
                            }
                            }

                            ;


                            RetryPolicy policy = new DefaultRetryPolicy
                                    (50000,
                                            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dashboardRequest.setRetryPolicy(policy);

                TwilioChatApplication.getInstance().addToRequestQueue(dashboardRequest);

                        } catch(Exception e){
                    circularProgressBar.setVisibility(View.GONE);
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    e.printStackTrace();
                }

            } else{
            utils.showAlertText(IncomingCallViewActivity.this, tv_message, getResources().getString(R.string.no_internet));

           // Toast.makeText(IncomingCallViewActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
        }

    private void callActivity(String status) {
        if (getIntent().getStringExtra("comeFrom").equalsIgnoreCase("Splash") && status.equalsIgnoreCase("rejected")) {
            //delay in ms
            int DELAY = 1000;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(IncomingCallViewActivity.this, SplashActivity.class);
                    i.putExtra("notificationData", notificationModel);
                    startActivity(i);
                    finish();
                }
            }, DELAY);


        }

        else{
            finish();
        }
    }
}
