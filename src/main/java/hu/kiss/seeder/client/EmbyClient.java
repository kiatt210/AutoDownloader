package hu.kiss.seeder.client;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EmbyClient {
	private static Logger logger = LogManager.getLogger();
	private String location;
	private static final String SELECT = "select played from UserDatas where UserDataKeyId = (select id from UserDataKeys2 where id= (select UserDataKeyId from MediaItems where path like ?))";

	public EmbyClient(String location) {
	    try{
		var exit = Runtime.getRuntime().exec(new String[]{"cp","-f",location,"/tmp/"}).onExit();
		exit.join();
		logger.debug("Copy exit code:"+exit.get().exitValue());
		this.location = "/tmp/"+Path.of(location).getFileName();
	    }
	    catch(Exception e){
		logger.error("SQLite db copy error",e);
	    }
	    
	}

	public Boolean isWatched(String fileName) {
		Connection conn = null;
		try {
			// db parameters
			String url = "jdbc:sqlite:" + location;
			logger.debug("url:"+url);
			// create a connection to the database
			conn = DriverManager.getConnection(url);
			logger.info("Connection to SQLite has been established.");
			PreparedStatement stmt = conn.prepareStatement(SELECT);
			stmt.setString(1, "%" + fileName);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				logger.info("Is first");
				var result = rs.getInt(1) == 1;
				rs.close();
				return result;
			}

			rs.close();
			return false;
		} catch (SQLException e) {
			logger.error(e.getMessage());
			return false;
		} finally {
			try {
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				logger.error(ex.getMessage());
			}
		}
	}
}
