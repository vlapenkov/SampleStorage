package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Toast;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplier;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.YesNoDialogFragment;
import com.yst.sklad.tsd.Utils.YesNoInterface;
import com.yst.sklad.tsd.services.SoapCallToWebService;
import com.yst.sklad.tsd.Utils.TextReaderFromHttp;

import java.io.InputStream;

/*
Один заказ/перемещение, в нем
список строк которые надо обработать
 */
public class OneOrderActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener,YesNoInterface {
    ProductsDbHelper mDbHelper;
    public static String ORDER_ID_MESSAGE="OrderID";
    private static String TAG="OneOrderActivity";
    SimpleCursorAdapter mAdapter=null;
    ListView lvData =null;
    long mCurrentOrderId;

    OrderToSupplier mCurrentOrder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_order);
        final Intent[] intent = {getIntent()};

        mCurrentOrderId = (long) intent[0].getSerializableExtra(ORDER_ID_MESSAGE);

        mDbHelper = new ProductsDbHelper(this);

        mCurrentOrder = mDbHelper.getOrderToSupplier(mCurrentOrderId);

        lvData = (ListView) findViewById(R.id.lvData);

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.orderitem_item,  null,
                new String[] { "rownumber","productid","quantity" ,"quantityfact","productname" },
                new int[] { R.id.text1, R.id.text2,R.id.text3 ,R.id.text4,R.id.text5}, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent   intent = new Intent(OneOrderActivity.this, OneOrderOneCellActivity.class);

                intent.putExtra(ORDER_ID_MESSAGE, mCurrentOrderId);

                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.oneorder_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.uploadto1s: {
                new SendOrder().execute(); break;
             //   getSupportLoaderManager().getLoader(0).forceLoad();
            }
            case R.id.clear: {
                YesNoDialogFragment.show(this,getString(R.string.deleteOrder),null);
          /*      DialogFragment newFragment = YesNoDialogFragment.newInstance(
                        R.string.deleteOrder,null);
                newFragment.show(getFragmentManager(), "dialog"); */
            }
        }

        return true;
    }




    @Override
    public void onRestart() {
        super.onRestart();
        getSupportLoaderManager().restartLoader(0, null, this);

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
                return mDbHelper.getOrderItems(String.valueOf(mCurrentOrderId));

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
        Cursor cur= mDbHelper.getArrivalItems(String.valueOf(mCurrentOrderId),ProductId);

        // если уже был ввод товара в ячейки то переключаемся туда
        if (cur!=null && cur.getCount()>0)  intent = new Intent(this, OneOrderCellsListActivity.class);

        else intent = new Intent(this, OneOrderOneCellActivity.class);


        intent.putExtra(ORDER_ID_MESSAGE, mCurrentOrderId);
        intent.putExtra(OneOrderCellsListActivity.PRODUCT_ID_MESSAGE, ProductId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void ProcessIfYes(Object[] params) {
        mDbHelper.deleteOrder(String.valueOf(mCurrentOrderId));
        finish();
    }

/*
Отправить поступление/перемещение  в 1С
 */
    private class SendOrder extends AsyncTask<String,Void,String> {

        ProgressDialog pDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OneOrderActivity.this);

            pDialog.setMessage(getString(R.string.shipment_is_being_uploaded));
            pDialog.show();

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();


            // Проверка что веб-сервис отработал без ошибок
            if (s!=null && s.contains(SoapCallToWebService.ResultOk)) {
             int numberStart=s.indexOf(SoapCallToWebService.ResultOk)+3;
             String number1S=   s.substring(numberStart,numberStart+8); // строка - номер поступления который вернул 1С
                Toast.makeText(OneOrderActivity.this, getString(R.string.orderWasUploaded)+" №" +number1S, Toast.LENGTH_LONG).show();

                // Удалить задание после выгрузки в 1С если такая настрока установлена
               // SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
               // Boolean delete_shipment_after_upload = preferences.getBoolean("delete_shipment_after_upload", false);
               /* if (delete_shipment_after_upload)
                {
                    mDbHelper.deleteOrder(mShipmentId);
                    OneOrderActivity.this.finish();
                }
                */
            }else
                Toast.makeText(OneOrderActivity.this, "Ошибка! Поступление/перемещение не было выгружено", Toast.LENGTH_LONG).show();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuffer chaine = new StringBuffer("");


            Cursor cursor=mDbHelper.getArrivalItemsByOrderId(mCurrentOrderId);

            if (cursor!=null && cursor.getCount()>0)
                cursor.moveToFirst();
            do{

                ArrivalItem item =ArrivalItem.fromCursor(cursor);
             chaine.append(item.toXML()); }
            while (cursor.moveToNext());


           int orderType = mCurrentOrder.OrderType; //mDbHelper.getOrderTypeByOrderId(mCurrentOrderId);

            SoapCallToWebService service= new SoapCallToWebService();
            InputStream stream = service.sendOrder(mCurrentOrder.NumberIn1S,orderType, chaine.toString());


            if (stream!=null) { String result = TextReaderFromHttp.GetStringFromStream(stream);

                return result;}
            return null;
        }
    }
}
