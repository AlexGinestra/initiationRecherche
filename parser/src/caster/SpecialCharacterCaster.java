package caster;

import java.util.List;

public class SpecialCharacterCaster implements Caster{
	

	private List<Character> charList;
	
	public SpecialCharacterCaster() {
		
	}
	
	
	/*
	 * add the chars cha to the charList
	 */
	public void addCharTofilter(char... cha) {
		for(char c : cha) {
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
		// TODO Auto-generated method stub
		return false;
	}

}
