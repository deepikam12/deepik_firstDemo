package com.tobaccocessation.api;

/**
 * Created by Ayush.Dimri on 10/5/2017.
 */

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Helper class that is used to provide references to initialized
 * RequestQueue(s) and ImageLoader(s)
 *
 * @author Ognyan Bankov
 *
 */
public class MyVolly {
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static MyVolly mInstance;
    private Context mContext;

    private MyVolly() {
        // no instances
    }


    public static void init(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);

        int memClass = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;
        mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
                cacheSize));
    }


    private MyVolly(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolly getInstance(Context context) {
        // If Instance is null then initialize new Instance
        if(mInstance == null){
            mInstance = new MyVolly(context);
        }
        // Return MySingleton new Instance
        return mInstance;
    }


    public RequestQueue getRequestQueue() {
        if(mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return  mRequestQueue;
        /*if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }*/
    }


    public<T> void addToRequestQueue(Request<T> request){
        // Add the specified request to the request queue
        getRequestQueue().add(request);
    }


    public static ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }





}
