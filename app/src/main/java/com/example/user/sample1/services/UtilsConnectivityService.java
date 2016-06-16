package com.example.user.sample1.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by user on 13.06.2016.
 */
public class UtilsConnectivityService {


    Context mContext ;
    public UtilsConnectivityService(Context context) throws NullPointerException{
        if (context==null) throw new NullPointerException("context must be set in UtilsConnectivityService ctor");
        mContext=context;
    }

    public boolean checkIfWifiTurnedOn ()
    {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
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
            WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            wifi.setWifiEnabled(true);


        }


    }


        public boolean checkConnectivity()
        {
            ConnectivityManager connMgr = (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) return true;
            return false;

        }



}
