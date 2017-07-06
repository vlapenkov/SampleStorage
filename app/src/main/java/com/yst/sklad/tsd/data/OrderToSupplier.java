package com.yst.sklad.tsd.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 03.07.2017.
 * Заказ поставщику
 */
public class OrderToSupplier {


    // number in 1S
    public String Id;
    public String DateOfOrder, Client,Comments;
    public int OrderType;

    public OrderToSupplier(String id, String dateOfOrder, String client, int orderType,String comments) {
        // correct id as regex


        Id = id;

        DateOfOrder = dateOfOrder;
        Client = client;
        Comments = comments;
        OrderType=orderType;

    }


    public  static String getCleanId(String oldId )
    {
        //   Pattern p = Pattern.compile("[1-9][0-9]+");
        Pattern p = Pattern.compile("[1-9][0-9]*");
        Matcher m = p.matcher(oldId);
        m.find();
        return m.group(0);

    }
}
