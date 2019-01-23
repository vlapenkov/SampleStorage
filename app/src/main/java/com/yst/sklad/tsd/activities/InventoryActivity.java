package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.BarCodeUtils;
import com.yst.sklad.tsd.Utils.ConnectivityHelper;
import com.yst.sklad.tsd.Utils.TextReaderFromHttp;
import com.yst.sklad.tsd.Utils.YesNoInterface;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.CellWithProductWithCount;
import com.yst.sklad.tsd.data.DeleteItemDto;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.YesNoDialogFragment;
import com.yst.sklad.tsd.services.SoapCallToWebService;

import java.io.InputStream;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AdapterView.OnItemClickListener,
        AdapterView.OnItemSelectedListener, YesNoInterface {


    private SimpleCursorAdapter mAdapter;
    private TextView tv_Totals;
    private ProductsDbHelper mDbHelper;

    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_INVENTORY;
    public static final String INFO_MESSAGE="INFO_MESSAGE";
    public static final String MESSAGE_TO_CREATE="TO_CREATE";
    ListView lvData;
    Long mItemSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listofproducts);

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        tv_Totals = (TextView) findViewById(R.id.textViewTotal);



        Button button = (Button)findViewById(R.id.buttonAdd);
        button.setFocusable(true);
        button.setFocusableInTouchMode(true);///add this line
        button.requestFocus();


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.inventory_item,  null,
                new String[] { "productid","stockcell","quantity","productname"},

                new int[] { R.id.text1, R.id.text2,R.id.text3, R.id.text4}, 0);

        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(mAdapter);
        lvData.setOnItemClickListener(this);
        lvData.setOnItemSelectedListener(this);
        getSupportLoaderManager().initLoader(0, null, this);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.oneorder_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadto1s:
            {
                if (ConnectivityHelper.checkConnectivity()) {
                    new InventoryActivity.SendInventory().execute("68");
                 //   new ListOfProductsWithCountActivity.SendMovementTo1S().execute();

                }
                break;
            }
            case R.id.clear: {
                DeleteItemDto[] params =  {new DeleteItemDto (null,true)};
                YesNoDialogFragment.show(this,getString(R.string.clear_all_strings),params  );

            }
            }


        return true;
    }

    public void Delete(View v)
    {
        DeleteItemDto[] params =  {new DeleteItemDto (mItemSelected,false)};
        YesNoDialogFragment.show(this,getString(R.string.deleteRow),params);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
        runOnUiThread(new Runnable() {
            public void run() {
                tv_Totals.setText(""+ mDbHelper.getItemsCount("inventory"));
            }
        });

        // tv_Totals.setText();

    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void Insert(View v) {

        Intent intent= new Intent(this,OneInventoryItemActivity.class);

        Bundle bundle = new Bundle();
        bundle.putBoolean(MESSAGE_TO_CREATE,true);

        intent.putExtras(bundle);

        //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Cursor res =  mDbHelper.getReadableDatabase().rawQuery( "select * from " + ProductsContract.Inventory.TABLE_NAME+ " where _id="+id, null );

        if (res!=null && res.getCount()>0) {
            res.moveToFirst();
            Intent intent = new Intent(this, OneInventoryItemActivity.class);

            Bundle bundle = new Bundle();
            bundle.putBoolean(MESSAGE_TO_CREATE, false);
           CellWithProductWithCount data= CellWithProductWithCount.fromCursor(res);
            bundle.putSerializable(INFO_MESSAGE, data);

            intent.putExtras(bundle);

            //   intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mItemSelected=id;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
   //     mItemSelected=null;
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


    /*
Отправить поступление/перемещение  в 1С
 */
    private class SendInventory extends AsyncTask<String,Void,String> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(InventoryActivity.this);

            pDialog.setMessage(getString(R.string.shipment_is_being_uploaded));
            pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();


            // Проверка что веб-сервис отработал без ошибок
            if (s!=null && s.contains(SoapCallToWebService.ResultOk)) {

                Toast.makeText(InventoryActivity.this, getString(R.string.orderWasUploaded), Toast.LENGTH_LONG).show();


            }else
                Toast.makeText(InventoryActivity.this, "Ошибка! Поступление/перемещение не было выгружено", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
            Cursor cursor =mDbHelper.getReadableDatabase().query(ProductsContract.Inventory.TABLE_NAME, null, null, null, null, null, null);


            if (cursor!=null && cursor.getCount()>0)
                cursor.moveToFirst();
            do{

                CellWithProductWithCount item =CellWithProductWithCount.fromCursor(cursor);
                chaine.append(item.toXML()); }
            while (cursor.moveToNext());


        //    int orderType = mCurrentOrder.OrderType; //mDbHelper.getOrderTypeByOrderId(mCurrentOrderId);
            String numberIn1S=params[0].toString();

            InputStream stream = SoapCallToWebService.sendInventory(numberIn1S, chaine.toString());


            if (stream!=null) { String result = TextReaderFromHttp.GetStringFromStream(stream);

                return result;}
            return null;
        }
    }
}
