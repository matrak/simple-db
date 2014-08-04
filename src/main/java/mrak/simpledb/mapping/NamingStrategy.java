package mrak.simpledb.mapping;

import mrak.simpledb.columns.ColumnType;

public interface NamingStrategy {
	String getColumnName(ColumnType type, String fieldName);
	String getTableName(String entityName);
}
