package com.bestspa.spa.client.Views;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.res.Resources;
import android.widget.TextView;

import com.bestspa.spa.client.R;

public class AlertDialogManager {
    private AlertDialog alertDialog;

    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        Builder builder = new Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("OK", null);
        if (status != null) {
            builder.setIcon(status.booleanValue() ? R.drawable.successd : R.drawable.fail);
        }
        this.alertDialog = builder.create();
        this.alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        this.alertDialog.show();
        this.alertDialog.getWindow().setLayout(-2, -2);
        TextView messageView = (TextView) this.alertDialog.findViewById(16908299);
        if (messageView != null) {
            messageView.setGravity(17);
        }
        messageView.setTextSize(16.0f);
        messageView.setTextColor(context.getResources().getColor(R.color.black));
        Resources resources = this.alertDialog.getContext().getResources();
        int color = resources.getColor(R.color.greenTxt);
        ((TextView) this.alertDialog.getWindow().getDecorView().findViewById(resources.getIdentifier("alertTitle", "id", "android"))).setTextColor(color);
//        this.alertDialog.getWindow().getDecorView().findViewById(resources.getIdentifier("titleDivider", "id", "android")).setBackgroundColor(color);
        this.alertDialog.getButton(-1).setTextColor(context.getResources().getColor(R.color.black));
    }

    public void Dismiss() {
        this.alertDialog.dismiss();
    }
}

