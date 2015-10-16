package mrak.simpledb.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import mrak.simpledb.mapping.Mapping;

public interface DatabaseHandler 
{
	PreparedStatement prepareStatement(String sql) throws Exception;
	PreparedStatement prepareStatement(String string, int options) throws Exception;
	PreparedStatement prepareInsert(boolean generateId, String sql) throws Exception;
	ResultSet retrieveGeneratedKeys(PreparedStatement ps) throws Exception;
	//ResultSet executeQuery(String string) throws Exception;
	<M> void registerMapping(Class<M> calzz, Mapping<M> mapping);
	<M> Mapping<M> getMapping(Class<M> clazz);
}
