package filters.localRejector;

import org.w3c.dom.Node;

public class EsteticalRestructurationRejector implements LocalRejectionFilter{

	private int sentenceTreadted;
	private int sentenceRejected;

	
	public EsteticalRestructurationRejector() {
		sentenceTreadted = 0;
		sentenceRejected = 0;
	}
	
	@Override
	/*
	 * return true if the Node n contains only a sentence restructuration
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		sentenceTreadted++;
		return false;
	}

	@Override
	public void printStatistics() {
		// TODO Auto-generated method stub
		
	}

}
