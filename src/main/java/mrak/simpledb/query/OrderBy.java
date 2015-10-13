package mrak.simpledb.query;

import mrak.simpledb.columns.Column;

public class OrderBy {
	
	private final boolean asc;
	private final Column column;
	
	public OrderBy(Column c, boolean asc) {
		this.column = c;
		this.asc = asc;
	}
	
	public static OrderBy by(Column c) {
		OrderBy ob = new OrderBy(c, true);
		return ob;
	}
	
	public static OrderBy by(Column c, boolean asc) {
		OrderBy ob = new OrderBy(c, asc);
		return ob;
	}
	
	public boolean isAsc() {
		return asc;
	}
	
	public Column getColumn() {
		return column;
	}
}
