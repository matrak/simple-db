package mrak.simpledb.mapping;

import mrak.simpledb.columns.ColumnType;
import mrak.simpledb.util.StringUtil;

public class DefaultNameStrategy implements NamingStrategy {

	public static final NamingStrategy INSTANCE = new DefaultNameStrategy();
	
	@Override
	public String getColumnName(ColumnType type, String fieldName) {
		return type.getShortTypeName() + "_" + StringUtil.toLcaseUnderscore(fieldName);
	}

	@Override
	public String getTableName(String entityName) {
		return "t_" + StringUtil.toLcaseUnderscore(entityName);
	}

}
