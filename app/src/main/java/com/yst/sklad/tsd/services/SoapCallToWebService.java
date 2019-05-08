package com.yst.sklad.tsd.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yst.sklad.tsd.Utils.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 09.06.2016.
 */
public final class SoapCallToWebService {
    public static final String  ResultOk ="Ok.";
    public static final String StringServiceUrl=Constants.STRING_URL_SHIPMENTS;


private static HashMap<String,String> mHeaders = new HashMap<>();

    static {
        mHeaders.put("Accept-Encoding","gzip,deflate");
        mHeaders.put("Content-Type", "application/soap+xml");
        mHeaders.put("Host", "37.1.84.52:8080");
        mHeaders.put("Connection", "Keep-Alive");
        mHeaders.put("User-Agent","AndroidApp");
        //mHeaders.put("Authorization","Basic Q2xpZW50NTkzMzppMjR4N2U=");
        mHeaders.put("Authorization","Basic Q2xpZW50UmF6dW1vdjpqczRuSG5ZOA==");

    }



/*
Получить заказ по коду
 */
    public final static InputStream receiveOrderByNumber(String numberIn1S)
    {
        int status=0;
        String xmlstring= "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <ser:GetOrderOfArrivalByNumber>\n" +
                "        <ser:Number>"+numberIn1S+"</ser:Number>\n" +
                "        <ser:Type>0</ser:Type>\n" +
                "      </ser:GetOrderOfArrivalByNumber>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";
     return   SendDataTo1SService(xmlstring,"http://37.1.84.50:8080/ServiceTransfer/GetOrderOfArrivalByNumber");




    }

/*
Получить остатки конкретного товара productId
 */
    public final static InputStream getRestOfOneProduct(String productId)
    {
        String xmlstring= "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
               " <ser:GetRestOfOneProduct>\n" +
         "<ser:productid>"+productId+"</ser:productid>\n"+
         "<ser:CodeOfBranch>00005</ser:CodeOfBranch>\n"+
      "</ser:GetRestOfOneProduct>\n"+
   "</soap:Body>\n" +
"</soap:Envelope>";

        return SendDataTo1SService(xmlstring,"http://37.1.84.50:8080/ServiceTransfer/GetRestOfOneProduct");
    }

    /*
    Получить текущие задания из 1С
     */
    public final static InputStream receiveCurrentShipments()
    {

        String xmlstring= "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <ser:GetAllOrdersOfShipment>\n" +
                "         <ser:CodeOfBranch></ser:CodeOfBranch>\n" +
                "      </ser:GetAllOrdersOfShipment>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";
        return SendDataTo1SService(xmlstring,"http://37.1.84.50:8080/ServiceTransfer/GetAllOrdersOfShipment");

    }

/*
Отправить задание в 1С
 */
    public final static InputStream sendShipment(String shipmentId, String innerstr)
    {

        String xmlstring= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/Transfer\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:ChangeOrder>\n" +
                "         <ser:Number>ТК00"+shipmentId+"</ser:Number>\n" +
                "         <ser:ArrayOfProducts>\n" + innerstr+
                "         </ser:ArrayOfProducts>\n" +
                "      </ser:ChangeOrder>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";
return SendDataTo1SService(xmlstring,"http://37.1.84.50:8080/ServiceTransfer/ChangeOrder");

    }

/*
Отправить пакет в 1С
 */
    private final static InputStream SendDataTo1SService(String xmlstring, String soapAction)
    { int status=0;

        HttpURLConnection connection = null;
        try {
            URL url = new URL(StringServiceUrl);
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestProperty("Content-Length", xmlstring.getBytes().length + "");
            connection.setRequestProperty("SOAPAction", soapAction);

            for(Map.Entry<String, String> entry : mHeaders.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                connection.setRequestProperty(key,value);

            }

            connection.setRequestMethod("POST");
            connection.setDoInput(true);

            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(xmlstring.getBytes("UTF-8"));
            outputStream.close();

            connection.connect();
            status = connection.getResponseCode();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            Log.d("HTTP Client", "HTTP status code : " + status);
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;

    }
/*
Отправка заказа в 1С
 */
    public final static InputStream sendOrder(String orderIn1S, int orderType,String innerStr)
    {

        String xmlstring= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/TransferArrival\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                "      <ser:ChangeOrderOfArrival>\n" +
                "         <ser:Number>"+orderIn1S+"</ser:Number>\n" +
                "         <ser:Type>"+orderType+"</ser:Type>\n" +
                "         <ser:ArrayOfCells>\n" + innerStr+
                "         </ser:ArrayOfCells>\n" +
                "      </ser:ChangeOrderOfArrival>\n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

    return SendDataTo1SService(xmlstring, "http://37.1.84.50:8080/ServiceTransfer/ChangeOrderOfArrival");
    }

    public final static InputStream  sendAndCreateMoveGoods(String innerStr)
    {
        String xmlstring= "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/TransferArrival\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>\n" +
                 "<ser:CreateMoveGoods>\n"+
                 "<ser:ArrayOfCells>\n"+
                 innerStr+
                "</ser:ArrayOfCells></ser:CreateMoveGoods></soapenv:Body></soapenv:Envelope>";
        return SendDataTo1SService(xmlstring, "http://37.1.84.50:8080/ServiceTransfer/CreateMoveGoods");


    }

    /*
    Отправить инвентаризацию
     */
    public static InputStream sendInventory(String numberIn1S, String itemsData) {
        String xmlstring="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/TransferArrival\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <ser:ChangeInventory>\n" +
                "         <ser:Number>"+numberIn1S+"</ser:Number>\n" +
                "         <ser:ArrayOfCells>"+itemsData+
                "</ser:ArrayOfCells></ser:ChangeInventory></soap:Body></soap:Envelope>";
        return SendDataTo1SService(xmlstring, "http://37.1.84.50:8080/ServiceTransfer/ChangeInventory");
    }

    /*
   ООтправить перемещение
    */
    public static InputStream sendTransfer( String itemsData) {
        String xmlstring="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/TransferArrival\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <ser:CreateMoved>\n" +
                "         <ser:ArrayOfCells>"+itemsData+
                "</ser:ArrayOfCells></ser:CreateMoved></soap:Body></soap:Envelope>";
        return SendDataTo1SService(xmlstring, "http://37.1.84.50:8080/ServiceTransfer/CreateMoved");
    }

    /*
   Создать внутреннее перемещение
    */
    public static InputStream sendInternalTransfer( String itemsData) {
        String xmlstring="<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\" xmlns:tran=\"http://37.1.84.50:8080/TransferArrival\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
      "<ser:CreateInsideMoved> <ser:ArrayOfCells2>" +itemsData+
                "</ser:ArrayOfCells2>\n" +
                "</ser:CreateInsideMoved>\n" +
                "</soap:Body>\n" +
                "</soap:Envelope>";
        return SendDataTo1SService(xmlstring, "http://37.1.84.50:8080/ServiceTransfer/CreateInsideMoved");
    }
}
