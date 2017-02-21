package eu.epitech.fernan_s.msa_m.yourimage.tools;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.http.HttpParameters;

/**
 * Created by quent on 21/02/2017.
 */

public class RequestTool {

    public static String POSTRequest(String endpointUrl, HttpParameters urlParameters, OAuthConsumer consumer, byte[] data) throws Exception {
        URL url = new URL(endpointUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + "---------------------------7d44e178b0434");
        urlConnection.setRequestMethod("POST");
        urlConnection.setDoOutput(true);

        //Build the list of parameters
        HttpParameters params = urlParameters;

        HttpParameters doubleEncodedParams =  new HttpParameters();
        Iterator<String> iter = params.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            doubleEncodedParams.put(key, OAuth.percentEncode(params.getFirst(key)));
        }
        doubleEncodedParams.put("realm", endpointUrl);
        consumer.setAdditionalParameters(doubleEncodedParams);
        consumer.sign(urlConnection);

        //Send the payload to the connection
        OutputStream OutputStream = urlConnection.getOutputStream();
        try {

            //Send the POST variables
            Iterator<String> iterator = params.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String str = "--"+"---------------------------7d44e178b0434" + "\r\n";
                str += "Content-Disposition: form-data; name=\""+ key + "\"" + "\r\n" + "\r\n";
                str += params.getFirst(key) + "\r\n";
                OutputStream.write(str.getBytes());
                OutputStream.flush();
            }

            //Send the file
            String str = "--" + "---------------------------7d44e178b0434" + "\r\n";
            str += "Content-Disposition: form-data; name=\"photo\"; filename=\""+"android.jpg"+"\"" + "\r\n";
            str += "Content-Type: " + "image/jpeg" + "\r\n\r\n";
            OutputStream.write(str.getBytes());
            OutputStream.write(data);
            str = "\r\n--" + "---------------------------7d44e178b0434" + "\r\n";
            OutputStream.write(str.getBytes());
        }
        finally {
            if (OutputStream != null) {
                OutputStream.close();
            }
        }

        //Send the request and read the output
        try {
            System.out.println("Response: " + urlConnection.getResponseCode() + " " + urlConnection.getResponseMessage());
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String inputStreamString = new Scanner(in,"UTF-8").useDelimiter("\\A").next();
            System.out.println(inputStreamString);
        }
        finally {
            urlConnection.disconnect();
        }
        return null;
    }
}
