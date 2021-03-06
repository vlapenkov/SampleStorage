package com.yst.sklad.tsd.activities;
/* --- */
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
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
import android.widget.TextView;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.adapters.ShipmentsCursorAdapter;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.Utils.ShipmentsDownloadHelper;
import com.yst.sklad.tsd.Utils.ConnectivityHelper;
import com.yst.sklad.tsd.services.DownloadAndImportShipments;

import java.lang.ref.WeakReference;

/*
Список заданий на отгрузку
 *  */

public class ShipmentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener,NavigationView.OnNavigationItemSelectedListener {

    ProgressDialog pDialog=null;
    ShipmentsCursorAdapter mAdapter=null;
    ListView lvData =null;

    private static final String TAG = "ShipmentsActivity";
    public static final String SHIPMENT_ID_MESSAGE="SHIPMENT_ID_MESSAGE";
    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_SHIPMENTS;


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

        // { добавляем номер версии в навигационное меню

        String version = ((MainApplication)getApplication()).getVersionName();
        //pInfo.versionName;

        TextView tv_app=(TextView) navigationView.getHeaderView(0).findViewById(R.id.app_name);
        tv_app.setText( tv_app.getText() +" "+version);

        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(this);

        // }

        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();
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

    /*
    * Поскольку получение данных работает через ContentProvider+Loader, то можно было обойтись без нее, но
      * т.к. при нажатии кнопки BACK должна проставляться галка после считывания - лучше использовать так
    */




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
            { if (ConnectivityHelper.checkConnectivity()) {

                new DownloadAndImportShipments(this).execute();
             //   getSupportLoaderManager().getLoader(0).forceLoad();
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
            case R.id.inventory: {

                Intent intent = new Intent(this, InventoryActivity.class);

                startActivity(intent);
                return true;
            }
            case R.id.transferInternal: {

                Intent intent = new Intent(this, InternalTransferActivity.class);

                startActivity(intent);
                return true;
            }

          case R.id.transfer:
            {
                Intent intent = new Intent(this, TransferActivity.class);

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
            case R.id.productwithcount:
            {
                Intent intent = new Intent(this, ListOfProductsWithCountActivity.class);

                startActivity(intent);
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

        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
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
/*
    public void RefreshList() {
        getSupportLoaderManager().restartLoader(0, null, this);

    }
    */


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

