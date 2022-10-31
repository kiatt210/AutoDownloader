/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.data;

import java.time.LocalDateTime;

/**
 *
 * @author KICSI
 */
public class DelugeTorrent {

    private String nev;
    private String id;
    private Status status;
    private String category;
    private LocalDateTime additionDate;

    public DelugeTorrent(String info) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DelugeTorrent) {
            //System.out.println(this.nev+" =? "+((DelugeTorrent) obj).getNev());
            if(this.nev == null || ((DelugeTorrent)(DelugeTorrent) obj).getNev() == null) return false;
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