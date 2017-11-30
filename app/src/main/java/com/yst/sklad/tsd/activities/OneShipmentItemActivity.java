package com.yst.sklad.tsd.activities;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.ShipmentItem;
import com.yst.sklad.tsd.dialogs.ProductOnRestsDialog;
import com.yst.sklad.tsd.dialogs.ProductPictureDialog;
import com.yst.sklad.tsd.Utils.BarCodeUtils;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/*
* Форма одной строки из задания
* */
public class OneShipmentItemActivity extends BaseScanActivity /* implements View.OnClickListener */ {
    ShipmentItem mShipmentItem;
    EditText et_Cell,et_QuantityFact;
    TextView tvRowNumber,tvProductId,tvBtnShowProductOnStocks,tvProductName,tvArticle,tv_CellName,tv_QuantityPlan,tv_RestCaption,tv_Rest,tv_Queue;

    public static String TAG="OneShipmentItemActivity";


    ProductsDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_shipment_item);

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        Intent intent = getIntent();
        mShipmentItem = (ShipmentItem)intent.getSerializableExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE);
        //String productName = mDbHelper.getProductById(mShipmentItem.ProductId).Name;
        Product product =mDbHelper.getProductById(mShipmentItem.ProductId);



        String productName = product!=null?product.Name:"";
        String productArticle = product!=null?product.Article:"";

        //  { если SMART DROID то скрываем кнопку SCAN
        Button btnScanBarCode = (Button) findViewById(R.id.btnScanBarCode);
        if (android.os.Build.MANUFACTURER.compareToIgnoreCase("pitech")==0)   btnScanBarCode.setVisibility(View.INVISIBLE);
        // }

         tvRowNumber  = (TextView) findViewById(R.id.tv_RowNumber);
        tvProductId = (TextView) findViewById(R.id.tv_ProductId);
         tvBtnShowProductOnStocks = (TextView) findViewById(R.id.btnShowProductOnStocks);

        tvBtnShowProductOnStocks.setPaintFlags(tvBtnShowProductOnStocks.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
         tvProductName =(TextView) findViewById(R.id.tvProductName);
        tvArticle =(TextView) findViewById(R.id.tv_Article);
        //TextView tv_CellPlanCaption = (TextView) findViewById(R.id.tv_CellPlanCaption);
         et_Cell = (EditText) findViewById(R.id.et_Cell);
         tv_CellName = (TextView) findViewById(R.id.tv_Storage);
         et_QuantityFact= (EditText) findViewById(R.id.et_QuantityFact);
         tv_QuantityPlan = (TextView) findViewById(R.id.tv_QuantityPlan);

         tv_RestCaption = (TextView) findViewById(R.id.tv_RestCaption);
        tv_Rest = (TextView) findViewById(R.id.tv_Rest);

        tv_Queue = (TextView) findViewById(R.id.tv_Queue);

        tv_Queue.setText(mShipmentItem.Queue);

        tvRowNumber.setText(String.valueOf(mShipmentItem.RowNumber));
        tvProductId.setText(String.valueOf(mShipmentItem.ProductId));
        tvArticle.setText(productArticle);

      //  tvProductId.setOnClickListener(this);



        String stock_cell ="";
        if(mShipmentItem.StockCellFact != null && !mShipmentItem.StockCellFact.isEmpty())
         stock_cell = mShipmentItem.StockCellFact;

        else stock_cell = mShipmentItem.StockCell;
        et_Cell.setText(stock_cell);
        tv_CellName.setText(mDbHelper.getNameOfCell(stock_cell));

      //  int rest = mDbHelper.getRestOfProductInShipmentItem(mShipmentItem.);

        //if (rest>0) tv_Rest.setText(Integer.toString(rest));

        if(mShipmentItem.Rest>0) { tv_Rest.setText(Integer.toString(mShipmentItem.Rest)); } else tv_RestCaption.setText("");

        if(mShipmentItem.QuantityFact!=0 && mShipmentItem.QuantityFact!=mShipmentItem.Quantity) et_QuantityFact.setText(String.valueOf(mShipmentItem.QuantityFact));
        else et_QuantityFact.setText(String.valueOf(mShipmentItem.Quantity));


        tvProductName.setText(productName);

        et_QuantityFact.requestFocus();
    }

    public void doOKAndToNext(View v)
    {int  fact =0;
        int id = mShipmentItem.getId();
      //  et_Cell = (EditText) findViewById(R.id.et_Cell);
        String cell = et_Cell.getText().toString();
        EditText et_QuantityFact= (EditText) findViewById(R.id.et_QuantityFact);
        String textFact = et_QuantityFact.getText().toString();
        if(textFact!=null&&!textFact.isEmpty())

        fact = Integer.parseInt(textFact);
        mDbHelper.updateShipmentItem(id,cell,fact);
        finish();

        ShipmentItem newShipmentItem = mDbHelper.getNextShipmentItem(mShipmentItem.ShipmentId,mShipmentItem.getId());
        if (newShipmentItem!=null)
        {     Intent intent = new Intent(this, OneShipmentItemActivity.class);

        intent.putExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE, newShipmentItem);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent); }
    }

        /*
        Обработка считывания
         */
    private void processBarcode(String barcode) {
        String cellRead="";

        int productId = 0;
        Product productFound = null;

        productFound = mDbHelper.getProductByBarCode(barcode);
        if (productFound!=null) productId=productFound.Id;

        //  это ячейка
        if (productId==0 && barcode!=null &&barcode.length()==8) { cellRead =BarCodeUtils.getCellFromBarCode(barcode);}
        else //  это товар
        {
            if (productId!=mShipmentItem.ProductId) Toast.makeText(OneShipmentItemActivity.this,R.string.products_shouldbe_equal, Toast.LENGTH_LONG).show();
            else //productId==mShipmentItem.ProductId
            {Toast.makeText(OneShipmentItemActivity.this,R.string.product_read, Toast.LENGTH_LONG).show();}

        }


        if (!cellRead.isEmpty()) {
            et_Cell.setText(cellRead);
            TextView tv_CellName = (TextView) findViewById(R.id.tv_Storage);
            tv_CellName.setText(mDbHelper.getNameOfCell(cellRead));
            Toast.makeText(OneShipmentItemActivity.this,R.string.cell_read, Toast.LENGTH_LONG).show();
        }
    }

    /*
    1. Обработка считывания для сканера
     */
    @Override
    public void onBarcodeScanned(String barcode){
        processBarcode(barcode);
    }

    /*
    2. Обработка считывания для камеры
     */

    public void scanBarCode(View v)
    {

        new ZxingOrient(OneShipmentItemActivity.this).initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

   /*     String cellRead="";
        String nameOfProduct = "";
        TextView tvProductName = (TextView)findViewById(R.id.tvProductName);
        EditText et_Cell = (EditText) findViewById(R.id.et_Cell); */

//retrieve scan result
        ZxingOrientResult scanningResult = ZxingOrient.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            String contents= scanningResult.getContents(); if (contents==null) return;

            processBarcode(contents);
       /*     boolean productIsFound = false;
            int productId = BarCodeUtils.getProductIdFromBarCode(contents);
            Product productFound = null;

            if (productId==0) {
                productFound = mDbHelper.getProductByBarCode(contents);
                if (productFound!=null) productId=productFound.Id;
            }



            //  это ячейка
                if (productId==0 && contents!=null &&contents.length()==8) { cellRead =BarCodeUtils.getCellFromBarCode(contents);}
            else //  это товар
                {
                if (productId!=mShipmentItem.ProductId) Toast.makeText(OneShipmentItemActivity.this,R.string.products_shouldbe_equal, Toast.LENGTH_LONG).show();
                else //productId==mShipmentItem.ProductId
                {Toast.makeText(OneShipmentItemActivity.this,R.string.product_read, Toast.LENGTH_LONG).show();}

            }
            Log.d(TAG + "/product", String.valueOf(productId));
            Log.d(TAG + "/cell", cellRead);
            if (!cellRead.isEmpty()) {
                et_Cell.setText(cellRead);
                TextView tv_CellName = (TextView) findViewById(R.id.tv_Storage);
                tv_CellName.setText(mDbHelper.getNameOfCell(cellRead));
                Toast.makeText(OneShipmentItemActivity.this,R.string.cell_read, Toast.LENGTH_LONG).show();
            }
*/

        }
    }

    public void onClickProductid(View v) {

        if (new UtilsConnectivityService(OneShipmentItemActivity.this).checkConnectivity()) {
            TextView tvId = (TextView) findViewById(R.id.tv_ProductId);
            ProductPictureDialog pDialog = new ProductPictureDialog();
            Bundle bundle = new Bundle();
            bundle.putString("productId", tvId.getText().toString());
            pDialog.setArguments(bundle);
            pDialog.show(getFragmentManager(), "Заголовок");
        }
    }
  /*  @Override
    public void onClick(View v) {

        if (new UtilsConnectivityService(OneShipmentItemActivity.this).checkConnectivity()) {
            TextView tvId = (TextView) findViewById(R.id.tv_ProductId);
            ProductPictureDialog pDialog = new ProductPictureDialog();
            Bundle bundle = new Bundle();
            bundle.putString("productId", tvId.getText().toString());
            pDialog.setArguments(bundle);
            pDialog.show(getFragmentManager(), "Заголовок");
    } */


    public void showProductOnStocks(View v) {
        if (new UtilsConnectivityService(OneShipmentItemActivity.this).checkConnectivity()) {
            TextView tvId = (TextView) findViewById(R.id.tv_ProductId);
            String productId = tvId.getText().toString();
            ProductOnRestsDialog pDialog = new ProductOnRestsDialog();
            Bundle bundle = new Bundle();
            bundle.putString("productId", tvId.getText().toString());
            pDialog.setArguments(bundle);
            pDialog.show(getFragmentManager(), "Заголовок");

        }
    }}

