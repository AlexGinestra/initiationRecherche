package filters.purifier;

import java.util.ArrayList;
import java.util.List;

public class SpecialCaracterPurifier implements PurifierFilter{
	
	private int charDeleted;
	private int sentenceNumber;
	private List<Character> charList;
	
	public SpecialCaracterPurifier(List<Character> specialCharacters) {
		charDeleted = 0;
		sentenceNumber = 0;
		charList = new ArrayList<Character>();
		for(char c : specialCharacters) {
			charList.add(c);
		}
	}
	
	/*
	 * return false if there is special caracters contained in charList in the parameters
	 * else 
	 * return true
	 * (non-Javadoc)
	 * @see caster.Caster#cast(java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder)
	 */
	@Override
	public boolean cast(StringBuilder before, StringBuilder after, StringBuilder comments) {
		if(before == null || after == null || comments == null) {
			return false;
		}
		
		sentenceNumber += 2;
		
		//supprime le caractere special en debut de phrase
		if(charList.contains(before.charAt(0))) {
			before.deleteCharAt(0);
			charDeleted++;
		}
		if(charList.contains(after.charAt(0))) {
			after.deleteCharAt(0);
			charDeleted++;
		}
		
		return true;
	}

	@Override
	public void printStatistics() {
		System.out.println("The sepecial caracters purifier treated " + sentenceNumber + " sentences, and deleted " + charDeleted + " char.");
	}

}
