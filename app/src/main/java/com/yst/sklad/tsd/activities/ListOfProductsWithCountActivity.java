package com.yst.sklad.tsd.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.TextView;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.ConnectivityHelper;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsDbHelper;

/*
Активность для считывания товаров и количества и переноса их в 1С как перемещение
 */
public class ListOfProductsWithCountActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    private SimpleCursorAdapter mAdapter;
    private TextView tv_Totals;
    private ProductsDbHelper mDbHelper;

    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_PRODUCTSWITHCOUNT;
    public static final String INFO_MESSAGE="INFO_MESSAGE";
    public static final String MESSAGE_TO_CREATE="TO_CREATE";
    ListView lvData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofproducts);

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        tv_Totals = (TextView)  findViewById(R.id.textViewTotal);

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.productrests_item,  null,
                new String[] { "productid","quantityfact","productname"},

                new int[] { R.id.text1, R.id.text2,R.id.text3 }, 0);

        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(mAdapter);
        lvData.setOnItemClickListener(this);
        getSupportLoaderManager().initLoader(0, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadto1s:
            {

                break;
            }
            case R.id.clear: {

            }

        }
        return true;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
       tv_Totals.setText(""+ mDbHelper.getSumOfQuantityFromProductWithCount());
       // tv_Totals.setText();

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void Insert(View v) {
    /*    ContentValues cv = new ContentValues();

        cv.put("productid", 9121503);
        cv.put("quantityfact", 10);

        Uri newUri = getContentResolver().insert(CONTENT_URI, cv); */
        Intent intent= new Intent(this,ProductWithCountActivity.class);


        Bundle bundle = new Bundle();
        bundle.putBoolean(MESSAGE_TO_CREATE,true);

        intent.putExtras(bundle);

        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void Delete(View v) {
       /* Uri uri = ContentUris.withAppendedId(CONTENT_URI, 123);
        int cnt = getContentResolver().delete(uri, null, null); */

        Uri uri = CONTENT_URI;
        int cnt = getContentResolver().delete(uri, null, null);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //Log.d("ListOfProductsActivity","="+id+"=" +position);
      Intent intent= new Intent(this,ProductWithCountActivity.class);

        ProductWithCount pwC= mDbHelper.getProductWithCount(id);

        Bundle bundle = new Bundle();
        bundle.putSerializable(INFO_MESSAGE, pwC);
        bundle.putBoolean(MESSAGE_TO_CREATE,false);

        intent.putExtras(bundle);

     //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}
