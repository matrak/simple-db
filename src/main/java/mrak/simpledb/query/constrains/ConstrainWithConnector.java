package mrak.simpledb.query.constrains;

public class ConstrainWithConnector<B> {
	
	private final Constrain<?> constrain;
	private final Connector connector;
	
	public ConstrainWithConnector(Constrain<?> constrain, Connector c) {
		this.constrain = constrain;
		this.connector = c;
	}

	public Constrain<?> getConstrain() {
		return constrain;
	}
	
	public Connector getConnector() {
		return connector;
	}

}
