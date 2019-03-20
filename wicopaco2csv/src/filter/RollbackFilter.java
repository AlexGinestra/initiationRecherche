package filter;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import parser.ParserXML;

public class RollbackFilter implements RejectionFilter{
	
	
	public List<String[]>[] listBefAft = null; //list[wordsNumberOfsentenceBefore], String[0] = before, String[1] = after
	
	
	public RollbackFilter() {
		newlist(3); 
		
	}
	
	
	/*
	 * create a new listBefAft with the size in parameter (copy the content if it already exists)
	 */
	private void newlist(int size) {
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
	 * return the word number in the tag : <m>
	 */
	private int getWordNumber(Node mTag){
		return Integer.parseInt(ParserXML.getAttributeContent(mTag.getAttributes(), "num_words"));
	}
	
	
	/*
	 * return true if the word str is already in the list
	 */
	public boolean alreadySeen(String str, int wordNumber) {
		if(wordNumber > listBefAft.length) {
			return false;
		}
		
		for(String[] strTab : listBefAft[wordNumber-1]) {
			if(strTab[0].equals(str) || strTab[1].equals(str)) {
				return true;
			}
		}
		
		
		return false;
	}
	
	
	
	
	@Override
	public boolean hasToBeRemoved(Node n) {

		
		// TODO Auto-generated method stub
		NodeList nList = n.getChildNodes();
		String before;
		
		/* Parcours les enfants de modif */
		for(int j = 0 ; j < nList.getLength() ; j++) {
			Node nTempBefAft = nList.item(j);
			//balise before
			if(nTempBefAft.getNodeName().equals("before")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						Node mTag = lTemp.item(i);
						if(alreadySeen(mTag.getTextContent() ,getWordNumber(mTag)) ) {
							return false;
						}
						else {
							before = mTag.getTextContent();
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
						//TODO
					}
				}
			}
		}
		
		
		return false;
	}

}
