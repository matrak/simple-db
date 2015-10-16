package mrak.simpledb.test;

import java.net.URL;
import java.sql.ResultSet;

import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.database.DatabaseUCanAccessHandler;
import mrak.simpledb.mapping.AnnotationMapping;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.test.entities.Baz;
import mrak.simpledb.test.entities.FooBar;

public class Mappings {
	
	static final Mapping<FooBar> FOO_BAR = new AnnotationMapping<FooBar>(FooBar.class) {
		
		@Override
		public void setGeneratedKeys(FooBar bean, ResultSet keys) throws Exception {
			int key = keys.getInt(1);
			bean.id = key;
		}
	};
	
	static final Mapping<Baz> BAZ = new AnnotationMapping<Baz>(Baz.class) {
		
		@Override
		public void setGeneratedKeys(Baz bean, ResultSet keys) throws Exception {
			int key = keys.getInt(1);
			bean.id = key;
		}
	};
	
	static final DatabaseHandler DB;
	
	static {
		URL databaseURL = MsAccessEmbeddableTest.class.getResource("/office_2010-simple-db-test.accdb");
		try {
			DB = new DatabaseUCanAccessHandler(databaseURL.getFile());
			DB.registerMapping(Baz.class, BAZ);
			DB.registerMapping(FooBar.class, FOO_BAR);
		} catch (Exception e) {
			throw new Error(e);
		}

				
	}

}
