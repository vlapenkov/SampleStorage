package com.yst.sklad.tsd.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.yst.sklad.tsd.data.ProductsDbHelper;

import java.util.UUID;

/**
 * Created by user on 03.06.2016.
 */
public class SimpleIntentService extends IntentService {


    public SimpleIntentService() {
        super("SimpleIntentService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        //     SystemClock.sleep(5000);
        Log.e("TAG2", "intent service started from intent");
        ProductsDbHelper dbhelper = new ProductsDbHelper(this);
        UUID idOne = UUID.randomUUID();

        int next_id = dbhelper.getLatestProductId()+1;

//        dbhelper.addProduct(next_id,"test product "+next_id,idOne.toString(),"test comments");
        dbhelper.addProduct(next_id,"test product "+next_id,"4028224119007","test comments",2,"article1");
    }
}

