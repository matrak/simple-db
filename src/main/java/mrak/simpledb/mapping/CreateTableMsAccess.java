package mrak.simpledb.mapping;

import mrak.simpledb.columns.Column;

public class CreateTableMsAccess implements CreateTable {

	@Override
	public <B> String getCreateTableSql(Mapping<B> mappingg) 
	{
		StringBuilder sql = new StringBuilder("CREATE TABLE ");
		sql.append(mappingg.getTableName()).append(" (\n");
		for(Column c : mappingg.getColumns()) {
			sql.append("\t").append(c.getName()).append(" ");
			
			if(!c.isKey()) {
				sql.append(columnToType(c));
			}
			
			else if(c.isKey()) {
				sql.append("COUNTER NOT NULL PRIMARY KEY");
			}
			
			else if(c.isForeignKey()) {
				sql.append(" NOT NULL");
			}
			sql.append(",\n");	
		}
		sql.delete(sql.length() - ",\n".length(), sql.length());
		sql.append("\n)");
		
		return sql.toString();
	}

	private String columnToType(Column col) {
		switch (col.getType()) {
		case BOOLEAN: 
			return "BIT";
		case DATE:
		case TIMESTAMP:
			return "DATETIME";
		case STRING:
			return "TEXT";
		case INTEGER:
		case ENUM_ID:
		case ENUM_ORDINAL:
			return "INTEGER";
		case ENUM_NAME:
			return "TEXT";
		default:
			throw new IllegalArgumentException("Unknown column type");
		}
	}	
}

