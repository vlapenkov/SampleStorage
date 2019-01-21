package com.yst.sklad.tsd.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.yst.sklad.tsd.MainApplication;

/**
 * Created by user on 19.12.2017.
 *
 */
public class AppDataProvider  extends ContentProvider {
    ProductsDbHelper helper;
    public static final String AUTHORITY = "com.yst.sklad.tsd";
    public static final String PATH_SHIPMENTS = "shipments";
    public static final String PATH_PRODUCTSWITHCOUNT = "productswithcount";
    public static final String PATH_SHIPMENTITEMS = "shipmentitems";
    public static final String PATH_INVENTORY = "inventory";
    //public static final String PATH_SHIPMENTS = "shipments";

    public static final Uri CONTENTURI_SHIPMENTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_SHIPMENTS );
    public static final Uri CONTENTURI_SHIPMENTITEMS = Uri.parse("content://" + AUTHORITY + "/" + PATH_SHIPMENTITEMS );

    public static final Uri CONTENTURI_PRODUCTSWITHCOUNT = Uri.parse("content://" + AUTHORITY + "/" + PATH_PRODUCTSWITHCOUNT );
    public static final Uri CONTENTURI_INVENTORY = Uri.parse("content://" + AUTHORITY + "/" + PATH_INVENTORY );

    private static final int SHIPMENTS = 1;
    private static final int SHIPMENT_ID = 2;
    private static final int SHIPMENTITEMS = 3;
    private static final int SHIPMENTITEMS_ID = 4;
    private static final int PRODUCTSWITHCOUNT = 5;
    private static final int PRODUCTSWITHCOUNT_ID = 6;
    private static final int INVENTORY = 7;
    private static final int INVENTORY_ID = 8;

    /*
    С несколькими таблицами uriMatcher должен выглядеть так
    https://stackoverflow.com/questions/13572352/own-contentprovider-with-sqlite-and-multiple-tables
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI(AUTHORITY,PATH_SHIPMENTS, SHIPMENTS);
        uriMatcher.addURI(AUTHORITY,PATH_SHIPMENTS + "/#",SHIPMENT_ID);
        uriMatcher.addURI(AUTHORITY,PATH_SHIPMENTITEMS, SHIPMENTITEMS);
        uriMatcher.addURI(AUTHORITY,PATH_SHIPMENTITEMS + "/#",SHIPMENTITEMS_ID);
        uriMatcher.addURI(AUTHORITY,PATH_PRODUCTSWITHCOUNT, PRODUCTSWITHCOUNT);
        uriMatcher.addURI(AUTHORITY,PATH_PRODUCTSWITHCOUNT+ "/#", PRODUCTSWITHCOUNT_ID);
        uriMatcher.addURI(AUTHORITY,PATH_INVENTORY, INVENTORY);
        uriMatcher.addURI(AUTHORITY,PATH_INVENTORY+ "/#", INVENTORY_ID);

    }

    private SQLiteDatabase database;

    // ((MainApplication)getApplication()).getDatabaseHelper();
    @Override
    public boolean onCreate() {
         helper = new ProductsDbHelper(getContext());
      //  database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        database= helper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
            {
                cursor = helper.getShipments(null); /* database.query(ProductsContract.ShipmentsEntry.TABLE_NAME,new String[]{"_id","client","numberin1s"},
                        s,null,null,null,null); */
                break;}
      /*      case SHIPMENTITEMS:
                cursor=helper.getShipmentItems("7891");
                break; */
            case PRODUCTSWITHCOUNT:
            {
                cursor=helper.getProductsWithCoount();

                break;}

            case INVENTORY : {
           // cursor =   helper.getReadableDatabase().query(ProductsContract.Inventory.TABLE_NAME, null, null, null, null, null, null);
                cursor= helper.getInventoryItems();
              break;
            }
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {

        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                return "vnd.android.cursor.dir/shipments";
            case PRODUCTSWITHCOUNT:
                return "vnd.android.cursor.dir/productswithcount";
            case INVENTORY:
                return "vnd.android.cursor.dir/inventory";
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        Uri contentUri;
        long id=0;
        database= helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                 id = database.insert(ProductsContract.ShipmentsEntry.TABLE_NAME,null,contentValues);
                contentUri = CONTENTURI_SHIPMENTS;
                break;
      /*      case SHIPMENTITEMS:
                contentUri = CONTENTURI_SHIPMENTITEMS;
                break; */
            case PRODUCTSWITHCOUNT:
                id = database.insert(ProductsContract.ProductWithCountEntry.TABLE_NAME,null,contentValues);
                contentUri = CONTENTURI_PRODUCTSWITHCOUNT;
                break;
            case INVENTORY:
                id = database.insert(ProductsContract.Inventory.TABLE_NAME,null,contentValues);
                contentUri = CONTENTURI_INVENTORY;
                break;
            default:
                throw new  SQLException("Insertion Failed for URI :" + uri);
        }
        if (id > 0) {
            Uri _uri = ContentUris.withAppendedId(contentUri, id);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Insertion Failed for URI :" + uri);

    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        int delCount = 0;
        database= helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                delCount =  database.delete(ProductsContract.ShipmentsEntry.TABLE_NAME,s,strings);
                break;
            case SHIPMENT_ID :
            {
                String id = uri.getPathSegments().get(1);
                database.delete(ProductsContract.ShipmentsEntry.TABLE_NAME,"_id=" + id,strings);
                break;
            }
            case PRODUCTSWITHCOUNT:
            {
                database.delete(ProductsContract.ProductWithCountEntry.TABLE_NAME,s,strings);
                break;
            }
            case INVENTORY:
            {
                database.delete(ProductsContract.Inventory.TABLE_NAME,s,strings);
                break;
            }
            case INVENTORY_ID:
            { String id = uri.getPathSegments().get(1);
                database.delete(ProductsContract.Inventory.TABLE_NAME,"_id=" + id,strings);
                break;
            }
     /*       case SHIPMENTITEMS:
                delCount =  database.delete(ProductsContract.ShipmentsItemEntry.TABLE_NAME,s,strings);
                break;

            case SHIPMENTITEMS_ID:
            {
                String id = uri.getPathSegments().get(1);
                database.delete(ProductsContract.ShipmentsItemEntry.TABLE_NAME,"_id=" + id,strings);
                break;
            } */
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int updCount = 0;
        database= helper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                updCount =  database.update(ProductsContract.ShipmentsEntry.TABLE_NAME,contentValues,s,strings);
                break;
            case SHIPMENT_ID :
            {String id = uri.getPathSegments().get(1);
                database.update(ProductsContract.ShipmentsEntry.TABLE_NAME,contentValues,"_id=" + id,strings);
                break;
            }
            case PRODUCTSWITHCOUNT_ID :
            {
                String id = uri.getPathSegments().get(1);
              database.update(ProductsContract.ProductWithCountEntry.TABLE_NAME,contentValues,"_id=" + id,strings);
                break;
            }
            case INVENTORY_ID :
            {
                String id = uri.getPathSegments().get(1);
                database.update(ProductsContract.Inventory.TABLE_NAME,contentValues,"_id=" + id,strings);
                break;
            }
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
}
