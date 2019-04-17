package filters.localRejector;

import org.w3c.dom.Node;

public class InternLinkRejector implements LocalRejectionFilter{

	private int sentenceTreadted;
	private int sentenceRejected;

	
	public InternLinkRejector() {
		sentenceTreadted = 0;
		sentenceRejected = 0;
	}
	
	@Override
	/*
	 * Return true if the Node contains a modification on a intern link
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		sentenceTreadted++;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void printStatistics() {
		// TODO Auto-generated method stub
		
	}

}
