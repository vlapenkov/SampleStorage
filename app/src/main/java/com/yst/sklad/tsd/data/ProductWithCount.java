package com.yst.sklad.tsd.data;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by user on 03.06.2016.
 */
public class ProductWithCount implements Serializable {

    public int Id,ProductId,Quantity;
    public String Name;


    public ProductWithCount(int id ,  int  productId,String name, int quantity ) {
        Id=id;
        Name=name;
        ProductId=productId;
        Quantity=quantity;

    }

    public static ProductWithCount fromCursor(Cursor cursor)
    {
        String name;
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductWithCountEntry._ID));
        int productid = cursor.getInt(cursor.getColumnIndexOrThrow("productid"));
        try {
             name = cursor.getString(cursor.getColumnIndexOrThrow("productname"));
        }catch (Exception e)
        {
            name="";
        }

        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantityfact"));

        return new ProductWithCount(id,productid,name,quantity);

    }



}
