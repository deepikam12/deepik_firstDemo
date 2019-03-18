package com.tobaccocessation.chat.accesstoken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.provider.Settings;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.tobaccocessation.FCM.Config;
import com.tobaccocessation.R;
import com.tobaccocessation.chat.listeners.TaskCompletionListener;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AccessTokenFetcher {
  SharedPreferences sharedPreferences;
String chat_access_token;
  private Context context;

  public AccessTokenFetcher(Context context) {
    this.context = context;
  }

  public void fetch(final TaskCompletionListener<String, String> listener) {
    sharedPreferences=context.getSharedPreferences(Config.SHARED_PREF, 0);
    chat_access_token=sharedPreferences.getString("chat_accessToken","");
   // JSONObject obj = new JSONObject(getTokenRequestParams(context));
    String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

    String requestUrl = "https://veronica-owl-4822.twil.io/chat-token"+ "?device=" + deviceId + "&identity=" + "katreena";

    JsonObjectRequest jsonObjReq =
        new JsonObjectRequest(Request.Method.POST, requestUrl, null, new Response.Listener<JSONObject>() {

          @Override
          public void onResponse(JSONObject response) {
            String token = null;
            try {
              token = response.getString("token");
            } catch (JSONException e) {
              e.printStackTrace();
              listener.onError("Failed to parse token JSON response");
            }
            listener.onSuccess(token);
          }
        }, new Response.ErrorListener() {

          @Override
          public void onErrorResponse(VolleyError error) {

            listener.onError("Failed to fetch token");
          }
        });
    jsonObjReq.setShouldCache(false);
    TwilioChatApplication.getInstance().addToRequestQueue(jsonObjReq);

  }


}
