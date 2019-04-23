package filters.localRejector;

import org.w3c.dom.Node;

import filters.FiltersStatistics;

public interface LocalRejectionFilter extends FiltersStatistics{

	/*
	 * this method apply to a <modif> tag return
	 * TRUE if it has to be deleted
	 * OR
	 * FALSE if it has to stay in the list
	 */
	public boolean hasToBeRemoved(Node n);
	
}
