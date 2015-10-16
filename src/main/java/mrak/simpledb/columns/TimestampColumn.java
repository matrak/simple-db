package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class TimestampColumn extends Column {

	public TimestampColumn(String n, Field f, Field embedded, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, embedded, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.DATE;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.TIMESTAMP);
		}
		else {		
			ps.setTimestamp(index, (Timestamp) val);	
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		getField().set(entity, rs.getTimestamp(index));
	}
}
