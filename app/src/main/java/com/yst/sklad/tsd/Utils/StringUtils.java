package com.yst.sklad.tsd.Utils;

import com.yst.sklad.tsd.services.SoapCallToWebService;

/**
 * Created by lapenkov on 24.01.2019.
 */

public class StringUtils {

   public static String getNumberFromResponse(String s, int length)
   {
       int numberStart=s.indexOf(SoapCallToWebService.ResultOk)+3;
       String number1S=   s.substring(numberStart,numberStart+8);
       return number1S;
   }
}
