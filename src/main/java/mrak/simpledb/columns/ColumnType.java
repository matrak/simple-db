package mrak.simpledb.columns;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import mrak.simpledb.columns.EnumIdColumn.EnumWithId;

public enum ColumnType {
	
	UNKNOWN      (null, null),
	BOOLEAN      ("b", BooleanColumn.class),
	DATE         ("d", DateColumn.class),
	TIMESTAMP    ("t", TimestampColumn.class),
	STRING       ("s", StringColumn.class),
	INTEGER      ("i", IntegerColumn.class),
	ENUM_ID      ("e_id", EnumIdColumn.class),
	ENUM_NAME    ("e_n",  EnumNameColumn.class),
	ENUM_ORDINAL ("e_o",  EnumOrdinalColumn.class);
	
	private final String shortTypeName;
	private final Class<? extends Column> columnClazz;
	
	private ColumnType(String shortTypeName, Class<? extends Column> columnClazz) 
	{
		this.shortTypeName = shortTypeName;
		this.columnClazz = columnClazz;
	}
	
	public String getShortTypeName() {
		return shortTypeName;
	}
	
	public Column createColumn(String name, Field field, boolean isId, boolean isFk) throws Exception
	{
		Constructor<?> constructor = this.columnClazz.getConstructors()[0];
		return (Column) constructor.newInstance(name, field, isId, isFk);
	}
	
	public static ColumnType associatedType(Field f) 
	{
		// FIXME look for the ManyToOne and ManyToMany
		
		Class<?> type = f.getType();
		if(type == Integer.class || type == Long.class || 
		   type == int.class     || type == long.class) {
			return INTEGER;
		}
		
		else if(type == Boolean.class || type == boolean.class) {
			return BOOLEAN;
		}
		
		else if(type == Date.class || type == java.util.Date.class) {
			return DATE;
		}
		
		else if(type == Timestamp.class) {
			return TIMESTAMP;
		}
		
		else if(type.isEnum()) {
			List<Class<?>> interfaces = Arrays.asList(type.getInterfaces());
			if(interfaces.contains(EnumWithId.class)) {
				return ENUM_ID;
			}
			else {
				Enumerated enumerated = f.getAnnotation(Enumerated.class);
				EnumType value = enumerated.value();
				return (value == EnumType.ORDINAL) ? ENUM_ORDINAL : ENUM_NAME;
			}
		}
		
		else if(type == String.class) {
			return STRING;
		}
		
		else {
			return null;
		}
		
	}
}
