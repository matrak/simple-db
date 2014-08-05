package mrak.simpledb.test.entities;

import mrak.simpledb.columns.EnumIdColumn.EnumWithId;

public enum SampleEnum implements EnumWithId {

	VAL_1 (10),
	VAL_2 (20),
	VAL_3 (30);
	
	private final int id;
	
	private SampleEnum(int id) {
		this.id = id;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
}
