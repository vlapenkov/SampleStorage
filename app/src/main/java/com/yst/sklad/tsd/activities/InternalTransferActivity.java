package com.yst.sklad.tsd.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.YesNoInterface;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.Cell2WithProductWithCount;
import com.yst.sklad.tsd.data.CellWithProductWithCount;
import com.yst.sklad.tsd.data.DeleteItemDto;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.YesNoDialogFragment;

import java.util.Random;

public class InternalTransferActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>,
 AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, YesNoInterface
{

    Long mItemSelected;
    private SimpleCursorAdapter mAdapter;
    private TextView tv_Totals;
    private ProductsDbHelper mDbHelper;

    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_INTERNALTRANSFER;
    public static final String INFO_MESSAGE="INFO_MESSAGE";
    public static final String MESSAGE_TO_CREATE="TO_CREATE";
    ListView lvData;
  //  Long mItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_internal_transfer);
        setContentView(R.layout.activity_listofproducts);

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        tv_Totals = (TextView) findViewById(R.id.textViewTotal);



        Button button = (Button)findViewById(R.id.buttonAdd);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);///add this line
        button.requestFocus();


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.internaltransfer_item,  null,
                new String[] { "productid","stockcellfrom","stockcellto","quantity","productname"},

                new int[] { R.id.text1, R.id.text2,R.id.text3, R.id.text4,R.id.text5}, 0);

        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(mAdapter);
        lvData.setOnItemClickListener(this);
      lvData.setOnItemSelectedListener(this);
        getSupportLoaderManager().initLoader(0, null, this);


    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        runOnUiThread(new Runnable() {
            public void run() {
                tv_Totals.setText(""+ mDbHelper.getItemsCount(ProductsContract.TransferOfProductsInternalEntry.TABLE_NAME));
            }
        });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

            mAdapter.swapCursor(null);
    }

    public void Insert(View view) {
        Intent intent= new Intent(this,OneInternalTransferActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MESSAGE_TO_CREATE,true);

        intent.putExtras(bundle);

        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent);
    }

    public void Delete(View view) {
        DeleteItemDto[] params =  {new DeleteItemDto (mItemSelected,false)};
        YesNoDialogFragment.show(this,getString(R.string.deleteRow),params);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor res =  mDbHelper.getReadableDatabase().rawQuery( "select * from " + ProductsContract.TransferOfProductsInternalEntry.TABLE_NAME+ " where _id="+id, null );

        if (res!=null && res.getCount()>0) {
            res.moveToFirst();
            Intent intent = new Intent(this, OneInternalTransferActivity.class);

            Bundle bundle = new Bundle();
            bundle.putBoolean(MESSAGE_TO_CREATE, false);
            Cell2WithProductWithCount data= Cell2WithProductWithCount.fromCursor(res);
            bundle.putSerializable(INFO_MESSAGE, data);

            intent.putExtras(bundle);

            //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
    }}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mItemSelected=id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void ProcessIfYes(Object[] params) {
        DeleteItemDto item= (DeleteItemDto)params[0];

        if (item.mDeleteAll) {
            Uri uri = CONTENT_URI;
            int cnt = getContentResolver().delete(uri, null, null);
        }
        else

        {

            Uri uri = ContentUris.withAppendedId(CONTENT_URI, item.mItem);
            int cnt = getContentResolver().delete(uri, null, null);

        }
    }

/*    public void Insert(View v) {

        Intent intent= new Intent(this,OneInternalTransferActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MESSAGE_TO_CREATE,true);

        intent.putExtras(bundle);

        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   //     startActivity(intent);
/*
        ContentValues cv = new ContentValues();


        cv.put("productid", "9123456");
        cv.put("stockcellfrom", "100");
        cv.put("stockcellto", "200");
        cv.put("quantity", 55);

        Uri newUri = getContentResolver().insert(CONTENT_URI, cv); */




}
