package com.yst.sklad.tsd.services;

import com.yst.sklad.tsd.data.ProductsDbHelper;

/**
 * Created by lapenkov on 14.04.2016.
 *
 */
public class BarCodeUtils {
    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        // only got here if we didn't return false
        return true;
    }

    /*
    *
    * */
    public static String trimLeadingZeroes(String s) {

        return s.replaceFirst("^0*","");

    }

    /*
        Прочитать товар
     */
    public static int getProductIdFromBarCode(String barCode)
    {
        if (barCode==null||barCode.length()!=12) return 0;

        String str_id=barCode.substring(4, 11);

        str_id=trimLeadingZeroes(str_id);

        if (!isInteger(str_id)) return 0;
        return Integer.parseInt(str_id);

    }

    public static String getCellFromBarCode(String barCode)
    {
        return barCode;

    }

    /*
    Добавляет дополнительные штрихкоды в базу
     */
    public  static String importAdditionalBarcodesToDb(int productId, String barcodes, ProductsDbHelper helper)
    {

        String[] arraybarcodes = barcodes.split("\\|");
        String barcode = (arraybarcodes.length>0) ? arraybarcodes[0]  :"";

        for (int i=1;i<arraybarcodes.length; i++)
        {
            helper.addProductBarcode(productId,arraybarcodes[i]);
        }

        return barcode;
    }
}
