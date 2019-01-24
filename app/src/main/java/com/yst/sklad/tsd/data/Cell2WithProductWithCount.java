package com.yst.sklad.tsd.data;

import android.database.Cursor;

/**
 * Created by lapenkov on 23.01.2019.
 */

public class Cell2WithProductWithCount extends CellWithProductWithCount {
    public String CellTo;
    public Cell2WithProductWithCount(int id, String cellFrom,String cellTo, int productId, String name, int quantity) {
        super(id, cellFrom, productId, name, quantity);
        CellTo=cellTo;
    }

    public static Cell2WithProductWithCount fromCursor(Cursor cursor)
    {

        String name;
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.TransferOfProductsInternalEntry._ID));
        int productid = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.TransferOfProductsInternalEntry.COLUMN_PRODUCTID));
        try {
            name = cursor.getString(cursor.getColumnIndexOrThrow("productname"));
        }catch (Exception e)
        {
            name="";
        }

        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.TransferOfProductsInternalEntry.COLUMN_COUNT_FACT));
        String cell = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.TransferOfProductsInternalEntry.COLUMN_STOCKCELLFROM));
        String cellTo = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.TransferOfProductsInternalEntry.COLUMN_STOCKCELLTO));

        return new Cell2WithProductWithCount(id,cell,cellTo,productid,name,quantity);

    }

    public String toXML() {
        return "<tran:Cell2>" +
                "<tran:productid>"+this.ProductId+"</tran:productid>" +
                "<tran:cell_out>"+this.Cell +"</tran:cell_out>" +
                "<tran:cell_in>"+this.CellTo +"</tran:cell_in>" +
                "<tran:quantity>"+this.Quantity+"</tran:quantity>" +
                "</tran:Cell2>";
    }
}