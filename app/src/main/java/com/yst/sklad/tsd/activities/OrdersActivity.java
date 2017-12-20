package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.AlertEnterString;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.Utils.OrderDownloadHelper;
import com.yst.sklad.tsd.Utils.YesNoInterface;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

/*
Заказы поставщикам и перемещения (для поступлений)
*
 */

public class OrdersActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener , YesNoInterface {

    ProductsDbHelper mDbHelper;

    SimpleCursorAdapter mAdapter = null;
    ListView lvData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.order_item, null,
                new String[]{"_id", "dateoforder", "client","ordertype"},
                new int[]{R.id.text1, R.id.text2, R.id.text3,R.id.text4}, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shipmentsmenu, menu);

        return true;
    }

    public void DownloadAndExportOrders (String orderNumber)
    {

        if  (orderNumber!=null && !orderNumber.isEmpty())
        {

            if (UtilsConnectivityService.checkConnectivity()) {
                new DownloadAndImportOrders().execute(orderNumber);

        }

    }}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_shipments: {

                AlertEnterString.show(this,"Введите номер поступления","Номер");

            }

            case R.id.clear: {

                new ClearAllOrders().execute();
            }
        }
        return true;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null, selectionArgs = null;
        String selection = null, sortOrder = null;
        return new CursorLoader(getApplicationContext(), null, projection, selection, selectionArgs, sortOrder) {
            @Override
            public Cursor loadInBackground() {
                return mDbHelper.getOrders();

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.ordersToSuppliers) + " (" + String.valueOf(data.getCount()) + ")");
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

    private void RefreshList() {
        getSupportLoaderManager().restartLoader(0, null, this);

    }

    @Override
    public void ProcessIfYes(Object[] params) {
        DownloadAndExportOrders( ((String) params[0]));
    }


    private class DownloadAndImportOrders extends AsyncTask<String, Integer, Long> {
        ProgressDialog pDialog;
        private boolean mNewOrdersWasAdded = false;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }


        @Override
        protected Long doInBackground(String... params) {
            OrderDownloadHelper.createDocuments(OrdersActivity.this, params);
            return null;
        }
        @Override
        protected void onPostExecute(Long along) {
            super.onPostExecute(along);
            pDialog.dismiss();
            if (mNewOrdersWasAdded) {
                String text = getString(R.string.newOrderWasAdded);
                String title = getString(R.string.downloadcomplete);
                AlertSuccess.show(OrdersActivity.this, title, text);
                //alertView(title,text);
            }

            RefreshList();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OrdersActivity.this);

            pDialog.setMessage(getString(R.string.shipments_are_being_downloaded));
            pDialog.show();

        }


    }

    /*
* class removes all orders
* */
    private class ClearAllOrders extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper.clearTable(ProductsContract.OrdersToSupplierEntry.TABLE_NAME);
            mDbHelper.clearTable(ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME);
            mDbHelper.clearTable(ProductsContract.ArrivalItemsEntry.TABLE_NAME);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            RefreshList();
        }
    }
}
