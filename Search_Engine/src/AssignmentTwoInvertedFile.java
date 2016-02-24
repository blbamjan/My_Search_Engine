import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import Utilities.Constants;
import Utilities.FileContentProcessing;



public class AssignmentTwoInvertedFile extends FileContentProcessing {
	
	
	public Map<String, List<DocumentFrequency>>  makeInvertedFile() throws Exception {
		
		
		/* Term Frequency as per document */
		HashMap<Integer, List<TermFrequencyEntry>> finalTermFrequency = new HashMap<Integer, List<TermFrequencyEntry>>();

		finalTermFrequency= runFileProcessing();

		/*Building Inverted Map of terms and corresponding doc_id, frequency pairs along with calculating DF*/
		List<TermFrequencyEachDoc> termDocIdFreq = new ArrayList<TermFrequencyEachDoc>() ;
		termDocIdFreq = buildTermWiseList(finalTermFrequency);
		
		Map<String, List<DocumentFrequency>> finalInvertedMap = new HashMap<String, List<DocumentFrequency>>();
		for(int i=0; i< termDocIdFreq.size(); i++) {
			//System.out.println(termDocIdFreq.get(i).term + ":" + termDocIdFreq.get(i).doc_id + ":" + termDocIdFreq.get(i).frequency);
			if (!finalInvertedMap.containsKey(termDocIdFreq.get(i).term)) {
				finalInvertedMap.put(termDocIdFreq.get(i).term, new ArrayList<DocumentFrequency>());
			}
			finalInvertedMap.get(termDocIdFreq.get(i).term).add(new DocumentFrequency(termDocIdFreq.get(i).doc_id, termDocIdFreq.get(i).frequency));
		}
		
		/*for (SortedMap.Entry<String, List<DocumentFrequency>> pair : finalInvertedMap.entrySet()) {
			 
			System.out.print(pair.getKey());
			for(DocumentFrequency entry : pair.getValue()) {
				System.out.print("[" + entry.doc_id + "," + entry.frequency + "]");
				
			}
			
			System.out.println("\n");	
		}*/
		
		/*Writing final output to the file*/
		writeIntoOutputFile(finalInvertedMap);
		return finalInvertedMap;
		}
	
		private void writeIntoOutputFile(Map<String, List<DocumentFrequency>> finalMap) throws Exception{
		
		File file =new File(Constants.TWO_OUTPUT_FILE);
		if(!file.exists()){
			file.createNewFile();
		}
		
		FileWriter OutputWriter = new FileWriter(Constants.TWO_OUTPUT_FILE);
	
		for (SortedMap.Entry<String, List<DocumentFrequency>> pair : finalMap.entrySet()) {
			 
			OutputWriter.write("'" + pair.getKey() + "' ==> [" + pair.getValue().size() + ", ["); 
			//Collections.sort(pair.getValue());
			for(DocumentFrequency entry : pair.getValue()) {
				OutputWriter.write("[" + entry.doc_id + "," + entry.frequency + "]");
				if (pair.getValue().indexOf(entry) < pair.getValue().size() - 1) {
					OutputWriter.write(", ");
				}
			}
			OutputWriter.write("]]");
			OutputWriter.write("\n");	
		}
		OutputWriter.flush();
		OutputWriter.close();
	 
	}
	
	
	private List<TermFrequencyEachDoc> buildTermWiseList(HashMap<Integer, List<TermFrequencyEntry>> finalTermFrequency) throws Exception {
		
		 List<TermFrequencyEachDoc> termDocIdFreq = new ArrayList<TermFrequencyEachDoc>() ;
		
		
		for (SortedMap.Entry<Integer, List<TermFrequencyEntry>> pair : finalTermFrequency.entrySet()) {
			
			
			for(TermFrequencyEntry entry : pair.getValue()) {
				//System.out.println(entry.term + ":" + entry.frequency + "\n");
				TermFrequencyEachDoc tDf = new TermFrequencyEachDoc(entry.term, pair.getKey(),entry.frequency);
				termDocIdFreq.add(tDf);
				
			}
		}
		
		
		return termDocIdFreq;
		
		
		
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AssignmentTwoInvertedFile runAssignmentTwo = new AssignmentTwoInvertedFile();
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Running Assignment Two to output Inverted File."+ "\n");

			runAssignmentTwo.makeInvertedFile();

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) + " milliseconds"+ "\n");
			System.out.println("Output File is located as /Search_Engine/OutputFile/AssignmentTwoOutput.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	

class TermFrequencyEachDoc {
		
		public String term;
		public int doc_id;
		public int frequency;
		
		public TermFrequencyEachDoc(String term, int doc_id, int frequency) {
			this.term = term;
			this.doc_id = doc_id;
			this.frequency = frequency;
		}
		
		
}

class DocumentFrequency {
	
	
	public int doc_id;
	public int frequency;
	
	public DocumentFrequency(int doc_id, int frequency) {
		this.doc_id = doc_id;
		this.frequency = frequency;
	}
}



}
