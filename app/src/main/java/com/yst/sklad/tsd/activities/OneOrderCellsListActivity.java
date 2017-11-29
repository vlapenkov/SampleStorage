package com.yst.sklad.tsd.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.YesNoDialogFragment;
import com.yst.sklad.tsd.Utils.YesNoInterface;

/*
Список считанных ячеек и количество  для контрентного заказа и товара
 */
public class OneOrderCellsListActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener,YesNoInterface{
    ProductsDbHelper mDbHelper;
       public static String PRODUCT_ID_MESSAGE="ProductID";
    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;
    long CurrentOrderId;
    int CurrentProductId;
    Product CurrentProduct;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.stockcellmenu, menu);
       // MenuItem searchItem = menu.findItem(R.id.search);
     //   SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      //  searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                //mDbHelper.addOrderToSupplier( new OrderToSupplierItem("1","20-02-2016","Иванов",0,null));
                getSupportLoaderManager().getLoader(0).forceLoad();


        }
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Intent intent = getIntent();

         CurrentOrderId = (long)intent.getSerializableExtra(OneOrderActivity.ORDER_ID_MESSAGE);
        CurrentProductId = (int) intent.getSerializableExtra(PRODUCT_ID_MESSAGE);
     //   CurrentProduct = mDbHelper.getProductById(CurrentProductId);


        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.oneordercells_item, null,
                new String[] { "stockcell", "productid","quantityfact" },
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
                return mDbHelper.getArrivalItems(String.valueOf(CurrentOrderId),CurrentProductId);

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.itemsInCells) + " ("+String.valueOf(data.getCount()) +")");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        YesNoDialogFragment.show(this,getString(R.string.deleteRow),new Object[]{id});


    }

    @Override
    public void ProcessIfYes(Object[] params) {
      Long l = (Long )params[0];
        Log.i("Value is", String.valueOf(l));
        mDbHelper.deleteArrivalItem(l);
        finish();


    }
}
