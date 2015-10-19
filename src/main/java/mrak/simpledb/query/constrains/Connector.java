package mrak.simpledb.query.constrains;

public enum Connector implements ConstrainChainPart {
	
	AND, OR;

	@Override
	public ConstrainChainPartType getPartType() {
		return ConstrainChainPartType.CONNECTOR;
	}
	
	@Override
	public String toString() {
		return "Connector: {" + name() + "}";
	}
}
