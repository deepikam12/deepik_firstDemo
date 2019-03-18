package com.tobaccocessation.api;

/**
 * Created by Ayush.Dimri on 10/5/2017.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


public class AppController extends android.app.Application {
    public static final String TAG = AppController.class.getSimpleName();
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static AppController mInstance;
    private static final String DEFAULT_CACHE_DIR = "mypicphotos";
    private int width;
    private int height;
    public AppController() {
        Log.v(TAG, "AppController");
    }

    public static final boolean DEVELOPER_MODE = false;
    public static View view;
    public static Bitmap bm = null;

    @SuppressWarnings("unused")
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        //MyVolly.init(this);


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

    }// endOncrea

    public static synchronized AppController getInstance() {
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
