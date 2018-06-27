/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.kiss.seeder.test;

import hu.kiss.seeder.data.DelugeTorrent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import hu.kiss.seeder.data.Status;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author KICSI
 */
public class DelugeTest {

    static String sampleInfoResponse = "\nName: Brawl.in.Cell.Block.99.2017.BDRip.x264.HuN-No1\n"
            + "ID: ca4a7772a93d133c332bc75f72d866741734800a\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/1.4 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:04:00\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Free.Fire.2016.BDRip.x264-GECKOS\n"
            + "ID: 5c2e5fb46c8df39b5fc62534680cf76caaec4ba1\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/518.0 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:03:57\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Hachiko.A.Dogs.Story.2009.BDRip.Xvid.Hun-VPM\n"
            + "ID: d930280fe5af223817bd2d4e5c565c5c89802199\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/1.1 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 0 days 02:23:14\n"
            + "Tracker status:\n"
            + "\n"
            + "Name: Hachiko.A.Dogs.Story.2009.BluRay.1080p.DTS.Hungarian-LoveYou\n"
            + "ID: 7ab89a814a537289b8158b05c0bcd6501b3b08d2\n"
            + "State: Paused\n"
            + "Size: 499.2 MiB/8.2 GiB Ratio: 0.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 12:42:05\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Korhataros.Szerelem.S01.720p.HDTV.x264.HUN-SFY\n"
            + "ID: d387decf3f41a6a79f86aa87c1b80e06ca2e3eb2\n"
            + "State: Paused\n"
            + "Size: 868.0 MiB/11.2 GiB Ratio: 0.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 12:51:49\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Now That’s What I Call No.1 Hits\n"
            + "ID: 9344e39a6bbe10defe5fa80112cea9b3251d3f82\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/528.8 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:08:11\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: PC World 2018 - 02.pdf\n"
            + "ID: e10934aae763ab8852d143f76cafdbc96aba221a\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/39.0 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 0 days 08:25:48\n"
            + "Tracker status:\n"
            + "\n"
            + "Name: Simply Number Ones - Various\n"
            + "ID: c4c12f4233496a5312396e5119e0ee9a94e4d3a6\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/615.5 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:08:11\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Sweet.Virginia.2017.LiMiTED.BDRip.x264-VETO\n"
            + "ID: 14e395992c85be9dad2f057bd0ed79d84a4d69b5\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/302.2 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:03:57\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: The.Blackcoats.Daughter.2015.BDRip.x264-RUSTED\n"
            + "ID: 5913b61b2ba669e5c09f30c290ab3ebd6bbba831\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/326.8 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:02:59\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: The.Pretender.S04.DVDRip.XviD.Hun-Dm\n"
            + "ID: 19d35007bf4bb8c9a512e78d6ae33853f746a700\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/6.8 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:04:00\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: The.Proposal.2009.RETAiL.BDRiP.x264.HuN-PpB\n"
            + "ID: f89a38ea9f80c34b690d18f2a0c57b95ddf5e22c\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/859.1 MiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 0 days 02:25:27\n"
            + "Tracker status:\n"
            + "\n"
            + "Name: Three.Billboards.Outside.Ebbing.Missouri.2017.RETAiL.BDRiP.x264.HuN-HyperX\n"
            + "ID: ca3b10f355ebf906ea4a21218c8bc57e08e8979b\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/1.0 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:08:10\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: Un.Petit.Boulot.2016.CUSTOM.BDRip.x264.HuN-nIk\n"
            + "ID: 530efec8032861b97317f6028715aa3ca63e6cd5\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/1.2 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:04:00\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "\n"
            + "Name: VA - NOW That's What I Call No.1 HITS [3CD] (2016) FLAC\n"
            + "ID: 6912d4cc4e58a9935570850c4f48e15e12ae6634\n"
            + "State: Paused\n"
            + "Size: 0.0 KiB/1.6 GiB Ratio: -1.000\n"
            + "Seed time: 0 days 00:00:00 Active: 1 days 02:08:13\n"
            + "Tracker status: ncore.cc: Announce OK\n"
            + "deluge-console info  6.53s user 0.36s system 96% cpu 7.114 total";

    private static List<DelugeTorrent> dTorrents = new ArrayList<DelugeTorrent>();

    public static void main(String args[]) throws FileNotFoundException, IOException, ParseException {
        String[] lines = sampleInfoResponse.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("Name: ")) {
                String info = "";
                for (int j = i; j < (i + 5 <= lines.length ? i + 5 : lines.length); j++) {
                    System.out.println("j:"+j);
                    System.out.println("i:"+i);
                    info += lines[j] + "\n";
                }
                i+=5;
                System.out.println("Start add");
                DelugeTorrent dt = new DelugeTorrent(info);
                dTorrents.add(dt);
                if (!dt.getStatus().equals(Status.PAUSED)) {
//                    isUpdatable = true;
                }
            }

        }
        
        System.out.println("Size: "+dTorrents.size());
        
    }

}
