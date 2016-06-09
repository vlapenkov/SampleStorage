package com.example.user.sample1.activities;

import android.content.Intent;
import android.database.Cursor;
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

/**
 * Created by user on 09.06.2016.
 */
public class OneShipmentActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,AdapterView.OnItemClickListener {
    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;


    ProductsDbHelper mDbHelper;
    String mCurFilter;
    String mShipmentId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_items);


        Intent intent = getIntent();
         mShipmentId = intent.getStringExtra(ShipmentsActivity.SHIPMENT_ID_MESSAGE);


        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);



        mAdapter = new SimpleCursorAdapter(this,
                R.layout.shipmentitem_item, null,
                new String[] { "rownumber","productid" ,"stockcell","productname","storageid" },
                new int[] { R.id.text1, R.id.text2,R.id.text3 ,R.id.text4,R.id.text5 }, 0);

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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}
