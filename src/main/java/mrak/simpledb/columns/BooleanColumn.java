package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BooleanColumn extends Column {

	public BooleanColumn(String n, Field f, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.BOOLEAN;
	}
	
	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.BIT);
		}
		else {		
			ps.setBoolean(index, (Boolean)val);	
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		getField().set(entity, rs.getBoolean(index));
	}
}
