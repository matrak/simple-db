package mrak.simpledb.test.entities;

import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_baz")
public class Baz {
	
	@Id
	public Integer id;
	
	public Date created;
	
	@ManyToOne
	public FooBar foobar;
}
