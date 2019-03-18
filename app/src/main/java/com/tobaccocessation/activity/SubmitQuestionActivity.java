package com.tobaccocessation.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Constant;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.adapter.SelectionListAdapter;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.NotificationModel;
import com.tobaccocessation.model.QuestionAnswer;
import com.tobaccocessation.model.SuggestionList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SubmitQuestionActivity extends AppCompatActivity implements View.OnClickListener {
    CircularProgressBar circularProgressBar;
    List<AnwerQuestionList> ans_id;
    TextView tv_retry, tv_message;
    String device_id, accesstoken, token, id, name;
    ImageView iv_navi, iv_chat, iv_video;
    ArrayList<SuggestionList> suggestionList;
    SuggestionList suggestion;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    NotificationModel notificationModel;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);
        circularProgressBar = findViewById(R.id.homeloader);
        initializeIds();
        submitAnswer();
        pref = getSharedPreferences(Config.SHARED_PREF, 0);
        LocalBroadcastManager.getInstance(SubmitQuestionActivity.this).registerReceiver((receiver), new IntentFilter("com.INCOMING_CALL.CHAT"));
        editor = pref.edit();
        iv_navi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubmitQuestionActivity.this, Landing_3Activity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                startActivity(i);

            }
        });

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

    private void initializeIds() {
        iv_navi = findViewById(R.id.iv_navi);
        iv_video = findViewById(R.id.iv_video);
        iv_chat = findViewById(R.id.iv_chat);
        tv_retry = findViewById(R.id.tv_retry);
        tv_message = findViewById(R.id.tv_empty);
        iv_chat.setOnClickListener(this);
        iv_video.setOnClickListener(this);
        ans_id = new ArrayList<AnwerQuestionList>();
        utils = new Utils(this);
        AnwerQuestionList answerList = new AnwerQuestionList();
        tv_retry.setOnClickListener(this);
        device_id = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        token = pref.getString("regId", null);
        accesstoken = pref.getString("accessToken", null);
        try {
            id = getIntent().getStringExtra("ques_id");
            name = getIntent().getStringExtra("name");
            ans_id = (List<AnwerQuestionList>) getIntent().getSerializableExtra("ansList");
            answerList.setQues_id(getIntent().getStringExtra("ques_id"));
            answerList.setAns(getIntent().getStringExtra("name"));
            answerList.setAns_id(SelectionListAdapter.ans_id);
            ans_id.add(answerList);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent i = new Intent(SubmitQuestionActivity.this, Landing_3Activity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    public void submitAnswer() {
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


                JSONObject answerObj = new JSONObject();
                answerObj.put("device_id", device_id);
                answerObj.put("device_token", Question1Activity.token);
                answerObj.put("answerList", array);

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

                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                        JSONArray jsonArray1 = obj.getJSONArray("result");
                                        suggestionList = new ArrayList<>();
                                        for (int j = 0; j < jsonArray1.length(); j++) {
                                            JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                            suggestion = new SuggestionList();
                                            suggestion.setTitle(jsonObject1.getString("title"));
                                            suggestion.setContent(jsonObject1.getString("suggestion"));
                                            suggestion.setSugg_id(jsonObject1.getString("suggestion_id"));

                                            suggestionList.add(suggestion);


                                        }

                                        tv_retry.setVisibility(View.GONE);

                                        // Toast.makeText(SubmitQuestionActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        // utils.showAlertText(SubmitQuestionActivity.this, tv_message, obj.getString("message"));

                                        Intent i = new Intent(SubmitQuestionActivity.this, SuggestionActivity.class);
                                        i.putExtra("ansList", (Serializable) ans_id);
                                        i.putExtra("name", name);
                                        i.putExtra("ques_id", id);
                                        i.putExtra("suggestionList", (Serializable) suggestionList);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    } else {
                                        tv_retry.setVisibility(View.VISIBLE);

                                        //   Toast.makeText(SubmitQuestionActivity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        utils.showAlertText(SubmitQuestionActivity.this, tv_message, obj.getString("message"));

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    tv_retry.setVisibility(View.VISIBLE);
                                    circularProgressBar.setVisibility(View.GONE);
                                    //  utils.showAlertText(SubmitQuestionActivity.this, tv_message, e.getMessage());

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
                        //   utils.showAlertText(SubmitQuestionActivity.this, tv_message, error.getMessage());

                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        tv_retry.setVisibility(View.VISIBLE);
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

                TwilioChatApplication.getInstance().addToRequestQueue(dashboardRequest);

            } catch (Exception e) {
                e.printStackTrace();
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            }

        } else {
            tv_retry.setVisibility(View.VISIBLE);
            //Toast.makeText(SubmitQuestionActivity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            utils.showAlertText(SubmitQuestionActivity.this, tv_message, getResources().getString(R.string.no_internet));

        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_retry:
                submitAnswer();
                break;

            case R.id.iv_video:
              Intent  i = new Intent(SubmitQuestionActivity.this, VideoStatusActivity.class);
                startActivity(i);
                break;
        }
    }
}
