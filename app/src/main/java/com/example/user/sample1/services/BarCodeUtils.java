package com.example.user.sample1.services;

/**
 * Created by lapenkov on 14.04.2016.
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
}
