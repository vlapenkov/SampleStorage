package com.yst.sklad.tsd;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.yst.sklad.tsd.data.ProductsDbHelper;

/**
 * Created by lapenkov on 29.11.2017.
 */

public class MainApplication extends Application {

    private static ProductsDbHelper mDbHelper;
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mDbHelper = new ProductsDbHelper(this);
        sContext=   getApplicationContext();
     //   mDbHelper.open();
    }

    public static ProductsDbHelper getDatabaseHelper() {
        return mDbHelper;
    }
    public static Context getContext() {
        return sContext;
    }

   //public  Context getApplication() {return getApplication();}


    public  String getVersionName()
    {

        PackageInfo pInfo = null;
        try {

            pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return pInfo.versionName;
    }
}
