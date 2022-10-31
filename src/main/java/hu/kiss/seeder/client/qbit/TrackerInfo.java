package hu.kiss.seeder.client.qbit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackerInfo {

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("tier")
    @Expose
    private String tier;

    @SerializedName("msg")
    @Expose
    private String msg;

    @SerializedName("num_peers")
    @Expose
    private long num_peers;

    @SerializedName("num_seeds")
    @Expose
    private long num_seeds;

    @SerializedName("num_leeches")
    @Expose
    private long num_leeches;

    @SerializedName("num_downloaded")
    @Expose
    private long num_downloaded;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getNum_peers() {
        return num_peers;
    }

    public void setNum_peers(long num_peers) {
        this.num_peers = num_peers;
    }

    public long getNum_seeds() {
        return num_seeds;
    }

    public void setNum_seeds(long num_seeds) {
        this.num_seeds = num_seeds;
    }

    public long getNum_leeches() {
        return num_leeches;
    }

    public void setNum_leeches(long num_leeches) {
        this.num_leeches = num_leeches;
    }

    public long getNum_downloaded() {
        return num_downloaded;
    }

    public void setNum_downloaded(long num_downloaded) {
        this.num_downloaded = num_downloaded;
    }
}
