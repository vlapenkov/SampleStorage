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


}
