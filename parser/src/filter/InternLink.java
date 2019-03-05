package filter;

import org.w3c.dom.Node;

public class InternLink implements Filter{

	@Override
	/*
	 * Return true if the Node contains a modification on a intern link
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

}
