package hu.kiss.seeder.run;

import jBittorrentAPI.BDecoder;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BitorrentApiTester {

    public static void main(String[] args) throws IOException {
        BufferedInputStream br = new BufferedInputStream(new FileInputStream("C:\\Work\\AutoDownloader\\out\\artifacts\\AutoDownloader_jar\\[nCore][hdser_hun]Oltari_csajok_S01_720p.torrent"));
        Map map = BDecoder.decode(br);
        HashMap infoMap = (HashMap) map.get("info");
        byte[] strBytes = (byte[])infoMap.get("name");
        System.out.println(new String(strBytes));

    }

}
