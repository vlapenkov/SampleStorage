package com.yst.sklad.tsd.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.common.StringUtils;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.ShipmentItem;

import java.io.Serializable;


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
        try {
          int productId= Integer.parseInt(etProductId.getText().toString());
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
            mDbHelper.addArrivalItem(new ArrivalItem(String.valueOf(CurrentOrderId), CurrentProductId, quantity, cell));
            finish();
        }
    }
}
