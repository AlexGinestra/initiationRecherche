package filters.localRejector;

import java.util.Arrays;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EsteticalRestructurationRejector implements LocalRejectionFilter{

	private int sentenceTreated;
	private int sentenceRejected;

	
	public EsteticalRestructurationRejector() {
		sentenceTreated = 0;
		sentenceRejected = 0;
	}
	
	
	/*
	 * return the Levenshtein distance between the two string x and y
	 */
	private int calculateLevenshtein(String lhs, String rhs) {
		int len0 = lhs.length() + 1;                                                     
	    int len1 = rhs.length() + 1;                                                     
	                                                                                    
	    // the array of distances                                                       
	    int[] cost = new int[len0];                                                     
	    int[] newcost = new int[len0];                                                  
	                                                                                    
	    // initial cost of skipping prefix in String s0                                 
	    for (int i = 0; i < len0; i++) cost[i] = i;                                     
	                                                                                    
	    // dynamically computing the array of distances                                  
	                                                                                    
	    // transformation cost for each letter in s1                                    
	    for (int j = 1; j < len1; j++) {                                                
	        // initial cost of skipping prefix in String s1                             
	        newcost[0] = j;                                                             
	                                                                                    
	        // transformation cost for each letter in s0                                
	        for(int i = 1; i < len0; i++) {                                             
	            // matching current letters in both strings                             
	            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             
	                                                                                    
	            // computing cost for each transformation                               
	            int cost_replace = cost[i - 1] + match;                                 
	            int cost_insert  = cost[i] + 1;                                         
	            int cost_delete  = newcost[i - 1] + 1;                                  
	                                                                                    
	            // keep minimum cost                                                    
	            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
	        }                                                                           
	                                                                                    
	        // swap cost/newcost arrays                                                 
	        int[] swap = cost; cost = newcost; newcost = swap;                          
	    }                                                                               
	                                                                                    
	    // the distance is the cost for transforming all letters in both strings        
	    return cost[len0 - 1];  
    }
	
	
	@Override
	/*
	 * return true if the Node n contains only a sentence restructuration
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		sentenceTreated++;
		
		NodeList nList = n.getChildNodes();
		String beforeM = null, afterM = null;
		
		/* Parcours les enfants de modif */
		for(int j = 0 ; j < nList.getLength() ; j++) {
			Node nTempBefAft = nList.item(j);
			//balise before
			if(nTempBefAft.getNodeName().equals("before")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						beforeM = lTemp.item(i).getTextContent();
					}
				}
			}
			//balise after
			else if(nTempBefAft.getNodeName().equals("after")) {
				NodeList lTemp = nTempBefAft.getChildNodes();
				for(int i = 0 ; i < lTemp.getLength()-1 ; i++) {
					if(lTemp.item(i).getNodeName().equals("m")) {
						afterM = lTemp.item(i).getTextContent();
					}
				}
			}
		}
		
		int levenshteinDist = calculateLevenshtein(afterM, beforeM);
		int lengthMinSentence = Math.min(afterM.length(), beforeM.length());
		
		if(lengthMinSentence < 4 && levenshteinDist < 3) {
			System.out.println("before = " + beforeM + "\t\tafter = "+afterM);
			return false;
		}
		
		//if the Levenshtein distance is higher than the half of the shorter modification
		if(2*levenshteinDist > lengthMinSentence) {
			sentenceRejected++;
			System.out.println("rejected : before = " + beforeM + "\t\tafter = "+afterM);
			return true;
		}
		System.out.println("before = " + beforeM + "\t\tafter = "+afterM);

		return false;
	}

	@Override
	public void printStatistics() {
		System.out.println("The estetical restructuration rejector treated " + sentenceTreated + " sentences, and rejected " + sentenceRejected +" sentences.");				
	}

}
