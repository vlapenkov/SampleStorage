package com.example.user.sample1.data;

/**
 * Created by user on 09.06.2016.
 */
public class ShipmentItem {
    public String ShipmentId;
    public int RowNumber,ProductId;
    public String StockCell;
    public int Quantity;

    public ShipmentItem(String shipmentId, int rowNumber, int productId, String stockCell, int quantity) {
        ShipmentId = shipmentId;
        RowNumber = rowNumber;
        ProductId = productId;
        StockCell = stockCell;
        Quantity = quantity;
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
}
