package com.yst.sklad.tsd.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Debug;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 02.06.2016.
 * Updated on 22.11.2016
 */
public class ProductsDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 17;

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


    private static final String SQL_CREATE_PRODUCT_BARCODES =
            "create table " + ProductsContract.ProductBarcodesEntry.TABLE_NAME + "(" +
                    ProductsContract.ProductBarcodesEntry._ID + " integer primary key autoincrement , " +
                    ProductsContract.ProductBarcodesEntry.COLUMN_PRODUCTID + " integer, " +
                    ProductsContract.ProductBarcodesEntry.COLUMN_BARCODE + " text "+
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
                    ProductsContract.ShipmentsItemEntry.COLUMN_QUEUE +" text, "+
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


    //
    // заказы поставщикам
    //
    private static final String SQL_CREATE_ORDERTOSUPPLIER_TABLE =
            "create table " + ProductsContract.OrdersToSupplierEntry.TABLE_NAME + "(" +
                    ProductsContract.OrdersToSupplierEntry._ID + " text primary key, " +
                    ProductsContract.OrdersToSupplierEntry.COLUMN_DATE + " text, " +
                    ProductsContract.OrdersToSupplierEntry.COLUMN_ORDERTYPE + " integer, " + // вид документа
                    ProductsContract.OrdersToSupplierEntry.COLUMN_CLIENT+ " text, " +
                    ProductsContract.OrdersToSupplierEntry.COLUMN_ARRIVALNUMBER + " text, " +
                    ProductsContract.OrdersToSupplierEntry.COLUMN_COMMENTS+ " text );";




    //
    // заказы поставщикам таболичная часть
    //
    private static final String SQL_CREATE_ORDERTOSUPPLIER_ITEMS_TABLE =
            "create table " + ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME + "(" +
                    ProductsContract.OrdersToSupplierItemEntry._ID + " integer primary key autoincrement, " +
                    ProductsContract.OrdersToSupplierItemEntry.COLUMN_ORDERTOSUPPLIERID + " text, " +
                    ProductsContract.OrdersToSupplierItemEntry.COLUMN_ROWNUMBER +" integer, "+
                    ProductsContract.OrdersToSupplierItemEntry.COLUMN_PRODUCTID+ "  integer,  " +
                    ProductsContract.OrdersToSupplierItemEntry.COLUMN_COUNT+ "  integer );";



    //
    //  приход относящийся к заказы поставщикам таболичная часть с разбивкой по ячейкам
    //
    private static final String SQL_CREATE_ARRIVAL_ITEMS_TABLE =
            "create table " + ProductsContract.ArrivalItemsEntry.TABLE_NAME + "(" +
                    ProductsContract.ArrivalItemsEntry._ID + " integer primary key autoincrement, " +
                    ProductsContract.ArrivalItemsEntry.COLUMN_ORDERTOSUPPLIERID + " text, " +
                    ProductsContract.ArrivalItemsEntry.COLUMN_PRODUCTID+ "  integer,  " +
                    ProductsContract.ArrivalItemsEntry.COLUMN_COUNT_FACT+ "  integer, " +
                    ProductsContract.ArrivalItemsEntry.COLUMN_STOCKCELL_FACT+ "  text " +
                    ");";




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

            // --- таблицы для поступлений
            db.execSQL(SQL_CREATE_ORDERTOSUPPLIER_TABLE);
            db.execSQL(SQL_CREATE_ORDERTOSUPPLIER_ITEMS_TABLE);
            db.execSQL(SQL_CREATE_ARRIVAL_ITEMS_TABLE);
            db.execSQL(SQL_CREATE_PRODUCT_BARCODES);

        //    initializeData(db);
        } catch (Exception e)
        {
            Log.e("db helper error",e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DROPTABLE_IFEXISTS ="drop table if exists ";
        if (newVersion>4) {
            dropAllTables(db);

            onCreate(db);
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
        // --- таблицы для поступлений
        db.execSQL(SQL_DROPTABLE_IFEXISTS + ProductsContract.ArrivalItemsEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS + ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS + ProductsContract.OrdersToSupplierEntry.TABLE_NAME);
        db.execSQL(SQL_DROPTABLE_IFEXISTS + ProductsContract.ProductBarcodesEntry.TABLE_NAME);


    }

    public void clearTable(String tableName)
    {
        SQLiteDatabase db =  this.getWritableDatabase();
        db.execSQL("delete from "+tableName);

    }

    public void deleteShipment(String shipmentid)
    {
        SQLiteDatabase db =  this.getWritableDatabase();

        db.execSQL("delete from "+ ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid="+shipmentid);
        db.execSQL("delete from "+ ProductsContract.ShipmentsEntry.TABLE_NAME+ " where _id="+shipmentid);

    }

    /*
    Удаляет информацию из заказа со всеми связанными таблицами (строки и приходы)
     */
    public void deleteOrder(String orderid)
    {
        SQLiteDatabase db =  this.getWritableDatabase();

        db.execSQL("delete from "+ ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME+ " where ordertosupplierid="+orderid);
        db.execSQL("delete from "+ ProductsContract.OrdersToSupplierEntry.TABLE_NAME+ " where _id="+orderid);
        db.execSQL("delete from "+ ProductsContract.ArrivalItemsEntry.TABLE_NAME+ " where ordertosupplierid="+orderid);

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


//+++ 12.07.2016 - проверка старая только по заданию и товару - не пропускает дубли строк с одним товаром!
    // public boolean checkIfShipmentItemsExistByShipmentAndProduct ( String shipmentId, int productid)
    // {
    //  SQLiteDatabase db = this.getReadableDatabase();
//         Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid='"+shipmentId+"' and productid="+productid, null );
//         if (res!=null && res.getCount()>0)
//         {   res.moveToFirst();     return true;
//         }
//         return false;
//     }


//+++ 12.07.2016 по Заданию, товару и номеру строки

    /*
    public boolean checkIfShipmentItemsExistByShipmentAndProductAndRow( String shipmentId, int productid, int rownumber)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid='"+shipmentId+ "' and productid="+productid
                +" and rownumber="+rownumber, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return true;
        }
        return false;

    }

    public boolean checkIfShipmentItemsExistByShipmentAndRow( String shipmentId, int rownumber)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery( "select * from " + ProductsContract.ShipmentsItemEntry.TABLE_NAME+ " where shipmentid='"+shipmentId+"' and rownumber="+rownumber, null );
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return true;
        }
        return false;

    }
*/


    /*
    Добавляет заказ поставщика в базу
     */
    public boolean addOrderToSupplier ( OrderToSupplier order)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ProductsContract.OrdersToSupplierEntry._ID, order.Id);
            cv.put(ProductsContract.OrdersToSupplierEntry.COLUMN_DATE, order.DateOfOrder);
            cv.put(ProductsContract.OrdersToSupplierEntry.COLUMN_ORDERTYPE, order.OrderType);
            cv.put(ProductsContract.OrdersToSupplierEntry.COLUMN_COMMENTS, order.Comments);
            cv.put(ProductsContract.OrdersToSupplierEntry.COLUMN_CLIENT, order.Client);
            //cv.put(ProductsContract.ShipmentsEntry.COLUMN_COMMENTS, shipment.comments);
            db.insert(ProductsContract.OrdersToSupplierEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }

    /*
    Добавляет дополнительные штрихкоды товара  в базу
     */
    public boolean addProductBarcode ( int productId, String barcode)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ProductsContract.ProductBarcodesEntry.COLUMN_PRODUCTID, productId);
            cv.put(ProductsContract.ProductBarcodesEntry.COLUMN_BARCODE, barcode);
            db.insert(ProductsContract.ProductBarcodesEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }




    /*
    Добавляет строку заказа поставщику в базу
     */
    public boolean addOrderToSupplierItem ( OrderToSupplierItem orderItem)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ProductsContract.OrdersToSupplierItemEntry.COLUMN_ORDERTOSUPPLIERID, orderItem.OrderId);
            cv.put(ProductsContract.OrdersToSupplierItemEntry.COLUMN_ROWNUMBER, orderItem.RowNumber);
            cv.put(ProductsContract.OrdersToSupplierItemEntry.COLUMN_PRODUCTID, orderItem.ProductId);
            cv.put(ProductsContract.OrdersToSupplierItemEntry.COLUMN_COUNT, orderItem.Quantity);
            db.insert(ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

    }


    /*
    Добавляет строку поступления в базу
     */

    public boolean addArrivalItem ( ArrivalItem item)
    {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues cv = new ContentValues();

            cv.put(ProductsContract.ArrivalItemsEntry.COLUMN_ORDERTOSUPPLIERID, item.OrderId);
            cv.put(ProductsContract.ArrivalItemsEntry.COLUMN_PRODUCTID, item.ProductId);
            cv.put(ProductsContract.ArrivalItemsEntry.COLUMN_COUNT_FACT, item.Quantity);
            cv.put(ProductsContract.ArrivalItemsEntry.COLUMN_STOCKCELL_FACT, item.StockCell);

            db.insert(ProductsContract.ArrivalItemsEntry.TABLE_NAME, null, cv);
        }
        catch (Exception e) {return false;}

        return true;

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
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_COUNT_FACT, 0);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_ROWNUMBER, shipmentItem.RowNumber);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_REST, shipmentItem.Rest);
            cv.put(ProductsContract.ShipmentsItemEntry.COLUMN_QUEUE, shipmentItem.Queue);

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

        Cursor res =  db.rawQuery("select * from " + ProductsContract.ProductsEntry.TABLE_NAME + " where _id=" + id, null);
        if (res!=null && res.getCount()>0)
        {   res.moveToFirst();     return Product.fromCursor(res);
         }else return null;

    }

    public Product getProductByBarCode(String barcode){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res =  db.rawQuery("select * from " + ProductsContract.ProductsEntry.TABLE_NAME + " where barcode=" + barcode, null);
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

/*
* Возвращает все id товаров в базе
* */
    public List<Integer> getProductIds() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor=  db.query(ProductsContract.ProductsEntry.TABLE_NAME, new String[]{"_id"}, null, null, null, null, "_id desc");

        List<Integer> list= new ArrayList<>();
        if (cursor!=null && cursor.getCount()>0) {
            cursor.moveToFirst();

            list.add(cursor.getInt(0));

            while ( cursor.moveToNext())
            {

                list.add(cursor.getInt(0));

            }

        }
        return list;

    }

    public Cursor getOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String  query= "select _id,  SUBSTR(dateoforder,6,5) dateoforder,ordertype,client from "+ProductsContract.OrdersToSupplierEntry.TABLE_NAME;
        return db.rawQuery(query,null);
        //return db.query(ProductsContract.OrdersToSupplierEntry.TABLE_NAME, null, null, null, null, null, null);
    }


    /*
    Получить таблицы товаров и
     */
    public boolean orderHasProductId(String orderId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String whereCond= ProductsContract.OrdersToSupplierItemEntry.COLUMN_ORDERTOSUPPLIERID +"="+orderId +
                " and "+ProductsContract.OrdersToSupplierItemEntry.COLUMN_PRODUCTID +"="+productId;
        Cursor cursor= db.query(ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME, null, whereCond, null, null, null, null);

        if (cursor!=null && cursor.getCount()>0) return true;
        return false;

    }

    /*
    * Получить последний номер строки по коду
    */
    public int getLastRowNumberOfOrder (String orderId)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT max(rownumber) rownumber from " + ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME + " where ordertosupplierid=" + orderId, null);

        if (cursor!=null && cursor.getCount()>0)
        { cursor.moveToFirst();
        return cursor.getInt(0); }
        return 0;

    }


    /*
    Получить строки заказов
     */
    public Cursor getOrderItems(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();

String        query = "SELECT orderstosuppliersitems._id, rownumber, orderstosuppliersitems.productid productid, quantity, aitems.quantityfact ,IFNULL(products.name,'---') productname  from orderstosuppliersitems " +
                "LEFT JOIN products ON orderstosuppliersitems.productid=products._id " +
                "LEFT JOIN (SELECT productid, SUM( quantityfact) quantityfact from arrivalitems " +
                 "WHERE ordertosupplierid="+ orderId +                 " GROUP BY ProductId) aitems "+
                "ON orderstosuppliersitems.productid=aitems.productid "+
                "WHERE orderstosuppliersitems.ordertosupplierid="+orderId;

        Cursor cursor= db.rawQuery(query, null);
        if (cursor!=null && cursor.getCount()>0)
        {
            return cursor;

        }
        return null;

    }




/*
Получить список ячеек для данного заказа и товара
 */
    public Cursor getArrivalItems(String orderId, int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String whereCond= ProductsContract.ArrivalItemsEntry.COLUMN_ORDERTOSUPPLIERID +"="+orderId +" and "+ProductsContract.ArrivalItemsEntry.COLUMN_PRODUCTID+"="+Integer.toString(productId);
        return db.query(ProductsContract.ArrivalItemsEntry.TABLE_NAME, null, whereCond, null, null, null, null);

    }


    /*
Получить список ячеек для данного заказа
 */
    public Cursor getArrivalItemsByOrderId(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String whereCond= ProductsContract.ArrivalItemsEntry.COLUMN_ORDERTOSUPPLIERID +"="+orderId ;
        return db.query(ProductsContract.ArrivalItemsEntry.TABLE_NAME, null, whereCond, null, null, null, null);

    }

    public int getOrderTypeByOrderId (long orderId)

    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= db.rawQuery("select ordertype from orderstosuppliers where _id="+orderId,null);
        if (cursor!=null && cursor.getCount()>0)
        {   cursor.moveToFirst();
            return cursor.getInt(0);

        }
        return -1;

    }


    /*
    Получить код товара по Id, -1 если не найден
     */
    public int getProductIdInOrderItemById (long id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor= db.rawQuery("SELECT productid from " + ProductsContract.OrdersToSupplierItemEntry.TABLE_NAME + " where _id=" + Long.toString(id), null);
        if (cursor!=null && cursor.getCount()>0)
        {   cursor.moveToFirst();
            return cursor.getInt(0);

        }
        return -1;
    }




    public Cursor getStockCells(String filter) {


        SQLiteDatabase db = this.getReadableDatabase();
        if (filter!=null)
         return db.query(ProductsContract.StockCellEntry.TABLE_NAME, null,"name like ? or _id like ? or storageid like ? COLLATE NOCASE", new String[] { "%"+filter+"%","%"+filter+"%","%"+filter+"%"}, null, null, null);
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

       // String sql_select = "select _id,SUBSTR(dateofshipment,6,5) dateofshipment, client from "+ProductsContract.ShipmentsEntry.TABLE_NAME + " order by dateofshipment";
         String sql_select = " SELECT s.* , IFNULL(si.quantityfact,0) as quantityfact, IFNULL(si.quantity,0) as quantity,IFNULL(si.rows_count,0) as rows_count  from (select _id,SUBSTR(dateofshipment,6,5) dateofshipment, client from "+ProductsContract.ShipmentsEntry.TABLE_NAME + ") s left join"+
        " (select shipmentId, sum(quantityfact) quantityfact,sum(quantity) quantity,count(*) rows_count from "+ProductsContract.ShipmentsItemEntry.TABLE_NAME +" group by shipmentId ) si on  s._id=si.shipmentId ";

         cursor =  db.rawQuery( sql_select, null );

        return  cursor;
        //Cursor res =  db.rawQuery( "select _id, name, storageid from " + ProductsContract.StockCellEntry.TABLE_NAME+ " inner join "+ProductsContract.StorageEntry.TABLE_NAME +" on stockcells.storageid= storages._id", null );

    }

    public Cursor getShipmentItems(String shipmentId) {


        SQLiteDatabase db = this.getReadableDatabase();

        String sql_select ="select shipmentitems._id, shipmentitems.rownumber, shipmentitems.productid,IFNULL(shipmentitems.stockcellfact,shipmentitems.stockcell) stockcell, shipmentitems.quantityfact, shipmentitems.queue queue, IFNULL(products.name,'---') as productname, IFNULL(stockcells.storageid,'---') storageid from shipmentitems  left outer join products on shipmentitems.productid=products._id" +
                "  left join stockcells on shipmentitems.stockcell=stockcells._id  where shipmentitems.shipmentid="+shipmentId +" order by shipmentitems._id";
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