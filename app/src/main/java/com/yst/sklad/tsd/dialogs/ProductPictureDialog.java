package com.yst.sklad.tsd.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.yst.sklad.tsd.R;

import java.io.InputStream;
import java.net.URL;

/**
 *
 * Диалог показывает товар из урл
 */
public class ProductPictureDialog extends DialogFragment implements View.OnClickListener {
    private static final String TAG="ProductPictureDialog";

    Bitmap bitmap;
    String mProductId ;
    ImageView mImg;
    String mUrlPrefix ="http://terminal.yst.ru/customforpartners/productpicture.ashx?productid=";

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        getDialog().setTitle(R.string.picture_of_product);
        View v = inflater.inflate(R.layout.dialog_picture, null);
        mProductId = getArguments().getString("productId");
        return v;
    }


    public void onClick(View v) {

        dismiss();
    }

    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

    }

    @Override
    public void onResume() {
        super.onResume();
        mImg = (ImageView)getView().findViewById(R.id.ivImg);
        mImg.setImageResource(R.drawable.ic_loading);
        //http://store.yst.ru/customforpartners/productpicture.ashx?productid=9174661
        new LoadImage().execute(mUrlPrefix+mProductId);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {



        protected Bitmap doInBackground(String... args) {
            try {
                Log.d(TAG +"/path of picture",args[0]);
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {
            mImg = (ImageView)getView().findViewById(R.id.ivImg);
            if(image != null)    mImg.setImageBitmap(image);
            else mImg.setImageResource(R.drawable.nophoto);


    }
}
}
