package mrak.simpledb.test.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_foobar")
public class FooBar {
	
	@Id
	public Integer id;
	
	public String testString;
	public Boolean testBoolean;
	
	public SampleEnum enumValue;
	
	@OneToMany
	public List<Baz> bazs;
	
}
