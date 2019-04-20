package filters.purifier;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import filters.FiltersStatistics;
import parser.CsvFileWriter;

public class SentencePurifier extends FiltersStatistics implements PurifierFilter{
	
	private int charTreated;
	private int charDeleted;
	
	public SentencePurifier() {
		charTreated = 0;
		charDeleted = 0;
	}
	
	/*
	 * return true if it's a sentence separator
	 */
	private boolean isSentenceSeparator(char c) {
		if(c == '.' || c == ':' || c == ';') {
			return true;
		}
		return false;
	}
	
	
	/*
	 * Cast to only one sentence if the StringBuilder are composed of multi sentences
	 * (non-Javadoc)
	 * @see casters.Caster#Caster(java.lang.StringBuilder, java.lang.StringBuilder, java.lang.StringBuilder)
	 */
	@Override
	public boolean cast(StringBuilder before, StringBuilder after, StringBuilder comments) {
		if(before == null || after == null || comments == null) {
			return false;
		}
		
		charTreated += before.length(); //data for stat
		
		int startIndex = 0, firstPoint = 0, lastPointB = 0, lastPointA = 0;
		
		//trouve le debut de la phrase
		for(startIndex = 0 ; startIndex < before.length() && startIndex < after.length() ; startIndex++) {
			
			if(before.charAt(startIndex) != after.charAt(startIndex)) {
				break;
			}
			if(isSentenceSeparator(before.charAt(startIndex))){
				firstPoint = startIndex;
			}
		}
		
		//aller jusqu a la fin de la phrase 
		for(lastPointB = startIndex ; lastPointB < before.length() && !isSentenceSeparator(before.charAt(lastPointB)); lastPointB++); 
		for(lastPointA = startIndex ; lastPointA < after.length() && !isSentenceSeparator(after.charAt(lastPointA)); lastPointA++); 

		charDeleted += before.length()-(lastPointB - firstPoint); //data for stat
		
		if(outputOn) {
			map = new HashMap<String,String>();
			// supprime les phrases suivantes
			if(before.length() > lastPointB) {
				map.put("end before", before.substring(lastPointB+1, before.length()));
				before.delete(lastPointB+1, before.length());
			}
			if(after.length() > lastPointA) {
				map.put("end after", after.substring(lastPointA+1, after.length()));
				after.delete(lastPointA+1, after.length());
			}
			
			//supprime les phrases precedentes 
			if(firstPoint != 0) {
				map.put("beginning before", before.substring(0, firstPoint+1));
				map.put("beginning after", after.substring(0, firstPoint+1));
				after.delete(0, firstPoint+2);
				before.delete(0, firstPoint+2);
			}
			
			if(!map.isEmpty()) {
				try {
					outputFile.write(map);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else {
			// supprime les phrases suivantes
			if(before.length() > lastPointB) {
				before.delete(lastPointB+1, before.length());
			}
			if(after.length() > lastPointA) {
				after.delete(lastPointA+1, after.length());
			}
			
			//supprime les phrases precedentes 
			if(firstPoint != 0) {
				after.delete(0, firstPoint+2);
				before.delete(0, firstPoint+2);
			}
		}
		
		return true;
	}

	@Override
	public void printStatistics() {
		System.out.println("The sentece purifier treated " + charTreated + " char, and deleted " + charDeleted +" char.");		
	}

	@Override
	public void createCSVOutput() {
		/* creation fichier csv de sortie et du writer */
		File file = new File("rejectedBySentencePurifier.csv"); 
		
		//test if the file already exist
		if(file.exists()) {
			System.out.println("le fichier rejectedBySentencePurifier.csv existe deja");
			System.exit(0);
		}
		
		//create the different column for the CSV file
		String[] titles = { "beginning before", "end before", "beginning after", "end after"};
		try {
			outputFile = new CsvFileWriter(file, '\t', titles);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}

}
