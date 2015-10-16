package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.BAZ;
import static mrak.simpledb.test.Mappings.FOO_BAR;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Date;
import java.util.List;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.database.DatabaseUCanAccessHandler;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.test.entities.Baz;
import mrak.simpledb.test.entities.FooBar;

import org.junit.Ignore;
import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 * (otherwise the test will fail)
 * 
 * Test the relation baz n --> 1 foobar for baz.
 * 
 */
public class MsAccessManyToOneTest {

	@Test
	@Ignore
	public void testOneToMany() throws Exception 
	{
		Column idColumn = Column.get(BAZ, "id");
		assertTrue(idColumn.isKey());
		
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(FooBar.class, FOO_BAR);
		database.registerMapping(Baz.class, BAZ);
		
		// foobar
		Query<FooBar> foobarQuery = new Query<>(database, FooBar.class);
		
		FooBar foobar = new FooBar();
		foobar.testBoolean = Boolean.FALSE;
		foobar.testString = "Test String";
		foobarQuery.insert(foobar);
		assertTrue(foobar.id > 0);
		
		ConstrainChain<FooBar> foobarById = new ConstrainChain<>(FOO_BAR);
		foobarById.add(new Constrain<Integer>(Column.get(FOO_BAR, "id"), foobar.id));
		List<FooBar> selectedFooBarList = foobarQuery.select(foobarById);
		FooBar selectedFoBar = selectedFooBarList.get(0);
		assertThat(selectedFooBarList.size(), is(1));
		assertSame(foobar.id, selectedFoBar.id);
		
		// baz
		Query<Baz> bazQuery = new Query<>(database, Baz.class);
		
		Baz baz = new Baz();
		baz.foobar = foobar;
		baz.created = new Date();
		bazQuery.insert(baz);
		assertTrue(baz.id > 0);
		
		ConstrainChain<Baz> bazById = new ConstrainChain<>(BAZ);
		bazById.add(new Constrain<Integer>(Column.get(BAZ, "id"), baz.id));
		List<Baz> selectedBazs = bazQuery.select(bazById);
		Baz selectedBaz = selectedBazs.get(0);
		assertThat(selectedBazs.size(), is(1));
		assertSame(baz.id, selectedBaz.id);		
		assertTrue(selectedBaz.foobar.id > 0);
		assertSame(selectedBaz.foobar.id, baz.foobar.id);
		
		System.out.println(selectedBaz);
		System.out.println(selectedBaz.foobar);
	}
	
}
