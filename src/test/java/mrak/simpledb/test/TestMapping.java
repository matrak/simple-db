package mrak.simpledb.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.sql.ResultSet;
import java.util.List;

import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.database.DatabaseMsAccessHandler;
import mrak.simpledb.mapping.AnnotationMapping;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.test.entities.FooBar;

import org.junit.Test;

public class TestMapping {
	
	@Test
	public void testMapping() throws Exception {
		
		Mapping<FooBar> foobarMapping = new AnnotationMapping<FooBar>(FooBar.class) {
			@Override
			public void setGeneratedKeys(FooBar bean, ResultSet keys) throws Exception {
				int key = keys.getInt(1);
				bean.id = key;
			}
		};
		
		URL databaseURL = TestMapping.class.getResource("/office-2010-foobar_baz.accdb");
		DatabaseHandler database = new DatabaseMsAccessHandler(databaseURL.getFile());
		Query<FooBar> foobarQuery = new Query<>(database, foobarMapping);
		
		FooBar insertFooBar = new FooBar();
		insertFooBar.testBoolean = Boolean.FALSE;
		insertFooBar.testString = "Test String";
		foobarQuery.insert(insertFooBar);
		
		ConstrainChain<FooBar> selectById = new ConstrainChain<>(foobarMapping);
		selectById.addConstrain(new Constrain<Integer>(foobarMapping.getColumnForFieldName("id"), insertFooBar.id));
		List<FooBar> selectedFooBarList = foobarQuery.select(selectById);
		
		assertThat(selectedFooBarList.size(), is(1));
		
		FooBar selectedFoBar = selectedFooBarList.get(0);
		
		assertSame(insertFooBar.id, selectedFoBar.id);
	}
	
}
