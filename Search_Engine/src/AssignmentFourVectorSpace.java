import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.regex.Pattern;

import Utilities.Constants;
import Utilities.FileContentProcessing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

public class AssignmentFourVectorSpace {
	
	private Map<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> finalInvertedMap = new HashMap<String, List<AssignmentTwoInvertedFile.DocumentFrequency>>();
	private Map<String, Double> idf = new HashMap<String, Double>();
	private Map<String, List<TermDocwiseTf>> term_tf = new HashMap<String, List<TermDocwiseTf>>();
	private Map<Integer,Double> doc_wt = new HashMap<Integer, Double>();
	private FileContentProcessing QueryProcessing = new FileContentProcessing();
	private Map<Integer,List<String>> queryMap = new HashMap<Integer, List<String>>();
	private Map<Integer,List<Integer>> queryRelMap = new HashMap<Integer, List<Integer>>();
	private Map<Integer,List<DocwiseCosineValues>> rankedQueryWeightsMap = new TreeMap<Integer, List<DocwiseCosineValues>>();
	private Map<Integer,QueryPrecisionRecall> queryPrecisionRecall = new TreeMap<Integer, QueryPrecisionRecall>();
	
	public void processVectorSpaceModel() throws Exception {
		
		processDocuments();
		//Dictionaries Mapping for idf and tf
		idfMapping();
		tfMapping();
		docWeightMapping();
		
		
		//Removing stop words from Queries
		QueryProcessing.createStopWordList(Constants.STOP_WORDS_FILE);
		String processedQuery = queryProcessing();
		
		/* Stemming the source file */
		processedQuery = QueryProcessing.stemming(processedQuery);
		
		//Building Term List in Queries
		buildQueryList(processedQuery); 
		
		//Calculating Cosine measures for queries and ranking
		calculateQueryWeights();
		
		//Mapping of cran.rel file 
		queryRelMapping();
		
		//Precision and Recall Calculation
		precisionRecallCalculation();
		
		//Outputting final values to the file
		writeFinalOutput();
	
		/*	DecimalFormat df = new DecimalFormat("0.0000");   

		for (Map.Entry<Integer,QueryPrecisionRecall> pair : queryPrecisionRecall.entrySet()) {
			
			System.out.print(pair.getKey() + "-->");
			System.out.print(df.format(pair.getValue().precision) + "," + df.format(pair.getValue().recall) + ";"  );
			
			System.out.print("\n");
		}*/
	}
	
	private void precisionRecallCalculation() {
		// TODO Auto-generated method stub
		
		double precision = 0.0;
		double recall = 0.0;
		
		double total_retrieved_doc = 0;
		double total_relevent_doc = 0;
				
		for (Map.Entry<Integer,List<DocwiseCosineValues>> pair : rankedQueryWeightsMap.entrySet()) {
			int	no_rel_doc = 0;
			//Total document retrieved count
			total_retrieved_doc = pair.getValue().size();
			//Total document relevent count
			//System.out.println(pair.getKey()+"-->");
			if(queryRelMap.get(pair.getKey())!=null){
				total_relevent_doc = queryRelMap.get(pair.getKey()).size();
				
				for(DocwiseCosineValues entry : pair.getValue()){
					if(queryRelMap.get(pair.getKey()).contains(entry.doc_id)){
						no_rel_doc++;
					}
				}
				
				/*System.out.print(no_rel_doc+",");
				System.out.print(total_retrieved_doc+",");
				System.out.print(total_relevent_doc+",");
				System.out.print("\n");*/
				precision = (no_rel_doc/total_retrieved_doc);
				recall = (no_rel_doc/total_relevent_doc);
			}
			else {
				precision = 0.0;
				recall = 0.0;					
			} 
			
			queryPrecisionRecall.put(pair.getKey(), new QueryPrecisionRecall(precision, recall));
	}
	
}
	
	private void queryRelMapping() throws NumberFormatException, IOException {
		// TODO Auto-generated method stub
		
		// Read the source file
		FileReader inputFile = new FileReader(Constants.QUERY_RELEVENCY_FILE);

		BufferedReader bufferReader = new BufferedReader(inputFile);

		String line = "";
		List<QueryRelDocs> queryRelList = new ArrayList<QueryRelDocs>();
		// Tokenising the source file and adding to the arrayList

		while ((line = bufferReader.readLine()) != null) {
			
			String[] parts = line.split("\\s+");
			
			int queryid = Integer.valueOf(parts[0].trim());
			int doc_id = Integer.valueOf(parts[1].trim());
			queryRelList.add(new QueryRelDocs(queryid, doc_id));
			}
		
		List<Integer> rel_docid_list= null;
		rel_docid_list= new ArrayList<Integer>();
		HashMap<Integer, List<Integer>> queryRelMapTemp = null;
		queryRelMapTemp = new HashMap<Integer, List<Integer>>();
		for (QueryRelDocs entry : queryRelList) {
			
			if(queryRelMapTemp.containsKey(entry.query_id)){
				rel_docid_list.add(entry.doc_id);
			}
			else{
				rel_docid_list= new ArrayList<>(entry.doc_id);
				rel_docid_list.add(entry.doc_id);
			}
			
			queryRelMapTemp.put(entry.query_id, rel_docid_list);	
		}
		
		queryRelMap.putAll(queryRelMapTemp);
		
		bufferReader.close();
}

	private void writeFinalOutput() throws IOException {
		// TODO Auto-generated method stub
			File file =new File(Constants.OUTPUT_FILE_ASSIGNMENTFOUR);
			if(!file.exists()){
				file.createNewFile();
			}
			
			FileWriter OutputWriter = new FileWriter(Constants.OUTPUT_FILE_ASSIGNMENTFOUR);
			
			   

			
			OutputWriter.write("Query Outputs formatted as: \n Query --> \n Number of Documents Retrieved:Number \n Precision:value \n Recall:value"
					+ " \n Cosine Values: \n Doc_Id:Cosine Value \n\n\n");
					
			
			DecimalFormat df = new DecimalFormat("0.0000");
			for (Map.Entry<Integer, List<DocwiseCosineValues>> pair : rankedQueryWeightsMap.entrySet()) {
				 
				OutputWriter.write("Q:" + pair.getKey() + " --> \n");
				OutputWriter.write("Number of Documents Retrieved:" + pair.getValue().size() + "\n");
				OutputWriter.write("Precision:" + df.format(queryPrecisionRecall.get(pair.getKey()).precision) + "\n");
				OutputWriter.write("Recall:" + df.format(queryPrecisionRecall.get(pair.getKey()).recall) + "\n");
				
				OutputWriter.write("Cosine Values: \n");
				for(DocwiseCosineValues entry : pair.getValue()) {
					OutputWriter.write(entry.doc_id + ":" + df.format(entry.cosine_val));
					if (pair.getValue().indexOf(entry) < pair.getValue().size() - 1) {
						OutputWriter.write(",  ");
					}
					if ((pair.getValue().indexOf(entry) %100)==0 && pair.getValue().indexOf(entry)!=0) {
						OutputWriter.write("\n");
					}
				}
				
				OutputWriter.write("\n\n");	
			}
			OutputWriter.flush();
			OutputWriter.close();
		 
		
		
	}

	private void calculateQueryWeights() {
		// TODO Auto-generated method stub
		Map<Integer,List<TermDocwiseCosineValues>> queryTermwiseWeightMap = new HashMap<Integer, List<TermDocwiseCosineValues>>();
		
		//Calculating individual querywise term cosine values in each document
		List<TermDocwiseCosineValues> docwise_wt = null;
		for (Map.Entry<Integer,List<String>> pair : queryMap.entrySet()) {
			
			
			Double cosineMeasure = 0.0;
			docwise_wt = new ArrayList<TermDocwiseCosineValues>();
			
			for(String entry : pair.getValue()){
				Double term_idf = idf.get(entry);	
					if(!term_tf.containsKey(entry))
						cosineMeasure += 0.0;
					else
					{
						List<TermDocwiseTf> docwise_tf = new ArrayList<TermDocwiseTf>();
						
						docwise_tf = term_tf.get(entry);
						for(TermDocwiseTf pair1 : docwise_tf){
							Double firstFactor = (1/doc_wt.get(pair1.doc_id));
							//Cosine value calculation by formula
							docwise_wt.add(new TermDocwiseCosineValues(pair1.doc_id, entry, firstFactor*pair1.tf * term_idf ));
						}
						
					}
			}
			
			queryTermwiseWeightMap.put(pair.getKey(), docwise_wt);
		}
		
		
		//Adding up document id wise cosine values for each queries 
		HashMap<Integer, Double> docCosineValues = null;
		List<DocwiseCosineValues> DocwiseCosineList = null;
		
		Map<Integer,List<DocwiseCosineValues>> queryWeightsMap = new HashMap<Integer, List<DocwiseCosineValues>>();
		for (Map.Entry<Integer,List<TermDocwiseCosineValues>> pair : queryTermwiseWeightMap.entrySet()) {
			docCosineValues = new HashMap<Integer, Double>();
			  
			//Adding document wise cosine values for each query 
				for(TermDocwiseCosineValues entry : pair.getValue()){
				if(docCosineValues.containsKey(entry.doc_id))
					docCosineValues.put(entry.doc_id,docCosineValues.get(entry.doc_id)+entry.cosine_val);
				else
					docCosineValues.put(entry.doc_id,entry.cosine_val);
				
				}
			
			DocwiseCosineList = new ArrayList<DocwiseCosineValues>();
			
			for (Map.Entry<Integer, Double> cos_values : docCosineValues.entrySet()) {
				DocwiseCosineList.add(new DocwiseCosineValues(cos_values.getKey(), cos_values.getValue()));
			}
			
			//Sorting Documents as per the cosine values in each query
			Collections.sort(DocwiseCosineList);
			queryWeightsMap.put(pair.getKey(), DocwiseCosineList);
			
		}
		
		//Ordering queries by inserting into Treemap
		rankedQueryWeightsMap = new TreeMap<Integer, List<DocwiseCosineValues>>(queryWeightsMap);
			
	}

	private void buildQueryList(String processedContent) {
		String[] lines = null;
		StringBuffer sb = null;
		String[] parts = Pattern.compile("(?m)^\\.i").split(processedContent);

		Map<Integer,String> queryStringMap = new HashMap<Integer, String>();
		for(String part: parts) {
			if (part != null && part.length()!=0) {
				sb = new StringBuffer();
				lines = part.split("\n");
				
				int queryid = Integer.valueOf(lines[0].trim());
				
				for(int i = 1; i < lines.length; i++) {
					sb.append(lines[i] + "\n");
				}
				
				queryStringMap.put(queryid, sb.toString().replaceAll(".w", ""));
			}
		}
		
		String token = null;
		List<String> term_list = null;
		
		
		for (Map.Entry<Integer, String> pair : queryStringMap.entrySet()) {
			
			String[] lines1 = pair.getValue().split("\n");
			 term_list = new ArrayList<String>();
			 
			for(String line: lines1) {
				
				StringTokenizer t = new StringTokenizer(line);

				while(t.hasMoreTokens()) {
					token = t.nextToken();
					term_list.add(token);
					
				}
			}
			
			queryMap.put(pair.getKey(),term_list);
		
		}
	}
	
	
	private String queryProcessing() throws Exception {
		// TODO Auto-generated method stub
		
		StringBuffer sb = new StringBuffer();

		// Read the source file
		FileReader inputFile = new FileReader(Constants.QUERY_SOURCE_FILE);

		BufferedReader bufferReader = new BufferedReader(inputFile);

		String line = "";
		// Tokenising the source file and adding to the arrayList

		while ((line = bufferReader.readLine()) != null) {
		
			sb.append(QueryProcessing.removeStopWords(line) + "\n");
		}

		bufferReader.close();

		return sb.toString();
		
	}

	public void tfMapping() throws Exception {
		//Term and DocIdwise TF mapping
				List<TermDocwiseTf> termdoctf = null;
				
				for (Map.Entry<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> pair : finalInvertedMap.entrySet()) {
					termdoctf = new ArrayList<TermDocwiseTf>();
					for(AssignmentTwoInvertedFile.DocumentFrequency entry : pair.getValue()) {
							
						Double ft = 1 + Math.log(entry.frequency);
						//OutputWriter.write("[" + entry.doc_id + "," +  + "]");
						termdoctf.add(new TermDocwiseTf(entry.doc_id,ft));
					}
					term_tf.put(pair.getKey(), termdoctf);
				}
		
	}
	
	private void docWeightMapping() {
		// TODO Auto-generated method stub
		
		for (Map.Entry<String, List<TermDocwiseTf>> pair : term_tf.entrySet()) {
			 
			for(TermDocwiseTf entry : pair.getValue()) {
				
				if(doc_wt.containsKey(entry.doc_id))
					doc_wt.put(entry.doc_id, doc_wt.get(entry.doc_id) + Math.pow(entry.tf, 4));
				else
					doc_wt.put(entry.doc_id,Math.pow(entry.tf, 4));
				
			}
			
		}
		
	}
	
	public void idfMapping() throws Exception {
		//Termwise IDF mapping
				int Nf = Constants.TOTAL_NO_OF_DOCS;
				for (Map.
						
						Entry<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> pair : finalInvertedMap.entrySet()) {
					Double idf_value = Math.log(1+Nf/pair.getValue().size());
					idf.put(pair.getKey(),idf_value );
				}
				
	}
	
	public Map<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> processDocuments()
			throws Exception {

		AssignmentTwoInvertedFile runAssignmentTwo = new AssignmentTwoInvertedFile();
		
		finalInvertedMap = runAssignmentTwo.makeInvertedFile();
		
		return finalInvertedMap;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		AssignmentFourVectorSpace runAssignmentFour = new AssignmentFourVectorSpace();
		try {
			long startTime = System.currentTimeMillis();
			System.out
					.println("Running Assignment Four to output Vector Space Model Output."
							+ "\n");

			runAssignmentFour.processVectorSpaceModel();

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime)
					+ " milliseconds" + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	class TermDocwiseTf {		
		
		public int doc_id;
		public double tf;
		
		public TermDocwiseTf(int doc_id, double tf) {
			this.doc_id = doc_id;
			this.tf = tf;
		}
	}
	
	
public class DocwiseCosineValues implements Comparable<DocwiseCosineValues> {		
		
		private int doc_id;
		private	 double cosine_val;
		
		public DocwiseCosineValues(int doc_id, double cosine_val) {
			this.doc_id = doc_id;
			this.cosine_val = cosine_val;
		}
		
		public double getCosineVal() {
			return cosine_val;
		}
		
		@Override
	    public int compareTo(DocwiseCosineValues o) {
	        //Descending
			return new Double(o.cosine_val).compareTo( cosine_val);
	    }
	    @Override
	    public String toString() {
	        return String.valueOf(cosine_val);
	    }
		
		
	}
	
class TermDocwiseCosineValues {		
		
		public int doc_id;
		public String term;
		public double cosine_val;
		
		public TermDocwiseCosineValues(int doc_id, String term, double cosine_val) {
			this.doc_id = doc_id;
			this.term = term;
			this.cosine_val = cosine_val;
		}
	}

class QueryRelDocs {		
	
	public int query_id;
	public int doc_id;
	
	
		public QueryRelDocs(int query_id, int doc_id) {
			this.query_id = query_id;
			this.doc_id = doc_id;
		}
		
	}

class QueryPrecisionRecall {		
	
	public double precision;
	public double recall;
	
	
		public QueryPrecisionRecall(double precision, double recall) {
			this.precision = precision;
			this.recall = recall;
		}
		
	}

}