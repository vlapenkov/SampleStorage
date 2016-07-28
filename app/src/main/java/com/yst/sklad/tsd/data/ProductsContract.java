package com.yst.sklad.tsd.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by user on 02.06.2016.
 */
public class ProductsContract {

    public static final String CONTENT_AUTHORITY = "com.example.user.sample1";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);



    public static final String PATH_PRODUCTS = "products";
    public static final String PATH_SHIPMENTS = "shipments";
    public static final String PATH_SHIPMENTITEMS = "shipmentitems";


    /*
       Товар
     */

    public static final class ProductsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_PRODUCTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        // Table name
        public static final String TABLE_NAME = "products";


        public static final String COLUMN_GUID = "guid";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_BARCODE = "barcode";
        public static final String COLUMN_PRODUCTTYPE = "producttype";
        public static final String COLUMN_ARTICLE = "artilce";
         public static final String COLUMN_COMMENTS = "comments";



        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /*
        Задание на отгрузку
     */
    public static final class ShipmentsEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHIPMENTS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIPMENTS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIPMENTS;

        // Table name
        public static final String TABLE_NAME = PATH_SHIPMENTS;


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

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_SHIPMENTITEMS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIPMENTITEMS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SHIPMENTITEMS;

        // Table name
        public static final String TABLE_NAME = PATH_SHIPMENTITEMS;


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


}
