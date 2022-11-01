/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client;

import hu.kiss.seeder.client.deluge.DTorrentStore;
import hu.kiss.seeder.mongo.DBSynchronizer;
import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Status;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author KICSI
 */
public class DelugeClient {


    private static final String TARTOS_LABEL = "tartos";
    private List<BitTorrent> dTorrents = new ArrayList<>();
    private List<String> tartosIds = new ArrayList<>();

    HashMap<String, String> options = new HashMap<>();
    private Boolean isUpdatable = false;
    private int runningSize = 0;
    private DTorrentStore store;

    public DelugeClient() {
        populateTartosIds();
        store = new DTorrentStore();
        store.addObserver(new DBSynchronizer());
    }

    public void populateSeededTorrents() {
        //Ki listázzuk a futtatott torrenteket
        String startConsoleCommand = "deluge-console info";

        String output = executeCommand(startConsoleCommand);

        String[] lines = output.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("Name: ")) {
                StringBuilder info = new StringBuilder();
                for (int j = i; j < (Math.min(i + 5, lines.length)); j++) {
                    info.append(lines[j]).append("\n");
                }
                i+=5;
                BitTorrent dt = new BitTorrent(info.toString());
                dTorrents.add(dt);
                store.add(dt);
                if (dt.getStatus() != null && !dt.getStatus().equals(Status.PAUSED)) {
                    isUpdatable = true;
                }
                else{
                    runningSize++;
                }
            }

        }
    }

    private void populateTartosIds() {
        try {
            JSONParser parser = new JSONParser();

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/home/svc_deluge/.config/deluge/label.conf")));
            StringBuilder configStr = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                configStr.append(line);
            }

            br.close();

            String labelsConf = "{" + configStr.toString().split("}\\{")[1];

            Object obj = parser.parse(labelsConf);
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject torrentLabels = (JSONObject) jsonObject.get("torrent_labels");
            HashMap<String, String> labelsMap = (HashMap) torrentLabels;
            labelsMap.entrySet().stream().filter(e -> e.getValue().equals(TARTOS_LABEL)).forEach(e -> tartosIds.add(e.getKey()));

            System.out.println("Tartos ids: ");
            tartosIds.forEach(id -> System.out.println(id));

        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        } catch (ParseException ex) {
            System.out.println(ex);
        }
    }

    public void addTorrent(String torrentFile) {

        System.out.println("Start add " + torrentFile);
        String addCommand = "deluge-console add " + torrentFile;
        String output = executeCommand(addCommand);
        System.out.println("Result: " + output);
        runningSize++;
    }

    public void removeTorrent(String id) {
        System.out.println("Start remove " + id);
        String rmCommand = "deluge-console rm " + id + " --remove_data";
        String output = executeCommand(rmCommand);
        System.out.println("Result: " + output);
        runningSize--;
    }

    public void pauseTorrent(String id) {
        System.out.println("Start pause " + id);
        String pauseCommand = "deluge-console pause " + id;
        String output = executeCommand(pauseCommand);
        System.out.println("Result: " + output);
        runningSize--;
    }

    private String executeCommand(String command) {

        StringBuilder output = new StringBuilder();

        Process p;
        try {
            System.out.println("Execute: " + command);
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            System.out.println("Response: " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public List<BitTorrent> getSeededTorrents() {
        return dTorrents;
    }

    public Boolean getIsUpdatable() {
        return isUpdatable;
    }

    public List<String> getTartosIds() {
        return tartosIds;
    }

    public int getRunningSize() {
        return runningSize;
    }
    

}
