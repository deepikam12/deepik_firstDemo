package com.tobaccocessation.BroadcastReceiver;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.model.NotificationModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Deepika.Mishra on 1/29/2019.
 */

public class MyService extends Service {
    private static final String TAG = "MyService";
    String accesstoken;
    SharedPreferences pref;
    NotificationModel notificationModel;
    Timer timer;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "ONCreate");
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.e(TAG, "onStartCommand UPPER");
        timer = new Timer(false);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "onStartCommand timer");
                pref = getSharedPreferences(Config.SHARED_PREF, 0);
                notificationModel = (NotificationModel) intent.getSerializableExtra("notificationData");
                accesstoken = pref.getString("accessToken", null);

                callServiceToUpdateUI();
            }
        }, 1, 20000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();


    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }


    private void callServiceToUpdateUI() {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("coach_id", notificationModel.getCoach_id());

                // circularProgressBar.setVisibility(View.VISIBLE);
//                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.BASE_URL + Urls.CoachStatus, jsonObject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());

                                    ///  circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {

                                        if (!(obj.getString("message").equalsIgnoreCase("free"))) {

                                           // stopSelf();
                                        } else {

                                        }
//                                        Intent i = new Intent(IncomingCallViewActivity.this, VideoActivity.class);
//                                        i.putExtra("notificationData", notificationModel);
//                                        startActivity(i);
//                                        finish();
                                    } else {

//                                        callActivity(status);
//
//                                        utils.showAlertText(IncomingCallViewActivity.this, tv_message, obj.getString("message"));

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    //    circularProgressBar.setVisibility(View.GONE);

                                    e.printStackTrace();

//                                    Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
                                    //   getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }


                            }
                        }, new Response.ErrorListener()

                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RCA", "error----" + error.getMessage());
//                        Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
//                        circularProgressBar.setVisibility(View.GONE);
//                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
