package mrak.simpledb.query.constrains;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import mrak.simpledb.columns.Column;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.Query;

public class ConstrainChain<B> implements Iterable<ConstrainWithConnector<B>> {
		
	private final List<Constrain<?>> cons;
	private final List<Connector> ops;
	
	private final Mapping<B> map;
	
	private boolean hasConstrains = false;
	
	public ConstrainChain(Mapping<B> map) {
		cons = new ArrayList<Constrain<?>>();
		ops = new ArrayList<Connector>();
		this.map = map;
	}
	
	public ConstrainChain<B> addConstrain(Constrain<?> c) {
		cons.add(c);
		hasConstrains = true;
		return this;
	}
	
	public ConstrainChain<B> connect(Connector con) {
		if(!hasConstrains) throw new IllegalArgumentException("Add some constrain first");
		ops.add(con);
		return this;
	}

	public boolean hasConstrains() {
		return hasConstrains;
	}
	
	/**
	 * Builds "constrain1=? and/or constrain2=?" string and 
	 * appends it to given sql StringBuilder.
	 * 
	 * Not a part of the public api and used only in @see {@link Query}
	 */
	public void appendConstrains(StringBuilder sql, boolean prependTableName) {

		String relation = prependTableName ? map.getTableName() + "." : "";

		for (ConstrainWithConnector<B> cc : this) {
			
			Connector conector = cc.getConnector();
			Column column = cc.getConstrain().getColumn();
			
			sql.append(relation).append(column.getName()).append("=?");
			
			if(conector != null) {
				sql.append(" ").append(conector.name()).append(" ");
			}
		}	
	}
	
	@Override
	public Iterator<ConstrainWithConnector<B>> iterator() {
		Iterator<ConstrainWithConnector<B>> iter = new Iterator<ConstrainWithConnector<B>>() {
			
			private Iterator<Constrain<?>> constrainsIterator = cons.iterator();
			private Iterator<Connector> connectorIterator = ops.iterator();
			
			@Override
			public void remove() {
				throw new IllegalAccessError("Do not remove constrains in this iterator");
			}
			
			@Override
			public ConstrainWithConnector<B> next() {
				Constrain<?> cons = constrainsIterator.hasNext() ? constrainsIterator.next() : null;
				Connector conn = connectorIterator.hasNext() ? connectorIterator.next() : null;
				return new ConstrainWithConnector<>(cons, conn);
			}
			
			@Override
			public boolean hasNext() {
				return constrainsIterator.hasNext();
			}
		};
		
		return iter;
	}
}
