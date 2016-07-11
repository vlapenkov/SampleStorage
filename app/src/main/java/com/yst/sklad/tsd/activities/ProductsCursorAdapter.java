package com.yst.sklad.tsd.activities;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.Product;

/**
 * Created by user on 03.06.2016.
 *
 */
public class ProductsCursorAdapter extends CursorAdapter  {

   
    public ProductsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView tvImg = (ImageView) view.findViewById(R.id.ivImg);
        TextView tvId = (TextView) view.findViewById(R.id.tvId);
        TextView tvText = (TextView) view.findViewById(R.id.tvText);
        // Extract properties from cursor


        Product product = Product.fromCursor(cursor);

        // Populate fields with extracted properties
        tvId.setText(String.valueOf(product.Id));
        tvText.setText(product.Name);
        int resourcePicture = R.drawable.wheel;
        switch (product.ProductType) {
            case 1: {resourcePicture=R.drawable.tire; break;}
        case 2: {resourcePicture=R.drawable.wheel; break;}
        case 3: {resourcePicture=R.drawable.battery; break;}
            case 4: {resourcePicture=R.drawable.ic_accesoir; break;}
        default:
        }
        tvImg.setImageResource(resourcePicture);



    }


}
