package caster;

import java.util.ArrayList;
import java.util.List;

public class SpecialCharacterCaster implements Caster{
	

	private List<Character> charList;
	
	public SpecialCharacterCaster(List<Character> specialCharacters) {
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
		
		//supprime le caractere special en debut de phrase
		if(charList.contains(before.charAt(0))) before.deleteCharAt(0);
		if(charList.contains(after.charAt(0))) after.deleteCharAt(0);
		
		return true;
	}

}
