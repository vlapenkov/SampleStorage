package com.example.user.sample1.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 09.06.2016.
 */
public class Shipment {
    // number in 1S
    public String Id;
    public String DateOfShipment, Client,Comments;

    public Shipment(String id, String dateOfShipment, String client, String comments) {
        // correct id as regex


        Id = id;

        DateOfShipment = dateOfShipment;
        Client = client;
        Comments = comments;
    }


  public  static String getCleanId(String oldId )
  {
      Pattern p = Pattern.compile("[1-9][0-9]+");
      Matcher m = p.matcher(oldId);
      m.find();
      return m.group(0);

  }
    @Override
    public String toString() {
        return "Shipment{" +
                "Id='" + Id + '\'' +
                ", DateOfShipment='" + DateOfShipment + '\'' +
                ", Client='" + Client + '\'' +
                ", Comments='" + Comments + '\'' +
                '}';
    }
}

