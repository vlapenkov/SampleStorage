package com.yst.sklad.tsd.data;

import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 09.06.2016.
 */
public class Shipment {
    // number in 1S
    public String Id;
    public String DateOfShipment, Client,Comments;

    public Shipment(String id, String dateOfShipment, String client, String comments) {
        // correct id as regex


        Id = id;

        DateOfShipment = dateOfShipment;
        Client = client;
        Comments = comments;
    }


  public  static String getCleanId(String oldId )
  {
   //   Pattern p = Pattern.compile("[1-9][0-9]+");
      Pattern p = Pattern.compile("[1-9][0-9]*");
      Matcher m = p.matcher(oldId);
      m.find();
      return m.group(0);

  }

/*
    public static Shipment fromCursor(Cursor cursor)
    {
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsEntry._ID));
        String dateOfShipment = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsEntry.COLUMN_DATE));
        String name = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsEntry.COLUMN_CLIENT));
        int productType = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE));
        String comments = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_COMMENTS));
        String article = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ProductsEntry.COLUMN_ARTICLE));

        return new Product(id,name,barcode,comments,productType,article);

    }
    */

    @Override
    public String toString() {
        return "Shipment{" +
                "Id='" + Id + '\'' +
                ", DateOfShipment='" + DateOfShipment + '\'' +
                ", Client='" + Client + '\'' +
                ", Comments='" + Comments + '\'' +
                '}';
    }
}

