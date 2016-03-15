package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.FOO_BAR;
import static org.junit.Assert.assertTrue;
import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.query.Query;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.query.constrains.Operator;
import mrak.simpledb.test.entities.FooBar;

import org.junit.Ignore;
import org.junit.Test;

public class MsAccessComplexQueryTest {

	@Test
	@Ignore
	public void testMapping() throws Exception 
	{
		Column idColumn = Column.get(FOO_BAR, "id");
		Column testStringBoolean = Column.get(FOO_BAR, "testBoolean");
		
		assertTrue(idColumn.isKey());
		
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(FooBar.class, FOO_BAR);
		
		ConstrainChain<FooBar> cc = new ConstrainChain<FooBar>(FOO_BAR);
		cc.add(new Constrain<Long>(idColumn, Operator.GEQ, 0L))
		  .and().add(new ConstrainChain<FooBar>(FOO_BAR)
				.add(new Constrain<Boolean>(testStringBoolean, Boolean.TRUE)).or()
				.add(new Constrain<Boolean>(testStringBoolean, Boolean.FALSE)));
		
		Query<FooBar> foobarQuery = new Query<>(database, FooBar.class);
		long count = foobarQuery.count(cc);
		System.out.println(count);
	}
	
}
