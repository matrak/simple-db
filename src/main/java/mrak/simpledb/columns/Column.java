package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import mrak.simpledb.mapping.Mapping;

// FIXME hash and equals
public abstract class Column {
	
	private final String name;
	private final Field field;
	private final Field embeddedIn;
	private final boolean isEmbedded;
	
	private final boolean key;
	private final boolean generatedValue;
	private final boolean foreignKey;
	
	public Column(String name, Field field, Field embeddedIn, boolean key, boolean generatedValue, boolean foreignKey) {
		this.name = name;
		this.field = field;
		this.embeddedIn = embeddedIn;
		this.isEmbedded = (embeddedIn != null);
		this.key = key;
		this.generatedValue = generatedValue;
		this.foreignKey = foreignKey;
		
		// TODO check access strategy: field vs property
		if(Modifier.isPrivate(field.getModifiers())) {
			field.setAccessible(true);
		}
		
		if(this.isEmbedded && Modifier.isPrivate(embeddedIn.getModifiers())) {
			embeddedIn.setAccessible(true);
		}		
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
		if(isEmbedded) {
			Object context = embeddedIn.get(entity);
			return context != null ? field.get(context) : null;
		}
		else {
			return field.get(entity);
		}
	}
	
	public Field getEmbeddedIn() {
		return embeddedIn;
	}
	
	public boolean isEmbedded() {
		return isEmbedded;
	}

	public abstract ColumnType getType();
	
	abstract public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception;

	abstract public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception;
	
	public static Column get(Mapping<?> m, String fieldName) {
		Column c = m.getColumnForFieldName(fieldName);
		if(c == null) {
			String mappingName = m != null ? m.getTableName() : "null";
			String error = String.format("Could not retrive column for the fieldName %s in mapping %s", fieldName, mappingName);
			throw new Error(error);
		}
		return c;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Column : ").append(name).append(", type : ").append(getType().name()).append(", is id : ").append(isKey());
		return b.toString();
	}
}
