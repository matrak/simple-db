package mrak.simpledb.mapping;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.AccessType;

import mrak.simpledb.columns.Column;

public abstract class Mapping<B> {
	
	private final String tableName;
	private final AccessType accessType;
	
	private final Map<String, Column> columnNameToColumn = new HashMap<String, Column>();
	private final List<Column> columns = new Vector<Column>();
		
	protected Mapping(String tableName, AccessType accessType) {
		this.tableName = tableName;
		this.accessType = accessType;
	}
	
	public List<Column> getKeyColumns() {
		List<Column> keys = new ArrayList<>();
		for(Column c : columns) {
			if(c.isKey()) keys.add(c);
		}
		return keys;
	}
	
	public Column getColumnFor(Field f) {
		return columnNameToColumn.get(f.getName());
	}
	
	public Column getColumnForFieldName(String name) {
		return columnNameToColumn.get(name);
	}
	
	public List<Column> getColumns() {
		return columns;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public AccessType getAccessType() {
		return accessType;
	}
	
	protected void addColumn(Column column) {
		columnNameToColumn.put(column.getField().getName(), column);
		columns.add(column);
	}
	
	public abstract void setGeneratedKeys(B bean, ResultSet keys) throws Exception;
	
	public abstract B newBean();
}
