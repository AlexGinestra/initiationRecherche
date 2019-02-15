package filtres;


public interface Filter {

	/*
	 * this method applies directly the filter to each parameters
	 * (it modifies the parameters)
	 * then it returns false if the filter doesn't worked
	 * OR
	 * true if everything is OK
	 */
	public boolean filtre(StringBuilder before, StringBuilder after, StringBuilder comments);

}
