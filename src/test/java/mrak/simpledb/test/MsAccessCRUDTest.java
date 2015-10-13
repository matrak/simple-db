package mrak.simpledb.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.sql.ResultSet;
import java.util.List;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.database.DatabaseUCanAccessHandler;
import mrak.simpledb.mapping.AnnotationMapping;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.test.entities.FooBar;

import org.junit.Ignore;
import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 */
public class MsAccessCRUDTest {
	
	static final Mapping<FooBar> FOO_BAR_MAPPING = new AnnotationMapping<FooBar>(FooBar.class) {
		
		@Override
		public void setGeneratedKeys(FooBar bean, ResultSet keys) throws Exception {
			int key = keys.getInt(1);
			bean.id = key;
		}
	};
	
	@Test
	@Ignore
	public void testMapping() throws Exception 
	{
		Column idColumn = Column.get(FOO_BAR_MAPPING, "id");
		assertTrue(idColumn.isKey());
		
		URL databaseURL = MsAccessCRUDTest.class.getResource("/office-2010-foobar_baz.accdb");
		DatabaseHandler database = new DatabaseUCanAccessHandler(databaseURL.getFile());
		Query<FooBar> foobarQuery = new Query<>(database, FOO_BAR_MAPPING);
		
		FooBar insertFooBar = new FooBar();
		insertFooBar.testBoolean = Boolean.FALSE;
		insertFooBar.testString = "Test String";
		foobarQuery.insert(insertFooBar);
		
		// test select by id
		ConstrainChain<FooBar> selectById = new ConstrainChain<>(FOO_BAR_MAPPING);
		selectById.add(new Constrain<Integer>(Column.get(FOO_BAR_MAPPING, "id"), insertFooBar.id));
		List<FooBar> selectedFooBarList = foobarQuery.select(selectById);
		
		assertThat(selectedFooBarList.size(), is(1));
		FooBar selectedFoBar = selectedFooBarList.get(0);
		assertSame(insertFooBar.id, selectedFoBar.id);
		
		// test select by string
		ConstrainChain<FooBar> selectByTestString = new ConstrainChain<>(FOO_BAR_MAPPING);
		selectByTestString.add(new Constrain<String>(Column.get(FOO_BAR_MAPPING, "testString"), insertFooBar.testString));
		List<FooBar> selectByTestStringList = foobarQuery.select(selectByTestString);
		
		selectedFoBar = selectByTestStringList.get(0);
		assertSame(insertFooBar.testString, insertFooBar.testString);
		
		// test select by string and id
		ConstrainChain<FooBar> selectByTestStringAndId = new ConstrainChain<>(FOO_BAR_MAPPING);
		selectByTestStringAndId
			.add(new Constrain<String>(Column.get(FOO_BAR_MAPPING, "testString"), insertFooBar.testString))
			.and()
			.add(new Constrain<Integer>(Column.get(FOO_BAR_MAPPING, "id"), insertFooBar.id));
			
		List<FooBar> selectByTestStringAndIdList = foobarQuery.select(selectByTestStringAndId);
		
		selectedFoBar = selectByTestStringAndIdList.get(0);
		assertSame(insertFooBar.testString, insertFooBar.testString);
		assertSame(insertFooBar.id, insertFooBar.id);
		
		// test delete by string and id
		foobarQuery.delete(selectByTestStringAndId);
		List<FooBar> checkDeleted = foobarQuery.select(selectByTestStringAndId);
		assertThat(checkDeleted.size(), is(0));
	}
	
}
