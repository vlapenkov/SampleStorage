package com.example.user.sample1.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsContract;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.data.Shipment;
import com.example.user.sample1.data.ShipmentItem;
import com.example.user.sample1.services.SoapCallToWebService;
import com.example.user.sample1.services.TextReaderFromHttp;
import com.example.user.sample1.services.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LoadEntitiesActivity extends AppCompatActivity {

    ProgressBar mProgressBar;
    private static final String stringUrlStoragesAndCells="http://yst.ru/data/Stores.txt";
    private static final String stringUrlProducts="http://yst.ru/data/Products.txt";
    private static final String stringUrlShipments="http://37.1.84.50:8080/YST/ws/ServiceTransfer";

    private void alertView( String message ) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle( "Загрузка завершена" )
                .setIcon(R.drawable.sun)
                .setMessage(message)
//  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//      public void onClick(DialogInterface dialoginterface, int i) {
//          dialoginterface.cancel();
//          }})
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_entities);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
    }


    private boolean checkConnectivity()
    {

        mProgressBar.setProgress(0);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        return false;

    }

   /* public void importAllStoragesAndCells(View v)
    {

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        if (checkConnectivity())
                            new DownloadAndImportStockCells().execute(stringUrlStoragesAndCells);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.import_stockcells_message).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();



    } */

    public void importAllProducts(View v)
    {
        if (checkConnectivity())
            new DownloadAndImportProducts().execute(stringUrlProducts);


    }



/*
Загрузка товаров
 */
    private class DownloadAndImportProducts extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);

        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            mProgressBar.setProgress(100);
            alertView("Товары в количестве "+Long.toString(aLong)+" загружены");
        }

        @Override
        protected Long doInBackground(String... params) {
            String[] lines;
            ProductsDbHelper dbHelper;

            String result = null;
            try {
                result = downloadUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dbHelper = new ProductsDbHelper(getBaseContext());


            dbHelper.clearTable(ProductsContract.ProductsEntry.TABLE_NAME);

            lines=   result.split(System.getProperty("line.separator"));

            int counter=0;
            for (String line:lines ){
                counter++;
                if (counter==1) { continue;}

                if (counter%10==0)
                    publishProgress((int) ((counter / (float) lines.length) * 100));
                String[] arr=line.split(";");

                Log.d("TAGIMPORT",arr[0]);
                int id  = Integer.parseInt(arr[0]);

               /* if (dbHelper.checkIfProductExists(id))

                    continue; */
                String name = arr[1];
                String barcode = arr[2];
                int productType = Integer.parseInt(arr[3]);
               // dbHelper.addProduct(id,name,barcode,"",productType);

            }
            return (long)lines.length;
        }

    }



    private String downloadUrl(String url) throws IOException {
        return  TextReaderFromHttp.readTextArrayFromUrl(url);
    }






}
