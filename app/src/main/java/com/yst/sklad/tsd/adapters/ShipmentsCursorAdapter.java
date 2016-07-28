package com.yst.sklad.tsd.adapters;

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
import com.yst.sklad.tsd.data.ProductsContract;
import com.yst.sklad.tsd.data.Shipment;

/**
 * Created by lapenkov on 26.07.2016.
 */
public class ShipmentsCursorAdapter extends CursorAdapter{

    public ShipmentsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.shipment_item, parent, false);
    }


    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        ImageView tvImg = (ImageView) view.findViewById(R.id.ivImg);
        TextView text1 = (TextView) view.findViewById(R.id.text1);
        TextView text2 = (TextView) view.findViewById(R.id.text2);
        TextView text3 = (TextView) view.findViewById(R.id.text3);
        //TextView tvText = (TextView) view.findViewById(R.id.tvText);
        // Extract properties from cursor

        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
        String dateOfShipment = cursor.getString(cursor.getColumnIndexOrThrow("dateofshipment"));
        String client = cursor.getString(cursor.getColumnIndexOrThrow("client"));
        Integer quantityfact = cursor.getInt(cursor.getColumnIndexOrThrow("quantityfact"));
        Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        Integer rows_count = cursor.getInt(cursor.getColumnIndexOrThrow("rows_count"));
     //   Integer rows_count_processed = cursor.getInt(cursor.getColumnIndexOrThrow("rows_count_processed"));


        // Populate fields with extracted properties
        text1.setText(String.valueOf(id));
        text2.setText(dateOfShipment);
        text3.setText(client);

        int resourcePicture = android.R.color.transparent;


        if(quantityfact>0 )  resourcePicture = R.drawable.checkbox_yes;
    //    if(rows_count==rows_count_processed && rows_count>0 )  resourcePicture = R.drawable.ic_assignment_turned_in_black_24dp;

        tvImg.setImageResource(resourcePicture);



    }
}
