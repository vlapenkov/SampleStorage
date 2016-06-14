package com.example.user.sample1.data;

import android.database.Cursor;

import java.io.Serializable;

/**
 * Created by user on 09.06.2016.
 */
public class ShipmentItem implements Serializable{
    public int Id;
    public String ShipmentId;
    public int RowNumber,ProductId;
    public String StockCell;
    public String StockCellFact;
    public int Quantity;
    public int QuantityFact;

    public int getId()
    {return this.Id;};

    public ShipmentItem(String shipmentId, int rowNumber, int productId, String stockCell, int quantity) {
        ShipmentId = shipmentId;
        RowNumber = rowNumber;
        ProductId = productId;
        StockCell = stockCell;
        Quantity = quantity;
    }

    public ShipmentItem(int id, String shipmentId, int rowNumber, int productId, String stockCell, String stockCellFact, int quantity, int quantityFact) {
        Id = id;
        ShipmentId = shipmentId;
        RowNumber = rowNumber;
        ProductId = productId;
        StockCell = stockCell;
        StockCellFact = stockCellFact;
        Quantity = quantity;
        QuantityFact = quantityFact;
    }

    @Override
    public String toString() {
        return "ShipmentItem{" +
                "ShipmentId='" + ShipmentId + '\'' +
                ", RowNumber=" + RowNumber +
                ", ProductId=" + ProductId +
                ", StockCell='" + StockCell + '\'' +
                ", Quantity=" + Quantity +
                '}';
    }

    /*
    *  "            <tran:Products>\n" +
                "               <tran:rownumber>24</tran:rownumber>\n" +
                "               <tran:productid>9130104</tran:productid>\n" +
                "               <tran:stockcell>81110735</tran:stockcell>\n" +
                "               <tran:quantity>3</tran:quantity>\n" +
                "               <tran:quantityfact>19</tran:quantityfact>\n" +
                "            </tran:Products>\n" +*/
    public String toXML() {
        return "<tran:Products>" +
                "<tran:rownumber>"+this.RowNumber+"</tran:rownumber>"+
                "<tran:productid>"+this.ProductId+"</tran:productid>" +
                "<tran:stockcell>"+this.StockCellFact +"</tran:stockcell>" +
                "<tran:quantity>"+this.Quantity+"</tran:quantity>" +
                "<tran:quantityfact>"+this.QuantityFact+"</tran:quantityfact>" +
                "</tran:Products>";
    }

/*
 public static final String COLUMN_SHIPMENTID = "shipmentid";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_ROWNUMBER = "rownumber";
        public static final String COLUMN_STOCKCELL = "stockcell";
        public static final String COLUMN_STOCKCELL_FACT = "stockcellfact";
        public static final String COLUMN_COUNT_FACT = "quantityfact";
        public static final String COLUMN_COUNT = "quantity";
 */
    public static ShipmentItem fromCursor(Cursor cursor)
    {
        Integer id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry._ID));
        String shipmentid = cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_SHIPMENTID));
        Integer rownumber = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_ROWNUMBER));
        Integer productid = cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_PRODUCTID));
        String stockcell= cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_STOCKCELL));
        String stockcellFact= cursor.getString(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_STOCKCELL_FACT));
        Integer quantity= cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_COUNT));
        Integer quantityFact= cursor.getInt(cursor.getColumnIndexOrThrow(ProductsContract.ShipmentsItemEntry.COLUMN_COUNT_FACT));

        return new ShipmentItem(id,shipmentid,rownumber,productid,stockcell,stockcellFact,quantity,quantityFact);

    }
}
