package com.yst.sklad.tsd;

import android.app.Application;

import com.yst.sklad.tsd.data.ProductsDbHelper;

/**
 * Created by lapenkov on 29.11.2017.
 */

public class MainApplication extends Application {

    /**
     * see NotePad tutorial for an example implementation of DataDbAdapter
     */
    private static ProductsDbHelper mDbHelper;

    /**
     * Called when the application is starting, before any other
     * application objects have been created. Implementations
     * should be as quick as possible...
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new ProductsDbHelper(this);
     //   mDbHelper.open();
    }

    public static ProductsDbHelper getDatabaseHelper() {
        return mDbHelper;
    }
}
