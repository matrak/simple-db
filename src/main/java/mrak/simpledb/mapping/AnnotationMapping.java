package mrak.simpledb.mapping;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import mrak.simpledb.columns.Column;
import mrak.simpledb.columns.ColumnType;
import mrak.simpledb.util.ReflectionUtil;

public abstract class AnnotationMapping<B> extends Mapping<B> {
	
	private final NamingStrategy naming;
	
	public AnnotationMapping(Class<B> beanClass, NamingStrategy naming) 
	{
		super(beanClass, getTableName(beanClass, naming), getAccessType(beanClass));
		
		this.naming = naming;
		
		try {
			Class<?> c = beanClass;
			while(c != Object.class) {
				initMapping(null, c);
				c = c.getSuperclass();
			}
		}
		catch(Exception e) {
			throw new Error(e);
		}
	}
	
	public AnnotationMapping(Class<B> beanClass) 
	{
		this(beanClass, DefaultNameStrategy.INSTANCE);
	}	
	
	private static String getTableName(Class<?> beanClass, NamingStrategy ns) 
	{	
		Table table = beanClass.getAnnotation(Table.class);
		if(table != null) {
			return table.name();
		}
		else {
			return ns.getTableName(beanClass.getSimpleName());
		}
	}
	
	private static AccessType getAccessType(Class<?> beanClass) 
	{
		Access accessType = beanClass.getAnnotation(Access.class);
		return accessType != null ? accessType.value() : AccessType.FIELD;
	}

	@Override
	public B newBean() {
		try {
			return getBeanClass().newInstance();
		} catch (Exception e) {
			throw new Error("Bean creation failed");
		}
	}
	
	private void initMapping(Field embeddedIn, Class<?> clazz) throws Exception 
	{
		List<Field> fields = new ArrayList<>(Arrays.asList(clazz.getDeclaredFields()));
		
		// TODO check getter / setter for different access strategy
		for(Field field : fields) {

			if(checkEmbeddables(field)) {
				continue;
			}
			
			if(ignoreFiled(field)) {
				continue;
			}

			boolean id = isId(field);
			boolean generatedValue = isGeneratedValue(field);
			boolean fk = isForeignKey(field);
			
			// FIXME check @Column annotation if present for the column type
			ColumnType columnType = getAssociatedType(fk, field);
			if(columnType == null || columnType == ColumnType.UNKNOWN) {
				throw new Error("Unknown column type for field " + field.getName() + " in " + clazz.getSimpleName());
			}
			
			String columnName = naming.getColumnName(columnType, field.getName());
			Column column = columnType.createColumn(columnName, field, embeddedIn, id, generatedValue, fk);
			
			addColumn(column);
		}
	}
	
	private boolean checkEmbeddables(Field f) throws Exception 
	{
		if(f.getType().isAnnotationPresent(Embeddable.class)) {
			initMapping(f, f.getType());
			return true;
		}
		else {
			return false;
		}
	}
	
	private boolean ignoreFiled(Field f) {
		return f.isAnnotationPresent(Transient.class) ||
			   f.isAnnotationPresent(OneToMany.class) ||
			   Modifier.isStatic(f.getModifiers());
	}
	
	private boolean isForeignKey(Field f) {
		return f.isAnnotationPresent(ManyToMany.class) ||
			   f.isAnnotationPresent(ManyToOne.class) ||
			   f.isAnnotationPresent(OneToOne.class);
	}
	
	private boolean isGeneratedValue(Field f) {
		return f.getAnnotation(GeneratedValue.class) != null;
	}
	
	private boolean isId(Field f) {
		return f.getAnnotation(Id.class) != null;
	}
	
	private ColumnType getAssociatedType(boolean fk, Field f) {
		Field field = f;
		
		if(fk) {
			Class<?> fkClazz = f.getDeclaringClass();
			List<Field> fkKeyMatch;
			boolean found = false;
			while(fkClazz != Object.class) {
				fkKeyMatch = ReflectionUtil.getFieldForAnnotation(Id.class, fkClazz);
				if(fkKeyMatch != null && fkKeyMatch.size() > 0) {
					field = fkKeyMatch.get(0);
					found = true;
					break;
				}
				fkClazz = fkClazz.getSuperclass();
			}
			if(!found) {
				throw new Error("Could not find id field for fk for " + f.getName() + " in " + f.getDeclaringClass());
			}
		}
		
		return ColumnType.associatedType(field);
	}
}
