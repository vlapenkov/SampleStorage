package com.yst.sklad.tsd.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by programmer on 11.09.2017.
 */

public class BaseScanActivity extends AppCompatActivity {


    @Override
    public void onResume(){
        super.onResume();
        this.registerReceiver(mBarcodeReceiver, new IntentFilter("DATA_SCAN"));
    }

    @Override
    public void onPause(){
        super.onPause();
        this.unregisterReceiver(mBarcodeReceiver);
    }

    public void onBarcodeScanned(String mBarcode){

    }


    public final BroadcastReceiver mBarcodeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                if(intent != null) {
                    String mBarcodeData = intent.getStringExtra("com.hht.emdk.datawedge.data_string");
                    if(mBarcodeData != null){
                        onBarcodeScanned(mBarcodeData);
                    }
                }
            } catch (Exception err){
                err.printStackTrace();
            }
        }
    };


}
