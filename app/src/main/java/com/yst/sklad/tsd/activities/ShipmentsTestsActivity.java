package com.yst.sklad.tsd.activities;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.AppDataProvider;
import android.support.v4.content.CursorLoader;
import android.widget.CursorAdapter;
import android.widget.ListView;

import java.util.Random;

/*
Тестовая активность для примера реализации CRUD  c контентпровайдером
 */
public class ShipmentsTestsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter mAdapter;

    public static final Uri CONTENT_URI = AppDataProvider.CONTENTURI_SHIPMENTS;
    ListView lvData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipments_tests);

        mAdapter = new SimpleCursorAdapter(this,
                R.layout.orderitem_item,  null,
                new String[] { "_id","client","quantity"},
     //           new String[] { "_id","stockcell","quantityfact"},
                new int[] { R.id.text1, R.id.text2,R.id.text3 }, 0);

        lvData = (ListView) findViewById(R.id.lvData);
        lvData.setAdapter(mAdapter);
        getSupportLoaderManager().initLoader(0, null, this);


    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);


    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public void Insert(View v) {
        ContentValues cv = new ContentValues();
        Random ran = new Random();
        int id = ran.nextInt(100) ;

        cv.put("_id", id +"");
        cv.put("client", "Vasya");
        cv.put("numberin1s", "Vasya");

        Uri newUri = getContentResolver().insert(CONTENT_URI, cv);

    }

    public void Delete(View v) {
       /* Uri uri = ContentUris.withAppendedId(CONTENT_URI, 123);
        int cnt = getContentResolver().delete(uri, null, null); */

        Uri uri = CONTENT_URI;
        int cnt = getContentResolver().delete(uri, null, null);



    }
/*
    public void onClickUpdate(View v) {
        ContentValues cv = new ContentValues();
        cv.put(CONTACT_NAME, "name 5");
        cv.put(CONTACT_EMAIL, "email 5");
        Uri uri = ContentUris.withAppendedId(CONTACT_URI, 2);
        int cnt = getContentResolver().update(uri, cv, null, null);

    }

    public void onClickDelete(View v) {
        Uri uri = ContentUris.withAppendedId(CONTACT_URI, 3);
        int cnt = getContentResolver().delete(uri, null, null);

    }

    public void onClickError(View v) {
        Uri uri = Uri.parse("content://ru.startandroid.providers.AdressBook/phones");
        try {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        } catch (Exception ex) {
            Log.d(LOG_TAG, "Error: " + ex.getClass() + ", " + ex.getMessage());
        }

    }*/

}
