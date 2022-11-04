/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.http.protocol.HttpCoreContext;
import org.apache.hc.core5.util.Timeout;
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
        requestBuilder.setConnectTimeout(Timeout.ofSeconds(30));
        requestBuilder.setConnectionRequestTimeout(Timeout.ofSeconds(30));

        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setDefaultRequestConfig(requestBuilder.build());
        this.client = httpClientBuilder.build();
    }

    public ClassicHttpResponse doPost(String url, Map<String, String> parameters, HttpClientContext context) {
        final ClassicHttpRequest post = ClassicRequestBuilder.post()
                .setUri(url)
                .build();

        List<NameValuePair> postParameters = new ArrayList<>();
        parameters.forEach((key, value) -> {
            postParameters.add(new BasicNameValuePair(key, value));
        });
        logger.debug("Params created");
        try {

            post.setEntity(new UrlEncodedFormEntity(postParameters, Charset.forName("UTF-8")));
            logger.debug("Start post");

            var handler = new HttpClientResponseHandler<String>(){
                @Override
                public String handleResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
                    return null;
                }
            };

            return (ClassicHttpResponse) client.execute(post,context);
        } catch (IOException ex) {
            logger.error("Error in sending post to "+url+"\n"+ex.getMessage());
        }
        return null;
    }

    public void doPostWithHandler(String url, Map<String, String> parameters, HttpClientContext context) {
        final ClassicHttpRequest post = ClassicRequestBuilder.post()
                .setUri(url)
                .build();

        List<NameValuePair> postParameters = new ArrayList<>();
        parameters.forEach((key, value) -> {
            postParameters.add(new BasicNameValuePair(key, value));
        });
        logger.debug("Params created");
        try {

            post.setEntity(new UrlEncodedFormEntity(postParameters, Charset.forName("UTF-8")));
            logger.debug("Start post");

            var handler = new HttpClientResponseHandler<String>(){
                @Override
                public String handleResponse(ClassicHttpResponse classicHttpResponse) throws HttpException, IOException {
                    return null;
                }
            };

            client.execute(post,context,handler);
        } catch (IOException ex) {
            logger.error("Error in sending post to "+url+"\n"+ex.getMessage());
        }
    }

    public ClassicHttpResponse doPost(String url, Map<String,String> parameters){
        return doPost(url,parameters,HttpClientContext.create());
    }

    public String getContent(ClassicHttpResponse response) {
        return getContent(response,"\r");
    }

    public String getContent(ClassicHttpResponse response, String lineSeparator) {
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

    public ClassicHttpResponse doGet(String url, Map<String,String> headers) {
        try {
            logger.debug("Start get url=" + url);

            final ClassicHttpRequest get = ClassicRequestBuilder.get()
                    .setUri(url)
                    .build();
            final HttpCoreContext coreContext = HttpCoreContext.create();
            logger.debug("Populate headers");
            if(headers != null){
                headers.forEach((key, value) -> get.setHeader(key, value));
            }
            logger.debug("Execute get");
            ClassicHttpResponse response = (ClassicHttpResponse) client.execute(get);

            logger.debug("Request executed");

            return response;
        } catch (IOException ex) {
            logger.error("Error while sending get to "+url+"\n"+ex.getMessage());
        }
        return null;
    }

    private void printHeaders(HttpResponse response) {
        //get all headers
        Header[] headers = response.getHeaders();
        for (Header header : headers) {
            logger.debug("Key: " + header.getName() + " = Value:" + header.getValue());
        }
    }


}