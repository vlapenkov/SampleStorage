package com.yst.sklad.tsd.data;

import android.database.Cursor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.07.2017.
 * Строка товаров в заказе поставщику
 */
public class ArrivalItem {


    public String OrderId;
    public int RowNumber,ProductId;
    public int Quantity;
    public int Id;
    public String StockCell;


    public ArrivalItem( String orderId,  int productId, int quantity, String stockcell) {
        OrderId = orderId;
        ProductId = productId;
        Quantity = quantity;
        StockCell=stockcell;
    }


    public static ArrivalItem fromCursor(Cursor cursor) {

            String orderId = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ArrivalItemsEntry.COLUMN_ORDERTOSUPPLIERID));
            Integer productid = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ArrivalItemsEntry.COLUMN_PRODUCTID));
            Integer quantity= cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ArrivalItemsEntry.COLUMN_COUNT_FACT));
            String stockcell= cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ArrivalItemsEntry.COLUMN_STOCKCELL_FACT));

            return new ArrivalItem(orderId,productid,quantity,stockcell);
    }

/*
Формат  отправки в 1С
 <tran:Cell>
               <tran:productid>9260507</tran:productid>
               <tran:stockcell>32202021</tran:stockcell>
               <tran:quantityfact>1</tran:quantityfact>
            </tran:Cell>
 */
    public String toXML() {
        return "<tran:Cell>" +
                "<tran:productid>"+this.ProductId+"</tran:productid>" +
                "<tran:stockcell>"+this.StockCell +"</tran:stockcell>" +
                "<tran:quantityfact>"+this.Quantity+"</tran:quantityfact>" +
                "</tran:Cell>";
    }
}
