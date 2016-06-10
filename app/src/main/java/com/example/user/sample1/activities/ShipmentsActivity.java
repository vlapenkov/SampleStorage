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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsDbHelper;

public class ShipmentsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {

   SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;

    public static String SHIPMENT_ID_MESSAGE="shipmentID";

    ProductsDbHelper mDbHelper;
    String mCurFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_entries);
        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);



        mAdapter = new SimpleCursorAdapter(this,
                R.layout.stockcell_item, null,
                new String[] { "_id", "dateofshipment","client" },
                new int[] { R.id.text1, R.id.text2,R.id.text3  }, 0);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                getSupportLoaderManager().getLoader(0).forceLoad();
                return true;
            case R.id.load_entities:
            {
                Intent intent = new Intent(this, LoadEntitiesActivity.class);
                startActivity(intent);
                return true;}
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shipments_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("MYTAG", String.valueOf(id));
        Intent intent = new Intent(this, OneShipmentActivity.class);

        intent.putExtra(SHIPMENT_ID_MESSAGE, Long.toString(id));

        startActivity(intent);
    }
}
