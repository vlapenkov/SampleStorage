package com.example.user.sample1.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.user.sample1.R;
import com.example.user.sample1.data.Product;
import com.example.user.sample1.data.ProductsDbHelper;
import com.example.user.sample1.dialogs.ProductPictureDialog;
import com.example.user.sample1.services.UtilsConnectivityService;

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
