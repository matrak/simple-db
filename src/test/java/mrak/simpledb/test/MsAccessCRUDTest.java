package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.FOO_BAR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.test.entities.FooBar;
import mrak.simpledb.test.entities.SampleEnum;
import mrak.simpledb.test.entities.SampleSimpleEnum;

import org.junit.Ignore;
import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 * (otherwise the test will fail)
 */
public class MsAccessCRUDTest {
	
	@Test
	@Ignore
	public void testMapping() throws Exception 
	{
		Column idColumn = Column.get(FOO_BAR, "id");
		assertTrue(idColumn.isKey());
		
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(FooBar.class, FOO_BAR);
		Query<FooBar> foobarQuery = new Query<>(database, FooBar.class);
		
		FooBar insertFooBar = new FooBar();
		insertFooBar.testBoolean = Boolean.FALSE;
		insertFooBar.testString = "Test String";
		insertFooBar.enumSimpleValue = SampleSimpleEnum.VAL_1;
		insertFooBar.enumValue = SampleEnum.VAL_2;
		foobarQuery.insert(insertFooBar);
		
		// test select by id
		ConstrainChain<FooBar> selectById = new ConstrainChain<>(FOO_BAR);
		selectById.add(new Constrain<Integer>(Column.get(FOO_BAR, "id"), insertFooBar.id));
		List<FooBar> selectedFooBarList = foobarQuery.select(selectById);
		long selectByIdCount = foobarQuery.count(selectById);
		
		assertSame(selectByIdCount, 1L);
		assertThat(selectedFooBarList.size(), is(1));
		FooBar selectedFoBar = selectedFooBarList.get(0);
		assertSame(insertFooBar.id, selectedFoBar.id);
		
		// test select by string
		ConstrainChain<FooBar> selectByTestString = new ConstrainChain<>(FOO_BAR);
		selectByTestString.add(new Constrain<String>(Column.get(FOO_BAR, "testString"), insertFooBar.testString));
		List<FooBar> selectByTestStringList = foobarQuery.select(selectByTestString);
		
		selectedFoBar = selectByTestStringList.get(0);
		assertSame(insertFooBar.testString,      selectedFoBar.testString);
		assertSame(insertFooBar.enumSimpleValue, selectedFoBar.enumSimpleValue);
		assertSame(insertFooBar.enumValue,       selectedFoBar.enumValue);
		
		// test select by string and id
		ConstrainChain<FooBar> selectByTestStringAndId = new ConstrainChain<>(FOO_BAR);
		selectByTestStringAndId
			.add(new Constrain<String>(Column.get(FOO_BAR, "testString"), insertFooBar.testString))
			.and()
			.add(new Constrain<Integer>(Column.get(FOO_BAR, "id"), insertFooBar.id));
			
		List<FooBar> selectByTestStringAndIdList = foobarQuery.select(selectByTestStringAndId);
		
		selectedFoBar = selectByTestStringAndIdList.get(0);
		assertSame(insertFooBar.testString, selectedFoBar.testString);
		assertSame(insertFooBar.id,         selectedFoBar.id);
		
		// test delete by string and id
		foobarQuery.delete(selectByTestStringAndId);
		List<FooBar> checkDeleted = foobarQuery.select(selectByTestStringAndId);
		assertThat(checkDeleted.size(), is(0));
	}
	
}
