package com.example.user.sample1.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.data.ShipmentItem;
import com.example.user.sample1.services.SoapCallToWebService;

import java.io.InputStream;

/*****
 * Created by user on 09.06.2016.
 */
public class OneShipmentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {
    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;

    public static String SHIPMENTITEM_ID_MESSAGE="shipmentItemID";
    ProductsDbHelper mDbHelper;
    String mCurFilter;
    String mShipmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_items);



        Intent intent = getIntent();
         mShipmentId = intent.getStringExtra(ShipmentsActivity.SHIPMENT_ID_MESSAGE);
        String shipmentNumber= getResources().getString(R.string.ShipmentNumber);
        setTitle(shipmentNumber+String.valueOf(mShipmentId));

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);



        mAdapter = new SimpleCursorAdapter(this,
                R.layout.shipmentitem_item, null,
                new String[] { "rownumber","productid","quantityfact" ,"stockcell","productname","storageid" },
                new int[] { R.id.text1, R.id.text2,R.id.text3 ,R.id.text4,R.id.text5,R.id.text6 }, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection=null,selectionArgs=null;
        String selection=null , sortOrder=null;
        return new CursorLoader( getApplicationContext(), null, projection, selection,selectionArgs,sortOrder )
        {
            @Override
            public Cursor loadInBackground()
            {
                return mDbHelper.getShipmentItems(mShipmentId);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stockcellmenu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:{
                new SendShipment().execute(ShipmentsActivity.StringUrlShipments) ;
            }
        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ShipmentItem shipmentItem= mDbHelper.getShipmentItemById(id);

        Intent intent = new Intent(this, OneShipmentItemActivity.class);

        intent.putExtra(SHIPMENTITEM_ID_MESSAGE, shipmentItem);
       intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(0, null, this);

    }



    private class SendShipment extends AsyncTask<String,Void,String> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OneShipmentActivity.this);

            pDialog.setMessage(getString(R.string.shipment_is_being_uploaded));
            pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");

            Cursor cursor=mDbHelper.getShipmentItemsByShipmentId(mShipmentId);
            if (cursor!=null && cursor.getCount()>0)
                cursor.moveToFirst();
            do{
                ShipmentItem item =ShipmentItem.fromCursor(cursor);
            /*if (item.QuantityFact>0)*/ chaine.append(item.toXML()); }
            while (cursor.moveToNext());


            InputStream stream = new SoapCallToWebService().sendShipment(ShipmentsActivity.StringUrlShipments,mShipmentId, chaine.toString());


            return null;
        }
    }
}
