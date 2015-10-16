package mrak.simpledb.test.entities;

import javax.persistence.Embeddable;

@Embeddable
public class SampleEmbeddable {

	public String someStringValue;
	public int someIntVlaue;
	
	public SampleEmbeddable() {}
	public SampleEmbeddable(String someStringValue, int someIntVlaue) {
		this.someStringValue = someStringValue;
		this.someIntVlaue = someIntVlaue;
	}
}
