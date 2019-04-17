package filters.localRejector;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NumberRejector implements LocalRejectionFilter{

	private int sentenceTreated;
	private int sentenceRejected;
	
	public NumberRejector() {
		sentenceTreated = 0;
		sentenceRejected = 0;
	}
	
	/*
	 * return true if there is a number in the String str
	 * else return false
	 */
	private boolean isNumberIn(String str) {
		for(int i = 0 ; i < str.length() ; i++) {
			if(str.charAt(i) == '0' || str.charAt(i) == '1' || str.charAt(i) == '2' || str.charAt(i) == '3'
					 || str.charAt(i) == '4' || str.charAt(i) == '5' || str.charAt(i) == '6'
					 || str.charAt(i) == '7' || str.charAt(i) == '8' || str.charAt(i) == '9') {
				return true;
			}
		}
		return false;
	}
	
	
	/*
	 * the parameters n is a <modif> tag
	 * return true if in <m> tag in <before> or <after> there is a number correction
	 * else
	 * return false
	 * (non-Javadoc)
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	@Override
	public boolean hasToBeRemoved(Node n) {
		sentenceTreated++;
		
		// TODO Auto-generated method stub
		NodeList nList = n.getChildNodes();
		boolean res;
		
		/* Parcours les enfants de modif */
		for(int j = 0 ; j < nList.getLength() ; j++) {
			Node nTempBefAft = nList.item(j);
			//balise before
			if(nTempBefAft.getNodeName().equals("before")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						Node mTag = lTemp.item(i);
						if(isNumberIn(mTag.getTextContent())){
							sentenceRejected++;
							return true;
						}
						return false;
					}
				}
			}
			//balise after
			else if(nTempBefAft.getNodeName().equals("after")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						Node mTag = lTemp.item(i);
						if(isNumberIn(mTag.getTextContent())) {
							sentenceRejected++;
							return true;
						}
						return false;
					}
				}
			}
		}
		
		return false;
	}

	@Override
	public void printStatistics() {
		System.out.println("The number rejector treated " + sentenceTreated + " sentences, and rejected " + sentenceRejected +" sentences.");		
	}

}
