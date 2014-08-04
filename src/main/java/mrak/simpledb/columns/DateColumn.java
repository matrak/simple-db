package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DateColumn extends Column {

	public DateColumn(String n, Field f, boolean key, boolean foreignKey) {
		super(n, f, key, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.DATE;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.DATE);
		}
		else {
			ps.setDate(index, (Date) val);	
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		getField().set(entity, rs.getDate(index));
	}
}
