package localRejector;

import org.w3c.dom.Node;

public class EsteticalRestructurationRejector implements LocalRejectionFilter{

	@Override
	/*
	 * return true if the Node n contains only a sentence restructuration
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

}
