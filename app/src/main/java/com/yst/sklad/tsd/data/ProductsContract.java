package com.yst.sklad.tsd.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 02.06.2016.
 */
public class ProductsContract {

    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_SHIPMENTS = "shipments";
    public static final String PATH_SHIPMENTITEMS = "shipmentitems";


    /*
       Товар
     */

    public static final class ProductsEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "products";


        public static final String COLUMN_GUID = "guid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BARCODE = "barcode";
        public static final String COLUMN_PRODUCTTYPE = "producttype";
        public static final String COLUMN_ARTICLE = "artilce";
         public static final String COLUMN_COMMENTS = "comments";

    }

    /*
    Штрихкоды товаров
     */
    public static final class ProductBarcodesEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "productbarcodes";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_BARCODE = "barcode";

    }

    /*
        Задание на отгрузку
     */
    public static final class ShipmentsEntry implements BaseColumns {
  // Table name
        public static final String TABLE_NAME = "shipments";


        public static final String COLUMN_DATE = "dateofshipment";
        public static final String COLUMN_CLIENT = "client";
        public static final String COLUMN_GUID = "guid";
        public static final String COLUMN_NUMBER = "numberin1s";
        public static final String COLUMN_COMMENTS = "comments";
        public static final String COLUMN_ISPROCESSED = "isprocessed";


    }

    /*
        Строка задания на отгрузку
     */
    public static final class ShipmentsItemEntry implements BaseColumns {
        // Table name
        public static final String TABLE_NAME = "shipmentitems";


        public static final String COLUMN_SHIPMENTID = "shipmentid";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_ROWNUMBER = "rownumber";
        public static final String COLUMN_STOCKCELL = "stockcell";
        public static final String COLUMN_STOCKCELL_FACT = "stockcellfact";
        public static final String COLUMN_COUNT_FACT = "quantityfact";
        public static final String COLUMN_COUNT = "quantity";
        public static final String COLUMN_REST = "rest";
        public static final String COLUMN_QUEUE ="queue" ;
        //public static final String COLUMN_ISPROCESSED = "isprocessed";


    }

    /*
      Склады
   */
    public static final class StorageEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "storages";

    }


    /*
  Ячейки
*/
    public static final class StockCellEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "stockcells";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_STORAGEID = "storageid";

    }

    /*
   Заказы поставщикам _id = номер заказа поставщику (СП003101)
*/
    public static final class OrdersToSupplierEntry implements BaseColumns {

        // Table name


        public static final String TABLE_NAME = "orderstosuppliers";
        public static final String COLUMN_NUMBERIN1S="numberin1s";
        public static final String COLUMN_CLIENT = "client";
        public static final String COLUMN_ARRIVALNUMBER = "arrivalnumber";
        public static final String COLUMN_COMMENTS = "comments";

        public static final String COLUMN_DATE = "dateoforder";
        public static final String COLUMN_ORDERTYPE ="ordertype" ; // 0 - заказ поставщику, 1- перемещение
    }

    /*
   Заказы поставщикам табличная часть
*/
    public static final class OrdersToSupplierItemEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "orderstosuppliersitems";
        public static final String COLUMN_ROWNUMBER = "rownumber";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_ORDERTOSUPPLIERID = "ordertosupplierid" ;// внешний ключ
        public static final String COLUMN_COUNT = "quantity";

    }

    /*
  Заказы поставщикам табличная часть с разбивкой по ячейкам
*/
    public static final class ArrivalItemsEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "arrivalitems";
     //   public static final String COLUMN_ROWNUMBER = "rownumber";
        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_ORDERTOSUPPLIERID = "ordertosupplierid" ;// внешний ключ
        public static final String COLUMN_COUNT_FACT = "quantityfact";
        public static final String COLUMN_STOCKCELL_FACT = "stockcell";

    }

/*
   Простая таблица для сканирования productId, count
 */
    public static final class ProductWithCountEntry implements BaseColumns {

        // Table name
        public static final String TABLE_NAME = "productwithcountitems";

        public static final String COLUMN_PRODUCTID = "productid";
        public static final String COLUMN_COUNT_FACT = "quantityfact";


    }


}
