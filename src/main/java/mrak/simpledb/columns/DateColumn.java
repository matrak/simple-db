package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DateColumn extends Column {

	public DateColumn(String n, Field f, Field embedded, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, embedded, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.DATE;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		Date d = chekcDate(val);
		if(d == null) {
			ps.setNull(index, java.sql.Types.DATE);
		}
		else {
			ps.setDate(index, d);	
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		getField().set(entity, rs.getDate(index));
	}
	
	private Date chekcDate(Object date) {
		if(date instanceof Date) {
			return (Date) date;
		}
		else if(date instanceof java.util.Date) {
			java.util.Date d = (java.util.Date) date;
			return new Date(d.getTime());
		}
		else {
			return null;
		}
	}
}
