package com.tobaccocessation.chat.messages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tobaccocessation.R;


public class AlertDialogHandler {
  public static void displayAlertWithMessage(String message, Context context) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message).setCancelable(false).setPositiveButton("OK", null);

    AlertDialog alert = builder.create();
    alert.show();
  }

  public static void displayCancellableAlertWithHandler(String message, Context context,
                                                        DialogInterface.OnClickListener handler) {
    AlertDialog.Builder builder =
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.AlertDialogCustom));
    builder.setMessage(message).setPositiveButton("OK", handler).setNegativeButton("Cancel", null);

    AlertDialog alert = builder.create();
    alert.show();
  }


}
