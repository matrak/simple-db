package mrak.simpledb.test;

import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.database.DatabaseUCanAccessHandler;
import mrak.simpledb.mapping.AnnotationMapping;
import mrak.simpledb.mapping.CreateTable;
import mrak.simpledb.mapping.CreateTableMsAccess;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.test.entities.Baz;
import mrak.simpledb.test.entities.FooBar;

public class SampleCreateTable {
	
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
		
		if(true) return;
		
		URL databaseURL = SampleCreateTable.class.getResource("/office-2010-foobar_baz.accdb");		
		DatabaseHandler database = new DatabaseUCanAccessHandler(databaseURL.getFile());
		
		String query = 
			"SELECT MSysObjects.Name AS table_name FROM MSysObjects WHERE " +
			"(((Left([Name], 1)) <> \"~\") AND " + 
			 "((Left([Name], 4)) <> \"MSys\") AND " + 
			 "((MSysObjectType.Type) In (1,4,6))) " +
			 "ORDER BY MSysObject.Name";
		
		String query1 = "SELECT * FROM MSysObjects WHERE " + 
			"(((MSysObjects.Type)=1) AND ((MSysObjects.Flags)=0))";
		
		ResultSet rs = database.prepareStatement(query1).executeQuery();
		while(rs.next()) {
			System.out.println(rs.getObject(0));
		}
		
		
		
	}
}
