package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.*;
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
import mrak.simpledb.test.entities.SampleEmbeddable;

import org.junit.Ignore;
import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 * (otherwise the test will fail)
 */
public class MsAccessEmbeddableTest {

	@Test
	@Ignore
	public void testMapping() throws Exception 
	{
		Column idColumn = Column.get(BAZ, "id");
		assertTrue(idColumn.isKey());
		
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(Baz.class, BAZ);
		database.registerMapping(FooBar.class, FOO_BAR);
		
		// baz
		Query<Baz> bazQuery = new Query<>(database, Baz.class);
		
		Baz baz = new Baz();
		baz.created = new Date();
		baz.embeddable = new SampleEmbeddable("Baz", 123);
		bazQuery.insert(baz);
		assertTrue(baz.id > 0);
		
		ConstrainChain<Baz> bazById = new ConstrainChain<>(BAZ);
		bazById.add(new Constrain<Integer>(Column.get(BAZ, "id"), baz.id));
		List<Baz> selectedBazs = bazQuery.select(bazById);
		Baz selectedBaz = selectedBazs.get(0);
		assertThat(selectedBazs.size(), is(1));
		assertSame(baz.id, selectedBaz.id);
		assertSame(baz.embeddable.someIntVlaue, selectedBaz.embeddable.someIntVlaue);
		assertSame(baz.embeddable.someStringValue, selectedBaz.embeddable.someStringValue);
		
		System.out.println(selectedBaz);
	}
	
}
