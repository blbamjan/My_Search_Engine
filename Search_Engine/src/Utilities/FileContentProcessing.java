package Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import Utilities.Constants;
import Utilities.Stemmer;




public class FileContentProcessing {

	private List<String> stop_words = new ArrayList<String>();
	private HashMap<Integer, String> docMap = new HashMap<Integer, String>();
	
	
	public HashMap<Integer, List<TermFrequencyEntry>> runFileProcessing() throws Exception {
	
	/* Stop words creation */

	createStopWordList(Constants.STOP_WORDS_FILE);

	/* Processing file by removing stop words, stemming */

	String processedContent = processing();

	/* Stemming the source file */
	processedContent = stemming(processedContent);

	buildDocumentMap(processedContent);
	
	/* Term Frequency as per document */
	HashMap<Integer, List<TermFrequencyEntry>> finalTermFrequency = new HashMap<Integer, List<TermFrequencyEntry>>();

	finalTermFrequency= buildTermFrequencyEachDoc();
	
	return finalTermFrequency;
	
}

public void buildDocumentMap(String processedContent) {
		String[] lines = null;
		StringBuffer sb = null;
		String[] parts = Pattern.compile("(?m)^\\.i").split(processedContent);

		for(String part: parts) {
			if (part != null && part.length()!=0) {
				sb = new StringBuffer();
				lines = part.split("\n");
				
				int docid = Integer.valueOf(lines[0].trim());
				
				for(int i = 1; i < lines.length; i++) {
					sb.append(lines[i] + "\n");
				}
				
				docMap.put(docid, sb.toString());
			}
		}
		
	}

	public HashMap<Integer, List<TermFrequencyEntry>> buildTermFrequencyEachDoc() throws Exception {

		HashMap<Integer, List<TermFrequencyEntry>> finalTermFreq = new HashMap<Integer, List<TermFrequencyEntry>>();
		
		HashMap<String, Integer> termFrequency = null;
		List<TermFrequencyEntry> freqs = null;
		String token = null;
		
		for (Map.Entry<Integer, String> pair : docMap.entrySet()) {
			
			String[] lines = pair.getValue().split("\n");
			termFrequency = new HashMap<String, Integer>();
			
			for(String line: lines) {
				
				StringTokenizer t = new StringTokenizer(line);

				while(t.hasMoreTokens()) {
					token = t.nextToken();
					
					if (termFrequency.containsKey(token)) {
						termFrequency.put(token, termFrequency.get(token) + 1);
					} else {
						termFrequency.put(token, 1);
					}
				}
			}

			freqs = new ArrayList<TermFrequencyEntry>();
			
			for (Map.Entry<String, Integer> terms : termFrequency.entrySet()) {
				freqs.add(new TermFrequencyEntry(terms.getKey(), terms.getValue()));
			}
			
			finalTermFreq.put(pair.getKey(), freqs);
			
			}
		return finalTermFreq;
		}

	

	public String stemming(String fileContent) throws Exception {
		StringBuffer sb = new StringBuffer();
		Stemmer s = new Stemmer();

		String[] lines = fileContent.split("\n");
		
		for(String line : lines) {
			sb.append(s.stemLine(line) + "\n");
		}

		return sb.toString();
	}

	public void createStopWordList(String filename) throws Exception {
		Scanner file_Scanner = new Scanner(new File(filename));
		Set<String> stop_words_set = new HashSet<>();
		
		while (file_Scanner.hasNext()) {
			stop_words_set.add(file_Scanner.next().trim());
			
		}
		stop_words.addAll(stop_words_set);

		file_Scanner.close();

	}

	public String processing() throws Exception {

		StringBuffer sb = new StringBuffer();

		// Read the source file
		FileReader inputFile = new FileReader(Constants.SOURCE_FILE);

		BufferedReader bufferReader = new BufferedReader(inputFile);

		String line = "";

		// Tokenising the source file and adding to the arrayList

		while ((line = bufferReader.readLine()) != null) {
		
			sb.append(removeStopWords(line) + "\n");
		}

		bufferReader.close();

		return sb.toString();

	}

	public String removeStopWords(String line) throws Exception {

		StringBuffer sb = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(line);
		String tokenNext = null;
		
		while (tokens.hasMoreTokens()) {
			
			
			tokenNext = tokens.nextToken();
			
			String cleanWords = tokenNext.replaceAll("[&,/\\()'']", "");
			
			
			
			if (!stop_words.contains(cleanWords) && !cleanWords.equals(".")) {
				
				if(cleanWords.contains("-")){
					sb.append(cleanWords + " ");
					for (String retval: cleanWords.split("-")){
						sb.append(retval + " ");
						
				      }
				
				}
				else
				sb.append(cleanWords + " ");
			}

		}
		
		return sb.toString();
	}
	
	public String removeQueryStopWords(String line, List<String> skipList) throws Exception {

		StringBuffer sb = new StringBuffer();
		StringTokenizer tokens = new StringTokenizer(line);
		String tokenNext = null;
		
		while (tokens.hasMoreTokens()) {
			
			
			tokenNext = tokens.nextToken();
			
			String cleanWords = tokenNext.replaceAll("[&,/'']", "");
			
			List<String> stop_words1 = stop_words;
			if (skipList != null) {
				stop_words1.removeAll(skipList);
			}
			
			if (!stop_words1.contains(cleanWords) && !cleanWords.equals(".")) {
				
				if(cleanWords.contains("-")){
					sb.append(cleanWords + " ");
					for (String retval: cleanWords.split("-")){
						sb.append(retval + " ");
						
				      }
				
				}
				else
				sb.append(cleanWords + " ");
			}

		}
		
		return sb.toString();
	}
	
	
public class TermFrequencyEntry implements Comparable {
		
		public String term;
		public int frequency;
		public TermFrequencyEntry(String term, int frequency) {
			this.term = term;
			this.frequency = frequency;
		}
		@Override
		public int compareTo(Object o) {
			return this.term.compareTo(((TermFrequencyEntry)o).term);
		}
	}
}