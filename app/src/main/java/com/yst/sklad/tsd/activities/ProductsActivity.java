package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
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
import java.util.List;

/*Форма списка товаров*/

public class ProductsActivity extends AppCompatActivity   implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener ,SearchView.OnQueryTextListener{

    private static final String TAG = "ProductsActivity";
    ProductsCursorAdapter mAdapter=null;
    ListView lvData =null;
    private static final String stringUrlProducts="http://yst.ru/data/Products.txt";

    public static String PRODUCT_ID_MESSAGE="productID";
    ProductsDbHelper mDbHelper;
    String mCurFilter;


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
                if (new UtilsConnectivityService(ProductsActivity.this).checkConnectivity())
                    new DownloadAndImportProducts().execute(stringUrlProducts);
                getSupportLoaderManager().getLoader(0).forceLoad();
                return true;
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
        Log.i(TAG, "Item selected "+ Long.toString(id));

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


    public void importAllProducts(View v)
    {
        if (new UtilsConnectivityService(ProductsActivity.this).checkConnectivity())
            new DownloadAndImportProducts().execute(stringUrlProducts);


    }

    private class DownloadAndImportProducts extends AsyncTask<String, Integer, Long> {

        ProgressDialog pDialog;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductsActivity.this);
            pDialog.setProgress(0);
            pDialog.setMax(100);

            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setMessage("Loading products ...");
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
            String format = getString(R.string.products_sucсessfully_downloaded);
            String message=String.format(format,aLong);
            String title =getString(R.string.downloadcomplete);
            AlertSuccess.show(ProductsActivity.this, title, message);

            RefreshList();

        }

        @Override
        protected Long doInBackground(String... params) {
            String[] lines;
            ProductsDbHelper dbHelper;

            String result = null;
            try {
                result = downloadUrl(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dbHelper = new ProductsDbHelper(getBaseContext());
///
            List<Integer> productIdsInDb=dbHelper.getProductIds();
          //  dbHelper.clearTable(ProductsContract.ProductsEntry.TABLE_NAME);

            lines=   result.split(System.getProperty("line.separator"));


            int counter=0;
            for (String line:lines ){
                counter++;
                if (counter==1) { continue;}

                if (counter%10==0) publishProgress((int) ((counter / (float) lines.length) * 100));
                String[] arr=line.split(";");

                Log.d(TAG +"/import",arr[0]);
                int id  = Integer.parseInt(arr[0]);

                if(productIdsInDb.contains(id)) continue;

                String name = arr[1];
                String barcode = arr[2];
                int productType = Integer.parseInt(arr[3]);
                String article = arr[4];
                dbHelper.addProduct(id,name,barcode,"",productType,article);

            }
            return (long)lines.length;
        }

    }

    private void RefreshList() {
        getSupportLoaderManager().restartLoader(0, null, this);

    }


    private String downloadUrl(String url) throws IOException {
        return  TextReaderFromHttp.readTextArrayFromUrl(url);
    }

}
