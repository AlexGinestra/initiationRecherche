package filter;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class RollbackFilter implements RejectionFilter{
	
	
	public List<String[]>[] listBefAft = null; //list[wordsNumberOfsentenceBefore], String[0] = before, String[1] = after
	
	
	public RollbackFilter() {
		newlist(3); 
		
	}
	
	
	/*
	 * create a new listBefAft with the size in parameter (copy the content if it already exists)
	 */
	private void newlist(int size) {
		if(listBefAft == null) {
			listBefAft = new ArrayList[size];
			for(int i = 0 ; i < size ; i++) {
				listBefAft[i] = new ArrayList<String[]>();
			}
		}
		else {
			List<String[]>[] listTemp = new ArrayList[size];
			for(int i = 0 ; i < size ; i++) {
				if(listBefAft != null) {
					listTemp[i] = listBefAft[i];
				}
				else {
					listTemp[i] = new ArrayList<String[]>();
				}
			}
			listBefAft = listTemp;
		}
	}

	@Override
	public boolean hasToBeRemoved(Node n) {
		// TODO Auto-generated method stub
		return false;
	}

}
