package com.tobaccocessation.chat.accesstoken;

import android.app.Application;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import com.tobaccocessation.chat.ChatClientManager;


public class TwilioChatApplication extends Application {
  private ChatClientManager basicClient;
 public static final String TAG ="TwilioChatApplication";
  private static RequestQueue mRequestQueue;
  private static ImageLoader mImageLoader;
 private static TwilioChatApplication mInstance;
  private static final String DEFAULT_CACHE_DIR = "mypicphotos";
  private int width;
  private int height;
  public TwilioChatApplication() {
    Log.v(TAG, "AppController");
  }

  public static final boolean DEVELOPER_MODE = false;
  public static View view;
  public static TwilioChatApplication get() {
    return mInstance;
  }

  public ChatClientManager getChatClientManager() {
    return this.basicClient;
  }

  @Override
  public void onCreate() {
    super.onCreate();

    TwilioChatApplication.mInstance =this;
    basicClient = new ChatClientManager(getApplicationContext());

    int SDK_INT = Build.VERSION.SDK_INT;
    if (SDK_INT > 9) {
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
              .permitAll().build();
      StrictMode.setThreadPolicy(policy);
    }
    if (DEVELOPER_MODE
            && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
              .detectAll().penaltyDialog().build());
      StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
              .detectAll().penaltyDeath().build());
    }
  }



  public static synchronized TwilioChatApplication getInstance() {
    return mInstance;
  }

  public static RequestQueue getRequestQueue() {
    if (mRequestQueue == null) {
      mRequestQueue = Volley.newRequestQueue(mInstance);

    }
    return mRequestQueue;
  }

  public <T> void addToRequestQueue(Request<T> req, String tag) {
    // set the default tag if tag is empty
    req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
    getRequestQueue().add(req);
  }

  public <T> void addToRequestQueue(Request<T> req) {
    req.setTag(TAG);
    getRequestQueue().add(req);
  }

  public int getWidth() {

    return width;
  }

  public void setWidth(int aWidth) {

    width = aWidth;

  }

  public int getHeight() {

    return height;
  }

  public void setHeight(int aHeight) {

    height = aHeight;
  }
}
