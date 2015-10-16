package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IntegerColumn extends Column {

	public IntegerColumn(String n, Field f, Field embedded, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, embedded, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.INTEGER;
	}
	
	@Override
	public void setEntityValue(ResultSet rs, int index, Object bean) throws Exception {
		getField().set(bean, rs.getInt(index));
	}
	
	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.INTEGER);
		}
		else {
			int i;
			if(val instanceof Long) {
				i = ((Long) val).intValue();
			}
			else {
				i = (int) val;
			}
			ps.setInt(index, i);	
		}
	}
}
