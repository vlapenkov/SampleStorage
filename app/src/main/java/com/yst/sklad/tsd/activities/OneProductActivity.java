package com.yst.sklad.tsd.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.Product;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.dialogs.ProductPictureDialog;
import com.yst.sklad.tsd.services.UtilsConnectivityService;

/*
* Форма одного товара из списка товаров
* */
public class OneProductActivity extends AppCompatActivity {

    //ProductsDbHelper dbhelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oneproduct);

        Intent intent = getIntent();
        String productId = intent.getStringExtra(ProductsActivity.PRODUCT_ID_MESSAGE);

        ProductsDbHelper dbhelper = new ProductsDbHelper(this);

        Product product = dbhelper.getProductById(Integer.parseInt(productId));

        TextView tvId = (TextView) findViewById(R.id.tv_Id);
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvBarCode = (TextView) findViewById(R.id.tvBarCode);
        TextView tvProductType = (TextView) findViewById(R.id.tvProductType);
        TextView tvArticle = (TextView) findViewById(R.id.tv_Article);



        tvId.setText(Integer.toString(product.Id));
        tvName.setText(product.Name);
        tvBarCode.setText(product.Barcode);
        tvProductType.setText(product.getStringTypeOfProduct());
        tvArticle.setText(product.Article);



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void showPicture (View v)
    {
        if (new UtilsConnectivityService(OneProductActivity.this).checkConnectivity()) {
        TextView tvId = (TextView) findViewById(R.id.tv_Id);
        ProductPictureDialog pDialog = new ProductPictureDialog();
        Bundle bundle = new Bundle();
        bundle.putString("productId", tvId.getText().toString());
        pDialog.setArguments(bundle);
        pDialog.show(getFragmentManager(), "Заголовок");
    }
    }


}
