package filters.localRejector;

import org.w3c.dom.Node;

public class EsteticalRestructurationRejector implements LocalRejectionFilter{

	private int sentenceTreated;
	private int sentenceRejected;

	
	public EsteticalRestructurationRejector() {
		sentenceTreated = 0;
		sentenceRejected = 0;
	}
	
	@Override
	/*
	 * return true if the Node n contains only a sentence restructuration
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		sentenceTreated++;
		return false;
	}

	@Override
	public void printStatistics() {
		System.out.println("The estetical restructuration rejector treated " + sentenceTreated + " sentences, and rejected " + sentenceRejected +" sentences.");				
	}

}
