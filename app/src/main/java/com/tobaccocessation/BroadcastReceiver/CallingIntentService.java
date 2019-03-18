package com.tobaccocessation.BroadcastReceiver;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
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
import com.tobaccocessation.activity.IncomingCallViewActivity;
import com.tobaccocessation.activity.VideoActivity;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Deepika.Mishra on 1/7/2019.
 */

public class CallingIntentService extends IntentService {
    private static final String TAG = "CallingIntentService";
    String accesstoken, connectType;
    NotificationModel notificationModel;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Timer timer;
    String videoJson;

    public CallingIntentService() {
        super("CallingIntentService");
        this.setIntentRedelivery(true);
    }
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        editor.putBoolean("isbooleanVideo", false);
        editor.commit();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        accesstoken = pref.getString("accessToken", "");


        editor = pref.edit();
        //  videoJson= intent.getStringExtra("VideoJson");

        notificationModel = (NotificationModel) intent.getSerializableExtra("notificationData");
        Log.e(TAG, "onHandleIntent UPPER");
        timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                connectType = pref.getString("connect_type", "");
                Log.e(TAG, "connectType:   " + connectType);
                if (connectType.equalsIgnoreCase("video")) {
                    Log.e(TAG, "onHandleIntent timer");
                    callServiceToUpdateUI();
                } else {
                    Log.e(TAG, "onstoptime");
                    editor.putBoolean("isbooleanVideo", false);
                    editor.commit();

                    stoptimertask();
                    stopSelf();
                }
            }
        }, 1, 5000);

    }


    private void callServiceToUpdateUI() {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject jsonObject = new JSONObject();

                //  jsonObject.put("coach_id", notificationModel.getCoach_id());
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.GET, Urls.BASE_URL + Urls.CoachAvailability, null,

                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.e(TAG, Urls.CoachAvailability + " web API response  " + response.toString());

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {
//
                                        if ((obj.getString("message").equalsIgnoreCase("available"))) {
                                            editor.putBoolean("isbooleanVideo", true);
                                            editor.commit();
                                            Log.e(TAG, "coach available ");
                                            // stopSelf();
                                        } else {
                                            Log.e(TAG, "coach not available ");
                                            editor.putBoolean("isbooleanVideo", false);
                                            editor.commit();

                                            stoptimertask();
                                            stopSelf();
                                        }
                                    } else {

                                    }

                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    e.printStackTrace();
                                }


                            }
                        }, new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "error----" + error.getMessage());
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
                        params.put("Content-Type", "application/json");
                        params.put("Authorization", "Bearer " + accesstoken);

                        return params;
                    }
                };


                RetryPolicy policy = new DefaultRetryPolicy
                        (50000,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                dashboardRequest.setRetryPolicy(policy);

                AppController.getInstance().

                        addToRequestQueue(dashboardRequest);

            } catch (Exception e) {
                e.getMessage();
//                circularProgressBar.setVisibility(View.GONE);
//                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                e.printStackTrace();
            }

        } else {
            // Toast.makeText(IncomingCallViewActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

}
