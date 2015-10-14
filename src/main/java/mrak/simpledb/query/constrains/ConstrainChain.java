package mrak.simpledb.query.constrains;

import java.util.ArrayList;
import java.util.List;

import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.Query;

public class ConstrainChain<B> implements ConstrainChainPart {

	private final List<ConstrainChainPart> cons;
	
	private final Mapping<B> map;
	
	private boolean hasConstrains = false;
	
	public ConstrainChain(Mapping<B> map) {
		cons = new ArrayList<>();
		this.map = map;
	}
	
	public ConstrainChain<B> add(ConstrainChainPart c) {
		cons.add(c);
		hasConstrains = (c instanceof Constrain) || hasConstrains;
		return this;
	}

	public ConstrainChain<B> connect(Connector con) {
		if(!hasConstrains) throw new IllegalArgumentException("There are no constrains to connect to");
		cons.add(con);
		return this;
	}

	public ConstrainChain<B> and() {
		return connect(Connector.AND);
	}

	public ConstrainChain<B> or() {
		return connect(Connector.OR);
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

		for (ConstrainChainPart ccp : cons) {
			switch(ccp.getPartType()) {
				case CHAIN:
					sql.append(" (");
					((ConstrainChain<?>) ccp).appendConstrains(sql, prependTableName);
					sql.append(") ");
					break;

				case CONNECTOR:
					sql.append(" ")
					   .append(((Connector) ccp).name())
					   .append(" ");
					break;

				case CONSTRAIN:
					sql.append(prependTableName ? map.getTableName() + "." : "")
					   .append(((Constrain<?>) ccp).getColumn().getName())
					   .append("=?");
					break;
			}
		}	
	}

	@SuppressWarnings("unchecked")
	public List<Constrain<B>> getConstrains() 
	{
		List<Constrain<B>> c = new ArrayList<Constrain<B>>();
		for(ConstrainChainPart p : cons) {
			if(p.getPartType() == ConstrainChainPartType.CONSTRAIN) {
				c.add((Constrain<B>) p);
			}
		}
		return c;
	}

	@Override
	public ConstrainChainPartType getPartType() {
		return ConstrainChainPartType.CHAIN;
	}
}
