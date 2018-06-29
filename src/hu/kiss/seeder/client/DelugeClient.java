/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.client;

import hu.kiss.seeder.client.utils.HTTPUtils;
import hu.kiss.seeder.data.DelugeTorrent;
import hu.kiss.seeder.data.Status;
import hu.kiss.seeder.data.Torrent;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author KICSI
 */
public class DelugeClient {

    private static Log logger = LogFactory.getLog(DelugeClient.class);

    private static final String TARTOS_LABEL = "tartos";
    private List<DelugeTorrent> dTorrents = new ArrayList<DelugeTorrent>();
    private List<String> tartosIds = new ArrayList<String>();

    HashMap<String, String> options = new HashMap<String, String>();
    private Boolean isUpdatable = false;
    private int runningSize = 0;

    public DelugeClient() {
        populateTartosIds();
    }

    public void populateSeededTorrents() {
        //Ki listázzuk a futtatott torrenteket
        String startConsoleCommand = "deluge-console info";

        String output = executeCommand(startConsoleCommand);

        String[] lines = output.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("Name: ")) {
                String info = "";
                for (int j = i; j < (i + 5 <= lines.length ? i + 5 : lines.length); j++) {
                    info += lines[j] + "\n";
                }
                i+=5;
                DelugeTorrent dt = new DelugeTorrent(info);
                dTorrents.add(dt);
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

            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("/root/.config/deluge/label.conf")));
            StringBuilder configStr = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                configStr.append(line);
            }

            br.close();

            String labelsConf = "{" + configStr.toString().split("\\}\\{")[1];

            Object obj = parser.parse(labelsConf);
            JSONObject jsonObject = (JSONObject) obj;

            JSONObject torrentLabels = (JSONObject) jsonObject.get("torrent_labels");
            HashMap<String, String> labelsMap = (HashMap) torrentLabels;
            labelsMap.entrySet().forEach(e -> {
                if (e.getValue().equals(TARTOS_LABEL)) {
                    tartosIds.add(e.getKey());
                }
            });

            logger.debug("Tartos ids: ");
            tartosIds.stream().forEach(id -> {
                logger.debug(id);
            });

        } catch (FileNotFoundException ex) {
            Logger.getLogger(DelugeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DelugeClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ParseException ex) {
            Logger.getLogger(DelugeClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addTorrent(String torrentFile) {
        logger.debug("Start add " + torrentFile);
        String addCommand = "deluge-console add " + torrentFile;
        String output = executeCommand(addCommand);
        logger.debug("Result: " + output);
        runningSize++;
    }

    public void removeTorrent(String id) {
        logger.debug("Start remove " + id);
        String rmCommand = "deluge-console rm " + id + " --remove_data";
        String output = executeCommand(rmCommand);
        logger.debug("Result: " + output);
        runningSize--;
    }

    public void pauseTorrent(String id) {
        logger.debug("Start pause " + id);
        String pauseCommand = "deluge-console pause " + id;
        String output = executeCommand(pauseCommand);
        logger.debug("Result: " + output);
        runningSize--;
    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            logger.debug("Execute: " + command);
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            logger.debug("Response: " + output);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }

    public List<DelugeTorrent> getSeededTorrents() {
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
