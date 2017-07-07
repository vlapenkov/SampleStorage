package com.yst.sklad.tsd.activities;
/* --- */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.adapters.ProductsCursorAdapter;
import com.yst.sklad.tsd.adapters.ShipmentsCursorAdapter;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.Shipment;
import com.yst.sklad.tsd.data.ShipmentItem;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.services.SoapCallToWebService;
import com.yst.sklad.tsd.services.UtilsConnectivityService;
import com.yst.sklad.tsd.services.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
Список заданий на отгрузку
 *  */

public class ShipmentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener,NavigationView.OnNavigationItemSelectedListener {

    private boolean mNewShipmentsWasAdded = false;
    ShipmentsCursorAdapter mAdapter=null;
    ListView lvData =null;

    private static final String TAG = "ShipmentsActivity";
    public static String SHIPMENT_ID_MESSAGE="shipmentID";
    public static final String StringUrlShipments="http://37.1.84.50:8080/YST/ws/ServiceTransfer";

    ProductsDbHelper mDbHelper;
    String mCurFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_shipments);

        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // for using drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        //Initializing NavigationView
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setDrawerListener(toggle);
        toggle.syncState();
      /*  ActionBar ab = getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayUseLogoEnabled(true);
        ab.setLogo(R.drawable.ic_launcher); */

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);

      /* mAdapter = new SimpleCursorAdapter(this,
                R.layout.shipment_item, mDbHelper.getShipments(mCurFilter),
                new String[] { "_id", "dateofshipment","client","quantityfact","quantity" },
                new int[] { R.id.text1, R.id.text2,R.id.text3,  }, 0); */

        mAdapter = new ShipmentsCursorAdapter(this,null,0);


            lvData.setAdapter(mAdapter);

       lvData.setOnItemClickListener(this);
        this.setTitle(R.string.shipments);



        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onRestart() {

            super.onRestart();
            getSupportLoaderManager().restartLoader(0, null, this);


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.shipmentsmenu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_shipments:
            { if (new UtilsConnectivityService(ShipmentsActivity.this).checkConnectivity()) {
                new DownloadAndImportShipments().execute(StringUrlShipments);
                getSupportLoaderManager().getLoader(0).forceLoad();
            }

                return true;}

            case R.id.clear:
            {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                new ClearAllShipments().execute();
                                break;
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.clear_shipments_message).setPositiveButton(R.string.yes, dialogClickListener)
                        .setNegativeButton(R.string.no, dialogClickListener).show();

                return true;
            }


        }
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch(id) {

            case R.id.shipments: {
                Intent intent = new Intent(this, ShipmentsActivity.class);

                startActivity(intent);
                return true;
            }

            case R.id.orders: {

                Intent intent = new Intent(this, OrdersActivity.class);

                startActivity(intent);
                return true;
            }

            case R.id.toPreferences: {
                Intent intent = new Intent(this, MyPreferencesActivity.class);

                startActivity(intent);
                return true;}

            case R.id.toStockCells: {

                Intent intent = new Intent(this, StockCellsActivity.class);

                startActivity(intent);
                return true;
            }
            case R.id.toProducts: {

               Intent intent = new Intent(this, ProductsActivity.class);

                startActivity(intent);

                //YesNoDialogFragment.newInstance(R.string.history_title).show(getFragmentManager(), "dialog");
                return true;
            }
            case R.id.exit: {

               finish();

                return true;
            }
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
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

    public void doPositiveClick() {
        Log.i(TAG, "Positive click");
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

            pDialog.setMessage(getString(R.string.shipments_are_being_downloaded));
            pDialog.show();

        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            pDialog.dismiss();
            if (mNewShipmentsWasAdded)
            {String text = getString(R.string.newShipmentsWereAdded);
                String title =getString(R.string.downloadcomplete);
                AlertSuccess.show(ShipmentsActivity.this, title, text);
                //alertView(title,text);
            }
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START))   drawer.closeDrawer(GravityCompat.START);
            RefreshList();
        }
        @Override
        protected Long doInBackground(String... params) {

            InputStream stream = SoapCallToWebService.receiveCurrentShipments(StringUrlShipments);

            XMLDOMParser parser = new XMLDOMParser();

            List<Shipment> listOfShipments = new ArrayList<Shipment>();
            List<ShipmentItem> listOfShipmentItems = new ArrayList<ShipmentItem>();

            Document doc = parser.getDocument(stream);

            NodeList nodeListOrders = doc.getElementsByTagName("Orders");

            ProductsDbHelper dbHelper = new ProductsDbHelper(getBaseContext());

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Boolean  onlySelectedStorages = preferences.getBoolean("onlySelectedStorages", false);
            Set<String> arrayOfStorages = preferences.getStringSet("storagesArray", new HashSet<String>());


            for(int j=0; j< nodeListOrders.getLength();j++) {
                Element e = (Element) nodeListOrders.item(j);

                String numberin1s = parser.getValue(e, "numberin1s");
                String dateofshipment = parser.getValue(e, "dateofshipment");
                String client = parser.getValue(e, "client");
                String comment = parser.getValue(e, "comment");

                String cleanId = Shipment.getCleanId(numberin1s);
                Shipment shipmentToAdd =new Shipment(cleanId,dateofshipment,client,comment);
                //--- {Lapenkov 27.07.2016 Не загружаем задания, которые есть в базе данных
                if (dbHelper.checkIfShipmentExists(cleanId)) continue;

                Boolean hasItems= false;
                NodeList nodeListProducts =e.getElementsByTagName("Products");

                for(int k=0; k< nodeListProducts.getLength();k++) {
                    {
                        Element p = (Element) nodeListProducts.item(k);
                        Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                        Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                        String stockcell = parser.getValue(p, "stockcell");
                        String queue = parser.getValue(p, "queue");
                        if(onlySelectedStorages)
                        {   String storage=dbHelper.getStorageOfCell(stockcell);
                        //Log.d(TAG+"storage","|"+storage+"|");
                        if (storage!=null ) {
                            if (!arrayOfStorages.contains(storage)) continue;

                        }else {
                            continue;
                        }

                        }
                        Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
                        Integer rest = Integer.parseInt(parser.getValue(p, "rest"));
                        ShipmentItem shipmentItemToAdd =new ShipmentItem(cleanId,rownumber,productid,stockcell,quantity,rest,queue);
                        listOfShipmentItems.add(shipmentItemToAdd);
                        hasItems=true;

                    }}
                if (hasItems)
                {listOfShipments.add(shipmentToAdd);
                Log.d(TAG+ "/shipment added", shipmentToAdd.toString());
                Log.d(TAG +"/shipmentitems added", listOfShipmentItems.toString());
                }

            }

            Log.d(TAG+"/shipmentsitems size", String.valueOf(listOfShipmentItems.size()));
            Log.d(TAG + "/shipments size", String.valueOf(listOfShipments.size()));

            mNewShipmentsWasAdded=false;
            for (Shipment shipment : listOfShipments)
            {
                if (!dbHelper.checkIfShipmentExists(shipment.Id))
                {    dbHelper.addShipment(shipment);
                mNewShipmentsWasAdded=true; }

            }

            for (ShipmentItem shipmentItem : listOfShipmentItems)
            {
                if (dbHelper.checkIfShipmentExists(shipmentItem.ShipmentId)
                //+++ 12.07.2016        && !dbHelper.checkIfShipmentItemsExistByShipmentAndProduct(shipmentItem.ShipmentId,shipmentItem.ProductId)
                 //                       && !dbHelper.checkIfShipmentItemsExistByShipmentAndProductAndRow(shipmentItem.ShipmentId,shipmentItem.ProductId,shipmentItem.RowNumber)
                        // added Lapenkov 27.07.2016 Не загружаем задания которые есть
                        //&& !dbHelper.checkIfShipmentItemsExistByShipmentAndRow(shipmentItem.ShipmentId,shipmentItem.RowNumber)
                        )
                    dbHelper.addShipmentItem(shipmentItem);

            }
            return null;
        }
    }

/*
* class removes all shipments
* */
    private class ClearAllShipments extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper.clearTable(ProductsContract.ShipmentsItemEntry.TABLE_NAME);
            mDbHelper.clearTable(ProductsContract.ShipmentsEntry.TABLE_NAME);

            return null;
        }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))    drawer.closeDrawer(GravityCompat.START);
        getSupportLoaderManager().getLoader(0).forceLoad();
    }
}
}
