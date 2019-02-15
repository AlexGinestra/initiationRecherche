package filter;

import org.w3c.dom.Node;

public class DateFilter implements Filter{

	/*
	 * the parameters n is a <modif> tag
	 * return true if the tag <m> in both of tag <before> and <after> contains a date correction
	 * else
	 * return false
	 * (non-Javadoc)
	 * @see filter.Filter#hasToBeRemoved(org.w3c.dom.Node)
	 */
	@Override
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

}
