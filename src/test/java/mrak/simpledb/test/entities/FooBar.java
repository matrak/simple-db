package mrak.simpledb.test.entities;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "t_foobar")
public class FooBar {
	
	@Id
	@GeneratedValue
	public Integer id;
	
	public String testString;
	public Boolean testBoolean;
	
	public SampleEnum enumValue;
	
	@OneToMany
	public List<Baz> bazs;
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("FooBar {\n");
		b.append("\t id: ").append(id).append(",\n");
		b.append("\t testString: ").append(testString).append(",\n");
		b.append("\t testBoolean: ").append(testBoolean).append(",\n");
		b.append("\t enumValue: ").append(enumValue).append(",\n");
		b.append("\t bazs: ").append(bazs != null ? bazs.size() : null).append(",\n");
		b.append("}");
		return b.toString();
	}
	
	
}
