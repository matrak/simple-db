package mrak.simpledb.columns;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EnumNameColumn extends Column {

	public EnumNameColumn(String n, Field f, Field embedded, boolean key, boolean generatedValue, boolean foreignKey) {
		super(n, f, embedded, key, generatedValue, foreignKey);
	}

	@Override
	public ColumnType getType() {
		return ColumnType.ENUM_NAME;
	}

	@Override
	public void setPreparedStatementValue(PreparedStatement ps, int index, Object val) throws Exception {
		if(val == null) {
			ps.setNull(index, java.sql.Types.VARCHAR);
		}
		else {
			Enum<?> en = (Enum<?>) val;
			System.out.println("Setting enum value " + en.name());
			ps.setString(index, en.name());
		}
	}

	@Override
	public void setEntityValue(ResultSet rs, int index, Object entity) throws Exception {
		Field f = getField();
		
		String name = rs.getString(index);
		Enum<?> value = null;
		
		if(name == null) {
			getField().set(entity, value);
			return;
		}
		
		@SuppressWarnings("unchecked")
		Class<? extends Enum<?>> type = (Class<? extends Enum<?>>) f.getType();
		for(Enum<?> e : type.getEnumConstants()) {
			if(e.name().equals(name)) {
				value = e;
				break;
			}
		}
		
		getField().set(entity, value);
	}
}
