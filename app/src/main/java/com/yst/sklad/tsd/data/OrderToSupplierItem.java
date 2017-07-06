package com.yst.sklad.tsd.data;

import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.07.2017.
 * Строка товаров в заказе поставщику
 */
public class OrderToSupplierItem {


    public String OrderId;
    public int RowNumber,ProductId;
    public int Quantity;
    public int Id;


    public OrderToSupplierItem(int id,String orderId, int rowNumber, int productId, int quantity) {

        Id=id;
        OrderId = orderId;
        RowNumber = rowNumber;
        ProductId = productId;
        Quantity = quantity;

    }

    public OrderToSupplierItem(String orderId, int rowNumber, int productId, int quantity) {
        // correct id as regex
        OrderId = orderId;

        RowNumber = rowNumber;
        ProductId = productId;
        Quantity = quantity;

    }

    public static OrderToSupplierItem fromCursor(Cursor cursor)
    {

        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry._ID));

        String orderId = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierItemEntry.COLUMN_ORDERTOSUPPLIERID));
        Integer rownumber = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierItemEntry.COLUMN_ROWNUMBER));
        Integer productid = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierItemEntry.COLUMN_PRODUCTID));
        Integer quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierItemEntry.COLUMN_COUNT));


        return new OrderToSupplierItem(id,orderId,rownumber,productid,quantity);

    }

    public  static String getCleanId(String oldId )
    {
        //   Pattern p = Pattern.compile("[1-9][0-9]+");
        Pattern p = Pattern.compile("[1-9][0-9]*");
        Matcher m = p.matcher(oldId);
        m.find();
        return m.group(0);

    }
}
