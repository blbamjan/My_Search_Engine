import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

import Utilities.FileContentProcessing;
import Utilities.Constants;




public class AssignmentOneTermFrequency extends FileContentProcessing {

		public void runTermFrequency() throws Exception {

		
		/* Term Frequency as per document */
		HashMap<Integer, List<TermFrequencyEntry>> finalTermFrequency = new HashMap<Integer, List<TermFrequencyEntry>>();

		finalTermFrequency= runFileProcessing();
		
		writeIntoOutputFile(finalTermFrequency);
		
	}
	
	private void writeIntoOutputFile(HashMap<Integer, List<TermFrequencyEntry>> finalTermFreqs) throws Exception{
		
		File file =new File(Constants.OUTPUT_FILE);
		if(!file.exists()){
			file.createNewFile();
		}
		
		FileWriter OutputWriter = new FileWriter(Constants.OUTPUT_FILE);
		
		for (SortedMap.Entry<Integer, List<TermFrequencyEntry>> pair : finalTermFreqs.entrySet()) {
			 
			OutputWriter.write("document-id:" + pair.getKey());
			OutputWriter.write(" ; unique-terms:" + pair.getValue().size() + "\n");
			//Collections.sort(pair.getValue()); 
			for(TermFrequencyEntry entry : pair.getValue()) {
				OutputWriter.write(entry.term + ":" + entry.frequency + "\n");
				
			}
			
			OutputWriter.write("\n");	
		}
		OutputWriter.flush();
		OutputWriter.close();
	 
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		AssignmentOneTermFrequency runAssignmentOne = new AssignmentOneTermFrequency();
		try {
			long startTime = System.currentTimeMillis();
			 
			System.out.println("Running Assignment One to output doc_id wise term frequency."+ "\n");
			//System.out.println(startingPath);
			runAssignmentOne.runTermFrequency();

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime) + " milliseconds"+ "\n");
			System.out.println("Output File is located at /Search_Engine/OutputFile/AssignmentOneOutput.txt");
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	

	
}
