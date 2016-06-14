package com.example.user.sample1.services;

import com.example.user.sample1.data.Product;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by user on 06.06.2016.
 */
public class TextReaderFromHttp {

    private static String readAll(Reader rd) throws IOException {
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
