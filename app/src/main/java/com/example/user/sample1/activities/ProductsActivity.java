package com.example.user.sample1.activities;

import android.content.Intent;
import android.database.Cursor;
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

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsDbHelper;

public class ProductsActivity extends AppCompatActivity   implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener ,SearchView.OnQueryTextListener{

    ProductsCursorAdapter mAdapter=null;
    ListView lvData =null;

    public static String PRODUCT_ID_MESSAGE="productID";
    ProductsDbHelper mDbHelper;
    String mCurFilter;



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
                getSupportLoaderManager().getLoader(0).forceLoad();
                return true;
            case R.id.load_entities:
            {
                Intent intent = new Intent(this, LoadEntitiesActivity.class);
                startActivity(intent);
                return true;}
            case R.id.toPreferences: {
                Intent intent = new Intent(this, MyPreferencesActivity.class);

                startActivity(intent);}

                case R.id.toStockCells:
                {

                    Intent intent = new Intent(this, StockCellsActivity.class);

                    startActivity(intent);

            }
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDbHelper = new ProductsDbHelper(this);
     //  SQLiteDatabase db= dbhelper.getReadableDatabase();

        mAdapter = new ProductsCursorAdapter(this,null,0);
       /* mAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence constraint) {
                // Search for states whose names begin with the specified letters.
                Cursor cursor = mDbHelper.getProducts(constraint.toString());
                return cursor;

            }
        }); */

        lvData = (ListView) findViewById(R.id.lvData);
       // lvData.setAdapter(scAdapter);
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
                return mDbHelper.getProducts(mCurFilter);
                // You better know how to get your database.
                //SQLiteDatabase DB = dbhelper.getReadableDatabase();
                // You can use any query that returns a cursor.
                //return DB.query(ProductsContract.ProductsEntry.TABLE_NAME, getProjection(), getSelection(), getSelectionArgs(), null, null, getSortOrder(), null );
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.i("TAG", "Item selected "+ Long.toString(id));

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
}
