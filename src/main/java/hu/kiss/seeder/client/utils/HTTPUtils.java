/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author KICSI
 */
public class HTTPUtils {

    private static Logger logger = LogManager.getLogger();

    HttpClient client;
    public HTTPUtils (){
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder.setConnectTimeout(30000);
        requestBuilder.setConnectionRequestTimeout(30000);

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
        this.client = httpClientBuilder.build();
    }

    public HttpResponse doPost(String url,Map<String,String> parameters){
        HttpPost post = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<>();
        parameters.forEach((key, value) -> {
            postParameters.add(new BasicNameValuePair(key, value));
        });

        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            logger.debug("Start post");
            return client.execute(post);
        } catch (IOException ex) {
            logger.error("Error in sending post to "+url+"\n"+ex.getMessage());
        }
        return null;
    }

    public String getContent(HttpResponse response) {
        return getContent(response,"\r");
    }

    public String getContent(HttpResponse response,String lineSeparator) {
        BufferedReader rd = null;
        try {

            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseStr = new StringBuilder();
            String line;

            while ((line = rd.readLine()) != null) {
                responseStr.append(line);
                responseStr.append(lineSeparator);
            }

            return responseStr.toString();
        } catch (IOException ex) {
            logger.info(ex);
        } catch (UnsupportedOperationException ex) {
            logger.info(ex);
        } finally {
            try {
                Objects.requireNonNull(rd).close();
            } catch (IOException ex) {
                logger.info(ex);
            }
        }
        return null;
    }

    public HttpResponse doGet(String url, Map<String,String> headers) {
        try {
            logger.debug("Start get url=" + url);
            HttpGet get = new HttpGet(url);
            logger.debug("Populate headers");
            if(headers != null){
                headers.forEach((key, value) -> get.setHeader(key, value));
            }
            logger.debug("Execute get");
            HttpResponse response = client.execute(get);

            logger.debug("Request executed");

            return response;
        } catch (IOException ex) {
            logger.error("Error while sending get to "+url+"\n"+ex.getMessage());
        }
        return null;
    }

    private void printHeaders(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            logger.debug("Key: " + header.getName() + " = Value:" + header.getValue());
        }
    }

}