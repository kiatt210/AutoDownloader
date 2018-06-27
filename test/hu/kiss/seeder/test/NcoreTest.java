/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.test;

import hu.kiss.seeder.client.NCoreClient;

/**
 *
 * @author KICSI
 */
public class NcoreTest {
 
    public static void main(String[] args){
        NCoreClient ncClient = new NCoreClient();
        ncClient.login("test","test","test");
        ncClient.populateHrTorrents();
        ncClient.logout();
    }
    
}