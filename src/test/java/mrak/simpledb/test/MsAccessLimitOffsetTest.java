package mrak.simpledb.test;

import static mrak.simpledb.test.Mappings.FOO_BAR;

import java.util.List;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.query.OrderBy;
import mrak.simpledb.query.Query;
import mrak.simpledb.test.entities.FooBar;
import mrak.simpledb.test.entities.SampleEnum;
import mrak.simpledb.test.entities.SampleSimpleEnum;

import org.junit.Test;

/**
 * (!) UCanAccess has to be added to the classpath
 * (otherwise the test will fail)
 */
public class MsAccessLimitOffsetTest {
	
	@Test
	//@Ignore
	public void test() throws Exception 
	{
		DatabaseHandler database = Mappings.DB;
		database.registerMapping(FooBar.class, FOO_BAR);
		Column idColumn = FOO_BAR.getColumnForFieldName("id");
		
		Query<FooBar> foobarQuery = new Query<>(database, FooBar.class);
		foobarQuery.delete(null);

		// insert foobars
		int COUNT = 10;
		for(int i = 0; i < COUNT; i++) {
			foobarQuery.insert(randomFoobar(i));
		}
		
		// check the count
		int checkCount = (int) foobarQuery.count(null);
		assert COUNT == checkCount;
		
		// select 0 - 4
		foobarQuery.setFirstRow(0);
		foobarQuery.setMaxRows(5);
		List<FooBar> select0_4 = foobarQuery.select(null, OrderBy.by(idColumn));
		for(FooBar fb : select0_4) {
			System.out.println("test string: " + fb.testString);
		}
		assert select0_4.get(0).testString.equals("0");

		// select 5 - 7
		foobarQuery.setFirstRow(5);
		foobarQuery.setMaxRows(3);
		List<FooBar> select5_7 = foobarQuery.select(null, OrderBy.by(idColumn));
		for(FooBar fb : select5_7) {
			System.out.println("test string: " + fb.testString);
		}
		assert select5_7.get(0).testString.equals("5");
		
		// select 8 - 9
		foobarQuery.setFirstRow(8);
		foobarQuery.setMaxRows(10);
		List<FooBar> select8_9 = foobarQuery.select(null, OrderBy.by(idColumn));
		for(FooBar fb : select8_9) {
			System.out.println("test string: " + fb.testString);
		}
		assert select8_9.get(0).testString.equals("8");
	}
	
	private static FooBar randomFoobar(int num) {
		FooBar fb = new FooBar();
		fb.testBoolean = Boolean.FALSE;
		fb.testString = String.valueOf(num);
		fb.enumSimpleValue = SampleSimpleEnum.VAL_1;
		fb.enumValue = SampleEnum.VAL_2;
		return fb;
	}
	
	
	
}
