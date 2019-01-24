package com.yst.sklad.tsd.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.BarCodeUtils;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.Cell2WithProductWithCount;
import com.yst.sklad.tsd.data.CellWithProductWithCount;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;

public class OneInternalTransferActivity extends BaseScanActivity {
    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_INTERNALTRANSFER;
    EditText et_CellFrom,et_ProductId,et_Quantity;
    EditText et_CellTo;
    TextView tv_productName, tv_cellName;
    ProductsDbHelper mDbHelper;

    Boolean mToCreate ;
    Cell2WithProductWithCount mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_internaltransfer_item);
        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();
        et_CellFrom = (EditText) findViewById(R.id.et_CellFrom);
        et_CellTo = (EditText) findViewById(R.id.et_CellTo);
        et_ProductId = (EditText) findViewById(R.id.et_ProductId);
        et_Quantity = (EditText) findViewById(R.id.et_QuantityFact);
        tv_productName = (TextView) findViewById(R.id.tv_ProductName);
        tv_cellName = (TextView) findViewById(R.id.tv_cellName);

        Bundle bundle = getIntent().getExtras();
        mToCreate=bundle.getBoolean(InventoryActivity.MESSAGE_TO_CREATE,true);


        if (!mToCreate) {
            mItem = (Cell2WithProductWithCount) bundle.getSerializable(InternalTransferActivity.INFO_MESSAGE);
            et_ProductId.setText(""+mItem.ProductId);
            et_ProductId.setEnabled(false);
            et_CellFrom.setText(""+mItem.Cell);
            et_CellFrom.setEnabled(false);
            et_CellTo.setText(""+mItem.CellTo);
            et_CellTo.setEnabled(false);
            et_Quantity.setText(""+mItem.Quantity);
            et_Quantity.requestFocus();
        }


    }

    /*
    1. Обработка считывания для сканера
     */
    @Override
    public void onBarcodeScanned(String barcode){
        // если повторно заходим в карточку, то она считана и не даем изменить товар и ячейку, только количество
        if(mToCreate)  processBarcode(barcode);
    }

    /*
       Обработка считывания
        */
    private void processBarcode(String barcode) {
        String cellRead = "";

        int productId = 0;
        Product productFound = null;
      //  Toast.makeText(OneInventoryItemActivity.this, barcode, Toast.LENGTH_LONG).show();
        productFound = mDbHelper.getProductByBarCode(barcode);
        if (productFound != null) {
            tv_productName.setText(productFound.Name);
            et_ProductId.setText(productFound.Id+"");
            et_CellFrom.requestFocus();

        } else if (barcode != null && barcode.length() == 8) {

            if (et_CellFrom.hasFocus()) {
                et_CellFrom.setText(BarCodeUtils.getCellFromBarCode(barcode));
                et_CellTo.requestFocus();
            }
            else {
                et_CellTo.setText(BarCodeUtils.getCellFromBarCode(barcode));
                et_Quantity.requestFocus();
            }
           //String cellName= mDbHelper.getNameOfCell(barcode);
          //  tv_cellName.setText(cellName);
            //et_ProductId.requestFocus();


        } else if (barcode != null && barcode.length() > 8) {
            et_ProductId.setText("" + BarCodeUtils.getProductIdFromBarCode(barcode));
            et_CellFrom.requestFocus();
        }

    }



    public void doOKAndToNext(View v)
    {

        try
        {
            int productId= Integer.parseInt(et_ProductId.getText().toString());
            String cellFrom = et_CellFrom.getText().toString();
            String cellTo = et_CellTo.getText().toString();
            String textFact = et_Quantity.getText().toString();
            int fact = Integer.parseInt(textFact);


            ContentValues cv = new ContentValues();

            cv.put("productid", productId);
            cv.put("stockcellFrom", cellFrom);
            cv.put("stockcellTo", cellFrom);
            cv.put("quantity", fact);

            // если нет то создаем иначе апдейтим
            if (mToCreate)
             getContentResolver().insert(CONTENT_URI, cv);
            else
            {
                Uri uri = ContentUris.withAppendedId(CONTENT_URI, mItem.Id);
                int cnt = getContentResolver().update(uri, cv, null, null);
            }


        }catch (Exception e )
        {
            Log.e("Ошибка при считывании товара",e.getMessage());
            return;
        }


        {     Intent intent = new Intent(this, OneInternalTransferActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(InventoryActivity.MESSAGE_TO_CREATE,true);

            intent.putExtras(bundle);
       //     intent.putExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE, newShipmentItem);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent); }
    }

    public void doCancel(View view) {
        finish();
    }
}
