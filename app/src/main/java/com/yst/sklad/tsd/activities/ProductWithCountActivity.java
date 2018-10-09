package com.yst.sklad.tsd.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.Utils.BarCodeUtils;
import com.yst.sklad.tsd.data.AppDataProvider;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductWithCount;
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.ProductsDbHelper;

public class ProductWithCountActivity extends BaseScanActivity {

    TextView tvProductName, tvProductCaption;
    EditText etQuantityFact,tvProductId;
    Button btnOKAndToNext;
    ProductWithCount data;
    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_PRODUCTSWITHCOUNT;
    boolean mToCreate;
    private final int defaultValueFact=1;
    ProductsDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_with_count);

        tvProductName =(TextView) findViewById(R.id.tv_ProductName) ;
        tvProductCaption =(TextView) findViewById(R.id.tv_ProductCaption) ;
        tvProductId =(EditText) findViewById(R.id.tv_ProductId) ;
        etQuantityFact =(EditText) findViewById(R.id.et_QuantityFact) ;
        btnOKAndToNext =(Button) findViewById(R.id.btnOKAndToNext);

        mDbHelper = ((MainApplication)getApplication()).getDatabaseHelper();

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mToCreate =bundle.getBoolean(ListOfProductsWithCountActivity.MESSAGE_TO_CREATE,true);
        if (!mToCreate)     data= (ProductWithCount)bundle.getSerializable(ListOfProductsWithCountActivity.INFO_MESSAGE);




if (!mToCreate) {
    tvProductId.setText("" + data.ProductId);
    tvProductName.setText(data.Name);
    //    tvProductCaption.setText(""+pwC.Id);
    etQuantityFact.setText("" + data.Quantity);
}else
{
    etQuantityFact.setText(""+defaultValueFact);
}
    }


    private void RefreshProductTexts (Integer productId)
    {
        Product product = null;
        if (productId!=null)   product =mDbHelper.getProductById(productId);

        String productName = product!=null?product.Name:"";

        tvProductName.setText(productName);
    }

    @Override
    public void onBarcodeScanned(String mBarcode) {
        if (!mToCreate)
        {Toast.makeText(this,"Вы редактируете количество товара, не нужно считывать товар! ",Toast.LENGTH_LONG).show();
            return;}
        // для режима добавления товара
        int newProductId=BarCodeUtils.getProductIdFromBarCode(mBarcode);

        tvProductId.setText("" +newProductId);
        if (newProductId>0)
        RefreshProductTexts(newProductId);
        btnOKAndToNext.requestFocus();
    }

    public void doOKAndToNext(View view) {

        String strProductId=tvProductId.getText().toString();

        if(TextUtils.isEmpty(strProductId)||!TextUtils.isDigitsOnly(strProductId) || strProductId.length()!=7)
        { Toast.makeText(this,"Неверный код товара",Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues cv = new ContentValues();

        int quantityfact=  Integer.parseInt(etQuantityFact.getText().toString());
        cv.put(ProductsContract.ProductWithCountEntry.COLUMN_COUNT_FACT, quantityfact);

        if(!mToCreate) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, data.Id);
            int cnt = getContentResolver().update(uri, cv, null, null);
        }else
        {

      //Integer foundId=     mDbHelper.checkIfProductWithCountExists(Integer.parseInt(tvProductId.getText().toString()));
            ProductWithCount productWithCount = mDbHelper.checkIfProductWithCountExists(Integer.parseInt(tvProductId.getText().toString()));

// такой товар уже есть (добавляем quantityfact)
    if (productWithCount!=null)
    {
        quantityfact =quantityfact+ productWithCount.Quantity;
        cv.put(ProductsContract.ProductWithCountEntry.COLUMN_COUNT_FACT, quantityfact);
        Uri uri = ContentUris.withAppendedId(CONTENT_URI, productWithCount.Id);
        int cnt = getContentResolver().update(uri, cv, null, null);

    }else

    {

        cv.put(ProductsContract.ProductWithCountEntry.COLUMN_PRODUCTID, Integer.parseInt(tvProductId.getText().toString()));
        Uri newUri = getContentResolver().insert(CONTENT_URI, cv);

    }
        }
        finish();
    }

    public void doCancel(View view )
    {
        finish();
    }
}
