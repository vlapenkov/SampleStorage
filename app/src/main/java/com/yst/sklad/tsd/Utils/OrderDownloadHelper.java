package com.yst.sklad.tsd.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yst.sklad.tsd.MainApplication;
import com.yst.sklad.tsd.R;
import com.yst.sklad.tsd.data.ArrivalItem;
import com.yst.sklad.tsd.data.OrderToSupplier;
import com.yst.sklad.tsd.data.OrderToSupplierItem;
import com.yst.sklad.tsd.data.ProductsDbHelper;
import com.yst.sklad.tsd.services.SoapCallToWebService;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lapenkov on 29.11.2017.
 */

public class OrderDownloadHelper {

    private static final String TAG = "OrderDownloadHelper";

    public static void createDocuments(Context context,String... params) {
        ProductsDbHelper dbHelper = ((MainApplication)context.getApplicationContext()).getDatabaseHelper();

        InputStream stream = SoapCallToWebService.receiveOrderByNumber(params[0]);
        List<Integer> listOfProductToAdd = new ArrayList<>();


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);

        Set<String> arrayOfProductTypes = preferences.getStringSet("producttypesArray", new HashSet<String>());

        HashSet<Integer> filterProductTypesSet = new HashSet<Integer>();

        List<String> resourceList = Arrays.asList(context.getResources().getStringArray( R.array.producttypesArray));


        //HashMap<String, String> map = new HashMap<String, String>();

        for (String item:arrayOfProductTypes)
        {

            int index=resourceList.indexOf(item);
            if (index>0)  filterProductTypesSet.add(index+1);
        }
        boolean flagAllProductTypes = arrayOfProductTypes.isEmpty();





        XMLDOMParser parser = new XMLDOMParser();

        Document doc = parser.getDocument(stream);

        int typeofarrival= Integer.parseInt( parser.getTopElementValue(doc, "typeofarrival"));
        String numberin1s=  parser.getTopElementValue(doc, "numberin1s");
        String cleanId = OrderToSupplier.getCleanId( numberin1s);
        // при загрузке этот же старый заказ удаляется

        if  (numberin1s!=null &&  !numberin1s.isEmpty())    dbHelper.deleteOrder(cleanId);


        //      String arrivalnumber=  parser.getTopElementValue(doc, "arrivalnumber");
        String dateoforder=  parser.getTopElementValue(doc, "dateoforder");
        String client=  parser.getTopElementValue(doc, "client");
        String comments=  parser.getTopElementValue(doc, "comment");
        //parser.getValue((Element) doc.getDocumentElement(),"client");



        OrderToSupplier  orderToSupplier=new OrderToSupplier(cleanId,numberin1s,dateoforder,client,typeofarrival,comments);

/*
В listOfProductToAdd добавляем товары которые должны быть добавлены в таблицы (если фильтр включен)
 */

        if (!flagAllProductTypes) {
            NodeList nodeListProducts = doc.getElementsByTagName("Product");

            for (int j = 0; j < nodeListProducts.getLength(); j++) {
                Element p = (Element) nodeListProducts.item(j);
                Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
                Integer producttype = Integer.parseInt(parser.getValue(p, "producttype"));
                if ( filterProductTypesSet.contains(producttype)) listOfProductToAdd.add(productid);

            }
        }


        dbHelper.addOrderToSupplier(orderToSupplier);

        // 1. Добавляем строки
        NodeList nodeListRows = doc.getElementsByTagName("Row");

        for(int j=0; j< nodeListRows.getLength();j++) {


            Element p = (Element) nodeListRows.item(j);
            Integer rownumber = Integer.parseInt(parser.getValue(p, "rownumber"));
            Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
            Integer quantity = Integer.parseInt(parser.getValue(p, "quantity"));


            //   Integer producttype = Integer.parseInt(parser.getValue(p, "producttype"));

            if (flagAllProductTypes || listOfProductToAdd.contains(productid))
                dbHelper.addOrderToSupplierItem(new OrderToSupplierItem(cleanId,rownumber,productid,quantity));

            //    if (!mDbHelper.checkIfProductExists(productid)) listOfProductToAdd.add(productid);

        }



        //2. Если нужно добавить товары то добавляем


        NodeList nodeListProducts = doc.getElementsByTagName("Product");

        for(int j=0; j< nodeListProducts.getLength();j++) {
            Element p = (Element) nodeListProducts.item(j);
            Integer productid = Integer.parseInt(parser.getValue(p, "productid"));

            if (flagAllProductTypes || listOfProductToAdd.contains(productid) && !dbHelper.checkIfProductExists(productid)) {

                String name = parser.getValue(p, "name");
                String barcodes = parser.getValue(p, "barcode");

                String firstBarcode= BarCodeUtils.importAdditionalBarcodesToDb(productid,barcodes,dbHelper);
                String article = parser.getValue(p, "article");
                Integer producttype = Integer.parseInt(parser.getValue(p, "producttype"));
                dbHelper.addProduct(productid,name,firstBarcode,"",producttype,article);
            }
        }




        // 3. Добавляем товары по ячейкая
        NodeList nodeListCells = doc.getElementsByTagName("Cell");

        for(int j=0; j< nodeListCells.getLength();j++) {

            Element p = (Element) nodeListCells.item(j);

            Integer productid = Integer.parseInt(parser.getValue(p, "productid"));
            String stockcell = parser.getValue(p, "stockcell");
            Integer quantityfact = Integer.parseInt(parser.getValue(p, "quantityfact"));
            if (flagAllProductTypes || listOfProductToAdd.contains(productid))
                dbHelper.addArrivalItem(new ArrivalItem(cleanId,productid,quantityfact,stockcell));

        }

    }
}
