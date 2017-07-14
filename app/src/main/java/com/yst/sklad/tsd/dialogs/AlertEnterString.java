package com.yst.sklad.tsd.dialogs;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;

import com.yst.sklad.tsd.R;
//import com.yst.sklad;
import com.yst.sklad.tsd.services.YesNoInterface;

/**
 * Created by lapenkov on 11.07.2017.
 */

public class AlertEnterString {

    public static void show(final Context context, String title, String message)
    {
        final EditText  input =new EditText(context);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        dialog.setTitle( title)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message);

       // final EditText finalInput = input;
        dialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                     //   ((OrdersActivity)context).mOrderNumber= input.getText().toString();
                      //  ((OrdersActivity)context).DownloadAndExportOrders( input.getText().toString());

                        String text = input.getText()!=null?input.getText().toString():null;

                        ((YesNoInterface)context).ProcessIfYes(new String[]{text});
                        dialoginterface.dismiss();
                    }
                });

        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                dialoginterface.dismiss();
            }
        });


        dialog.setView(input);

        dialog.show();

    }
}
