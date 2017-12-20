package com.yst.sklad.tsd.activities;

import android.app.PendingIntent;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.Constants;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.services.StockCellsDownloadIntentService;
import com.yst.sklad.tsd.Utils.TextReaderFromHttp;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

import java.io.IOException;

/*
Список ячеек (складов) скаждый склад если в нем нет ячеек кодируется в ячейку
 */
public class StockCellsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnQueryTextListener,AdapterView.OnItemClickListener{

   SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;

    private static final String TAG = "StockCellsActivity";
    private static final String stringUrlStoragesAndCells= Constants.STRING_URL_STORAGESANDCELLS;
    private static final int REQUEST_CODE = 0;


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
    public boolean onQueryTextChange(String query) {
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
                if (UtilsConnectivityService.checkConnectivity())
                    importAllStockCells();
         //        new DownloadAndImportStockCells().execute(stringUrlStoragesAndCells);
           //     getSupportLoaderManager().getLoader(0).forceLoad();


        }
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, String.valueOf(id));
    }

    /*
    Старт сервиса импорт ячеек и складов
     */
    private void importAllStockCells() {
        PendingIntent pendingIntent = createPendingResult(REQUEST_CODE, new Intent(), 0);
        Intent intent = new Intent(this, StockCellsDownloadIntentService.class);
        intent.putExtra(StockCellsDownloadIntentService.PENDING_RESULT, pendingIntent); // pendingIntent - передается в IntentService
        intent.putExtra("url", stringUrlStoragesAndCells);
        startService(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE && resultCode==StockCellsDownloadIntentService.RESULT_CODE)
        {
            int result = data.getIntExtra(StockCellsDownloadIntentService.RESULT, -1);

            String message=String.format("Ячейки в количестве %1$s успешно загружены",result);
            String title =getString(R.string.downloadcomplete);
            AlertSuccess.show(StockCellsActivity.this, title, message);
            getSupportLoaderManager().restartLoader(0, null, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


}
