package com.yst.sklad.tsd.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.adapters.ProductsCursorAdapter;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.services.ProductsDownloadIntentService;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

/*Форма списка товаров*/

public class ProductsActivity extends AppCompatActivity   implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener ,SearchView.OnQueryTextListener{

    private static final String TAG = "ProductsActivity";
    ProductsCursorAdapter mAdapter=null;
    ListView lvData =null;
    private static final String stringUrlProducts="http://yst.ru/data/Products.txt";

    public static String PRODUCT_ID_MESSAGE="productID";
    ProductsDbHelper mDbHelper;
    String mCurFilter;
    private static final int REQUEST_CODE = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDbHelper = new ProductsDbHelper(this);

        mAdapter = new ProductsCursorAdapter(this,null,0);


        lvData = (ListView) findViewById(R.id.lvData);
        // lvData.setAdapter(scAdapter);
        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDbHelper.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
            { if (new UtilsConnectivityService(ProductsActivity.this).checkConnectivity())
                importAllProducts();

                break;
                 }
            case R.id.clear: {
            new ClearAllProducts().execute();
            }

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
                return mDbHelper.getProducts(mCurFilter);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.Products)+ " ("+String.valueOf(data.getCount()) +")");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

         Intent intent = new Intent(this, OneProductActivity.class);
         intent.putExtra(PRODUCT_ID_MESSAGE, Long.toString(id));
         startActivity(intent);


    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
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
        return true;
    }


    public void importAllProducts()
    {

        PendingIntent pendingIntent = createPendingResult(REQUEST_CODE, new Intent(), 0);

        Intent intent = new Intent(this, ProductsDownloadIntentService.class);
        intent.putExtra(ProductsDownloadIntentService.PENDING_RESULT, pendingIntent); // pendingIntent - передается в IntentService
        intent.putExtra("url", stringUrlProducts);
        startService(intent);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode==REQUEST_CODE && resultCode==ProductsDownloadIntentService.RESULT_CODE)
        {
            int result = data.getIntExtra(ProductsDownloadIntentService.RESULT, -1);
            // Update UI View with the result

           // String format = getString(R.string.products_sucсessfully_downloaded);
         //   String message = format;
            String message=String.format("Товары в количестве %1$s успешно загружены",result);
            String title =getString(R.string.downloadcomplete);
            AlertSuccess.show(ProductsActivity.this, title, message);
            getSupportLoaderManager().restartLoader(0, null, this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*
    Удаляет все товары
     */
    private class ClearAllProducts extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            mDbHelper.clearTable(ProductsContract.ProductsEntry.TABLE_NAME);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getSupportLoaderManager().getLoader(0).forceLoad();
        }

}}
