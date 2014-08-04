package mrak.simpledb.test;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mrak.simpledb.mapping.AnnotationMapping;
import mrak.simpledb.mapping.CreateTable;
import mrak.simpledb.mapping.CreateTableMsAccess;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.test.entities.Baz;
import mrak.simpledb.test.entities.FooBar;

public class TestCreate {
	
	List<Baz> baz = new ArrayList<>();
		
	public static void main(String[] args) throws Exception {
				
		Mapping<FooBar> foobarMapping = new AnnotationMapping<FooBar>(FooBar.class) {
			
			@Override
			public void setGeneratedKeys(FooBar bean, ResultSet keys) throws Exception {
				int key = keys.getInt(1);
				bean.id = key;
			}
		};

		Mapping<Baz> bazMapping = new AnnotationMapping<Baz>(Baz.class) {
			
			@Override
			public void setGeneratedKeys(Baz bean, ResultSet keys) throws Exception {
				int key = keys.getInt(1);
				bean.id = key;
			}
		};
		
		CreateTable createTable = new CreateTableMsAccess();
		
		System.out.println(createTable.getCreateTableSql(foobarMapping));
		System.out.println(createTable.getCreateTableSql(bazMapping));		
	}
}
