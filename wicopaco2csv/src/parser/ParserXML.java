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

import caster.PurifierFilter;
import caster.SentencePurifier;
import caster.SpecialCaracterPurifier;
import filter.RejectionFilter;
import filter.NumberRejector;

public class ParserXML {

	private List<PurifierFilter> casters;
	private List<RejectionFilter> filters;
	private CsvFileWriter writer;
	
	public ParserXML(CsvFileWriter w) {
		casters = new ArrayList<PurifierFilter>();
		filters = new ArrayList<RejectionFilter>();
		writer = w;
	}
	
	
	public void addPurifier(PurifierFilter... cas) {
		for(PurifierFilter c : cas) {
			casters.add(c);
		}
	}
	
	
	public void addRejector(RejectionFilter... fil) {
		for(RejectionFilter f : fil) {
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
						
			for(int i = 0 ; i < nList.getLength()-1 ; i++) {				
				if(nList.item(i).getNodeName().equals("modif")) {
					
					boolean hasToBeAddedInDB = true;
					/* applique les filtres sur le contenu */
					for(RejectionFilter f : filters) {
						if(f.hasToBeRemoved(nList.item(whiteList.get(i)))) {
							hasToBeAddedInDB = false;
							break;
						}
					}
					
					if(hasToBeAddedInDB) {
						/* Parcours les enfants de modif */
						traiterModif(nList.item(i), strB, strA, strC, map);
					}
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
		for(PurifierFilter c : casters) {
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
		
		Character[] specChar = {'*','/','#','$','�'};
		List<Character> specialCharacters = Arrays.asList(specChar);
		
		
		//adding differents casters
		parser.addPurifier(new SentencePurifier());
		parser.addPurifier(new SpecialCaracterPurifier(specialCharacters));
		
		//adding differents filters
		parser.addRejector(new NumberRejector());
		
		
		//start the treatment
		parser.parser();
	}
	
}

