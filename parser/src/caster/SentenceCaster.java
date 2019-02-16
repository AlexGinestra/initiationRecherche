package caster;


public class SentenceCaster implements Caster{

	
	public SentenceCaster() {
		
	}
	
	/*
	 * return true if it's a sentence separator
	 */
	private boolean isSentenceSeparator(char c) {
		if(c == '.' || c == ':' || c == ';') {
			return true;
		}
		return false;
	}
	
	
	/*
	 * Cast to only one sentence if the StringBuilder are composed of multi sentences
	 * (non-Javadoc)
	 * @see casters.Caster#Caster(java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder)
	 */
	@Override
	public boolean cast(StringBuilder before, StringBuilder after, StringBuilder comments) {
		if(before == null || after == null || comments == null) {
			return false;
		}
		int startIndex = 0, firstPoint = 0, lastPointB = 0, lastPointA = 0;
		
		//trouve le debut de la phrase
		for(startIndex = 0 ; startIndex < before.length() && startIndex < after.length() ; startIndex++) {
			
			if(before.charAt(startIndex) != after.charAt(startIndex)) {
				break;
			}
			if(isSentenceSeparator(before.charAt(startIndex))){
				firstPoint = startIndex;
			}
		}
		
		//aller jusqu a la fin de la phrase 
		for(lastPointB = startIndex ; lastPointB < before.length() && !isSentenceSeparator(before.charAt(lastPointB)); lastPointB++); 
		for(lastPointA = startIndex ; lastPointA < after.length() && !isSentenceSeparator(after.charAt(lastPointA)); lastPointA++); 

		// supprime les phrases suivantes
		if(before.length() > lastPointB) {
			before.delete(lastPointB+1, before.length());
		}
		if(after.length() > lastPointA) {
			after.delete(lastPointA+1, after.length());
		}
		
		//supprime les phrases precedentes 
		if(firstPoint != 0) {
			after.delete(0, firstPoint+2);
			before.delete(0, firstPoint+2);
		}
		
		return true;
	}
	
	/*
	public static void main(String... args) {
		StringBuilder b = new StringBuilder(), a = new StringBuilder(), c = new StringBuilder();
		b.append("az.ertyuiop. fkfk");
		a.append("Az.ertyyuiop. fkfk");
		c.append("dldkdk");
		HashMap<String, String> map = new HashMap<String, String>();
		
		MyFilter filter = new MyFilter();
		
		filter.filtre(b, a, c, map);
		System.out.println(b);
		System.out.println(a);
		
	}*/

}
