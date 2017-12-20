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
    public static final String PATH_SHIPMENTITEMS = "shipmentitems";
    //public static final String PATH_SHIPMENTS = "shipments";

    public static final Uri CONTENTURI_SHIPMENTS = Uri.parse("content://" + AUTHORITY + "/" + PATH_SHIPMENTS );
    public static final Uri CONTENTURI_SHIPMENTITEMS = Uri.parse("content://" + AUTHORITY + "/" + PATH_SHIPMENTITEMS );


    private static final int SHIPMENTS = 1;
    private static final int SHIPMENT_ID = 2;
    private static final int SHIPMENTITEMS = 3;
    private static final int SHIPMENTITEMS_ID = 4;

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

    }

    private SQLiteDatabase database;

    // ((MainApplication)getApplication()).getDatabaseHelper();
    @Override
    public boolean onCreate() {
         helper = new ProductsDbHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                cursor = helper.getShipments(null); /* database.query(ProductsContract.ShipmentsEntry.TABLE_NAME,new String[]{"_id","client","numberin1s"},
                        s,null,null,null,null); */
                break;
            case SHIPMENTITEMS:
                cursor=helper.getShipmentItems("7891");
                break;
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
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        Uri contentUri;
        long id = database.insert(ProductsContract.ShipmentsEntry.TABLE_NAME,null,contentValues);
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                contentUri = CONTENTURI_SHIPMENTS;
                break;
            case SHIPMENTITEMS:
                contentUri = CONTENTURI_SHIPMENTITEMS;
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
            case SHIPMENTITEMS:
                delCount =  database.delete(ProductsContract.ShipmentsItemEntry.TABLE_NAME,s,strings);
                break;

            case SHIPMENTITEMS_ID:
            {
                String id = uri.getPathSegments().get(1);
                database.delete(ProductsContract.ShipmentsItemEntry.TABLE_NAME,"_id=" + id,strings);
                break;
            }
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return delCount;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int updCount = 0;
        switch (uriMatcher.match(uri)) {
            case SHIPMENTS:
                updCount =  database.update(ProductsContract.ShipmentsEntry.TABLE_NAME,contentValues,s,strings);
                break;
            case SHIPMENT_ID :
            {String id = uri.getPathSegments().get(1);
                database.update(ProductsContract.ShipmentsEntry.TABLE_NAME,contentValues,"_id=" + id,strings);
                break;
            }
            default:
                throw new IllegalArgumentException("This is an Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return updCount;
    }
}
