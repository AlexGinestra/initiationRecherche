package filtres;


public interface Filter {

	/*
	 * this method applies to each StringBuilder the filter they are create for
	 * then it returns false if the filter doesn't worked
	 * OR
	 * true if everything is OK
	 */
	public boolean filtre(StringBuilder before, StringBuilder after, StringBuilder comments);

}
