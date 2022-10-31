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

    private final static String WARN_CSS_CLASS="stopped";

    private String fajlNev;
    private int id;
    private String pageHREF;
    private String status;
    private int hatravan;
    private String torrentNev;
    private String inforBarImg;

    private Boolean warn;

    public Torrent(String nev){
        this.fajlNev = nev;
        this.torrentNev = nev;
    }

    public Torrent(Element torrentElement) {
        this.fajlNev = torrentElement.select("div.hnr_tname > a").attr("title");
        this.pageHREF = torrentElement.select("div.hnr_tname > a").attr("href");
        this.id = Integer.parseInt(pageHREF.split("&id=")[1]);
        this.status = torrentElement.select("div.hnr_tseed").text();
        this.warn = torrentElement.select("div.hnr_tstart span").hasClass(WARN_CSS_CLASS);
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

    public String getFajlNev() {
        return fajlNev;
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

    public String getTorrentNev() {
        return torrentNev;
    }

    public void setTorrentNev(String torrentNev) {
        this.torrentNev = torrentNev;
    }

    public String getInforBarImg() {
        return inforBarImg;
    }

    public void setInforBarImg(String inforBarImg) {
        this.inforBarImg = inforBarImg;
    }

    public void setPageHREF(String pageHREF) {
        this.pageHREF = pageHREF;
    }

    public Boolean isWarn() {
        return warn;
    }

    public void setWarn(Boolean warn) {
        this.warn = warn;
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
        return "Torrent[" + fajlNev + "," + status + "," + hatravan + "," + pageHREF + ","+inforBarImg+ "]\n";
    }

    @Override
    public boolean equals(Object obj) {

        if(((Torrent)obj).getTorrentNev() == null || this.torrentNev == null){
            return false;
        }

        if (obj instanceof Torrent) {
            //System.out.println("Compare: "+fajlNev+" ?= "+((Torrent) obj).getTorrentNev());
            return
                    fajlNev.replace(".pdf", "").equals(((Torrent) obj).getTorrentNev().replace(".pdf", ""))
                            ||
                            fajlNev.equals(((Torrent) obj).getTorrentNev());


        } else {
            return super.equals(obj);
        }
    }

}