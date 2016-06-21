package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.services.TextReaderFromHttp;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StockCellsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener{

   SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;
    private static final String stringUrlStoragesAndCells="http://yst.ru/data/Stores.txt";
    private static final String TAG = "StockCellsActivity";


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
                new String[] { "_id", "name","storageid" },
                new int[] { R.id.text1, R.id.text2,R.id.text3  }, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }


    private String downloadUrl(String url) throws IOException {
        return  TextReaderFromHttp.readTextArrayFromUrl(url);
    }


    private class DownloadAndImportStockCells extends AsyncTask<String, Integer, Long> {
        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(StockCellsActivity.this);
            pDialog.setProgress(0);
            pDialog.setMax(100);

            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMessage(getString(R.string.stockcells_are_being_downloaded));
            pDialog.show();
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            pDialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            //pDialog.setProgress(100);
            pDialog.dismiss();
            String message = getResources().getString(R.string.products_sucсessfully_downloaded);
            String title =getResources().getString(R.string.downloadcomplete);
            AlertSuccess.show(StockCellsActivity.this,title,message);
            //      getLoaderManager().getLoader(0).forceLoad();
            RefreshList();

        }



        @Override
        protected Long doInBackground(String... urls) {
            String[] lines;
            ProductsDbHelper dbHelper;
            // params comes from the execute() call: params[0] is the url.
            try {
                String result =  downloadUrl(urls[0]);

                dbHelper = new ProductsDbHelper(getBaseContext());

                // обязательно очищаем все таблицы из-за внешних ключей
                dbHelper.clearTable(ProductsContract.ShipmentsItemEntry.TABLE_NAME);
                dbHelper.clearTable(ProductsContract.ShipmentsEntry.TABLE_NAME);
                dbHelper.clearTable(ProductsContract.StorageEntry.TABLE_NAME);
                dbHelper.clearTable(ProductsContract.StockCellEntry.TABLE_NAME);

                List<String> listofstorages = new ArrayList<String>() ;
                lines=   result.split(System.getProperty("line.separator"));

                int counter=0;
                for (String line:lines
                        ) {
                    counter++;
                    if (counter==1) { continue;}

                    if (counter%10==0)
                        publishProgress((int) ((counter / (float) lines.length) * 100));
                    String[] arr=line.split(";");

                    String storage = arr[0];
                    String cellname = arr[1];
                    String barcode = arr[3];

                    // add storege
                    if (!listofstorages.contains(storage))
                    {
                        listofstorages.add(storage);
                        dbHelper.addStorage(storage);
                    }
                    // add stockcell
                    dbHelper.addStockCell(barcode,cellname,storage);


                }

            } catch (IOException e) {
                return (long)-1;
            }finally {

            }
            return (long)lines.length;
        }

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
                return mDbHelper.getStockCells(mCurFilter);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.stockcells) + " ("+String.valueOf(data.getCount()) +")");
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

    private void RefreshList ()
    {
        getSupportLoaderManager().restartLoader(0, null, this);

    }


    @Override
    public boolean onQueryTextChange(String newText) {
       /* String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
        // Don't do anything if the filter hasn't actually changed.
        // Prevents restarting the loader when restoring state.
        if (mCurFilter == null && newFilter == null) {
            return true;
        }
        if (mCurFilter != null && mCurFilter.equals(newFilter)) {
            return true;
        }
        mCurFilter = newFilter;

        getSupportLoaderManager().restartLoader(0, null, this);
        return true; */
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stockcellmenu, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                if (new UtilsConnectivityService(this).checkConnectivity())
                 new DownloadAndImportStockCells().execute(stringUrlStoragesAndCells);
                getSupportLoaderManager().getLoader(0).forceLoad();


        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.valueOf(id));
    }
}
