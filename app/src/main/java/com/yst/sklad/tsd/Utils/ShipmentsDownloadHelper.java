package com.yst.sklad.tsd.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.data.Shipment;
import com.yst.sklad.tsd.data.ShipmentItem;
import com.yst.sklad.tsd.services.SoapCallToWebService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lapenkov on 29.11.2017.
 */

public final  class ShipmentsDownloadHelper {

    private static final String TAG = "ShipmentsDownloadHelper";

    public static void createDocuments(Context context)
    {

    ProductsDbHelper   dbHelper = ((MainApplication)context.getApplicationContext()).getDatabaseHelper();

        InputStream stream = SoapCallToWebService.receiveCurrentShipments();

        XMLDOMParser parser = new XMLDOMParser();

        List<Shipment> listOfShipments = new ArrayList<Shipment>();
        List<ShipmentItem> listOfShipmentItems = new ArrayList<ShipmentItem>();

        Document doc = parser.getDocument(stream);

        NodeList nodeListOrders = doc.getElementsByTagName("Orders");


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean  onlySelectedStorages = preferences.getBoolean("onlySelectedStorages", false);
        Set<String> arrayOfStorages = preferences.getStringSet("storagesArray", new HashSet<String>());


        for(int j=0; j< nodeListOrders.getLength();j++) {
            Element e = (Element) nodeListOrders.item(j);

            String numberin1s = parser.getValue(e, "numberin1s");
            String dateofshipment = parser.getValue(e, "dateofshipment");
            String client = parser.getValue(e, "client");
            String comment = parser.getValue(e, "comment");

            String cleanId = Shipment.getCleanId(numberin1s);
            Shipment shipmentToAdd =new Shipment(cleanId,dateofshipment,client,comment);
            //--- {Lapenkov 27.07.2016 Не загружаем задания, которые есть в базе данных
            if (dbHelper.checkIfShipmentExists(cleanId)) continue;

            Boolean hasItems= false;
            NodeList nodeListProducts =e.getElementsByTagName("Products");

            for(int k=0; k< nodeListProducts.getLength();k++) {
                {
                    Element p = (Element) nodeListProducts.item(k);
                    Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
                    Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                    String stockcell = parser.getValue(p, "stockcell");
                    String queue = parser.getValue(p, "queue");
                    if(onlySelectedStorages)
                    {   String storage=dbHelper.getStorageOfCell(stockcell);
                        //Log.d(TAG+"storage","|"+storage+"|");
                        if (storage!=null ) {
                            if (!arrayOfStorages.contains(storage)) continue;

                        }else {
                            continue;
                        }

                    }
                    Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));
                    Integer rest = Integer.parseInt(parser.getValue(p, "rest"));
                    ShipmentItem shipmentItemToAdd =new ShipmentItem(cleanId,rownumber,productid,stockcell,quantity,rest,queue);
                    listOfShipmentItems.add(shipmentItemToAdd);
                    hasItems=true;

                }}
            if (hasItems)
            {listOfShipments.add(shipmentToAdd);
                Log.d(TAG+ "/shipment added", shipmentToAdd.toString());
                Log.d(TAG +"/shipmentitems added", listOfShipmentItems.toString());
            }

        }

        Log.d(TAG+"/shipmentsitems size", String.valueOf(listOfShipmentItems.size()));
        Log.d(TAG + "/shipments size", String.valueOf(listOfShipments.size()));

     boolean   mNewShipmentsWasAdded=false;
        for (Shipment shipment : listOfShipments)
        {
            if (!dbHelper.checkIfShipmentExists(shipment.Id))
            {    dbHelper.addShipment(shipment);
                mNewShipmentsWasAdded=true; }

        }

        for (ShipmentItem shipmentItem : listOfShipmentItems)
        {
            if (dbHelper.checkIfShipmentExists(shipmentItem.ShipmentId)
                //+++ 12.07.2016        && !dbHelper.checkIfShipmentItemsExistByShipmentAndProduct(shipmentItem.ShipmentId,shipmentItem.ProductId)
                //                       && !dbHelper.checkIfShipmentItemsExistByShipmentAndProductAndRow(shipmentItem.ShipmentId,shipmentItem.ProductId,shipmentItem.RowNumber)
                // added Lapenkov 27.07.2016 Не загружаем задания которые есть
                //&& !dbHelper.checkIfShipmentItemsExistByShipmentAndRow(shipmentItem.ShipmentId,shipmentItem.RowNumber)
                    )

                dbHelper.addShipmentItem(shipmentItem);

        }
    }
}
