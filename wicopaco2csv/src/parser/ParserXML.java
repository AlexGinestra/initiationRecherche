package parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import filters.FiltersStatistics;
import filters.globalRejector.GlobalRejectionFilter;
import filters.globalRejector.RollbackFilter;
import filters.localRejector.LocalRejectionFilter;
import filters.localRejector.NumberRejector;
import filters.purifier.PurifierFilter;
import filters.purifier.SentencePurifier;
import filters.purifier.SpecialCaracterPurifier;

public class ParserXML {

	private List<PurifierFilter> purifiers;
	private List<LocalRejectionFilter> localRejectors;
	private List<GlobalRejectionFilter> globalRejectors;
	private CsvFileWriter writer;
	
	public ParserXML(CsvFileWriter w) {
		purifiers = new ArrayList<PurifierFilter>();
		localRejectors = new ArrayList<LocalRejectionFilter>();
		globalRejectors = new ArrayList<GlobalRejectionFilter>();

		writer = w;
	}
	
	
	public void addPurifier(PurifierFilter... cas) {
		for(PurifierFilter c : cas) {
			purifiers.add(c);
		}
	}
	
	
	public void addLocalRejector(LocalRejectionFilter... fil) {
		for(LocalRejectionFilter f : fil) {
			localRejectors.add(f);
		}
	}
	
	public void addGlobalRejector(GlobalRejectionFilter... fil) {
		for(GlobalRejectionFilter f : fil) {
			globalRejectors.add(f);
		}
	}
	
	
	
	/*
	 * return the content of an attributes attri in the Node n if it exists
	 * else return null
	 */
	public static String getAttributeContent(NamedNodeMap n, String attri) {
		if(n == null || attri == null) {
			return null;
		}
		for(int i = 0 ; i < n.getLength() ; i++) {
			if(n.item(i).getNodeName().equals(attri)) {
				return n.item(i).getTextContent();
			}
		}
		return null;
	}
	
	
	
	/*
	 * traite le fichier voulu
	 */
	public void parser() throws IOException {
		
		/* ouverture du fichier xml en entre */
		//Document document = ParserXML.getDocumentTraversal("../alex/doss.nosync/wico_v1.xml");
		Document document = ParserXML.getDocumentTraversal("../alex/truc.xml");
		DocumentTraversal traversal = (DocumentTraversal) document;
		if(traversal == null) {
			System.out.println("erreur");
			System.exit(0);
		}
		
		/* variables de stockage de lecture => ecriture */
		Map<String, String> map = new HashMap<String, String>();
		
		/* debut de la lecture */
		NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		Node n = iterator.nextNode();
	
		StringBuilder strB = new StringBuilder(), strA = new StringBuilder(), strC = new StringBuilder();
		
		
		
		
		if(n.getNodeName().contentEquals("modifs")) {
			NodeList nList = n.getChildNodes(); //var temp
			
			ArrayList<Node> nodeList = new ArrayList<Node>(); //contains <modif> items that are going to be treated		
						
			//add all the <modif> nodes in the nodeList that will be treated
			for(int i = 0 ; i < nList.getLength()-1 ; i++) {				
				if(nList.item(i).getNodeName().equals("modif")) {
					nodeList.add(nList.item(i));
				}
			}
			
			//clean the node list that have to be treated
			for(GlobalRejectionFilter f : globalRejectors) {
				f.cleanTheList(nodeList);
			}
					
			//treat the node list that will be in the output file
			for(Node node : nodeList) {
				boolean hasToBeAddedInDB = true;
				/* apply local rejectorfilters */
				for(LocalRejectionFilter f : localRejectors) {
					if(f.hasToBeRemoved(node)) {
						hasToBeAddedInDB = false;
						break;
					}
				}
				
				if(hasToBeAddedInDB) {
					/* apply a purification on the case, then add it to the output file */
					traiterModif(node, strB, strA, strC, map);
				}
			}
			
		}
		
		//fermeture writer
		writer.close();
	}
	
	
	
	/*
	 * treat the <modif> tag contents
	 */
	public void traiterModif(Node node, StringBuilder strBefore, StringBuilder strAfter, StringBuilder strComments, Map<String, String> map) throws IOException {
		
		strBefore.delete(0, strBefore.length());
		strAfter.delete(0, strAfter.length());
		strComments.delete(0, strComments.length());
		
		/* Recupere l'attribut commentaire */
		NamedNodeMap attributes = node.getAttributes();
		for(int j = 0 ; j < attributes.getLength() ; j++) {
			if(attributes.item(j).getNodeName().equals("wp_comment")) {
				strComments.append(attributes.item(j).getTextContent());
			}
		}
		
		NodeList nSousList = node.getChildNodes();
		/* Parcours les enfants de modif */
		for(int j = 0 ; j < nSousList.getLength() ; j++) {
			Node nTempBefAft = nSousList.item(j);
			if(nTempBefAft.getNodeName().equals("before")) {
				strBefore.append(nTempBefAft.getTextContent());
			}
			else if(nTempBefAft.getNodeName().equals("after")) {
				strAfter.append(nTempBefAft.getTextContent());
			}
		}
		//on ajoute dans le csv
		if(caster(strBefore, strAfter, strComments, map)) {
			writer.write(map);
			map = new HashMap<String, String>();
			strBefore.delete(0, strBefore.length());
			strAfter.delete(0, strAfter.length());
			strComments.delete(0, strComments.length());
		}	
	}
	

	/*
	 * return a Document : usefull for the XMLparsing
	 */
	public static Document getDocumentTraversal(String fileName) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document docATraiter = null;
		try {
			DocumentBuilder loader = factory.newDocumentBuilder();
			docATraiter = loader.parse(fileName);
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return docATraiter;
	}
	
	
	
	/*
	 * apply the cast on the parameters
	 */
	public boolean caster(StringBuilder before, StringBuilder after, StringBuilder comments, Map<String,String> map) {
		for(PurifierFilter c : purifiers) {
			if(!c.cast(before, after, comments)) {
				return false;
			}
		}
		map.put("before", before.toString());
		map.put("after", after.toString());
		map.put("comments", comments.toString());
		return true;
	}
	
	
	
	/*
	 * print the statistics of the filters
	 */
	private void printStatistics() {
		for(FiltersStatistics f : globalRejectors) {
			f.printStatistics();
		}
		for(FiltersStatistics f : localRejectors) {
			f.printStatistics();
		}
		for(FiltersStatistics f : purifiers) {
			f.printStatistics();
		}
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		
		/* creation fichier csv de sortie et du writer */
		File file = null; 
		
		if(args.length > 0) {
			file = new File(args[0]); 
		}
		else {
			file = new File("sortie.csv"); 
		}
		
		//test if the file already exist
		if(file.exists()) {
			System.out.println("le fichier existe deja");
			System.exit(0);
		}
		
		//create the different column for the CSV file
		String[] titles = { "before" , "after" , "comments"};
		CsvFileWriter writer = new CsvFileWriter(file, '\t', titles);
		
		//add the writer to the parser
		ParserXML parser = new ParserXML(writer);
		
		Character[] specChar = {'*','/','#','$','�'};
		List<Character> specialCharacters = Arrays.asList(specChar);
		
		
		//adding differents casters
		parser.addPurifier(new SentencePurifier());
		parser.addPurifier(new SpecialCaracterPurifier(specialCharacters));
		
		//adding differents localRejector
		parser.addLocalRejector(new NumberRejector());
		
		
		//adding differents globalRejector
		parser.addGlobalRejector(new RollbackFilter());
		
		//start the treatment
		parser.parser();
		
		//print the statistics
		parser.printStatistics();
	}
	
}


