package com.tobaccocessation.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.opengl.Visibility;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
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
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.Utils.Urls;
import com.tobaccocessation.Utils.Utils;
import com.tobaccocessation.api.AppController;
import com.tobaccocessation.chat.accesstoken.TwilioChatApplication;
import com.tobaccocessation.model.Answers;
import com.tobaccocessation.model.AnwerQuestionList;
import com.tobaccocessation.model.QuestionAnswer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class Landing_3Activity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_next, head, msg, tv_username, tv_message;
    ImageView iv_contact, iv_logout;
    SharedPreferences.Editor editor;
    SharedPreferences pref;
    Boolean isLogin;
    ArrayList<QuestionAnswer> qnaList;
    ArrayList<Answers> answersList;
    CircularProgressBar circularProgressBar;
    Answers answers;
    String device_id, accesstoken, token;
    Boolean isDataLoaded = false;
    static List<AnwerQuestionList> ans_id;
    Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_3);
        pref = getSharedPreferences(Config.SHARED_PREF, MODE_PRIVATE);
        findviewIds();
        set_Font();


    }

    @Override
    protected void onResume() {
        super.onResume();
        isLogin = pref.getBoolean("isLogin", false);
        if (isLogin == true) {
            tv_username.setText(pref.getString("first_name", ""));
            iv_logout.setVisibility(View.VISIBLE);
        } else {
            iv_logout.setVisibility(View.GONE);
            tv_username.setText("");
        }
        utils = new Utils(this);
        device_id = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        token = pref.getString("token", null);

        accesstoken = pref.getString("accessToken", null);
       // getMessagingDetailData();
    }

    private void findviewIds() {
        qnaList = new ArrayList<>();
        circularProgressBar = findViewById(R.id.homeloader);
        tv_message = findViewById(R.id.tv_empty);
        iv_contact = findViewById(R.id.iv_contact);
        tv_username = findViewById(R.id.tv_username);
        iv_logout = findViewById(R.id.iv_logout);
        head = (TextView) findViewById(R.id.head);
        msg = (TextView) findViewById(R.id.msg);
        tv_next = (TextView) findViewById(R.id.tv_next);
        tv_next.setOnClickListener(this);
        iv_logout.setOnClickListener(this);
        iv_contact.setOnClickListener(this);
       // tv_next.setClickable(false);
        isLogin = pref.getBoolean("isLogin", false);
        if (isLogin == true) {
            tv_username.setText(pref.getString("first_name", ""));
            iv_logout.setVisibility(View.VISIBLE);
        } else {
            iv_logout.setVisibility(View.GONE);
            tv_username.setText("");
        }
        device_id = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        SharedPreferences pref = getSharedPreferences(Config.SHARED_PREF, 0);
        token = pref.getString("token", null);

        accesstoken = pref.getString("accessToken", null);
    }

    public void set_Font() {
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Bold.TTF");
        head.setTypeface(tf);
        tv_next.setTypeface(tf);

        Typeface tyf = Typeface.createFromAsset(getAssets(), "fonts/NewsGothicBT Roman.TTF");
        msg.setTypeface(tyf);
    }


    //calling web service for Question...
    public void getMessagingDetailData() {
        //  int connectStatus = ConnectivityReceiver.isConnected(MessagingActivity.this);
        if (Utils.isConnectingToInternet(this)) {

            try {
                if (isDataLoaded == true) {
                    //  circularProgressBar.setVisibility(View.VISIBLE);
                } else
                    circularProgressBar.setVisibility(View.GONE);
                qnaList.clear();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.GET, Urls.BASE_URL + Urls.questionList, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    circularProgressBar.setVisibility(View.GONE);

                                    Log.i("RCA", response.toString());

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {
                                        JSONArray jsonArray = obj.getJSONArray("result");
                                        // qnaList.clear();
                                        if (jsonArray.length() > 0) {
                                            int i = 0;
                                            for (i = 0; i < jsonArray.length(); i++) {

                                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                JSONObject jObject = jsonObject.getJSONObject("asset");
                                                QuestionAnswer qna = new QuestionAnswer();
                                                qna.setQuestion(jObject.getString("question"));
                                                qna.setQuestion_type(jObject.getString("question_type"));
                                                qna.setInput_type(jObject.getString("input_type"));
                                                qna.setMandatory(jObject.getString("mandatory"));
                                                qna.setId(jObject.getString("id"));
                                                qna.setAbout(jObject.getString("about"));
                                                qna.setIcon(jObject.getString("icon"));

                                                JSONArray jsonArray1 = jObject.getJSONArray("answer");
                                                answersList = new ArrayList<>();
                                                for (int j = 0; j < jsonArray1.length(); j++) {
                                                    JSONObject jsonObject1 = jsonArray1.getJSONObject(j);
                                                    answers = new Answers();
                                                    answers.setAnsId(jsonObject1.getString("ans_id"));
                                                    answers.setAnswer(jsonObject1.getString("ans"));
                                                    answersList.add(answers);

                                                }


                                                qna.setAnswersList(answersList);
                                                qnaList.add(qna);

                                            }
                                            tv_next.setClickable(true);
                                            if (pref.getBoolean("isLogin", false) == true) {
                                                Intent intent = new Intent(Landing_3Activity.this, RetakeSurveyActivity.class);
                                                intent.putExtra("questionList", qnaList);
                                                startActivity(intent);

                                            } else {
                                                Intent intent = new Intent(Landing_3Activity.this, HaveAccountActivity.class);
                                                intent.putExtra("questionList", qnaList);
                                                startActivity(intent);
                                            }

                                            //  finish();
                                        } else {
                                            //   Toast.makeText(Terms_and_CondActivity.this, getString(R.string.no_data), Toast.LENGTH_SHORT).show();
                                        }
                                    }


                                } catch (Exception e) {

                                    //Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                    utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));
                                    Log.i("RCA", "error----" + e.getMessage());
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    e.printStackTrace();
                                    //Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                                    utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));

//                                    Constants.showAlert(coordinatorLayout, MessagingActivity.this, true, "Sorry!", "Unable to refresh data");
                                    circularProgressBar.setVisibility(View.GONE);
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
                        // Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                        utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));

                        if (error.networkResponse == null) {
                            if (error.getClass().equals(TimeoutError.class)) {
                            }
                        }
                    }
                });

//                RetryPolicy policy = new DefaultRetryPolicy
//                        (20000,
//                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//                dashboardRequest.setRetryPolicy(policy);
                dashboardRequest.setRetryPolicy   (new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                TwilioChatApplication.getInstance().addToRequestQueue(dashboardRequest);

            } catch (Exception e) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                //  Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));

                e.printStackTrace();
            }

        } else {
            // Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.no_internet));

        }

    }


    /*Logout: Clear all local storage and hit webservice to logout and redirect to Splash Screen*/
    void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Setting message manually and performing action on button click
        builder.setMessage("Are you sure you want to log out?")
                .setCancelable(true)
                .setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Servicelogout();
                        dialog.dismiss();

                    }
                })
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();
                    }
                });

        //Creating dialog box
        final AlertDialog alert = builder.create();
        //Setting the title manually
        //alert.setTitle("Log Out");
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg0) {
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.logout_color));
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.logout_color));
            }
        });

        alert.show();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_next:
                getMessagingDetailData();
                isDataLoaded = true;
//                if (pref.getBoolean("isLogin", false) == true) {
//                    Intent intent = new Intent(Landing_3Activity.this, RetakeSurveyActivity.class);
//                    intent.putExtra("questionList", qnaList);
//                    startActivity(intent);
//
//                } else {
//                    Intent intent = new Intent(Landing_3Activity.this, HaveAccountActivity.class);
//                    intent.putExtra("questionList", qnaList);
//                    startActivity(intent);
//                }
//                Intent i = new Intent(Landing_3Activity.this, HaveAccountActivity.class);
//                startActivity(i);
                break;

            case R.id.iv_logout:
                logout();
                break;

            case R.id.iv_contact:
                Intent i = new Intent(Landing_3Activity.this, Question1_DrawerActivity.class);
                startActivity(i);
                break;

        }
    }


    public void Servicelogout() {
        if (Utils.isConnectingToInternet(this)) {
            try {

                JSONObject answerObj = new JSONObject();
                answerObj.put("token_id", token);
                //circularProgressBar.setVisibility(View.VISIBLE);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                JsonObjectRequest dashboardRequest = new JsonObjectRequest(Request.Method.POST, Urls.logout, answerObj,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    Log.i("RCA", response.toString());
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                    circularProgressBar.setVisibility(View.GONE);

                                    JSONObject obj = new JSONObject(response.toString());

                                    if (obj.getString("status").equalsIgnoreCase("success")) {

                                        // Toast.makeText(Landing_3Activity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        //  utils.showAlertText(Landing_3Activity.this, tv_message,  obj.getString("message"));

                                        editor = pref.edit();

                                        editor.putBoolean("isLogin", false);
                                        editor.putString("first_name", "");
                                        editor.putString("mobile", "");
                                        editor.putString("email", "");
                                        editor.putString("accessToken", "");
                                        editor.putString("token", "");
                                        editor.putString("connect_type", "");
                                        editor.putString("isbooleanVideo", "");
                                        editor.commit();
                                        iv_logout.setVisibility(View.GONE);
                                        tv_username.setText("");
//                                        Intent i = new Intent(Landing_3Activity.this, SuggestionActivity.class);
//
//                                        //   i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                                        startActivity(i);
                                    } else {
                                        utils.showAlertText(Landing_3Activity.this, tv_message, obj.getString("message"));


                                        //     Toast.makeText(Landing_3Activity.this, obj.getString("message"), Toast.LENGTH_SHORT).show();

                                    }


                                } catch (Exception e) {
                                    Log.i("RCA", "error----" + e.getMessage());
                                    circularProgressBar.setVisibility(View.GONE);
                                    utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));

                                    e.printStackTrace();
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                                }


                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("RCA", "error----" + error.getMessage());
                        utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.server_error));
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        circularProgressBar.setVisibility(View.GONE);
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
            }

        } else {
            utils.showAlertText(Landing_3Activity.this, tv_message, getResources().getString(R.string.no_internet));
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            Toast.makeText(Landing_3Activity.this, getResources().getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }

    }
}
