package mrak.simpledb.query;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mrak.simpledb.columns.Column;
import mrak.simpledb.database.DatabaseHandler;
import mrak.simpledb.mapping.Mapping;
import mrak.simpledb.query.constrains.Constrain;
import mrak.simpledb.query.constrains.ConstrainChain;

public class Query<B> {

	public static boolean DEBUG = true;
	
	private final Mapping<B> map;
	private final DatabaseHandler database;
	
	private int firstRow = -1;
	private int maxRows = -1;
	private DbEngine engine = DbEngine.MSACCESS;
	
	public Query(DatabaseHandler handler, Class<B> clazz) {
		this.map = handler.getMapping(clazz);
		this.database = handler;
	}
	
	public void setFirstRow(int firstRow) {
		this.firstRow = firstRow;
	}
	
	public void setMaxRows(int maxRows) {
		this.maxRows = maxRows;
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
			
			if(column.isGeneratedValue()) {
				generateId = true;
			}
			
			if(column.isKey() && column.isGeneratedValue()) {
				continue;
			}
			else {
				Object val = null;
				if(column.isForeignKey()) 
				{
					Object foreign = column.getFieldValue(bean);
					if(foreign != null) {
						Mapping<?> foreignMapping = database.getMapping(foreign.getClass());
						List<Column> foreignIds = foreignMapping.getKeyColumns();
						// TODO add support to multiple ids
						Column foreignId = foreignIds.get(0);
						val = foreignId.getFieldValue(foreign);
					}
				}
				else {
					val = column.getFieldValue(bean);
				}
				
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
		
		_log_("insert", sql.toString());
		
		PreparedStatement ps = database.prepareInsert(generateId, sql.toString());
		
		int index = 1;
		for (Column column : columns) {
			if(column.isKey() && column.isGeneratedValue()) {
				continue;
			}
			else {
				Object value = columnValues.get((index - 1));
				_log_("insert (set ps value)", column.getName() + " index " + (index) + " " + value);
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
	
	public long count(ConstrainChain<B> constrains) throws Exception 
	{
		StringBuilder sql = new StringBuilder();
		List<Column> keys = map.getKeyColumns();
		sql.append("SELECT COUNT(");
		appendColumnNames(keys, sql);
		sql.append(") FROM ").append(map.getTableName());
		
		if(constrains != null && constrains.hasConstrains()) {
			sql.append(" WHERE ");
			constrains.appendConstrains(sql, false);
		}
		
		_log_("count", sql);
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		if(constrains != null && constrains.hasConstrains()) {
			int index = 1;
			for (Constrain<B> con : constrains.getConstrains()) {
				Column c = con.getColumn();
				c.setPreparedStatementValue(ps, index++, con.getValue());
			}
		}
		
		ResultSet rs = ps.executeQuery();
		rs.next();
		long count = rs.getLong(1);
		return count;
	}
	
	public List<B> select(ConstrainChain<B> constrains, OrderBy... orderBy) throws Exception {
		
		boolean hasConstrains = constrains != null && constrains.hasConstrains();
		boolean hasOrderBy = orderBy != null && orderBy.length > 0;
		
		String orderByString = null;
		if(hasOrderBy) {
			StringBuilder b = new StringBuilder();
			for (OrderBy ob : orderBy) {
				b.append(ob.getColumn().getName()).append(ob.isAsc() ? " ASC" : " DESC").append(",");
			}
			b.deleteCharAt(b.length() - 1);
			orderByString = b.toString();
		} 
		
		StringBuilder sql = new StringBuilder();
		
		sql.append("SELECT ");
		if(maxRows > -1 && engine == DbEngine.MSACCESS) {
			sql.append("TOP ").append(maxRows).append(" ");
		}
		appendColumnNames(map.getColumns(), sql);
		sql.append(" FROM ").append(map.getTableName());
		
		if(hasConstrains) {
			sql.append(" WHERE ");
			constrains.appendConstrains(sql, false);
		}

		// AND ( (key columns) not in (select top OFFSET key columns from table where WHERE order by))
		boolean hasSubQuery = false;
		if(firstRow > 0 && engine == DbEngine.MSACCESS && hasOrderBy) {
			if(hasConstrains) sql.append(" AND ((");
			else sql.append(" WHERE ((");
			appendColumnNames(map.getKeyColumns(), sql);
			sql.append(") NOT IN (SELECT TOP ").append(firstRow).append(" ");
			appendColumnNames(map.getKeyColumns(), sql);
			sql.append(" FROM ").append(map.getTableName());
			
			// FIXME will not work, see setPreparedStatementValue
			if(hasConstrains) {
				hasSubQuery = true;
				sql.append(" WHERE ");
				constrains.appendConstrains(sql, false);
			}
			
			sql.append(" ORDER BY ").append(orderByString).append(")");
			sql.append(")");
		}		
		
		if(hasOrderBy) {
			sql.append(" ORDER BY ").append(orderByString);
		}
		
		_log_("select", sql);
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		if(constrains != null && constrains.hasConstrains()) {
			int index = 1;
			for (Constrain<B> con : constrains.getConstrains()) {
				Column c = con.getColumn();
				c.setPreparedStatementValue(ps, index++, con.getValue());
			}
			if(hasSubQuery) {
				for (Constrain<B> con : constrains.getConstrains()) {
					Column c = con.getColumn();
					c.setPreparedStatementValue(ps, index++, con.getValue());
				}	
			}
		}
		
		ResultSet rs = ps.executeQuery();
		Vector<B> result = new Vector<B>();

		while (rs.next()) {
			B bean = map.newBean();
			int index = 1;
			for (Column c : map.getColumns()) {
				if(c.isForeignKey())
				{
					Mapping<?> foreignMapping = database.getMapping(c.getField().getType());
					List<Column> foreignIds = foreignMapping.getKeyColumns();
					// TODO support for multiple ids
					Column foreignId = foreignIds.get(0);
					Object foreign = foreignMapping.newBean();
					foreignId.setEntityValue(rs, index, foreign);
					
					c.getField().set(bean, foreign);
				}
				else if(c.isEmbedded()) 
				{
					// TODO add support for nested embedded objects 
					Object context = c.getEmbeddedIn().get(bean);
					if(context == null) {
						context = c.getEmbeddedIn().getType().newInstance();
						c.getEmbeddedIn().set(bean, context);
					}
					c.setEntityValue(rs, index, context);
				}
				else 
				{
					
					c.setEntityValue(rs, index, bean);
				}
				index++;
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
	
	public void deleteBean(B bean) throws Exception {
		
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
		
		_log_("select", sql);
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		
		int index = 1;
		for (Column column : idColumns) {
			column.setPreparedStatementValue(ps, index, column.getFieldValue(bean));			
		}
		
		ps.executeUpdate();
	}
	
	public void delete(ConstrainChain<B> constrains) throws Exception {
		
		StringBuilder sql = new StringBuilder();
		sql.append("DELETE FROM ");
		sql.append(map.getTableName());

		if(constrains != null && constrains.hasConstrains()) {
			sql.append(" WHERE ");
			constrains.appendConstrains(sql, false);
		}

		_log_("delete", sql);
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		
		if(constrains != null && constrains.hasConstrains()) {
			int index = 1;
			for (Constrain<B> con : constrains.getConstrains()) {
				Column c = con.getColumn();
				c.setPreparedStatementValue(ps, index++, con.getValue());
			}
		}
		
		ps.executeUpdate();		
	}
	
	// TODO update for ConstrainChain
	public void update(B bean) throws Exception {

		StringBuilder sql = new StringBuilder();
		StringBuilder where = new StringBuilder();
		
		sql.append("UPDATE ").append(map.getTableName()).append(" SET ");
		
		// count all columns which are not in the WHERE statement
		int whereIndex = 1;
		List<Column> columns = map.getColumns();
		for (Column column : columns) {
			if(column.isKey()) {
				where.append(column.getName()).append("=? AND ");
			}
			else {
				whereIndex++;
				sql.append(column.getName()).append("=? ,");	
			}
		}
		sql.deleteCharAt(sql.length()-1);
		sql.append(" WHERE ").append(where.substring(0, where.length() - " AND ".length()));
		
		_log_("update", sql);
		
		PreparedStatement ps = database.prepareStatement(sql.toString());
		
		int index = 1;
		for (Column column : columns) {
			
			if(column.isKey()) 
			{
				column.setPreparedStatementValue(ps, whereIndex++, column.getFieldValue(bean));
			}
			else if(column.isForeignKey()) 
			{
				Object foreign = column.getFieldValue(bean);
				Object foreignKeyValue = null;
				if(foreign != null) {
					Mapping<?> foreignMapping = database.getMapping(foreign.getClass());
					List<Column> foreignIds = foreignMapping.getKeyColumns();
					// TODO add support to multiple ids
					Column foreignId = foreignIds.get(0);
					foreignKeyValue = foreignId.getFieldValue(foreign);
				}
				
				column.setPreparedStatementValue(ps, index++, foreignKeyValue);
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
	
	private void _log_(String method, Object s) {
		if(DEBUG) {
			System.out.print("Query->" + method + " : ");
			System.out.println(s);
		}
	}

	private void _log_(String method, StringBuilder s) {
		_log_(method, s.toString());
	}
	
}
