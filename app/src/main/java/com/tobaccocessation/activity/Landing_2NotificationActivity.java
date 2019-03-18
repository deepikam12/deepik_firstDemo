package com.tobaccocessation.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tobaccocessation.R;

public class Landing_2NotificationActivity extends AppCompatActivity implements View.OnClickListener {
    Dialog dialog;
    TextView tv_dont_allow, tv_ok;
    private static final int NOTIFICATION_PERMISSION_CODE = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_2notification);
        callDailogBox();
        set_Font();
      //  requestNotificationPermission();

        //some code

    }
    public void set_Font()
    {
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/NewsGothicBT Bold.TTF");
        tv_dont_allow.setTypeface(tf);
        tv_ok.setTypeface(tf);
    }



    private void requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY) == PackageManager.PERMISSION_GRANTED)
            return;

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NOTIFICATION_POLICY)) {

        }

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NOTIFICATION_POLICY}, NOTIFICATION_PERMISSION_CODE );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == NOTIFICATION_PERMISSION_CODE ) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void callDailogBox() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_notification_dialog);
        tv_ok = (TextView) dialog.findViewById(R.id.tv_ok);
        tv_dont_allow = (TextView) dialog.findViewById(R.id.tv_dont_allow);
        tv_ok.setOnClickListener(this);
        tv_dont_allow.setOnClickListener(this);
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        {
            switch (v.getId()) {
                case R.id.tv_ok:
                    Intent i = new Intent(Landing_2NotificationActivity.this, Landing_3Activity.class);
                    startActivity(i);
                    break;
                case R.id.tv_dont_allow:
                    dialog.dismiss();
                    break;
                default:
                    break;
            }

        }

    }


}