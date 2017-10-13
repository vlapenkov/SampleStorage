package com.yst.sklad.tsd.data;

import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.07.2017.
 * Заказ поставщику
 */
public class OrderToSupplier {


    // number in 1S
    public String Id;
    public String DateOfOrder, Client,Comments,NumberIn1S;
    public int OrderType;

    public OrderToSupplier(String id,String numberIn1S, String dateOfOrder, String client, int orderType,String comments) {
        // correct id as regex


        Id = id;

        DateOfOrder = dateOfOrder;
        Client = client;
        Comments = comments;
        OrderType=orderType;
        NumberIn1S=numberIn1S;

    }


    public static OrderToSupplier fromCursor(Cursor cursor)
    {
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry._ID));
        String numberIn1S = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry.COLUMN_NUMBERIN1S));
        String dateOfOrder = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry.COLUMN_DATE));
        String client = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry.COLUMN_CLIENT));
        int ordertype  = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry.COLUMN_ORDERTYPE));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.OrdersToSupplierEntry.COLUMN_COMMENTS));

        return new OrderToSupplier(Integer.toString(id),numberIn1S,dateOfOrder,client,ordertype,comments);

    }

    public  static String getCleanId(String numberin1s )
    {
        //   Pattern p = Pattern.compile("[1-9][0-9]+");
        Pattern p = Pattern.compile("[1-9][0-9]*");
        Matcher m = p.matcher(numberin1s);
        m.find();

       return m.group(0);
     /* int  result=  Integer.parseInt(m.group(0));
      return result; */

    }
}
