package mrak.simpledb.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * http://ucanaccess.sourceforge.net/site.html
 * (!) Since ucanaccess is not in any maven repo, all dependencies
 * are added locally to the projects classpath (libs/ucanacess/...).
 */
public class DatabaseUCanAccessHandler extends DatabaseDefaultHandler {
	
	private final String DRIVER = "net.ucanaccess.jdbc.UcanaccessDriver";
	private final String ACCESS_CONNSTR = "jdbc:ucanaccess://%s";
	
	private Connection connection;
	private final String databaseURL;
	
	public DatabaseUCanAccessHandler(String databaseFilePath) throws Exception {
		
		File dataBaseFile = new File(databaseFilePath);
		if(!dataBaseFile.exists()) {
			throw new IllegalArgumentException("Database " + databaseFilePath + " doesn't exist");
		}
		
		Class.forName(DRIVER);
		databaseURL = String.format(ACCESS_CONNSTR, dataBaseFile.getAbsolutePath());
	}
	
	private Connection getConnection() throws SQLException {
		if(connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(databaseURL);
		}
		return connection;
	}	
	
	@Override
	public PreparedStatement prepareStatement(String sql) throws Exception {
		return getConnection().prepareStatement(sql);
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int options) throws Exception {
		return getConnection().prepareStatement(sql, options);
	}

	@Override
	public PreparedStatement prepareInsert(boolean generateId, String sql) throws Exception {
		return prepareStatement(sql);
	}

	@Override
	public ResultSet retrieveGeneratedKeys(PreparedStatement ps) throws Exception {
		ResultSet keys = prepareStatement("SELECT @@IDENTITY").executeQuery(); //ps.executeQuery("SELECT @@IDENTITY;");
		keys.next();
		return keys;
	}
}
