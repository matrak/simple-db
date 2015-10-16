package mrak.simpledb.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import mrak.simpledb.mapping.Mapping;

public abstract class DatabaseDefaultHandler implements DatabaseHandler {

	protected Map<Class<?>, Mapping<?>> mappings = new HashMap<Class<?>, Mapping<?>>();

	@Override
	public PreparedStatement prepareInsert(boolean generateId, String sql) throws Exception {
		return prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
	}

	@Override
	public ResultSet retrieveGeneratedKeys(PreparedStatement ps) throws Exception {
		return ps.getGeneratedKeys();
	}
	
	@Override
	public <M> void registerMapping(Class<M> clazz, Mapping<M> mapping) {
		mappings.put(clazz, mapping);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <M> Mapping<M> getMapping(Class<M> clazz) {
		return (Mapping<M>) mappings.get(clazz);
	}
	
}
