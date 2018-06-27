/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.nodes.Element;

/**
 *
 * @author KICSI
 */
public class Torrent implements Comparable<Torrent> {

    private String nev;
    private int id;
    private String pageHREF;
    private String status;
    private int hatravan;

    public Torrent(String nev){
        this.nev = nev;
    }
    
    public Torrent(Element torrentElement) {
        this.nev = torrentElement.select("div.hnr_tname > a").attr("title");
        this.pageHREF = torrentElement.select("div.hnr_tname > a").attr("href");
        this.id = Integer.parseInt(pageHREF.split("&id=")[1]);
        this.status = torrentElement.select("div.hnr_tseed").text();
        String hatravanStr = torrentElement.select("div.hnr_ttimespent").text();

        Pattern oraPattern = Pattern.compile("[0-9]+ó");
        Pattern percPattern = Pattern.compile("[0-9]+p");
        Matcher m = oraPattern.matcher(hatravanStr);

        if (m.find()) {
            String oraStr = m.group(0);
            this.hatravan += Integer.parseInt(oraStr.replace("ó", "")) * 60;
        }

        m = percPattern.matcher(hatravanStr);
        if (m.find()) {
            String percStr = m.group(0);
            this.hatravan += Integer.parseInt(percStr.replace("p", ""));
        }

    }

    public String getNev() {
        return nev;
    }

    public String getPageHREF() {
        return pageHREF;
    }

    public String getStatus() {
        return status;
    }

    public int getHatravan() {
        return hatravan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    @Override
    public int compareTo(Torrent o) {
        if (this.hatravan > o.hatravan) {
            return 1;
        } else if (this.hatravan < o.hatravan) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Torrent[" + nev + "," + status + "," + hatravan + "," + pageHREF + "]\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Torrent) {
            return
            nev.replace(".pdf", "").equals(((Torrent) obj).getNev().replace(".pdf", ""))
                    ||
            nev.equals(((Torrent) obj).getNev());
            

        } else {
            return super.equals(obj); 
        }
    }

}
