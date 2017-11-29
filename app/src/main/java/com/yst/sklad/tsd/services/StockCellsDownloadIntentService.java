package com.yst.sklad.tsd.services;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;

import com.yst.sklad.tsd.Utils.TextReaderFromHttp;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;

import java.util.HashSet;

/**
 * Created by lapenkov on 06.11.2017.
 */

public class StockCellsDownloadIntentService extends IntentService {
    public static final String PENDING_RESULT = "pending_result";
    public static final String RESULT = "result";
    public static final int RESULT_CODE = "countMsgs".hashCode();
    public static final int ID_OF_PROGRESS = 11;


    public StockCellsDownloadIntentService() {
        super("StockCellsDownloadIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        int id =ID_OF_PROGRESS;
        ProgressNotificationCallback progress =  new ProgressNotificationCallback(this,id,"Загрузка складов и ячеек","Идет загрузка данных");


         HashSet<String> listOfStorages = new HashSet<>();
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

        dbHelper.clearTable(ProductsContract.ShipmentsItemEntry.TABLE_NAME);
        dbHelper.clearTable(ProductsContract.ShipmentsEntry.TABLE_NAME);
        dbHelper.clearTable(ProductsContract.StorageEntry.TABLE_NAME);
        dbHelper.clearTable(ProductsContract.StockCellEntry.TABLE_NAME);

        int counter=0;

        if (lines.length >0)
        {
            for (String line : lines) {
                String[] arr=line.split(";");
                if (!listOfStorages.contains(arr[0])) listOfStorages.add(arr[0]);
            }

            SQLiteDatabase db= dbHelper.getWritableDatabase();
            try {
                db.beginTransaction();

                // добавить склады
                for (String storageName: listOfStorages)
                {
                    ContentValues cv = new ContentValues();
                    cv.put(ProductsContract.StorageEntry._ID, storageName);
                    db.insert(ProductsContract.StorageEntry.TABLE_NAME,null,cv);

                }

                // добавить ячейки
                for (String line : lines) {
                    counter++;
                    if (counter == 1) {
                        continue;
                    }
                    if (counter % 100 == 0) {
                        progress.onProgress(lines.length, counter);
                    }
                    String[] arr=line.split(";");

                    String storage = arr[0];
                    String cellname = arr[1];
                    String barcode = arr[3];

                    ContentValues cv = new ContentValues();
                    cv.put(ProductsContract.StockCellEntry._ID, barcode);
                    cv.put(ProductsContract.StockCellEntry.COLUMN_NAME, cellname);
                    cv.put(ProductsContract.StockCellEntry.COLUMN_STORAGEID, storage);

                    db.insert(ProductsContract.StockCellEntry.TABLE_NAME, null, cv);
                    // add stockcell
                    //dbHelper.addStockCell(barcode,cellname,storage);




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
