package parser;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import caster.Caster;
import caster.SentenceCaster;
import caster.SpecialCharacterCaster;
import filter.Filter;
import filter.NumberFilter;

public class ParserXML {

	private List<Caster> casters;
	private List<Filter> filters;
	private CsvFileWriter writer;
	
	public ParserXML(CsvFileWriter w) {
		casters = new ArrayList<Caster>();
		filters = new ArrayList<Filter>();
		writer = w;
	}
	
	
	public void addCaster(Caster... cas) {
		for(Caster c : cas) {
			casters.add(c);
		}
	}
	
	
	public void addFilter(Filter... fil) {
		for(Filter f : fil) {
			filters.add(f);
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
		
		ArrayList<Integer> whiteList = new ArrayList<Integer>();
		
		
		if(n.getNodeName().contentEquals("modifs")) {
			NodeList nList = n.getChildNodes();
			
			
			/* creation de la white list */
			int j, k;
			Node currentN, nextN;
			for(int i = 0 ; i < nList.getLength()-1 ; i++) {				
				if(nList.item(i).getNodeName().equals("modif")) {
					j = 0;
					k = 1;
					currentN = nList.item(i);
					
					//look for the next "modif" node
					while(i+k < nList.getLength() && !nList.item(i+k).getNodeName().equals("modif")) {
						k++;
					}
					if(i+k == nList.getLength()) {
						whiteList.add(i);
						break;
					}
					nextN = nList.item(i+k);
					
					// look if there is a step backward in the correction
					while(i+k < nList.getLength() && getAttributeContent(currentN.getAttributes(), "wp_after_rev_id").equals(
									getAttributeContent(nextN.getAttributes(), "wp_before_rev_id"))) {
						j = k;
						currentN = nList.item(i+j);
						
						//look for the next "modif" node
						while(i+k < nList.getLength() && !nList.item(i+k).getNodeName().equals("modif")) {
							k++;
						}
						if(i+k < nList.getLength()) {
							nextN = nList.item(i+k);
						}
					}
					if(j == 0) {
						whiteList.add(i);
					}
					else {
						i += k;
					}
				}
			}
			
			
			/* applique les filtres sur la white list */
			for(int i = whiteList.size()-1 ; i > 0  ; i--) {
				for(Filter f : filters) {
					if(f.hasToBeRemoved(nList.item(whiteList.get(i)))) {
						whiteList.remove(i);
						break;
					}
				}
			}
			
			
			/* Parcours les enfants de modfis */
			for(int i : whiteList) {				
				traiterModif(nList.item(i), strB, strA, strC, map);
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
		if(caster(strBefore, strAfter, strComments, map) ) {
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
		for(Caster c : casters) {
			if(!c.cast(before, after, comments)) {
				return false;
			}
		}
		map.put("before", before.toString());
		map.put("after", after.toString());
		map.put("comments", comments.toString());
		return true;
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
		
		List<Character> specialCharacters = List.of('*','/','#','$','€');
		
		//adding differents casters
		parser.addCaster(new SentenceCaster());
		parser.addCaster(new SpecialCharacterCaster(specialCharacters));
		
		//adding differents filters
		parser.addFilter(new NumberFilter());
		
		
		//start the treatment
		parser.parser();
	}
	
}


