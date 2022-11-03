package hu.kiss.seeder.mongo;


import hu.kiss.seeder.data.BitTorrent;
import hu.kiss.seeder.data.Torrent;

import java.sql.*;

public class TorrentDb {

    private Connection conn;

    public TorrentDb(){
        connect();
        createTable();
    }

    private Connection connect() {
        // SQLite connection string
        String url = "jdbc:sqlite:/home/seeder/torrents.db";
        conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        System.out.println("Connection created");
        return conn;
    }

    private void createTable(){
        String createStmt = "CREATE TABLE IF NOT EXISTS torrent (\n" +
                "\tid text PRIMARY KEY,\n" +
                "   \tnev text NOT NULL,\n" +
                "\timage text not null,\n" +
                "\t status text not null\n"+
                ")";
        try{
            Statement stmt = conn.createStatement();
            stmt.execute(createStmt);
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void truncate(){
        String truncateStmt = "DELETE FROM torrent";

        try {
            PreparedStatement preparedStatement = conn.prepareStatement(truncateStmt);
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(Torrent torrent){
        String insetStmt = "INSERT INTO torrent (id,nev,image,status) values (?,?,?,?)";
        try{
            PreparedStatement preparedStatement = conn.prepareStatement(insetStmt);
            preparedStatement.setLong(1,torrent.getId());
            preparedStatement.setString(2,torrent.getTorrentNev());
            preparedStatement.setString(3,torrent.getInforBarImg());
            preparedStatement.setString(4,torrent.getStatus());

            preparedStatement.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void remove(BitTorrent torrent){
        String deleteStmt = "DELETE FROM  torrent where nev = ?";
        try{
            PreparedStatement stmt = conn.prepareCall(deleteStmt);
            stmt.setString(1,torrent.getNev());

            stmt.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }

}
