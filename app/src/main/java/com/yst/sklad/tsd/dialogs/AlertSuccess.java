package com.yst.sklad.tsd.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.yst.sklad.tsd.R;

/**
 * Created by lapenkov on 16.06.2016.
 */
public class AlertSuccess {

    public static void show(Context context,String title, String message)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle( title)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();

    }
}
