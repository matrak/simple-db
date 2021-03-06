package mrak.simpledb.query.constrains;

import mrak.simpledb.columns.Column;

public class Constrain<V> implements ConstrainChainPart {
	
	private Column column;
	V value;
	Operator operator;
	
	
	public Constrain(Column f, V value) {
		this.column = f;
		operator = Operator.EQ;
		this.value = value;
	}
	
	public Constrain(Column f, Operator op, V value) {
		this.column = f;
		this.operator = op;
		this.value = value;
	}	
	
	public Column getColumn() {
		return column;
	}
	
	public V getValue() {
		return value;
	}
	
	public Operator getOperator() {
		return operator;
	}

	@Override
	public ConstrainChainPartType getPartType() {
		return ConstrainChainPartType.CONSTRAIN;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(this.getClass().getSimpleName());
		b.append(" {\n")
		 .append("column: ").append(column).append(",\n")
		 .append("value: ").append(value).append(",\n")
		 .append("operator: ").append(operator).append("\n")
		 .append("}");
		return b.toString();
	}
	
}
