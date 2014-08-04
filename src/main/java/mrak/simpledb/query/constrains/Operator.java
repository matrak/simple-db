package mrak.simpledb.query.constrains;

public enum Operator {
	
	NEQ("!=", "ungleich", "nicht am"),
	L ("<", "kleiner", "fr�her als"),
	LEQ ("<=", "kleiner gleich", "fr�her oder am"),
	EQ ("=", "gleich", "am"),
	GEQ (">=", "gr��er gleich", "sp�ter oder am"),
	G (">", "gr��er", "sp�ter als"),
	LIKE("LIKE", "", null);
	
	public String op = null;
	public String human = null;
	public String humanDate = null;
	
	private Operator(String o, String h, String hDt) {
		this.op = o;
		this.human = h;
		this.humanDate = hDt;
	}
	
	@Override
	public String toString() {
		return op;
	}
	
	public String humanStr() {
		return human;
	}
	
}
