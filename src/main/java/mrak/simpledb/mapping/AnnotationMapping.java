package mrak.simpledb.mapping;

import java.lang.reflect.Field;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import mrak.simpledb.columns.ColumnType;
import mrak.simpledb.util.ReflectionUtil;

public abstract class AnnotationMapping<B> extends Mapping<B> {
	
	private final Class<B> beanClass;
	private final NamingStrategy naming;
	
	public AnnotationMapping(Class<B> beanClass, NamingStrategy naming) 
	{
		super(getTableName(beanClass, naming), getAccessType(beanClass));
		
		this.beanClass = beanClass;
		this.naming = naming;
		
		try {
			Class<?> c = beanClass;
			while(c != Object.class) {
				initMapping(c);
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
	
	public Class<B> getBeanClass() {
		return beanClass;
	}
	
	@Override
	public B newBean() {
		try {
			return beanClass.newInstance();
		} catch (Exception e) {
			throw new Error("Bean creation failed");
		}
	}
	
	private void initMapping(Class<?> clazz) throws Exception 
	{
		Field[] fields = clazz.getDeclaredFields();
		// TODO check getter / setter for different access strategy
		for(Field field : fields) {
			
			if(ignoreFiled(field)) { 
				continue;
			}

			boolean id = isId(field);
			boolean generatedValue = isGeneratedValue(field);
			boolean fk = isForeignKey(field);
			
			ColumnType columnType = getAssociatedType(fk, field);
			// FIXME @Column annotation!
			String columnName = naming.getColumnName(columnType, field.getName());
			
			addColumn(columnType.createColumn(columnName, field, id, generatedValue, fk));
		}
	}
	
	private boolean ignoreFiled(Field f) {
		return f.getAnnotation(Transient.class) != null || 
				f.getAnnotation(OneToMany.class) != null;
	}
	
	private boolean isForeignKey(Field f) {
		return f.getAnnotation(ManyToOne.class) != null || 
				f.getAnnotation(ManyToMany.class) != null;
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
			field = ReflectionUtil.getFieldForAnnotation(Id.class, f.getDeclaringClass()).get(0);
		}
		
		return ColumnType.associatedType(field);
	}
}
