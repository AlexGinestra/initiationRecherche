package filtres;

import java.util.Map;

public interface Filter {

	public boolean filtre(StringBuilder before, StringBuilder after, StringBuilder comments, Map<String, String> map);

}
