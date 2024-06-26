/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author KICSI
 */
public class BitTorrent {

    private String nev;
    private String id = "";
    private Status status;
    private String category;
    private String path;
    private List<String> tags;
    
    private LocalDateTime additionDate;

    public BitTorrent(String info) {
        System.out.println("Cretae new Deluge Torrent from: " + info);
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
        System.out.println("Nev: " + nev);
        System.out.println("Id: " + id);
        System.out.println("staus: " + status);
    }

    public BitTorrent() {

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

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getAdditionDate() {
        return additionDate;
    }

    public void setAdditionDate(LocalDateTime additionDate) {
        this.additionDate = additionDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPath() {
	return path;
    }

    public void setPath(String path) {
	this.path = path;
    }

    public List<String> getTags() {
	return this.tags;
    }

    public void setTags(String tags) {
	this.tags = Arrays.asList(tags.split(","));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BitTorrent) {
            //System.out.println(this.nev+" =? "+((DelugeTorrent) obj).getNev());
            if(this.nev == null || ((BitTorrent)(BitTorrent) obj).getNev() == null) return false;
            return this.nev.equals(((BitTorrent) obj).getNev());
        } else {
            return super.equals(obj);
        }
    }

    @Override
    public String toString() {
        return "DelugeTorrent[nev=" + nev + ",id=" + id + ",status=" + status + "]";
    }


}
