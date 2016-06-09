package com.example.user.sample1.services;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by user on 09.06.2016.
 */
public final class SoapCallToWebService {

    public final static InputStream Call(String stringUrlShipments)
    {
        int status=0;
        String xmlstring= "<soap:Envelope xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ser=\"http://37.1.84.50:8080/ServiceTransfer\">\n" +
                "   <soap:Header/>\n" +
                "   <soap:Body>\n" +
                "      <ser:GetAllOrdersOfShipment>\n" +
                "         <ser:CodeOfBranch></ser:CodeOfBranch>\n" +
                "      </ser:GetAllOrdersOfShipment>\n" +
                "   </soap:Body>\n" +
                "</soap:Envelope>";
        StringBuffer chaine = new StringBuffer("");

        HttpURLConnection connection = null;
        try {
            URL url = new URL(stringUrlShipments);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept-Encoding", "gzip,deflate");
            connection.setRequestProperty("Content-Type", "application/soap+xml");
            connection.setRequestProperty("Content-Length", xmlstring.getBytes().length + "");
            connection.setRequestProperty("SOAPAction", "http://37.1.84.50:8080/ServiceTransfer/GetAllOrdersOfShipment");
            connection.setRequestProperty("Host", "37.1.84.50:8080");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("User-Agent", "AndroidApp");
            connection.setRequestProperty("Authorization", "Basic Q2xpZW50NTkzMzppMjR4N2U=");

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
        } finally {

            Log.i("HTTP Client", "HTTP status code : " + status);
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputStream;
    }
}
