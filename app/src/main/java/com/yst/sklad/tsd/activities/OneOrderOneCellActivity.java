package com.yst.sklad.tsd.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplierItem;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.services.BarCodeUtils;

import java.io.Serializable;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/*
Форма в которой есть товар ячейка и количество  для считывания данных
 */
public class OneOrderOneCellActivity extends AppCompatActivity {

    EditText etProductId;
    EditText etQuantity;
    TextView tvProductName;
    TextView tvArticle;
    EditText et_Cell;


    public static String TAG="OneOrderOneCellActivity";
    long  CurrentOrderId ;
    Integer CurrentProductId ;


    ProductsDbHelper mDbHelper;

    /*
    Обновить название и артикул при изменении кода
     */
    private void RefreshProductTexts (Integer productId,boolean setProductId)
    {
        Product product = null;
        if (productId!=null)   product =mDbHelper.getProductById(productId);

        String productName = product!=null?product.Name:"";
        String productArticle = product!=null?product.Article:"";

        if (product!=null) CurrentProductId=productId;

        if (setProductId&& productId!=null)   etProductId.setText(String.valueOf(productId));
        tvArticle.setText(productArticle);
        tvProductName.setText(productName);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_order_one_cell);

        mDbHelper = new ProductsDbHelper(this);

        Intent intent = getIntent();


        etProductId = (EditText) findViewById(R.id.tv_ProductId);
        tvProductName =(TextView) findViewById(R.id.tvProductName);
        tvArticle =(TextView) findViewById(R.id.tv_Article);
        et_Cell = (EditText) findViewById(R.id.et_Cell);

        CurrentOrderId = (long)intent.getSerializableExtra(OneOrderActivity.ORDER_ID_MESSAGE);
        //CurrentProductId =
        Serializable serProductId=  intent.getSerializableExtra(OneOrderCellsListActivity.PRODUCT_ID_MESSAGE);
        if (serProductId!=null) CurrentProductId = (int)serProductId;


        RefreshProductTexts(CurrentProductId,true);
        //String productName = mDbHelper.getProductById(mShipmentItem.ProductId).Name;
  //      Product product =mDbHelper.getProductById(CurrentProductId);

   //     String productName = product!=null?product.Name:"";
   //     String productArticle = product!=null?product.Article:"";

//        TextView tvRowNumber  = (TextView) findViewById(R.id.tv_RowNumber);
    //  EditText tvProductId = (EditText) findViewById(R.id.tv_ProductId);


        TextView tv_CellName = (TextView) findViewById(R.id.tv_Storage);
        etQuantity= (EditText) findViewById(R.id.et_QuantityFact);
        TextView tv_QuantityPlan = (TextView) findViewById(R.id.tv_QuantityPlan);

        TextView tv_RestCaption = (TextView) findViewById(R.id.tv_RestCaption);
        TextView tv_Rest = (TextView) findViewById(R.id.tv_Rest);



        etProductId.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 7) RefreshProductTexts(Integer.valueOf(s.toString()),false);
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });





    }

    /*
    Нажатие кнопки OK
     */
    public void doOKAndToNext(View v)
    {
        int quantity=0;
        String cell="";
      String errorMessage="";
        int productId=0;
        try {
           productId= Integer.parseInt(etProductId.getText().toString());
           Product product = mDbHelper.getProductById(productId);

             cell= et_Cell.getText().toString();

            if ( product==null)
            { errorMessage= "Отсутствует товар с данным кодом"; etProductId.requestFocus(); }

             else if (cell==null || cell.isEmpty())
            { errorMessage= "Заполните поле ячейка"; et_Cell.requestFocus(); }
            else
            {
               String cellName= mDbHelper.getNameOfCell(cell);
                if (cellName==null || cellName.isEmpty()) errorMessage= "Ячейка с данным кодом не найдена";
                et_Cell.requestFocus();
            }


        }
        catch (Exception e) {
            errorMessage= "Введен неверный код товара или ячейки";
         }

        if (errorMessage.isEmpty())
        try {
             quantity = Integer.parseInt( etQuantity.getText().toString());
           if(quantity==0)  errorMessage= "Введите количество ";

        }catch (Exception e)
        {
            errorMessage= "Введите количество ";
        }



        if (!errorMessage.isEmpty())      Toast.makeText(this,errorMessage, Toast.LENGTH_LONG).show();


        else {
            // добавить запись в таблицу поступлений
            mDbHelper.addArrivalItem(new ArrivalItem(String.valueOf(CurrentOrderId), productId, quantity, cell));


            // если товара нет в таблице заказов (пересорт)
            // то получить номер последней строки rNumber и запись туда
            if (!mDbHelper.orderHasProductId(String.valueOf(CurrentOrderId),productId)) {
                int rNumber = mDbHelper.getLastRowNumberOfOrder(String.valueOf(CurrentOrderId));
                rNumber++;
                mDbHelper.addOrderToSupplierItem(new OrderToSupplierItem(String.valueOf(CurrentOrderId),rNumber,productId,quantity));
            }


            finish();
        }
    }


    public void scanBarCode(View v)
    {

        new ZxingOrient(OneOrderOneCellActivity.this).initiateScan();
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

            boolean productIsFound = false;
            int productId = BarCodeUtils.getProductIdFromBarCode(contents);
            Product productFound = null;


            //RefreshProductTexts(productId)
            if (productId==0) {
                productFound = mDbHelper.getProductByBarCode(contents);
                if (productFound!=null) productId=productFound.Id;
            }
            if (productId>0)
            {
                RefreshProductTexts(productId,true);

            }



            //  это ячейка
            if (productId==0 && contents!=null &&contents.length()==8) { cellRead =BarCodeUtils.getCellFromBarCode(contents);}

            Log.d(TAG + "/product", String.valueOf(productId));
            Log.d(TAG + "/cell", cellRead);
            if (!cellRead.isEmpty()) {
                et_Cell.setText(cellRead);
                TextView tv_CellName = (TextView) findViewById(R.id.tv_Storage);
                tv_CellName.setText(mDbHelper.getNameOfCell(cellRead));
             //   Toast.makeText(OneShipmentItemActivity.this,R.string.cell_read, Toast.LENGTH_LONG).show();
            }


        }
    }
}
