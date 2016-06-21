package com.yst.sklad.tsd.data;

import android.database.Cursor;

/**
 * Created by user on 03.06.2016.
 */
public class Product {

    public int Id,ProductType;
    public String Name, Barcode,Comments, Article;


    public Product(int id , String name, String barcode, String comments, int producttype,String article) {
        Id=id;
        Name=name;
        Barcode=barcode;
        Comments=comments;
        ProductType=producttype;
        Article=article;
    }

    public static Product fromCursor(Cursor cursor)
    {
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry._ID));
        String barcode = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_BARCODE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_NAME));
        int productType = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_COMMENTS));
        String article = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_ARTICLE));

        return new Product(id,name,barcode,comments,productType,article);

    }

    

    public String getStringTypeOfProduct()
    {
        String  name = "шины";
        switch (this.ProductType)
        {
            case 1: {name="шины" ; break;}
            case 2: {name="диски" ; break;}
            case 3: {name="аккумуляторы"; break;}
            case 4: {name="аксессуары"; break;}

        }
   return name;
    }
}
