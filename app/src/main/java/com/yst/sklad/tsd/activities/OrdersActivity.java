package com.yst.sklad.tsd.activities;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplier;
import com.yst.sklad.tsd.data.OrderToSupplierItem;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.ShipmentItem;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

/*
Заказы поставщикам и перемещения (для поступлений)
 */

public class OrdersActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener{

    ProductsDbHelper mDbHelper;

    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.shipment_item, null,
                new String[] { "_id", "dateoforder","client" },
                new int[] { R.id.text1, R.id.text2,R.id.text3  }, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shipmentsmenu, menu);
       // MenuItem searchItem = menu.findItem(R.id.search);
     //   SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      //  searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                addOrdersToDb();
        }
        return true;
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
                return mDbHelper.getOrders();

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.ordersToSuppliers) + " ("+String.valueOf(data.getCount()) +")");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ShipmentItem shipmentItem= mDbHelper.getShipmentItemById(id);

       Intent intent = new Intent(this, OneOrderActivity.class);

        intent.putExtra(OneOrderActivity.ORDER_ID_MESSAGE, id);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void addOrdersToDb () {
           mDbHelper.addOrderToSupplier( new OrderToSupplier("2","21-03-2017","Петров",0,null));
                mDbHelper.addOrderToSupplierItem(new OrderToSupplierItem("2",1,9160850,10));
                mDbHelper.addOrderToSupplierItem(new OrderToSupplierItem("2",2,9160849,15));

                //mDbHelper.addOrderToSupplier( new OrderToSupplierItem("1","20-02-2016","Иванов",0,null));

                mDbHelper.addArrivalItem(new ArrivalItem("2",9160850,1,"123123"));
                mDbHelper.addArrivalItem(new ArrivalItem("2",9160850,1,"123124"));
                getSupportLoaderManager().getLoader(0).forceLoad();



    }
}
