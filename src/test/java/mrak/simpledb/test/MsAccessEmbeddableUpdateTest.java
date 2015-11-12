package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.BAZ;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.test.entities.Baz;
import mrak.simpledb.test.entities.SampleEmbeddable;

import org.junit.Ignore;
import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 * (otherwise the test will fail)
 */
public class MsAccessEmbeddableUpdateTest {
	
	@Test
	@Ignore
	public void test() throws Exception 
	{
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(Baz.class, BAZ);
		Query<Baz> bazQuery = new Query<>(database, Baz.class);
		
		SampleEmbeddable embeddable = new SampleEmbeddable("Baz", 123);
		
		Baz baz = new Baz();
		baz.created = new Date();
		baz.embeddable = embeddable;
		bazQuery.insert(baz);
		assertTrue(baz.id > 0);
		
		int id = baz.id;
		baz = bazQuery.select(byId(id)).get(0);
		assertSame(id, baz.id);
		assertSame(embeddable.someIntVlaue,    baz.embeddable.someIntVlaue);
		assertSame(embeddable.someStringValue, baz.embeddable.someStringValue);
		
		baz.embeddable.someStringValue = "BazBaz";
		bazQuery.update(baz);
		baz = bazQuery.select(byId(id)).get(0);
		assertSame(baz.embeddable.someStringValue, "BazBaz");
	}
	
	private ConstrainChain<Baz> byId(int id) {
		return new ConstrainChain<Baz>(BAZ).add(new Constrain<Integer>(Column.get(BAZ, "id"), id));
	}
}
