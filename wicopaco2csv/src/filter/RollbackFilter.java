package filter;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.ParserXML;

public class RollbackFilter implements RejectionFilter{
	
	
	public List<String[]>[] listBefAft = null; //list[wordsNumberOfsentenceBefore], String[0] = before, String[1] = after
	
	
	public RollbackFilter() {
		newList(3); 
		
	}
	
	
	/*
	 * create a new listBefAft with the size in parameter (copy the content if it already exists)
	 */
	private void newList(int size) {
		if(listBefAft == null) {
			listBefAft = new ArrayList[size];
			for(int i = 0 ; i < size ; i++) {
				listBefAft[i] = new ArrayList<String[]>();
			}
		}
		else {
			List<String[]>[] listTemp = new ArrayList[size];
			for(int i = 0 ; i < size ; i++) {
				if(listBefAft[i] != null) {
					listTemp[i] = listBefAft[i];
				}
				else {
					listTemp[i] = new ArrayList<String[]>();
				}
			}
			listBefAft = listTemp;
		}
	}
	
	/*
	 * add the two String in the listBefAft
	 */
	private void addWordsInList(String before, int wordNumbBef, String after) {
		if(listBefAft.length < wordNumbBef) {
			newList(wordNumbBef);
		}
		String[] strinngTab = new String[2];
		strinngTab[0] = before;
		strinngTab[1] = after;
		listBefAft[wordNumbBef].add(strinngTab);
	}
	
	
	/*
	 * return the word number in the tag : <m>
	 */
	private int getWordNumber(Node mTag){
		return Integer.parseInt(ParserXML.getAttributeContent(mTag.getAttributes(), "num_words"));
	}
	
	
	/*
	 * return true if the word str is already in the list at the before place 
	 */
	public boolean beforeAlreadySeen(String str, int wordNumber) {
		if(wordNumber > listBefAft.length) {
			return false;
		}
		
		for(String[] strTab : listBefAft[wordNumber-1]) {
			if(strTab[0].equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * return true if the word str is already in the list at the after place
	 */
	public boolean afterAlreadySeen(String str, int wordNumber) {
		if(wordNumber > listBefAft.length) {
			return false;
		}
		
		for(String[] strTab : listBefAft[wordNumber-1]) {
			if(strTab[1].equals(str)) {
				return true;
			}
		}
		return false;
	}
	
	
	
	@Override
	public boolean hasToBeRemoved(Node n) {

		
		// TODO Auto-generated method stub
		NodeList nList = n.getChildNodes();
		String before = null, after = null;
		int wordNumbBef = 0, wordNumbAft = 0;
		boolean isARollback = false;
		
		/* Parcours les enfants de modif */
		for(int j = 0 ; j < nList.getLength() ; j++) {
			Node nTempBefAft = nList.item(j);
			//balise before
			if(nTempBefAft.getNodeName().equals("before")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						Node mTag = lTemp.item(i);
						before = mTag.getTextContent();
						wordNumbBef = getWordNumber(mTag);
						if(afterAlreadySeen(before, wordNumbBef)) {
							isARollback = true;
						}
					}
				}
			}
			//balise after
			else if(nTempBefAft.getNodeName().equals("after")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						Node mTag = lTemp.item(i);
						after = mTag.getTextContent();
						wordNumbAft = getWordNumber(mTag);
						if(isARollback && beforeAlreadySeen(after, wordNumbAft)) {
							return true;
						}
						addWordsInList(before, wordNumbBef, after);
					}
				}
			}
		}
		
		
		return false;
	}

}
