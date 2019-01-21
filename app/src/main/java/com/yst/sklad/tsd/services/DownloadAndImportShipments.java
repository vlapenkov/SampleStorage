package com.yst.sklad.tsd.services;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.ShipmentsDownloadHelper;
import com.yst.sklad.tsd.Utils.XMLDOMParser;
import com.yst.sklad.tsd.activities.ShipmentsActivity;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.Shipment;
import com.yst.sklad.tsd.data.ShipmentItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lapenkov on 16.01.2019.
 */

public    class DownloadAndImportShipments extends AsyncTask<Void, Integer, Long> {
    private static final String TAG = "DownloadAndImportShipments";
    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_SHIPMENTS;
    private boolean mNewShipmentsWasAdded = false;
    Context mContext;
    ProgressDialog mProgress;


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    //  private MainActivity activity;

    public DownloadAndImportShipments(Context context)
    {
        mContext=context;
        int counter=0;


    }

    /*
    Если ошибка при загрузке то возвращается null, который обрабатывается в  onPostExecute
     */
    @Override
    protected Long doInBackground(Void... params) {

        ProductsDbHelper dbHelper = ((MainApplication)mContext.getApplicationContext()).getDatabaseHelper();


            InputStream stream = SoapCallToWebService.receiveCurrentShipments();

        if (stream==null) return null;


        XMLDOMParser parser = new XMLDOMParser();

        List<Shipment> listOfShipments = new ArrayList<Shipment>();
        List<ShipmentItem> listOfShipmentItems = new ArrayList<ShipmentItem>();

        try {
           //if (true) throw new Exception("123");
            Document doc = parser.getDocument(stream);

            NodeList nodeListOrders = doc.getElementsByTagName("Orders");

            long ordersCount = nodeListOrders.getLength();


            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            Boolean onlySelectedStorages = preferences.getBoolean("onlySelectedStorages", false);
            Set<String> arrayOfStorages = preferences.getStringSet("storagesArray", new HashSet<String>());

            long counter = 0;

            for (int j = 0; j < nodeListOrders.getLength(); j++) {
                Element e = (Element) nodeListOrders.item(j);

                mProgress.setProgress((int) Math.floor(counter * 100 / ordersCount));

                String numberin1s = parser.getValue(e, "numberin1s");
                String dateofshipment = parser.getValue(e, "dateofshipment");
                String client = parser.getValue(e, "client");
                String comment = parser.getValue(e, "comment");

                String cleanId = Shipment.getCleanId(numberin1s);
                Shipment shipmentToAdd = new Shipment(cleanId, dateofshipment, client, comment);
                //--- {Lapenkov 27.07.2016 Не загружаем задания, которые есть в базе данных
                if (dbHelper.checkIfShipmentExists(cleanId)) continue;

                Boolean hasItems = false;
                NodeList nodeListProducts = e.getElementsByTagName("Products");

                for (int k = 0; k < nodeListProducts.getLength(); k++) {
                    {
                        Element p = (Element) nodeListProducts.item(k);
                        Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                        Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                        String stockcell = parser.getValue(p, "stockcell");
                        String queue = parser.getValue(p, "queue");
                        if (onlySelectedStorages) {
                            String storage = dbHelper.getStorageOfCell(stockcell);
                            //Log.d(TAG+"storage","|"+storage+"|");
                            if (storage != null) {
                                if (!arrayOfStorages.contains(storage)) continue;

                            } else {
                                continue;
                            }

                        }
                        Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
                        Integer rest = Integer.parseInt(parser.getValue(p, "rest"));
                        ShipmentItem shipmentItemToAdd = new ShipmentItem(cleanId, rownumber, productid, stockcell, quantity, rest, queue);
                        listOfShipmentItems.add(shipmentItemToAdd);
                        hasItems = true;

                    }
                }
                if (hasItems) {
                    listOfShipments.add(shipmentToAdd);
                    counter++;
                    Log.d(TAG + "/shipment added", shipmentToAdd.toString());
                    Log.d(TAG + "/shipmentitems added", listOfShipmentItems.toString());
                }

            }

            Log.d(TAG + "/shipmentsitems size", String.valueOf(listOfShipmentItems.size()));
            Log.d(TAG + "/shipments size", String.valueOf(listOfShipments.size()));

            boolean mNewShipmentsWasAdded = false;
            for (Shipment shipment : listOfShipments) {
                if (!dbHelper.checkIfShipmentExists(shipment.Id)) {

                    ContentValues cv = dbHelper.prepareShipment(shipment);
                    Uri newUri = mContext.getContentResolver().insert(CONTENT_URI, cv);
                    mNewShipmentsWasAdded = true;
                }

            }


            mProgress.setProgress(0);
            //  mProgress.setTitle("Загрузка товаров в заданиях");
            int counterShipmentItems = 0;
            for (ShipmentItem shipmentItem : listOfShipmentItems) {
                counterShipmentItems++;
                int progressNeeded = (int) Math.floor(counterShipmentItems * 100 / listOfShipmentItems.size());
                if (progressNeeded != mProgress.getProgress())
                    mProgress.setProgress(progressNeeded);
                if (dbHelper.checkIfShipmentExists(shipmentItem.ShipmentId)
                    //+++ 12.07.2016        && !dbHelper.checkIfShipmentItemsExistByShipmentAndProduct(shipmentItem.ShipmentId,shipmentItem.ProductId)
                    //                       && !dbHelper.checkIfShipmentItemsExistByShipmentAndProductAndRow(shipmentItem.ShipmentId,shipmentItem.ProductId,shipmentItem.RowNumber)
                    // added Lapenkov 27.07.2016 Не загружаем задания которые есть
                    //&& !dbHelper.checkIfShipmentItemsExistByShipmentAndRow(shipmentItem.ShipmentId,shipmentItem.RowNumber)
                        )

                    dbHelper.addShipmentItem(shipmentItem);


            }
            return counter;
        }catch (Exception e)
        {

            return null;
        }

    }

    @Override
    protected void onPreExecute() {
        mProgress = new ProgressDialog(mContext);
        mProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //mProgress.setTitle("Title...");
        mProgress.setMessage("Загрузка заданий и товаров...");
        mProgress.setMax(100);
        mProgress.show();

    }

    @Override
    protected void onPostExecute(Long result) {
      //  super.onPostExecute(aLong);

        if (mProgress != null && mProgress.isShowing())
            mProgress.dismiss();

        if (result==null)

        {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
         builder.setPositiveButton(R.string.yes, null).setMessage("Ошибка при загрузке заданий").show();}
     }

}
