package filters.purifier;

import filters.FiltersStatistics;

public interface PurifierFilter extends FiltersStatistics{

	/*
	 * this method applies directly the cast to each parameters
	 * (it modifies the parameters)
	 * then it returns false if the cast doesn't worked
	 * OR
	 * true if everything is OK
	 */
	public boolean cast(StringBuilder before, StringBuilder after, StringBuilder comments);

}
