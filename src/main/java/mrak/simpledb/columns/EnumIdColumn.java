package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

// TODO add check to associated field if it implements the interface
public class EnumIdColumn extends Column {

	public interface EnumWithId {
		int getId();
	}
	
	public EnumIdColumn(String n, Field f, boolean key, boolean foreignKey) {
		super(n, f, key, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.ENUM_ID;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.INTEGER);
		}
		else {
			EnumWithId en = (EnumWithId) val;
			ps.setInt(index, en.getId());
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		Field f = getField();
		Integer enumId = rs.getInt(index);
		Enum<?> value = null;
		
		if(enumId == null) {
			getField().set(entity, value);
			return;
		}		
		
		@SuppressWarnings("unchecked")
		Class<? extends Enum<?>> type = (Class<? extends Enum<?>>) f.getType();
		for(Enum<?> e : type.getEnumConstants()) {
			EnumWithId eid = (EnumWithId) e;
			if(eid.getId() == enumId) {
				value = e;
				break;
			}
		}
		
		getField().set(entity, value);
	}
}
