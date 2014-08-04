package mrak.simpledb.util;

/**
 * :-)
 */
public class StringUtil {
	
	public static String toLcaseUnderscore(String camelCaseName) 
	{
		StringBuilder b = new StringBuilder();
		for(char c : camelCaseName.toCharArray()) {
			char cc = c;
			if(Character.isUpperCase(c)) {
				b.append('_');
				cc = Character.toLowerCase(c);
			}
			b.append(cc);
		}
		return b.toString();
	}
}
