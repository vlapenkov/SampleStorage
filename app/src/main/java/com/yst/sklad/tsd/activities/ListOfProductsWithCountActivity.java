package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.ConnectivityHelper;
import com.yst.sklad.tsd.Utils.TextReaderFromHttp;
import com.yst.sklad.tsd.Utils.YesNoInterface;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.YesNoDialogFragment;
import com.yst.sklad.tsd.services.SoapCallToWebService;

import java.io.InputStream;

/*
Активность для считывания товаров и количества и переноса их в 1С как перемещение
 */
public class ListOfProductsWithCountActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, YesNoInterface {

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
        getMenuInflater().inflate(R.menu.oneorder_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadto1s:
            {
                if (ConnectivityHelper.checkConnectivity()) {
                    new SendMovementTo1S().execute();

                }
                break;
            }
            case R.id.clear: {

                YesNoDialogFragment.show(this,getString(R.string.clear_all_strings),null);
            }

        }
        return true;
    }

private void deleteAllItems()
{
    Uri uri = CONTENT_URI;
    int cnt = getContentResolver().delete(uri, null, null);
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
                tv_Totals.setText(""+ mDbHelper.getSumOfQuantityFromProductWithCount());
            }
        });

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

    @Override
    public void ProcessIfYes(Object[] params) {
        deleteAllItems();
    }


    private class SendMovementTo1S extends AsyncTask<String,Void,String> {
        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ListOfProductsWithCountActivity.this);

            pDialog.setMessage(getString(R.string.shipment_is_being_uploaded));
            pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            // Проверка что веб-сервис отработал без ошибок
            if (s!=null && s.contains(SoapCallToWebService.ResultOk)) {
                int numberStart = s.indexOf(SoapCallToWebService.ResultOk) + 3;
                String number1S = s.substring(numberStart, numberStart + 8); // строка - номер поступления который вернул 1С
                Toast.makeText(ListOfProductsWithCountActivity.this, getString(R.string.orderWasUploaded) + " №" + number1S, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");
          // Cursor cursor1=mDbHelper.getProductsWithCoount();
            Cursor cursor=getContentResolver().query(CONTENT_URI,null,null,null,null);

            if (cursor!=null && cursor.getCount()>0)
                cursor.moveToFirst();
            do{

                ProductWithCount pwC =ProductWithCount.fromCursor(cursor);
                chaine.append(pwC.toXML()); }
            while (cursor.moveToNext());

            InputStream stream = SoapCallToWebService.sendAndCreateMoveGoods(chaine.toString());

            if (stream!=null) {
                String result = TextReaderFromHttp.GetStringFromStream(stream);
                return result;
        }
        return null;
    }}
}
