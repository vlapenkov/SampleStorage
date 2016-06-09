package com.example.user.sample1.data;

import android.database.Cursor;

/**
 * Created by user on 03.06.2016.
 */
public class Product {

    public int Id,ProductType;
    public String Name, Barcode,Comments;

    public Product(int id , String name, String barcode, String comments, int producttype) {
        Id=id;
        Name=name;
        Barcode=barcode;
        Comments=comments;
        ProductType=producttype;
    }

    public static Product fromCursor(Cursor cursor)
    {
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry._ID));
        String barcode = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_BARCODE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_NAME));
        int productType = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_COMMENTS));

        return new Product(id,name,barcode,comments,productType);

    }

    public String getStringTypeOfProduct()
    {
        switch (this.ProductType)
        {
            case 1: return "tyre";
            case 2: return "disk";
            case 3: return "battery";
            case 4: return "accessoir";
            default: return "other";
        }

    }
}
