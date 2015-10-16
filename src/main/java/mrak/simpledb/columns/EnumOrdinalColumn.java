package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EnumOrdinalColumn extends Column {

	public EnumOrdinalColumn(String n, Field f, Field embedded, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, embedded, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.ENUM_ORDINAL;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.INTEGER);
		}
		else {
			Enum<?> en = (Enum<?>) val;
			ps.setInt(index, en.ordinal());
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		Field f = getField();
		
		Integer ordinal = rs.getInt(index);
		Enum<?> value = null;
		
		if(ordinal == null) {
			getField().set(entity, value);
			return;
		}
		
		@SuppressWarnings("unchecked")
		Class<? extends Enum<?>> type = (Class<? extends Enum<?>>) f.getType();
		for(Enum<?> e : type.getEnumConstants()) {
			if(e.ordinal() == ordinal) {
				value = e;
				break;
			}
		}
		
		getField().set(entity, value);
	}
}
