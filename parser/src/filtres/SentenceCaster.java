package filtres;


public class SentenceCaster implements Filter{

	
	public SentenceCaster() {
		
	}
	
	
	@Override
	public boolean filtre(StringBuilder before, StringBuilder after, StringBuilder comments) {
		if(before == null || after == null || comments == null) {
			return false;
		}
		int startIndex = 0, firstPoint = 0, lastPointB = 0, lastPointA = 0;
		
		//trouve le debut de la phrase
		for(startIndex = 0 ; startIndex < before.length() && startIndex < after.length() ; startIndex++) {
			
			if(before.charAt(startIndex) != after.charAt(startIndex)) {
				break;
			}
			if(before.charAt(startIndex) == '.') {
				firstPoint = startIndex;
			}
		}
		
		//aller jusqu a la fin de la phrase 
		for(lastPointB = startIndex ; lastPointB < before.length() && before.charAt(lastPointB) != '.'; lastPointB++); 
		for(lastPointA = startIndex ; lastPointA < after.length() && after.charAt(lastPointA) != '.'; lastPointA++); 

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
