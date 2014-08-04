package mrak.simpledb.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.constrains.ConstrainChain;
import mrak.simpledb.query.constrains.ConstrainWithConnector;

public class Query<B> {

	public static boolean DEBUG = false;
	
	private final Mapping<B> map;
	private final DatabaseHandler database;
	
	public Query(DatabaseHandler handler, Mapping<B> m) {
		this.map = m;
		this.database = handler;
	}
	
	public void insert(B bean) throws Exception {
				
		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO ")
		   .append(map.getTableName())
		   .append(" (");
		
		List<Object> columnValues = new ArrayList<Object>();
		
		StringBuffer sqlValues = new StringBuffer();
		sqlValues.append(" VALUES (");
		
		boolean generateId = false;
		List<Column> columns = map.getColumns();
		for (Column column : columns) {
			
			if(column.isGeneratedId()) {
				generateId = true;
			}
			
			if(column.isKey() && column.isGeneratedId()) {
				continue;
			}
			else {
				Object val = column.getField().get(bean);
				columnValues.add(val);
				sql.append(column.getName()).append(",");
				sqlValues.append("?,");
			}
		}
			 
		sql.deleteCharAt(sql.length()-1);
		sql.append(")");
		sqlValues.deleteCharAt(sqlValues.length()-1);
		sqlValues.append(");");
		
		sql.append(sqlValues);
		
		PreparedStatement ps = database.prepareInsert(generateId, sql.toString());
		
		int index = 1;
		for (Column column : columns) {
			if(column.isKey() && column.isGeneratedId()) {
				continue;
			}
			else {
				Object value = columnValues.get((index - 1));
				column.setPreparedStatementValue(ps, index++, value);	
			}
		}
				
		if(!generateId) {
			ps.executeUpdate();
			ps.close();

			ps.getConnection().commit();
			ps.getConnection().close();
		}
		else {
			ps.executeUpdate();
			ResultSet keys = database.retrieveGeneratedKeys(ps);
			
			map.setGeneratedKeys(bean, keys);
			
			keys.close();
			ps.close();

			ps.getConnection().commit();
			ps.getConnection().close();
		}
	}
	
	public List<B> select(ConstrainChain<B> constrains, OrderBy order, Column... orderBy) throws Exception {
		
		List<Column> columns = map.getColumns();
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT ");
		appendColumnNames(columns, sql);
		sql.append(" FROM ").append(map.getTableName());
		
		if(constrains != null && constrains.hasConstrains()) {
			sql.append(" WHERE ");
			constrains.appendConstrains(sql, false);
		}
		
		if(orderBy != null && orderBy.length > 0) {
			sql.append(" ORDER BY ");
			appendColumnNames(Arrays.asList(orderBy), sql);
			
			if(order != null) {
				sql.append(" ").append(order.name());
			}
		}
	
		if(DEBUG) {
			sysout("select", sql);
		}
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		if(constrains != null && constrains.hasConstrains()) {
			int index = 1;
			for (ConstrainWithConnector<B> con : constrains) {
				Column c = con.getConstrain().getColumn();
				c.setPreparedStatementValue(ps, index++, con.getConstrain().getValue());
			}
		}
		
		ResultSet rs = ps.executeQuery();
		Vector<B> result = new Vector<B>();

		while (rs.next()) {
			B bean = map.newBean();
			int index = 1;
			for (Column c : columns) {
				c.setEntityValue(rs, index++, bean);
			}
			result.add(bean);
		}

		return result;
	}
	
	private static void appendColumnNames(List<? extends Column> cl, StringBuilder b) {
		for (Column c : cl) {
			b.append(c.getName()).append(",");
		}
		b.deleteCharAt(b.length()-1);
	}
	
	public List<B> select(ConstrainChain<B> c) throws Exception {
		return select(c, null);
	}
	
	public void delete(B bean) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(map.getTableName());
		sql.append(" WHERE ");
		
		List<Column> columns = map.getColumns();
		List<Column> idColumns = new ArrayList<Column>();
		
		for (Column column : columns) { 
			if(column.isKey()) {
				idColumns.add(column);
				sql.append(column.getName()).append("=? AND ");
			}
		}
		sql.delete(sql.length()- " AND ".length(), sql.length());
		
		if(DEBUG) {
			sysout("select", sql);
		}
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		
		int index = 1;
		for (Column column : idColumns) {
			column.setPreparedStatementValue(ps, index, column.getFieldValue(bean));			
		}
		
		ps.executeUpdate();
	}
	
	public void delete(B bean, ConstrainChain<B> cons) throws Exception {
		throw new Error("NOT IMPLEMENTED");
	}
	
	public void update(B bean) throws Exception {

		StringBuilder sql = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		sql.append("UPDATE ").append(map.getTableName()).append(" SET ");
		
		// first index with in the "where" parameters 
		int idIndex = 0;
		List<Column> columns = map.getColumns();
		for (Column column : columns) {
			if(column.isKey()) {
				where.append(column.getName()).append("=? AND ");
			}
			else {
				idIndex++;
				sql.append(column.getName()).append("=? ,");	
			}
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" WHERE ").append(where.substring(0, where.length() - " AND ".length()));
		
		if(DEBUG) {
			sysout("select", sql);
		}
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		
		int index = 1;
		for (Column column : columns) {
			if(!column.isKey()) {
				column.setPreparedStatementValue(ps, idIndex++, column.getFieldValue(bean));
			}
			else {
				column.setPreparedStatementValue(ps, index++, column.getFieldValue(bean));
			}
		}
				
		ps.executeUpdate();
		ps.close();
		
		ps.getConnection().commit();
		ps.getConnection().close();
	}
	
	private void sysout(String method, String s) {
		System.out.print("Query->" + method + " :");
		System.out.println(s);
	}

	private void sysout(String method, StringBuilder s) {
		sysout(method, s.toString());
	}
	
	
}
