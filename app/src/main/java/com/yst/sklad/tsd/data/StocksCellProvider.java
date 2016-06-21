package com.yst.sklad.tsd.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by user on 07.06.2016.
 */
public class StocksCellProvider extends ContentProvider{

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    private ProductsDbHelper mdbHelper;
    @Override
    public boolean onCreate() {

        mdbHelper = new ProductsDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
       SQLiteDatabase db =mdbHelper.getReadableDatabase();
        return db.query(ProductsContract.StockCellEntry.TABLE_NAME,null,null,null,null,null,null);

    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
