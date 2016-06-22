package com.yst.sklad.tsd.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 02.06.2016.
 */
public class ProductsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 14;

    static final String DATABASE_NAME = "products.db";

    public static final String COLUMN_ID = "_id";


    private static final String SQL_CREATE_PRODUCTS_TABLE =
            "create table " + ProductsContract.ProductsEntry.TABLE_NAME + "(" +
                    ProductsContract.ProductsEntry._ID + " integer primary key , " +
                    ProductsContract.ProductsEntry.COLUMN_NAME + " text, " +
                    ProductsContract.ProductsEntry.COLUMN_BARCODE + " text, " +
                    ProductsContract.ProductsEntry.COLUMN_ARTICLE + " text, " +
                   ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE + " integer, " +
                    ProductsContract.ProductsEntry.COLUMN_COMMENTS +" text "+
                    ");";

// _id = guid задания на отгрузку
    private static final String SQL_CREATE_SHIPMENTS_TABLE =
            "create table " + ProductsContract.ShipmentsEntry.TABLE_NAME + "(" +
                    ProductsContract.ShipmentsEntry._ID + " text primary key , " +
                    ProductsContract.ShipmentsEntry.COLUMN_DATE + " text, " +
                    ProductsContract.ShipmentsEntry.COLUMN_CLIENT + " text, " +
                    ProductsContract.ShipmentsEntry.COLUMN_NUMBER + " text, " +
                    ProductsContract.ShipmentsEntry.COLUMN_COMMENTS +" text "+
                    ");";


    // COLUMN_SHIPMENTID = guid задания на отгрузку

    private static final String SQL_CREATE_SHIPMENTITEMS_TABLE =
            "create table " + ProductsContract.ShipmentsItemEntry.TABLE_NAME + "(" +
                    ProductsContract.ShipmentsItemEntry._ID + " integer primary key autoincrement, " +
                    ProductsContract.ShipmentsItemEntry.COLUMN_SHIPMENTID + " text, " +
                    ProductsContract.ShipmentsItemEntry.COLUMN_PRODUCTID + " integer, " +
                    ProductsContract.ShipmentsItemEntry.COLUMN_ROWNUMBER +" integer, "+
                    ProductsContract.ShipmentsItemEntry.COLUMN_STOCKCELL +" text, "+
                    ProductsContract.ShipmentsItemEntry.COLUMN_COUNT +" integer, "+
                    ProductsContract.ShipmentsItemEntry.COLUMN_STOCKCELL_FACT +" text, "+
                    ProductsContract.ShipmentsItemEntry.COLUMN_COUNT_FACT +" integer, "+
                    ProductsContract.ShipmentsItemEntry.COLUMN_REST +" integer, "+
                    // специально удаляем внешний ключ по ProductId т.к. товары постоянно добавляются
                  // "FOREIGN KEY( "+ ProductsContract.ShipmentsItemEntry.COLUMN_PRODUCTID+") REFERENCES "+ ProductsContract.ProductsEntry.TABLE_NAME+" ("+COLUMN_ID+") ," +
                    "FOREIGN KEY( "+ ProductsContract.ShipmentsItemEntry.COLUMN_SHIPMENTID+") REFERENCES "+ProductsContract.ShipmentsEntry.TABLE_NAME +" ("+COLUMN_ID+")" + ");";

// _ID - текстовое название склада
    private static final String SQL_CREATE_STORAGE_TABLE =
            "create table " + ProductsContract.StorageEntry.TABLE_NAME + "(" +
                    ProductsContract.StorageEntry._ID + " text primary key  );";

    // _ID - штрихкод ячейки
    private static final String SQL_CREATE_STOCKCELLS_TABLE =
            "create table " + ProductsContract.StockCellEntry.TABLE_NAME + "(" +
                    ProductsContract.StockCellEntry._ID + " integer primary key autoincrement, " +
                    ProductsContract.StockCellEntry.COLUMN_NAME + " text, " +
                    ProductsContract.StockCellEntry.COLUMN_STORAGEID+ " text, " +
                    "FOREIGN KEY( "+ ProductsContract.StockCellEntry.COLUMN_STORAGEID+") REFERENCES "+ProductsContract.StorageEntry.TABLE_NAME +" ("+COLUMN_ID+")" + ");";


    public ProductsDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public ProductsDbHelper(Context context, int version)
    {
        super(context, DATABASE_NAME, null, version);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        try {

            db.execSQL(SQL_CREATE_PRODUCTS_TABLE);
            db.execSQL(SQL_CREATE_SHIPMENTS_TABLE);
            db.execSQL(SQL_CREATE_SHIPMENTITEMS_TABLE);
            db.execSQL(SQL_CREATE_STORAGE_TABLE);
            db.execSQL(SQL_CREATE_STOCKCELLS_TABLE);

        //    initializeData(db);
        } catch (Exception e)
        {
            Log.e("db helper error",e.getMessage());
            e.printStackTrace();
        }
    }



    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public void dropAllTables(SQLiteDatabase db)
    {
        final String SQL_DROPTABLE_IFEXISTS ="drop table if exists ";
        db.execSQL(SQL_DROPTABLE_IFEXISTS+ProductsContract.ProductsEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS+ProductsContract.ShipmentsEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS+ProductsContract.ShipmentsItemEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS+ProductsContract.StorageEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS + ProductsContract.StockCellEntry.TABLE_NAME);

    }

    public void clearTable(String tableName)
    {
        SQLiteDatabase db =  this.getWritableDatabase();
        db.execSQL("delete from "+tableName);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DROPTABLE_IFEXISTS ="drop table if exists ";
        if (newVersion>4) {
            dropAllTables(db);

           onCreate(db);
        }

    }

    public boolean doesTableExist( String tableName) {
        SQLiteDatabase db =  this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '" + tableName + "'", null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }


    public boolean checkIfShipmentExists ( String shipmentId)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsEntry.TABLE_NAME+ " where _id='"+shipmentId+"'", null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return true;
        }
        return false;

    }

    public boolean checkIfShipmentItemsExistByShipmentAndProduct ( String shipmentId,int productid)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid='"+shipmentId+"' and productid="+productid, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return true;
        }
        return false;

    }



    public boolean addShipment ( Shipment shipment)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ProductsContract.ShipmentsEntry._ID, shipment.Id);
            cv.put(ProductsContract.ShipmentsEntry.COLUMN_DATE, shipment.DateOfShipment);
            cv.put(ProductsContract.ShipmentsEntry.COLUMN_CLIENT, shipment.Client);
            //cv.put(ProductsContract.ShipmentsEntry.COLUMN_COMMENTS, shipment.comments);
            db.insert(ProductsContract.ShipmentsEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }

    public boolean addShipmentItem ( ShipmentItem shipmentItem)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_SHIPMENTID, shipmentItem.ShipmentId);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_PRODUCTID, shipmentItem.ProductId);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_STOCKCELL, shipmentItem.StockCell);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_COUNT, shipmentItem.Quantity);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_ROWNUMBER, shipmentItem.RowNumber);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_REST, shipmentItem.Rest);

            db.insert(ProductsContract.ShipmentsItemEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {
            return false;
        }

        return true;

    }

    public boolean addProduct ( int id, String name,String barcode,String comments,int productType,String article )
    {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ProductsContract.ProductsEntry._ID, id);
            cv.put(ProductsContract.ProductsEntry.COLUMN_NAME, name);

            cv.put(ProductsContract.ProductsEntry.COLUMN_BARCODE, barcode);
            cv.put(ProductsContract.ProductsEntry.COLUMN_COMMENTS, comments);
            cv.put(ProductsContract.ProductsEntry.COLUMN_PRODUCTTYPE, productType);
            cv.put(ProductsContract.ProductsEntry.COLUMN_ARTICLE, article);

            db.insert(ProductsContract.ProductsEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }

    public boolean addStorage ( String id)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ProductsContract.StorageEntry._ID, id);

            db.insert(ProductsContract.StorageEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;
    }

    // id = barcode
    public boolean addStockCell (String id , String name, String storageid )
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();
            cv.put(ProductsContract.StockCellEntry._ID, id);
            cv.put(ProductsContract.StockCellEntry.COLUMN_NAME, name);
            //cv.put(ProductsContract.ProductsEntry.COLUMN_GUID, guid);
            cv.put(ProductsContract.StockCellEntry.COLUMN_STORAGEID, storageid);

            db.insert(ProductsContract.StockCellEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }


    public boolean updateShipmentItem(int cellid , String cell, int quantity )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("Update "+ ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " set stockcellfact='"+cell+"', quantityfact="+quantity +" where _id="+cellid );
return true;
    }



    public int numberOfRows(String tablename){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db,tablename);
        return numRows;
    }

    public int getLatestProductId()
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select max(_id) id from products", null );
        if (res!=null && res.getCount()>0) {   res.moveToFirst();     return res.getInt(0); }
        return 0;
    }


    public boolean checkIfProductExists(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ProductsEntry.TABLE_NAME+ " where _id="+id, null );
        if (res!=null && res.getCount()>0) return true;
        else return false;

    }

    public Product getProductById(int id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ProductsEntry.TABLE_NAME+ " where _id="+id, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return Product.fromCursor(res);
         }else return null;

    }

    public Product getProductByBarCode(String barcode){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ProductsEntry.TABLE_NAME+ " where barcode="+barcode, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return Product.fromCursor(res);
        }else return null;

    }
/*
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(ProductsContract.ProductsEntry.TABLE_NAME, null, null, null, null, null, null);
    }
*/
    public Cursor getProducts(String filter) {

        SQLiteDatabase db = this.getReadableDatabase();
        if (filter != null)

        //    return db.rawQuery("select * from " + ProductsContract.ProductsEntry.TABLE_NAME + " where name like '%" + filter + "%'" +" or _id like '%"+ filter + "%'" , null);

          return  db.query(ProductsContract.ProductsEntry.TABLE_NAME, null, "name like ? or _id like ?", new String[] { filter+"%",filter+"%"}, null, null, "_id desc");
        return db.query(ProductsContract.ProductsEntry.TABLE_NAME, null, null, null, null, null, "_id desc");
    }

    public Cursor getStockCells(String filter) {


        SQLiteDatabase db = this.getReadableDatabase();
        if (filter!=null)
         return db.query(ProductsContract.StockCellEntry.TABLE_NAME, null,"name like ? or _id like ? or storageid like ?", new String[] { "%"+filter+"%","%"+filter+"%","%"+filter+"%"}, null, null, null);
        return db.query(ProductsContract.StockCellEntry.TABLE_NAME, null, null, null, null, null, null);
        //Cursor res =  db.rawQuery( "select _id, name, storageid from " + ProductsContract.StockCellEntry.TABLE_NAME+ " inner join "+ProductsContract.StorageEntry.TABLE_NAME +" on stockcells.storageid= storages._id", null );

}


    public String getStorageOfCell(String cell) {


        SQLiteDatabase db = this.getReadableDatabase();

         Cursor cursor= db.rawQuery("SELECT storageid from " + ProductsContract.StockCellEntry.TABLE_NAME + " where _id=" + cell, null);
        if (cursor!=null && cursor.getCount()>0)
        {   cursor.moveToFirst();
            return cursor.getString(0);

        }
    return "";
    }

    public String getNameOfCell(String cell) {


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= db.rawQuery("SELECT name from " + ProductsContract.StockCellEntry.TABLE_NAME + " where _id=" + cell,null);
        if (cursor!=null && cursor.getCount()>0)
        {   cursor.moveToFirst();
            return cursor.getString(0);

        }
        return "";
    }

    public Cursor getShipments(String filter) {

        Cursor cursor =  null;
        SQLiteDatabase db = this.getReadableDatabase();
        if (filter!=null)
            cursor= db.query(ProductsContract.ShipmentsEntry.TABLE_NAME, null,"_id like ? or dateofshipment like ? or client like ?", new String[] { "%"+filter+"%","%"+filter+"%","%"+filter+"%"}, null, null, null);
        //    cursor= db.query(ProductsContract.ShipmentsEntry.TABLE_NAME, null, null, null, null, null, null);

        String sql_select = "select _id,SUBSTR(dateofshipment,6,5) dateofshipment, client from "+ProductsContract.ShipmentsEntry.TABLE_NAME + " order by dateofshipment";
        cursor =  db.rawQuery( sql_select, null );

        return  cursor;
        //Cursor res =  db.rawQuery( "select _id, name, storageid from " + ProductsContract.StockCellEntry.TABLE_NAME+ " inner join "+ProductsContract.StorageEntry.TABLE_NAME +" on stockcells.storageid= storages._id", null );

    }

    public Cursor getShipmentItems(String shipmentId) {


        SQLiteDatabase db = this.getReadableDatabase();

        String sql_select ="select shipmentitems._id, shipmentitems.rownumber, shipmentitems.productid,IFNULL(shipmentitems.stockcellfact,shipmentitems.stockcell) stockcell, shipmentitems.quantityfact, IFNULL(products.name,'---') as productname, IFNULL(stockcells.storageid,'---') storageid from shipmentitems  left outer join products on shipmentitems.productid=products._id" +
                "  left join stockcells on shipmentitems.stockcell=stockcells._id  where shipmentitems.shipmentid="+shipmentId +" order by rownumber";
       //     Cursor cur = db.query(ProductsContract.ShipmentsItemEntry.TABLE_NAME, null,"shipmentid = ?", new String[] { shipmentId}, null, null, null);

        Cursor res =  db.rawQuery( sql_select, null );

        return res;

    }

    public ShipmentItem getShipmentItemById(long id){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where _id="+id, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return ShipmentItem.fromCursor(res);
        }else throw new NullPointerException("No shipment item found with id="+id);

    }

    public Cursor getShipmentItemsByShipmentId(String  shipmentid){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid="+shipmentid, null );
        return res;

    }

    public ShipmentItem getNextShipmentItem(String shipmentId, int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where _id>"+id+ " and shipmentid='"+shipmentId+"' order by _id", null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return ShipmentItem.fromCursor(res);
        }
         else
          return null;


    }


    public int getRestOfProductInShipmentItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select  rest from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where _id="+id, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return res.getInt(0);
        }
        else
            return 0;
    }
}