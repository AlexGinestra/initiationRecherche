package filters.globalRejector;

import java.util.List;

import org.w3c.dom.Node;

import filters.FiltersStatistics;

public interface GlobalRejectionFilter extends FiltersStatistics{
	
	/*
	 * this method apply to a <modif> tag list return
	 * TRUE if it has to be deleted
	 * OR
	 * FALSE if it has to stay in the list
	 */
	public void cleanTheList(List<Node> nodeList);

}
