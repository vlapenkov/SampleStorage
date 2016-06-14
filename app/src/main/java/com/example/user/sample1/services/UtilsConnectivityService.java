package com.example.user.sample1.services;

import android.content.Context;
import android.net.wifi.WifiManager;

/**
 * Created by user on 13.06.2016.
 */
public class UtilsConnectivityService {

    Context mContext ;
    public UtilsConnectivityService(Context context) throws NullPointerException{
        if (context==null) throw new NullPointerException("context must be set");
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

}
