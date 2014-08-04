package mrak.simpledb.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DatabaseDefaultHandler implements DatabaseHandler {

	@Override
	public PreparedStatement prepareStatement(String sql) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareStatement(String string, int options) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreparedStatement prepareInsert(boolean generateId, String sql) throws Exception {
		return prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
	}

	@Override
	public ResultSet retrieveGeneratedKeys(PreparedStatement ps) throws Exception {
		return ps.getGeneratedKeys();
	}
	
}
