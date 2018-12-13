package com.bestspa.spa.client.Utiles;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.bestspa.spa.client.R;
import com.tt.whorlviewlibrary.WhorlView;

public class UtilFun {

    public static Dialog progressDialog;


    public static void ShowProgres(Context context,String message)
    {
        progressDialog = new Dialog(context);
        final View dialogView = LayoutInflater.from(context).inflate(R.layout.progress_layout_resgistration, null);
        progressDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(dialogView);
        progressDialog.setCancelable(false);
        ((WhorlView)progressDialog.findViewById(R.id.whorl2)).start();
        ((TextView)progressDialog.findViewById(R.id.dialogText)).setText(message);
        progressDialog.show();
    }

    public static void DismissProgress()
    {
        progressDialog.dismiss();
    }

}
