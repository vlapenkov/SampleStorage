package com.example.user.sample1.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import com.example.user.sample1.R;
import com.example.user.sample1.activities.ShipmentsActivity;

/**
 * Created by lapenkov on 16.06.2016.
 */
public  class YesNoDialogFragment extends DialogFragment {

    public static YesNoDialogFragment newInstance(int title) {
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
                                ((ShipmentsActivity) getActivity()).doPositiveClick();
                            }
                        }
                )
                .setNegativeButton(R.string.no, null
                    /*    new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((ShipmentsActivity)getActivity()).doNegativeClick();
                            }
                        }*/
                )
                .create();
    }


}
