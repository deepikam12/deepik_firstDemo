package com.tobaccocessation.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;

import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.R;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;
import static android.content.ContentValues.TAG;

/**
 * Created by Ayush.Dimri on 6/7/2018.
 */

public class Utils {
    public static String MY_PREFS_NAME = "TOBACCO_CESSATION_Prefs";
    Context context;
    private Activity activity;
    private String code = "";
    private String status = "";
    ;

Snackbar snackBar;
    public Utils(Activity activity) {
        this.activity = activity;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
            }
        }
        return false;
    }
    public static boolean validatePassword(String password) {
        Pattern specailCharPatten = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        Pattern UpperCasePatten = Pattern.compile("[A-Z ]");
        Pattern lowerCasePatten = Pattern.compile("[a-z ]");
        Pattern digitCasePatten = Pattern.compile("[0-9 ]");

        if (!specailCharPatten.matcher(password).find() || !UpperCasePatten.matcher(password).find() || !lowerCasePatten.matcher(password).find() || !digitCasePatten.matcher(password).find()) {
            return false;
        }
        return true;
    }

    public static void show_alert(Context c, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {


                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public  void showAlertText(final Context context, final TextView tv_message, String message){
        Timer t = new Timer(false);
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        tv_message.setVisibility(View.GONE);
                    }
                });
            }
        }, 5000);
        tv_message.setVisibility(View.VISIBLE);
        tv_message.setText(message);
    }

    public static boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public void setInboxNotificationCount(TextView ncount, Context context)
    {
        int c=0;int nCount=0,mCount=0;
        try {
            SharedPreferences pref = context.getSharedPreferences(Utils.MY_PREFS_NAME, Context.MODE_PRIVATE);

            c= Integer.parseInt( pref.getString(Constant.BAGDE_COUNT, ""));

            if (c> 0) {
//                if(c==1)
//                {
//                    ncount.setText(String.valueOf(c));
//                    ncount.setVisibility(View.VISIBLE);
//
//                }
//                else {
//                    int count = c + 1;
                ncount.setText(String.valueOf(c));
                ncount.setVisibility(View.VISIBLE);
                // }
            } else {
                ncount.setVisibility(View.INVISIBLE);
            }
        }
        catch(Exception e)
        {
            System.out.print(e);
        }
    }
    /*
         createdBy: Ankit
         createdDate: 6/8/2018
         Description: This method is used to show toast message.
     */
    public static void showToast(Context con, String message) {
        Toast.makeText(con, message, Toast.LENGTH_SHORT).show();
    }



    public  static  void videoClickstatus(String errorType, TextView tv_setStatus){


    }

    public static void hideKeyboard(Activity context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            Log.e(TAG, "hidekeyboard" + e.getMessage());
        }

    }
/* setting data in shared preferences
 */
    public void setPrefrence(String key, String value) {
        SharedPreferences prefrence = context.getSharedPreferences(
                context.getString(R.string.app_name), 0);
        SharedPreferences.Editor editor = prefrence.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /*
     * retreiving the data from shared preferences     */
    public String getPrefrence(String key) {
        SharedPreferences prefrence = context.getSharedPreferences(
                context.getString(R.string.app_name), 0);
        String data = prefrence.getString(key, "");
        return data;
    }


    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private static SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }


}