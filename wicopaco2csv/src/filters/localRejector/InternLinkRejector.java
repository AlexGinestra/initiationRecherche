package filters.localRejector;

import org.w3c.dom.Node;

public class InternLinkRejector implements LocalRejectionFilter{

	private int sentenceTreated;
	private int sentenceRejected;

	
	public InternLinkRejector() {
		sentenceTreated = 0;
		sentenceRejected = 0;
	}
	
	@Override
	/*
	 * Return true if the Node contains a modification on a intern link
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		sentenceTreated++;
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void printStatistics() {
		System.out.println("The intern link rejector treated " + sentenceTreated + " sentences, and rejected " + sentenceRejected +" sentences.");				
	}

}
