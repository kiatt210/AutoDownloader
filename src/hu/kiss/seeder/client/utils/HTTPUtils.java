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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

/**
 *
 * @author KICSI
 */
public class HTTPUtils {
    
    HttpClient client;
    public HTTPUtils (){
        this.client = client = HttpClientBuilder.create().build();;
    }
    
    public HttpResponse doPost(String url,Map<String,String> parameters){
        HttpPost post = new HttpPost(url);
        ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
        parameters.entrySet().forEach(param -> {
            postParameters.add(new BasicNameValuePair(param.getKey(),param.getValue()));
        });
        
        try {
            post.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
            HttpResponse response = client.execute(post);
            return response;
        } catch (IOException ex) {
            System.out.println("Error in sending post to "+url+"\n"+ex.getMessage());
        } 
        return null;
    }
    
    public String getContent(HttpResponse response) {
        BufferedReader rd = null;
        try {
            
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder responseStr = new StringBuilder();
            String line;
            
            while ((line = rd.readLine()) != null) {
                responseStr.append(line);
                responseStr.append('\r');
            }   rd.close();
            String content = responseStr.toString();
            return content;
        } catch (IOException ex) {
            Logger.getLogger(HTTPUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedOperationException ex) {
            Logger.getLogger(HTTPUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Logger.getLogger(HTTPUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public HttpResponse doGet(String url, Map<String,String> headers) {
        try {
            System.out.println("Start get url=" + url);
            HttpGet get = new HttpGet(url);
            if(headers != null){
                headers.entrySet().forEach( head -> {
                    get.setHeader(head.getKey(), head.getValue());
                });
            }
            
            HttpResponse response = client.execute(get);
//            printHeaders(response);
            
            System.out.println("Request executed");
            
            return response;
        } catch (IOException ex) {
            System.out.println("Error while sending get to "+url+"\n"+ex.getMessage());
        }
        return null;
    }
    
    private void printHeaders(HttpResponse response) {
        //get all headers
        Header[] headers = response.getAllHeaders();
        for (Header header : headers) {
            System.out.println("Key: " + header.getName() + " = Value:" + header.getValue());
        }
    }
    
}
