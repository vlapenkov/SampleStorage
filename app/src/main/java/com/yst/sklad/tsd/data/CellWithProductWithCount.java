package com.yst.sklad.tsd.data;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by lapenkov on 21.01.2019.
 */

public class CellWithProductWithCount implements Serializable {

    public int Id,ProductId,Quantity;
    public String Name, Cell;


    public CellWithProductWithCount(int id , String cell,  int  productId,String name, int quantity ) {
        Id=id;
        Name=name;
        ProductId=productId;
        Quantity=quantity;
       Cell=cell;
    }

    public static CellWithProductWithCount fromCursor(Cursor cursor)
    {

        String name;
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.Inventory._ID));
        int productid = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.Inventory.COLUMN_PRODUCTID));
        try {
            name = cursor.getString(cursor.getColumnIndexOrThrow("productname"));
        }catch (Exception e)
        {
            name="";
        }

        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.Inventory.COLUMN_COUNT_FACT));
        String cell = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.Inventory.COLUMN_STOCKCELLFROM));

        return new CellWithProductWithCount(id,cell,productid,name,quantity);

    }

    public String toXML() {
        return "<tran:Cell>" +
                "<tran:productid>"+this.ProductId+"</tran:productid>" +
                "<tran:stockcell>"+this.Cell +"</tran:stockcell>" +
                "<tran:quantityfact>"+this.Quantity+"</tran:quantityfact>" +
                "</tran:Cell>";
    }
}


