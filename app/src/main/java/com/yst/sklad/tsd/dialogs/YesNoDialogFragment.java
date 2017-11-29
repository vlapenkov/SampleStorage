package com.yst.sklad.tsd.dialogs;

import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.YesNoInterface;

/**
 * Created by lapenkov on 16.06.2016.
 */
public  class YesNoDialogFragment extends DialogFragment {
    public static void show(final Context context, String title, final Object[] params)
    {

        android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(context);

        dialog.setTitle( title)
                .setIcon(R.drawable.ic_launcher);
        //        .setMessage(message);

        // final EditText finalInput = input;
        dialog.setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                //   ((OrdersActivity)context).mOrderNumber= input.getText().toString();
                //  ((OrdersActivity)context).DownloadAndExportOrders( input.getText().toString());

                ((YesNoInterface)context).ProcessIfYes(params);
                dialoginterface.dismiss();
            }
        });

        dialog.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                dialoginterface.dismiss();
            }
        });


        dialog.show();

    }
}
/*



public  class YesNoDialogFragment extends DialogFragment {
    Object[] _params;
    public static YesNoDialogFragment newInstance(int title,Object[] params) {
//        _params=params;
        YesNoDialogFragment frag = new YesNoDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                ((YesNoInterface) getActivity()).ProcessIfYes( params);
                            }
                        }
                )
                .setNegativeButton(R.string.no, null

                )
                .create();
    }


}*/
