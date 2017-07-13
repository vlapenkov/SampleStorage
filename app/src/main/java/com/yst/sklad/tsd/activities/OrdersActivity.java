package com.yst.sklad.tsd.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplier;
import com.yst.sklad.tsd.data.OrderToSupplierItem;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.Shipment;
import com.yst.sklad.tsd.data.ShipmentItem;
import com.yst.sklad.tsd.dialogs.AlertEnterString;
import com.yst.sklad.tsd.dialogs.AlertSuccess;
import com.yst.sklad.tsd.services.SoapCallToWebService;
import com.yst.sklad.tsd.services.UtilsConnectivityService;
import com.yst.sklad.tsd.services.XMLDOMParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
Заказы поставщикам и перемещения (для поступлений)
*
 */

public class OrdersActivity extends AppCompatActivity  implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    ProductsDbHelper mDbHelper;

    SimpleCursorAdapter mAdapter = null;
    ListView lvData = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        mDbHelper = new ProductsDbHelper(this);
        lvData = (ListView) findViewById(R.id.lvData);


        mAdapter = new SimpleCursorAdapter(this,
                R.layout.order_item, null,
                new String[]{"_id", "dateoforder", "client","ordertype"},
                new int[]{R.id.text1, R.id.text2, R.id.text3,R.id.text4}, 0);

        lvData.setAdapter(mAdapter);

        lvData.setOnItemClickListener(this);

        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.shipmentsmenu, menu);
        // MenuItem searchItem = menu.findItem(R.id.search);
        //   SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        //  searchView.setOnQueryTextListener(this);
        return true;
    }

    public void DownloadAndExportOrders (String orderNumber)
    {

        if  (orderNumber!=null && !orderNumber.isEmpty())
        {
         //   Toast.makeText(this,orderNumber, Toast.LENGTH_LONG).show();

            if (new UtilsConnectivityService(OrdersActivity.this).checkConnectivity()) {
                new DownloadAndImportOrders().execute(orderNumber);

        }

    }}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.import_shipments: {

                AlertEnterString.show(this,"Введите номер поступления","Номер");

           /*     if (new UtilsConnectivityService(OrdersActivity.this).checkConnectivity()) {
                    new DownloadAndImportOrders().execute(ShipmentsActivity.StringUrlShipments);
                    getSupportLoaderManager().getLoader(0).forceLoad();
                }
                */
            }

            case R.id.clear: {


                mDbHelper.clearTable(ProductsContract.OrdersToSupplierEntry.TABLE_NAME);
                mDbHelper.clearTable(ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME);
                mDbHelper.clearTable(ProductsContract.ArrivalItemsEntry.TABLE_NAME);

                RefreshList();
            }
        }
        return true;

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = null, selectionArgs = null;
        String selection = null, sortOrder = null;
        return new CursorLoader(getApplicationContext(), null, projection, selection, selectionArgs, sortOrder) {
            @Override
            public Cursor loadInBackground() {
                return mDbHelper.getOrders();

            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        this.setTitle(getResources().getString(R.string.ordersToSuppliers) + " (" + String.valueOf(data.getCount()) + ")");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //ShipmentItem shipmentItem= mDbHelper.getShipmentItemById(id);

        Intent intent = new Intent(this, OneOrderActivity.class);

        intent.putExtra(OneOrderActivity.ORDER_ID_MESSAGE, id);
        // intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void RefreshList() {
        getSupportLoaderManager().restartLoader(0, null, this);

    }


    private class DownloadAndImportOrders extends AsyncTask<String, Integer, Long> {
        ProgressDialog pDialog;
        private boolean mNewOrdersWasAdded = false;

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected Long doInBackground(String... params) {
            InputStream stream = SoapCallToWebService.receiveOrderByNumber(params[0]);

            List<Integer> listOfProductToAdd = new ArrayList<>();

            XMLDOMParser parser = new XMLDOMParser();

            Document doc = parser.getDocument(stream);

            int typeofarrival= Integer.parseInt( parser.getTopElementValue(doc, "typeofarrival"));
           String numberin1s=  parser.getTopElementValue(doc, "numberin1s");
            String cleanId = OrderToSupplier.getCleanId(numberin1s);
            // при загрузке этот же старый заказ удаляется
            if  (numberin1s!=null &&  !numberin1s.isEmpty())    mDbHelper.deleteOrder(cleanId);


            String arrivalnumber=  parser.getTopElementValue(doc, "arrivalnumber");
            String dateoforder=  parser.getTopElementValue(doc, "dateoforder");
            String client=  parser.getTopElementValue(doc, "client");
            String comments=  parser.getTopElementValue(doc, "comment");
            //parser.getValue((Element) doc.getDocumentElement(),"client");



            OrderToSupplier  orderToSupplier=new OrderToSupplier(cleanId,dateoforder,client,typeofarrival,comments);


            mDbHelper.addOrderToSupplier(orderToSupplier);

            // 1. Добавляем строки
            NodeList nodeListRows = doc.getElementsByTagName("Row");

            for(int j=0; j< nodeListRows.getLength();j++) {


                Element p = (Element) nodeListRows.item(j);
                Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
             //   Integer producttype = Integer.parseInt(parser.getValue(p, "producttype"));

                mDbHelper.addOrderToSupplierItem(new OrderToSupplierItem(cleanId,rownumber,productid,quantity));

                if (!mDbHelper.checkIfProductExists(productid)) listOfProductToAdd.add(productid);

                    /*
                Log.d("rownumber", rownumber.toString());
                Log.d("productid", productid.toString()); */


            }

            //2. Если нужно добавить товары то добавляем
            if (!listOfProductToAdd.isEmpty())
            {
                NodeList nodeListProducts = doc.getElementsByTagName("Product");

            for(int j=0; j< nodeListProducts.getLength();j++) {
                Element p = (Element) nodeListProducts.item(j);
                Integer productid = Integer.parseInt(parser.getValue(p, "productid"));

                if (listOfProductToAdd.contains(productid) && !mDbHelper.checkIfProductExists(productid)) {

                    String name = parser.getValue(p, "name");
                    String barcode = parser.getValue(p, "barcode");
                    String article = parser.getValue(p, "article");
                    Integer producttype = Integer.parseInt(parser.getValue(p, "producttype"));
                    mDbHelper.addProduct(productid,name,barcode,"",producttype,article);
                }
            }
            }



            // 3. Добавляем товары по ячейкая
            NodeList nodeListCells = doc.getElementsByTagName("Cell");

            for(int j=0; j< nodeListCells.getLength();j++) {

                Element p = (Element) nodeListCells.item(j);

                Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                String stockcell = parser.getValue(p, "stockcell");
                Integer quantityfact = Integer.parseInt(parser.getValue(p, "quantityfact"));

                mDbHelper.addArrivalItem(new ArrivalItem(cleanId,productid,quantityfact,stockcell));

            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OrdersActivity.this);

            pDialog.setMessage(getString(R.string.shipments_are_being_downloaded));
            pDialog.show();

        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            pDialog.dismiss();
            if (mNewOrdersWasAdded) {
                String text = getString(R.string.newOrderWasAdded);
                String title = getString(R.string.downloadcomplete);
                AlertSuccess.show(OrdersActivity.this, title, text);
                //alertView(title,text);
            }
            // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            // if (drawer.isDrawerOpen(GravityCompat.START))   drawer.closeDrawer(GravityCompat.START);
            RefreshList();
        }
    }
}
