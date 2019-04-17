package globalRejector;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import localRejector.LocalRejectionFilter;
import parser.ParserXML;

public class RollbackFilter implements GlobalRejectionFilter{
	
	
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
				if(i < listBefAft.length && listBefAft[i] != null) {
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
		listBefAft[wordNumbBef-1].add(strinngTab);
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
		for(List<String[]> l : listBefAft) {
			for(String[] strTab : l) {
				if(strTab[1].equals(str)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	
	
	/*public boolean hasToBeRemoved(Node n) {
		
		// TODO Auto-generated method stub
		NodeList nList = n.getChildNodes();
		String before = null, after = null;
		int wordNumbBef = 0, wordNumbAft = 0;
		boolean isARollback = false;
		
		/ Parcours les enfants de modif /
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
						System.out.println(before +" " +afterAlreadySeen(before, wordNumbBef));
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
						System.out.println(after+ " " + beforeAlreadySeen(after, wordNumbAft));

						if(isARollback && beforeAlreadySeen(after, wordNumbAft)) {
							return true;
						}
						addWordsInList(before, wordNumbBef, after);
					}
				}
			}
		}
		System.out.println("\n");
		return false;
	}*/


	@Override
	/*
	 * nodeList : <modif> tag list
	 * remove from the list the changes that are canceled 
	 * like: A -> B then B -> A 
	 * (non-Javadoc)
	 * @see globalRejector.GlobalRejectionFilter#cleanTheList(java.util.List)
	 */
	public void cleanTheList(List<Node> nodeList) {
		List<Integer> nodeWillBeRemoved = new ArrayList<Integer>();
		int[][] wordsNumber = new int[nodeList.size()][2]; //wordsNumber[index][0] = nbOfWordsBefore , wordsNumber[index][1] = nbOfWordsAfter

		String before = null, after = null;
		/* add all the case to the list to see if there is no rollback */
		for(int k = 0 ; k < nodeList.size() ; k++) {
			Node n = nodeList.get(k);
			NodeList nList = n.getChildNodes();
			
			/* browse the child of <modif> tag */
			for(int j = 0 ; j < nList.getLength() ; j++) {
				Node nTempBefAft = nList.item(j);
				
				//<before> tag
				if(nTempBefAft.getNodeName().equals("before")) {
					NodeList lTemp = nTempBefAft.getChildNodes();
					for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
						if(lTemp.item(i).getNodeName().equals("m")) {
							Node mTag = lTemp.item(i);
							before = mTag.getTextContent();
							System.out.println(k+ "    "+before);
							wordsNumber[k][0] = getWordNumber(mTag);							
						}
					}
				}
				
				//<after> tag
				else if(nTempBefAft.getNodeName().equals("after")) {
					NodeList lTemp = nTempBefAft.getChildNodes();
					for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
						if(lTemp.item(i).getNodeName().equals("m")) {
							Node mTag = lTemp.item(i);
							after = mTag.getTextContent();
							wordsNumber[k][1] = getWordNumber(mTag);
							addWordsInList(before, wordsNumber[k][0], after);
						}
					}
				}
			}
		}
		
		/* keep only the on which has to be treated */
		for(int k = 0 ; k < nodeList.size() ; k++) {
			Node n = nodeList.get(k);
			NodeList nList = n.getChildNodes();

			
			/* browse the child of <modif> tag */
			for(int j = 0 ; j < nList.getLength() ; j++) {
				Node nTempBefAft = nList.item(j);
				
				//<before> tag
				if(nTempBefAft.getNodeName().equals("before")) {
					NodeList lTemp = nTempBefAft.getChildNodes();
					for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
						if(lTemp.item(i).getNodeName().equals("m")) {
							Node mTag = lTemp.item(i);
							before = mTag.getTextContent();
						}
					}
				}
				
				//<after> tag
				else if(nTempBefAft.getNodeName().equals("after")) {
					NodeList lTemp = nTempBefAft.getChildNodes();
					for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
						if(lTemp.item(i).getNodeName().equals("m")) {
							Node mTag = lTemp.item(i);
							after = mTag.getTextContent();
							if(afterAlreadySeen(before, wordsNumber[k][0]) && beforeAlreadySeen(after, wordsNumber[k][1])) {
								nodeWillBeRemoved.add(k);
							}
						}
					}
				}
			}
		}
		
		/* remove the node from the main list */
		for(int i = nodeWillBeRemoved.size()-1 ; i >= 0 ; i--) {
			nodeList.remove((int)nodeWillBeRemoved.get(i));
		}
	}

}
