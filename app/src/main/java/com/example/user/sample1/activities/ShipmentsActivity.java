package com.example.user.sample1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.data.Shipment;
import com.example.user.sample1.data.ShipmentItem;
import com.example.user.sample1.services.SoapCallToWebService;
import com.example.user.sample1.services.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ShipmentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {

    private boolean mNewShipmentsWasAdded = false;
   SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;

    private static final String TAG = "ShipmentsActivity";
    public static String SHIPMENT_ID_MESSAGE="shipmentID";
    private static final String stringUrlShipments="http://37.1.84.50:8080/YST/ws/ServiceTransfer";

    ProductsDbHelper mDbHelper;
    String mCurFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipment_items);
      /*  ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setLogo(R.drawable.ic_launcher); */

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);



       mAdapter = new SimpleCursorAdapter(this,
                R.layout.stockcell_item, mDbHelper.getShipments(mCurFilter),
                new String[] { "_id", "dateofshipment","client" },
                new int[] { R.id.text1, R.id.text2,R.id.text3  }, 0);
      /*  String[] from = new String[] {"client"};
        int[] to = new int[] {android.R.id.text1};
        mAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, mDbHelper.getShipments(mCurFilter), from, to); */

        lvData.setAdapter(mAdapter);

       lvData.setOnItemClickListener(this);
        this.setTitle(R.string.shipments);



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
                return mDbHelper.getShipments(mCurFilter);

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
    public boolean onQueryTextSubmit(String query) {
        if (mCurFilter == null && query == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(query)) {
            return true;
        }
        mCurFilter = query;

        getSupportLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        return false;
    }


    private boolean checkConnectivity()
    {
   ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) return true;
        return false;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shipments_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
//        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (checkConnectivity())
                    new DownloadAndImportShipments().execute(stringUrlShipments);

                getSupportLoaderManager().getLoader(0).forceLoad();
                return true;

            case R.id.toPreferences: {
                Intent intent = new Intent(this, MyPreferencesActivity.class);

                startActivity(intent);
                return true;}

            case R.id.toStockCells:
            {

                Intent intent = new Intent(this, StockCellsActivity.class);

                startActivity(intent);
                return true;
            }
            case R.id.toProducts:
            {

                Intent intent = new Intent(this, ProductsActivity.class);

                startActivity(intent);
                return true;
            }
            /*default:
                return super.onOptionsItemSelected(item); */

        }
        return true;
    }

    private void alertView( String title, String message ) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        dialog.setTitle( title)
                .setIcon(R.drawable.ic_launcher)
                .setMessage(message)
//  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//      public void onClick(DialogInterface dialoginterface, int i) {
//          dialoginterface.cancel();
//          }})
                .setPositiveButton(R.string.button_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.valueOf(id));
        Intent intent = new Intent(this, OneShipmentActivity.class);

        intent.putExtra(SHIPMENT_ID_MESSAGE, Long.toString(id));

        startActivity(intent);
    }

    private void RefreshList() {
        getSupportLoaderManager().restartLoader(0, null, this);

    }
    private class DownloadAndImportShipments extends AsyncTask<String, Integer, Long> {


        ProgressDialog pDialog;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShipmentsActivity.this);
            pDialog.setProgress(0);
            pDialog.setMax(10);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMessage("Loading shipments ....");
            pDialog.show();

        }
        @Override
        protected Long doInBackground(String... params) {

            InputStream stream = SoapCallToWebService.Call(stringUrlShipments);

            XMLDOMParser parser = new XMLDOMParser();

            List<Shipment> listOfShipments = new ArrayList<Shipment>();
            List<ShipmentItem> listOfShipmentItems = new ArrayList<ShipmentItem>();

            Document doc = parser.getDocument(stream);

            NodeList nodeListOrders = doc.getElementsByTagName("Orders");

            for(int j=0; j< nodeListOrders.getLength();j++) {
                Element e = (Element) nodeListOrders.item(j);

                String numberin1s = parser.getValue(e, "numberin1s");
                String dateofshipment = parser.getValue(e, "dateofshipment");
                String client = parser.getValue(e, "client");
                String comment = parser.getValue(e, "comment");

                String cleanId = Shipment.getCleanId(numberin1s);
                Shipment shipmentToAdd =new Shipment(cleanId,dateofshipment,client,comment);
                listOfShipments.add(shipmentToAdd);
                Log.d(TAG+ "/shipment added", shipmentToAdd.toString());


                NodeList nodeListProducts =e.getElementsByTagName("Products");
                for(int k=0; k< nodeListProducts.getLength();k++) {
                    {
                        Element p = (Element) nodeListProducts.item(k);
                        Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                        Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                        String stockcell = parser.getValue(p, "stockcell");
                        Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
                        ShipmentItem shipmentItemToAdd =new ShipmentItem(cleanId,rownumber,productid,stockcell,quantity);
                        listOfShipmentItems.add(shipmentItemToAdd);


                    }}
                Log.d(TAG +"/shipmentitems added", listOfShipmentItems.toString());

            }

            Log.d(TAG+"/shipmentsitems size", String.valueOf(listOfShipmentItems.size()));
            Log.d(TAG+"/shipments size", String.valueOf(listOfShipments.size()));


            ProductsDbHelper dbHelper = new ProductsDbHelper(getBaseContext());

            mNewShipmentsWasAdded=false;
            for (Shipment shipment : listOfShipments)
            {
                if (!dbHelper.checkIfShipmentExists(shipment.Id))
                {    dbHelper.addShipment(shipment);
                mNewShipmentsWasAdded=true; }

            }

            for (ShipmentItem shipmentItem : listOfShipmentItems)
            {
                if (dbHelper.checkIfShipmentExists(shipmentItem.ShipmentId) && !dbHelper.checkIfShipmentItemsExistByShipmentAndProduct(shipmentItem.ShipmentId,shipmentItem.ProductId))
                    dbHelper.addShipmentItem(shipmentItem);

            }


            return null;


        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            pDialog.dismiss();
            if (mNewShipmentsWasAdded)
            {String text = getResources().getString(R.string.newShipmentsWereAdded);
            String title =getResources().getString(R.string.downloadcomplete);
            alertView(title,text);}
            RefreshList();
        }


    }
}
