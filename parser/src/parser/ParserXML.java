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

import filtres.Filter;
import filtres.MyFilter;

public class ParserXML {

	private Filter[] filtres;
	public ParserXML(Filter[] fil) {
		filtres = fil;
	}
	
	public ParserXML(Filter fil) {
		filtres = new Filter[1];
		filtres[0] = fil;
	}

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
	
	
	public boolean filtrer(StringBuilder before, StringBuilder after, StringBuilder comments, Map<String,String> map) {
		for(Filter f : filtres) {
			if(!f.filtre(before, after, comments, map)) {
				return false;
			}
		}
		map.put("before", before.toString());
		map.put("after", after.toString());
		map.put("comments", comments.toString());
		return true;
	}
	
	
	public static void main(String[] args) throws IOException {
		
		ParserXML parser = new ParserXML(new MyFilter());
		
		/* creation fichier csv de sortie et du writer */
		File file = new File("sortie.csv"); 
		if(file.exists()) {
			System.out.println("le fichier existe deja");
			System.exit(0);
		}
		//String[] titles = { "before" , "after" , "comments"};
		String[] titles = { "after"};
		CsvFileWriter writer = new CsvFileWriter(file, '\t', titles);
		
		/* ouverture du fichier xml en entree */
		Document document = ParserXML.getDocumentTraversal("../doss.nosync/wico_v1.xml");
		DocumentTraversal traversal = (DocumentTraversal) document;
		if(traversal == null) {
			System.out.println("erreur");
			System.exit(0);
		}
		/* variables de stockage de lecture => ecriture */
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		Map<String, String> mapTemp = new HashMap<String, String>();
		
		/* debut de la lecture */
		NodeIterator iterator = traversal.createNodeIterator(document.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);
		Node n = iterator.nextNode();
	
		if(n.getNodeName().contentEquals("modifs")) {
			NodeList nList = n.getChildNodes();
			
			/* Parcours les enfants de modfis */
			for(int i = 0 ; i < nList.getLength() ; i++) {				
				Node nTemp = nList.item(i);
				
				if(nTemp.getNodeName().equals("modif")) {
					
					StringBuilder strBefore = new StringBuilder(), strAfter = new StringBuilder(), strComments = new StringBuilder();
					
					/* Recupere l'attribut commentaire */
					NamedNodeMap attributes = nTemp.getAttributes();
					for(int j = 0 ; j < attributes.getLength() ; j++) {
						if(attributes.item(j).getNodeName().equals("wp_comment")) {
							strComments.append(attributes.item(j).getTextContent());
						}
					}
					
					NodeList nSousList = nTemp.getChildNodes();
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
					if(parser.filtrer(strBefore, strAfter, strComments, mapTemp) ) {
						writer.write(mapTemp);
						mapTemp = new HashMap<String, String>();
						strBefore.delete(0, strBefore.length());
						strAfter.delete(0, strAfter.length());
						strComments.delete(0, strComments.length());

					}					
				}
			}
		}
		//fermeture writer
		writer.close();
	}
	
}


