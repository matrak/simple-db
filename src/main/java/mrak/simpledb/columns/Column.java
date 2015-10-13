package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import mrak.simpledb.mapping.Mapping;

// FIXME hash and equals
public abstract class Column {
	
	private final String name;
	private final Field field;
	
	private final boolean key;
	private final boolean generatedValue;
	private final boolean foreignKey;
	
	public Column(String name, Field field, boolean key, boolean generatedValue, boolean foreignKey) {
		this.name = name;
		this.field = field;
		this.key = key;
		this.generatedValue = generatedValue;
		this.foreignKey = foreignKey;
	}
	
	public String getName() {
		return name;
	}
	
	public Field getField() {
		return field;
	}
	
	public boolean isKey() {
		return key;
	}
	
	public boolean isGeneratedValue() {
		return generatedValue;
	}
	
	public boolean isForeignKey() {
		return foreignKey;
	}
	
	public Object getFieldValue(Object entity) throws IllegalArgumentException, IllegalAccessException {
		return field.get(entity);
	}
		
	public abstract ColumnType getType();
	
	abstract public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception;

	abstract public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception;
	
	public static Column get(Mapping<?> m, String fieldName) {
		return m.getColumnForFieldName(fieldName);
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Column : ").append(name).append(", type : ").append(getType().name()).append(", is ID : ").append(isKey());
		return b.toString();
	}
}
