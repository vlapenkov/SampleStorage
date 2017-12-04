package com.yst.sklad.tsd.activities;

import android.os.Bundle;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.Constants;
import com.yst.sklad.tsd.Utils.DeviceHelper;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplierItem;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.Utils.BarCodeUtils;

import java.io.Serializable;

import me.sudar.zxingorient.ZxingOrient;
import me.sudar.zxingorient.ZxingOrientResult;

/*
Форма в которой есть товар ячейка и количество  для считывания данных
 */
public class OneOrderOneCellActivity extends BaseScanActivity {

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

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        Intent intent = getIntent();


        etProductId = (EditText) findViewById(R.id.tv_ProductId);
        tvProductName =(TextView) findViewById(R.id.tvProductName);
        tvArticle =(TextView) findViewById(R.id.tv_Article);
        et_Cell = (EditText) findViewById(R.id.et_Cell);

        //  { если SMART DROID то скрываем кнопку SCAN
        Button btnScanBarCode = (Button) findViewById(R.id.btnScanBarCode);
        if (DeviceHelper.getTypeOfHardware()==1)   btnScanBarCode.setVisibility(View.INVISIBLE);
        // }
        CurrentOrderId = (long)intent.getSerializableExtra(OneOrderActivity.ORDER_ID_MESSAGE);
        //CurrentProductId =
        Serializable serProductId=  intent.getSerializableExtra(OneOrderCellsListActivity.PRODUCT_ID_MESSAGE);
        if (serProductId!=null) CurrentProductId = (int)serProductId;


        RefreshProductTexts(CurrentProductId,true);


        etQuantity= (EditText) findViewById(R.id.et_QuantityFact);

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


             cell= et_Cell.getText().toString();

            /*
              Product product = mDbHelper.getProductById(productId);
            if ( product==null)
            {
               errorMessage= "Отсутствует товар с данным кодом"; etProductId.requestFocus();

            }
*/

            if (productId< Constants.INITIAL_PRODUCT_ID)
            {
                errorMessage= "Отсутствует товар с данным кодом"; etProductId.requestFocus();

            }
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

    /*
       1. Обработка считывания для сканера
        */
    @Override
    public void onBarcodeScanned(String barcode) {
        processBarcode(barcode);

    }

    /*
    Обработка при считывании штрихкода

     */
    private void processBarcode(String barcode)
    {
        String cellRead="";
        EditText et_Cell = (EditText) findViewById(R.id.et_Cell);

        String contents=barcode;

        Log.d(TAG + "/scanned result: ", barcode);



        Product productFound = null;
        int productId=0;
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

    public void scanBarCode(View v)
    {

        new ZxingOrient(OneOrderOneCellActivity.this).initiateScan();
    }


  /*
    2. Обработка считывания для камеры
     */

        public void onActivityResult(int requestCode, int resultCode, Intent intent) {


//retrieve scan result
        ZxingOrientResult scanningResult = ZxingOrient.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

            String contents= scanningResult.getContents(); if (contents==null) return;
            processBarcode(contents);


        }
    }
}
