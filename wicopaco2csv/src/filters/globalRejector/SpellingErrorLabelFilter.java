package filters.globalRejector;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import filters.FiltersStatistics;
import parser.CsvFileWriter;
import parser.ParserXML;

public class SpellingErrorLabelFilter extends FiltersStatistics implements GlobalRejectionFilter{
	
	private int[] errorLabels;
	
	public SpellingErrorLabelFilter() {
		if(!loadLabels()) {
			System.out.println("erreur dans le chargement du dictionnaire d\'etiquettes");
			errorLabels = null;
		}
		
	}

	
	
	private boolean loadLabels() {
		/* ouverture du fichier */
		Document document = ParserXML.getDocumentTraversal("../doss.nosync/spelling_error-v3.xml");
		DocumentTraversal traversal = (DocumentTraversal) document;
		if(traversal == null) {
			return false;
		}
		
		/* lecture du fichier */
		NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		Node n = iterator.nextNode();
		
		ArrayList<Integer> listLabel = new ArrayList<Integer>();
		
		if(n.getNodeName().contentEquals("spelling_labels")) {
			NodeList nList = n.getChildNodes(); //var temp

			//add all the <modif> nodes in the nodeList that will be treated
			for(int i = 0 ; i < nList.getLength()-1 ; i++) {				
				if(nList.item(i).getNodeName().equals("annotation")) {
					NodeList nltemp = nList.item(i).getChildNodes();
					
					/* tag <annotation> */
					Node modif_id, label;
					for(int j = 0 ; j < nltemp.getLength()-1 ; j++) {
						if(nltemp.item(j).getNodeName().equals("modif_id")){
							modif_id = nltemp.item(j);
						}
						/* not used for the moment
						 * else if(nltemp.item(j).getNodeName().equals("label")) {
						 
							label = nltemp.item(j);
						}*/
					}
					
					
							
				}
			}
		}
		
		
		
		return true;
	}
	
	
	private boolean isInTheErrorLabels(int label) {
		for(int i = 0 ; i < errorLabels.length ; i++) {
			if(errorLabels[i] == label) {
				return true;
			}
			else if(errorLabels[i] > label) {
				break;
			}
		}
		return false;
	}
	
	@Override
	public void cleanTheList(List<Node> nodeList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createCSVOutput() {
		/* creation fichier csv de sortie et du writer */
		File file = new File("rejectedBySpellingErrorLabelFilter.csv"); 
		
		//test if the file already exist
		if(file.exists()) {
			System.out.println("le fichier SpellingErrorLabelFilter.csv existe deja");
			System.exit(0);
		}
		
		//create the different column for the CSV file
		String[] titles = { "before" , "after", "id"};
		try {
			outputFile = new CsvFileWriter(file, '\t', titles);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void printStatistics() {
		// TODO Auto-generated method stub
		
	}

}
