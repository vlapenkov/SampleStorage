package com.yst.sklad.tsd.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ProductsDbHelper;

public class OneOrderActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener{
    ProductsDbHelper mDbHelper;
    public static String ORDER_ID_MESSAGE="OrderID";
    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;
    long CurrentOrderId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_order);
        final Intent[] intent = {getIntent()};

        CurrentOrderId = (long) intent[0].getSerializableExtra(ORDER_ID_MESSAGE);

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.stockcell_item, null,
                new String[] { "rownumber", "productid","quantity" },
                new int[] { R.id.text1, R.id.text2,R.id.text3  }, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent   intent = new Intent(OneOrderActivity.this, OneOrderOneCellActivity.class);

                intent.putExtra(ORDER_ID_MESSAGE, CurrentOrderId);

                // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.stockcellmenu, menu);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection=null,selectionArgs=null;
        String selection=null , sortOrder=null;
        return new CursorLoader( getApplicationContext(), null, projection, selection,selectionArgs,sortOrder )
        {
            @Override
            public Cursor loadInBackground()
            {
                return mDbHelper.getOrderItems(String.valueOf(CurrentOrderId));

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.itemsInOrder) + " ("+String.valueOf(data.getCount()) +")");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    //    Log.d("item clicked", String.valueOf(id));

       Intent intent ;



        int ProductId =  mDbHelper.getProductIdInOrderItemById(id);
        Cursor cur= mDbHelper.getArrivalItems(String.valueOf(CurrentOrderId),ProductId);

        // если уже был ввод товара в ячейки то переключаемся туда
        if (cur!=null && cur.getCount()>0)  intent = new Intent(this, OneOrderCellsListActivity.class);

        else intent = new Intent(this, OneOrderOneCellActivity.class);


        intent.putExtra(ORDER_ID_MESSAGE, CurrentOrderId);
        intent.putExtra(OneOrderCellsListActivity.PRODUCT_ID_MESSAGE, ProductId);
       // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
