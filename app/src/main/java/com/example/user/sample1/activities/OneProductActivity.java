package com.example.user.sample1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.Product;
import com.example.user.sample1.data.ProductsDbHelper;

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



        tvId.setText(Integer.toString(product.Id));
        tvName.setText(product.Name);
        tvBarCode.setText(product.Barcode);
        tvProductType.setText(product.getStringTypeOfProduct());



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
