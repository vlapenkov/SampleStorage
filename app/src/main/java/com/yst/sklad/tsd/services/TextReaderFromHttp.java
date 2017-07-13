package com.yst.sklad.tsd.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by user on 06.06.2016.
 */
public class TextReaderFromHttp {


    public static String GetStringFromStream(InputStream inputStream)
    {
    final int bufferSize = 1024;
    final char[] buffer = new char[bufferSize];
    final StringBuilder out = new StringBuilder();
        try {
    Reader in = new InputStreamReader(inputStream, "UTF-8");
    for(;;)

    {
        int rsz = in.read(buffer, 0, buffer.length);
        if (rsz < 0)
            break;
        out.append(buffer, 0, rsz);
    }

    return out.toString();
    }catch (Exception e)
        {return null;}
    }

    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
/*
* read text from url
* */

    public static String readTextArrayFromUrl(String url) throws IOException {
        URL url1 = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
        conn.setRequestMethod("GET");
      //  conn.setRequestProperty("Accept", "application/text");
        conn.connect();
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String data = readAll(br);


            return data;
            //   return ( new JSONArray(jsonText));


        } finally {


        }
    }
}
