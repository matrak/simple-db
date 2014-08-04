package mrak.simpledb.mapping;

public interface CreateTable {
	<B> String getCreateTableSql(Mapping<B> mappingg);
}