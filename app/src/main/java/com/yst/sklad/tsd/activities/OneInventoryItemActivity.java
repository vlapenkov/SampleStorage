package com.yst.sklad.tsd.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.BarCodeUtils;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.CellWithProductWithCount;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.ShipmentItem;

public class OneInventoryItemActivity extends BaseScanActivity {
    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_INVENTORY;
    EditText et_Cell,et_ProductId,et_Quantity;
    TextView tv_productName;
    ProductsDbHelper mDbHelper;

    Boolean mToCreate ;
    CellWithProductWithCount mItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_inventory_item);
        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();
        et_Cell = (EditText) findViewById(R.id.et_Cell);
        et_ProductId = (EditText) findViewById(R.id.et_ProductId);
        et_Quantity = (EditText) findViewById(R.id.et_QuantityFact);
        tv_productName = (TextView) findViewById(R.id.tv_ProductName);

        Bundle bundle = getIntent().getExtras();
        mToCreate=bundle.getBoolean(InventoryActivity.MESSAGE_TO_CREATE,true);
        if (!mToCreate) {
            mItem = (CellWithProductWithCount) bundle.getSerializable(ListOfProductsWithCountActivity.INFO_MESSAGE);
            et_ProductId.setText(""+mItem.ProductId);
            et_ProductId.setEnabled(false);
            et_Cell.setText(""+mItem.Cell);
            et_Cell.setEnabled(false);
            et_Quantity.setText(""+mItem.Quantity);
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
            et_Quantity.requestFocus();

        } else if (barcode != null && barcode.length() == 8) {
            et_Cell.setText(BarCodeUtils.getCellFromBarCode(barcode));
            et_ProductId.requestFocus();


        } else if (barcode != null && barcode.length() > 8)
            et_ProductId.setText(BarCodeUtils.getProductIdFromBarCode(barcode));


    }



    public void doOKAndToNext(View v)
    {

        try
        {
            int productId= Integer.parseInt(et_ProductId.getText().toString());
            String cell = et_Cell.getText().toString();
            String textFact = et_Quantity.getText().toString();
            int fact = Integer.parseInt(textFact);

            ContentValues cv = new ContentValues();

            cv.put("productid", productId);
            cv.put("stockcell", cell);
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


        {     Intent intent = new Intent(this, OneInventoryItemActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean(InventoryActivity.MESSAGE_TO_CREATE,true);

            intent.putExtras(bundle);
       //     intent.putExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE, newShipmentItem);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent); }
    }
}
