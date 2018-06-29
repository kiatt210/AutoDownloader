/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.data;

import hu.kiss.seeder.client.NCoreClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 * @author KICSI
 */
public class DelugeTorrent {

    private static Log logger = LogFactory.getLog(DelugeTorrent.class);

    private String nev;
    private String id;
    private Status status;

    public DelugeTorrent(String info) {
        logger.debug("Cretae new Deluge Torrent from: " + info);
        String[] lines = info.split("\n");
        for (String line : lines) {
            if (line.startsWith("Name: ")) {
                nev = line.replace("Name: ", "");
            } else if (line.startsWith("ID: ")) {
                id = line.replace("ID: ", "");
            } else if (line.startsWith("State: ")) {
                status = Status.findByStr(line.split("State: ")[1].split(" ")[0]);

            }
        }
//        logger.debug("Nev: " + nev);
//        logger.debug("Id: " + id);
//        logger.debug("staus: " + status);
    }

    public DelugeTorrent() {
        
    }

    public String getNev() {
        return nev;
    }

    public String getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DelugeTorrent) {
            //logger.debug(this.nev+" =? "+((DelugeTorrent) obj).getNev());
            return this.nev.equals(((DelugeTorrent) obj).getNev());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "DelugeTorrent[nev=" + nev + ",id=" + id + ",status=" + status + "]";
    }

}
