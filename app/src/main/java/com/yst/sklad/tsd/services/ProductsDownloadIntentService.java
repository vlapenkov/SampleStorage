package com.yst.sklad.tsd.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;

import java.io.IOException;

/**
 * Created by lapenkov on 03.11.2017.
 * Загрузка товаров в фоне
 */

public class ProductsDownloadIntentService extends IntentService {

    public static final String PENDING_RESULT = "pending_result";
    public static final String RESULT = "result";
    public static final int RESULT_CODE = "countMsgs".hashCode();
    public static final int ID_OF_PROGRESS = 10;


    public ProductsDownloadIntentService() {
        super("ProductsDownloadIntentService");
    }



    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int id =ID_OF_PROGRESS;
        ProgressNotificationCallback progress =  new ProgressNotificationCallback(this,id,"Загрузка товаров","Идет загрузка данных");

        String[] lines;
        ProductsDbHelper dbHelper;

        String resultString;
        try {
            resultString = TextReaderFromHttp.readTextArrayFromUrl(intent.getStringExtra("url"));
        } catch (Exception e) {
            e.printStackTrace();
            progress.onComplete("Error");
            return;
        }

        dbHelper = new ProductsDbHelper(getBaseContext());

        lines=   resultString.split(System.getProperty("line.separator"));


        dbHelper.clearTable(ProductsContract.ProductsEntry.TABLE_NAME);
        dbHelper.clearTable(ProductsContract.ProductBarcodesEntry.TABLE_NAME);

        int counter=0;

        if (lines.length >0)
        {
            SQLiteDatabase db= dbHelper.getWritableDatabase();
            try {
            db.beginTransaction();

            for (String line : lines) {

                counter++;
                if (counter == 1) {
                    continue;
                }

                if (counter % 100 == 0) {


                    progress.onProgress(lines.length, counter);
                }
                String[] arr = line.split(";");

                int productId = Integer.parseInt(arr[0]);

                String name = arr[1];
                String barcodes = arr[2];

                String firstBarcode = BarCodeUtils.importAdditionalBarcodesToDb(productId, barcodes, dbHelper);

                int productType = Integer.parseInt(arr[3]);
                String article = arr[4];
              //       dbHelper.addProduct(productId,name,firstBarcode,"",productType,article);

                ContentValues cv = new ContentValues();
                cv.put(ProductsContract.ProductsEntry._ID, productId);
                cv.put(ProductsContract.ProductsEntry.COLUMN_NAME, name);

                cv.put(ProductsContract.ProductsEntry.COLUMN_BARCODE, firstBarcode);
                cv.put(ProductsContract.ProductsEntry.COLUMN_COMMENTS, "");
                cv.put(ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE, productType);
                cv.put(ProductsContract.ProductsEntry.COLUMN_ARTICLE, article);

                db.insert(ProductsContract.ProductsEntry.TABLE_NAME, null, cv);



            }
                db.setTransactionSuccessful();
            }
            finally {
                db.endTransaction();
                db.close();
            }



        }

        progress.onComplete("OK");

        Intent resultIntent = new Intent();

        resultIntent.putExtra(RESULT,lines.length );

        // плучаем PendingIntent из активности
        PendingIntent pendingIntentFromActivity = intent.getParcelableExtra(PENDING_RESULT);

        try {
            pendingIntentFromActivity.send(this, RESULT_CODE, resultIntent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }
}
