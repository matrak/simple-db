package mrak.simpledb.test.entities;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "t_baz")
public class Baz {
	
	@Id
	@GeneratedValue
	public Integer id;
	
	public Date created;
	
	@ManyToOne
	public FooBar foobar;
	
	public SampleEmbeddable embeddable;
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("Baz {\n");
		b.append("\t id: ").append(id).append(",\n");
		b.append("\t created: ").append(created).append(",\n");
		b.append("\t foobar: ").append(foobar != null ? foobar.id : null).append(",\n");
		b.append("\t embeddable: ").append(embeddableToStrign()).append("\n");
		b.append("}");
		return b.toString();
	}
	
	private String embeddableToStrign() {
		if(embeddable == null) { 
			return null;
		}
		else {
			return String.format("embeddable: { someStringValue: %s, someIntVlaue: %s }", 
					embeddable.someStringValue, embeddable.someIntVlaue);
		}
		
	}
}
