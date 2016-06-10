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

    public void importAllStoragesAndCells(View v)
    {
        if (checkConnectivity())
            new DownloadAndImportStockCells().execute(stringUrlStoragesAndCells);

    }

    public void importAllProducts(View v)
    {
        if (checkConnectivity())
            new DownloadAndImportProducts().execute(stringUrlProducts);


    }

    public void importShipments(View v)
    {
        if (checkConnectivity())
            new DownloadAndImportShipments().execute(stringUrlShipments);


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
                dbHelper.addProduct(id,name,barcode,"",productType);

            }
            return (long)lines.length;
        }

    }
    private class DownloadAndImportStockCells extends AsyncTask<String, Integer, Long> {
        @Override
        protected void onProgressUpdate(Integer... values) {
           // super.onProgressUpdate(values);
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            //super.onPostExecute(aLong);
            mProgressBar.setProgress(100);
            alertView("Ячейки в количестве "+Long.toString(aLong)+" загружены");

           }

        @Override
        protected Long doInBackground(String... urls) {
            String[] lines;
            ProductsDbHelper dbHelper;
            // params comes from the execute() call: params[0] is the url.
            try {
                String result =  downloadUrl(urls[0]);

                 dbHelper = new ProductsDbHelper(getBaseContext());
                dbHelper.clearTable(ProductsContract.StorageEntry.TABLE_NAME);
                dbHelper.clearTable(ProductsContract.StockCellEntry.TABLE_NAME);
                List<String> listofstorages = new ArrayList<String>() ;
                lines=   result.split(System.getProperty("line.separator"));

                int counter=0;
                for (String line:lines
                        ) {
                    counter++;
                    if (counter==1) { continue;}

                    if (counter%10==0)
                       publishProgress((int) ((counter / (float) lines.length) * 100));
                    String[] arr=line.split(";");

                    String storage = arr[0];
                    String cellname = arr[1];
                    String barcode = arr[3];

                    // add storege
                    if (!listofstorages.contains(storage))
                    {
                        listofstorages.add(storage);
                        dbHelper.addStorage(storage);
                    }
                    // add stockcell
                    dbHelper.addStockCell(barcode,cellname,storage);


                }

            } catch (IOException e) {
                return (long)-1;
            }finally {

            }
            return (long)lines.length;
        }

    }

    private class DownloadAndImportShipments extends AsyncTask<String, Integer, Long> {
        @Override
        protected Long doInBackground(String... params) {

            InputStream stream = SoapCallToWebService.Call(stringUrlShipments);


            XMLDOMParser parser = new XMLDOMParser();




            List<Shipment> listOfShipments = new ArrayList<Shipment>();
            List<ShipmentItem> listOfShipmentItems = new ArrayList<ShipmentItem>();

            Document doc = parser.getDocument(stream);

            NodeList nodeListOrders = doc.getElementsByTagName("Orders");

            for(int j=0; j< nodeListOrders.getLength();j++) {
                Element e = (Element) nodeListOrders.item(j);

                String numberin1s = parser.getValue(e, "numberin1s");
                String dateofshipment = parser.getValue(e, "dateofshipment");
                String client = parser.getValue(e, "client");
                String comment = parser.getValue(e, "comment");

                String cleanId = Shipment.getCleanId(numberin1s);
                Shipment shipmentToAdd =new Shipment(cleanId,dateofshipment,client,comment);
                listOfShipments.add(shipmentToAdd);
                Log.d("shipment added", shipmentToAdd.toString());


                NodeList nodeListProducts =e.getElementsByTagName("Products");
                for(int k=0; k< nodeListProducts.getLength();k++) {
                    {
                        Element p = (Element) nodeListProducts.item(k);
                        Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                        Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                        String stockcell = parser.getValue(p, "stockcell");
                        Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
                        ShipmentItem shipmentItemToAdd =new ShipmentItem(cleanId,rownumber,productid,stockcell,quantity);
                        listOfShipmentItems.add(shipmentItemToAdd);
                        Log.d("shipment added", listOfShipmentItems.toString());

                    }}

            }

            Log.d("shipmentsitems size", String.valueOf(listOfShipmentItems.size()));
            Log.d("shipments size", String.valueOf(listOfShipments.size()));


            ProductsDbHelper dbHelper = new ProductsDbHelper(getBaseContext());


            for (Shipment shipment : listOfShipments)
            {
                if (!dbHelper.checkIfShipmentExists(shipment.Id))
                dbHelper.addShipment(shipment);

            }

            for (ShipmentItem shipmentItem : listOfShipmentItems)
            {
                if (dbHelper.checkIfShipmentExists(shipmentItem.ShipmentId) && !dbHelper.checkIfShipmentItemsExistByShipmentAndProduct(shipmentItem.ShipmentId,shipmentItem.ProductId))
                    dbHelper.addShipmentItem(shipmentItem);

            }


            return null;


        }
    }
    private String downloadUrl(String url) throws IOException {
        return  TextReaderFromHttp.readTextArrayFromUrl(url);
    }






}
