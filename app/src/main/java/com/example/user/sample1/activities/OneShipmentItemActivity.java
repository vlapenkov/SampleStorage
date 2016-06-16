package com.example.user.sample1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sample1.R;
import com.example.user.sample1.data.Product;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.data.ShipmentItem;
import com.example.user.sample1.dialogs.ProductPictureDialog;
import com.example.user.sample1.services.BarCodeUtils;
import com.example.user.sample1.services.UtilsConnectivityService;
import com.google.zxing.common.StringUtils;

import javax.xml.datatype.Duration;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

public class OneShipmentItemActivity extends AppCompatActivity implements View.OnClickListener {
    ShipmentItem mShipmentItem;
    EditText et_Cell;
    public static String TAG="OneShipmentItemActivity";


    ProductsDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_shipment_item);

        mDbHelper = new ProductsDbHelper(this);



        Intent intent = getIntent();
        mShipmentItem = (ShipmentItem)intent.getSerializableExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE);
        //String productName = mDbHelper.getProductById(mShipmentItem.ProductId).Name;
        Product product =mDbHelper.getProductById(mShipmentItem.ProductId);
        String productName = product!=null?product.Name:"";

        TextView tvRowNumber  = (TextView) findViewById(R.id.tv_RowNumber);
        TextView tvProductId = (TextView) findViewById(R.id.tv_ProductId);
        TextView tvProductName =(TextView) findViewById(R.id.tvProductName);
        //TextView tv_CellPlanCaption = (TextView) findViewById(R.id.tv_CellPlanCaption);
        EditText et_Cell = (EditText) findViewById(R.id.et_Cell);
        TextView tv_Storage = (TextView) findViewById(R.id.tv_Storage);
        EditText et_QuantityFact= (EditText) findViewById(R.id.et_QuantityFact);
        TextView tv_QuantityPlan = (TextView) findViewById(R.id.tv_QuantityPlan);

        tvRowNumber.setText(String.valueOf(mShipmentItem.RowNumber));
        tvProductId.setText(String.valueOf(mShipmentItem.ProductId));

        tvProductId.setOnClickListener(this);


        if(mShipmentItem.StockCellFact != null && !mShipmentItem.StockCellFact.isEmpty()) et_Cell.setText(mShipmentItem.StockCellFact);
        else et_Cell.setText(mShipmentItem.StockCell);


        if(mShipmentItem.QuantityFact!=0 && mShipmentItem.QuantityFact!=mShipmentItem.Quantity) et_QuantityFact.setText(String.valueOf(mShipmentItem.QuantityFact));
        else et_QuantityFact.setText(String.valueOf(mShipmentItem.Quantity));


        tvProductName.setText(productName);

        et_QuantityFact.requestFocus();
    }

    public void doOKAndToNext(View v)
    {int  fact =0;
        int id = mShipmentItem.getId();
        et_Cell = (EditText) findViewById(R.id.et_Cell);
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

    public void scanBarCode(View v)
    {

        new ZxingOrient(OneShipmentItemActivity.this).initiateScan();
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {

        String cellRead="";
        String nameOfProduct = "";
        TextView tvProductName = (TextView)findViewById(R.id.tvProductName);
        EditText et_Cell = (EditText) findViewById(R.id.et_Cell);

//retrieve scan result
        ZxingOrientResult scanningResult = ZxingOrient.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            String contents= scanningResult.getContents(); if (contents==null) return;

            int productId = BarCodeUtils.getProductIdFromBarCode(contents);

            if (productId==0) { cellRead =BarCodeUtils.getCellFromBarCode(contents);}
            else {
                if (productId!=mShipmentItem.ProductId) Toast.makeText(OneShipmentItemActivity.this,R.string.products_shouldbe_equal, Toast.LENGTH_LONG).show();
                else //productId==mShipmentItem.ProductId
                {Toast.makeText(OneShipmentItemActivity.this,R.string.product_read, Toast.LENGTH_LONG).show();}

            }
            Log.d(TAG+"/product",String.valueOf(productId));
            Log.d(TAG+"/cell",cellRead);
            if (!cellRead.isEmpty()) {
                et_Cell.setText(cellRead);
                Toast.makeText(OneShipmentItemActivity.this,R.string.cell_read, Toast.LENGTH_LONG).show();
            }


        }
    }

    @Override
    public void onClick(View v) {

        if (new UtilsConnectivityService(OneShipmentItemActivity.this).checkConnectivity()) {
            TextView tvId = (TextView) findViewById(R.id.tv_ProductId);
            ProductPictureDialog pDialog = new ProductPictureDialog();
            Bundle bundle = new Bundle();
            bundle.putString("productId", tvId.getText().toString());
            pDialog.setArguments(bundle);
            pDialog.show(getFragmentManager(), "Заголовок");
    }
}}
