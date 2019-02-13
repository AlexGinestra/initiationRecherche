package filtres;


public interface Filter {

	/*
	 * return false if the filter doesn't worked
	 */
	public boolean filtre(StringBuilder before, StringBuilder after, StringBuilder comments);

}
