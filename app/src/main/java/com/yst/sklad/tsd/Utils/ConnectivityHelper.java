package com.yst.sklad.tsd.Utils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import com.yst.sklad.tsd.MainApplication;

/**
 * Created by user on 13.06.2016.
 */
public class ConnectivityHelper {


    /**
     * Keeps a reference of the application context
     */

    private static Context mContext ;
   /* public UtilsConnectivityService(Context context) throws NullPointerException{


        if (context==null) throw new NullPointerException("context must be set in UtilsConnectivityService ctor");
        mContext=context;

    } */
    static {
        mContext = MainApplication.getContext();
    }


    public boolean checkIfWifiTurnedOn ()
    {
        WifiManager wifi = (WifiManager)  mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.getWifiState()==3 ||wifi.getWifiState()==2) return true;
        return false;
       /* if (wifi.getWifiState()==1)
            wifi.setWifiEnabled(true);
        else if (wifi.getWifiState()==3)
            wifi.setWifiEnabled(false); */
    }
    public void setWifiOn()
    {
        if(!this.checkIfWifiTurnedOn())
        {
            WifiManager wifi = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);

        }

    }

/*
Проверить подключение
 */
        public static boolean checkConnectivity()
        {
            ConnectivityManager connMgr = (ConnectivityManager)MainApplication.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) return true;
            return false;

        }





}
