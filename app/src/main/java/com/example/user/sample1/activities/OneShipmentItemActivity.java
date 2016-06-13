package com.example.user.sample1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.data.ShipmentItem;
import com.google.zxing.common.StringUtils;

public class OneShipmentItemActivity extends AppCompatActivity {
    ShipmentItem mShipmentItem;
    EditText et_Cell;
    EditText et_QuantityFact;


    ProductsDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_shipment_item);

        mDbHelper = new ProductsDbHelper(this);



        Intent intent = getIntent();
        mShipmentItem = (ShipmentItem)intent.getSerializableExtra(OneShipmentActivity.SHIPMENTITEM_ID_MESSAGE);
        String productName = mDbHelper.getProductById(mShipmentItem.ProductId).Name;
        //Log.d("Victiry!",mShipmentItem.StockCell);
        TextView tvRowNumber  = (TextView) findViewById(R.id.tv_RowNumber);
        TextView tvProductId = (TextView) findViewById(R.id.tv_ProductId);
        TextView tvProductName =(TextView) findViewById(R.id.tvProductName);
        //TextView tv_CellPlanCaption = (TextView) findViewById(R.id.tv_CellPlanCaption);
        EditText et_Cell = (EditText) findViewById(R.id.et_Cell);
        TextView tv_Storage = (TextView) findViewById(R.id.tv_Storage);
        //EditText et_CellFact = (EditText) findViewById(R.id.et_CellFact);
        //TextView tv_StorageFact =
        EditText et_QuantityFact= (EditText) findViewById(R.id.et_QuantityFact);
        TextView tv_QuantityPlan = (TextView) findViewById(R.id.tv_QuantityPlan);

        tvRowNumber.setText(String.valueOf(mShipmentItem.RowNumber));
        tvProductId.setText(String.valueOf(mShipmentItem.ProductId));
        //tvProductName.setText(mShipmentItem.);
        //tv_CellPlanCaption.setText(mShipmentItem.StockCell);

        //tv_Storage.setText();

        if(mShipmentItem.StockCellFact != null && !mShipmentItem.StockCellFact.isEmpty()) et_Cell.setText(mShipmentItem.StockCellFact);
        else et_Cell.setText(mShipmentItem.StockCell);


        if(mShipmentItem.QuantityFact!=0 && mShipmentItem.QuantityFact!=mShipmentItem.Quantity) et_QuantityFact.setText(String.valueOf(mShipmentItem.QuantityFact));
        else et_QuantityFact.setText(String.valueOf(mShipmentItem.Quantity));


        tvProductName.setText(productName);
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
}
