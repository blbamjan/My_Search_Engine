import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.regex.Pattern;

import Utilities.BooleanOperations;
import Utilities.Constants;
import Utilities.FileContentProcessing;
import Utilities.Stemmer;

public class AssignmentThreeBooleanQuery extends FileContentProcessing {

	private BooleanOperations booleanOperations = new BooleanOperations();
	private Map<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> finalInvertedMap = new HashMap<String, List<AssignmentTwoInvertedFile.DocumentFrequency>>();

	public void processBooleanQuery() throws Exception {

		processDocuments();
		/* Taking Boolean query input */
		String q;
		//while(true) {
			
			 System.out.println("Enter Boolean Query to search documents (All terms including brackets should be separated by space):");
			 System.out.print("\n");
			Scanner in = new Scanner(System.in);
			q = in.nextLine();
			
			/* Removing stop words and stemming the query */
			String clean_query = cleanQuery(q);
			//System.out.println(clean_query);
			
			List<Integer> result = executeQuery(clean_query);
			
			System.out.println("Your Query Output:");
			System.out.print("[");
			for(int i =0; i<result.size(); i++) {
				System.out.print(result.get(i));
				if(i!=result.size()-1) {
					System.out.print(",");
				}
			}
			System.out.print("]");
			System.out.print("\n");
			
		//}
	}

	private List<Integer> executeQuery(String clean_query) throws Exception {
		// tokenize query
		// retrieve document id list for each term
		String[] tokens = Pattern.compile(" ").split(clean_query);
		Queue<Object> origQuery = new LinkedList<Object>(Arrays.asList(tokens));
		/*for(Object values: origQuery){
			System.out.println(values);
		}*/
		return compute(origQuery);

	}

	private List<Integer> compute(Queue<Object> origQuery) throws Exception {
		List<Integer> result = null;
		Queue<Object> activeQuery = new LinkedList<Object>();
		Queue<Object> consumeQuery = new LinkedList<Object>();

		boolean consume = false;
		boolean consumed = false;
		boolean inParen = false;

		while (!origQuery.isEmpty()) {

			Object elm = origQuery.poll();
//System.out.println(elm);
			if (consumed) {
				activeQuery.add(elm);
			} else if ("(".equals(elm)) {
				activeQuery.addAll(consumeQuery);
				consumeQuery.clear();
				consumeQuery.add(elm);
				inParen = true;
			} else {

				consumeQuery.add(elm);

				if (inParen) {
					if (")".equals(elm)) {
						inParen = false;
						consume = true;
					}
				} else {
					if (consumeQuery.contains("not")
							|| consumeQuery.contains("NOT")) {
						if (consumeQuery.size() == 4)
							consume = true;
					} else {
						if (consumeQuery.size() == 3) {
							consume = true;
						} else if (origQuery.peek() == null) {
							consume = true;
						}
					}
				}

			}

			if (consume) {
				activeQuery.add(consume(consumeQuery));

				consumeQuery.clear();
				consume = false;
				consumed = true;
			}
		}

		if (activeQuery.size() > 1) {
			result = compute(activeQuery);
		} else {
			result = (List<Integer>) activeQuery.poll();
		}

		return result;
	}

	private List<Integer> consume(Queue<Object> consumeQuery) {
		List<Integer> left = null;
		List<Integer> right = null;
		Object dummy = null;
		String operator = null;
		List<Object> ops = new LinkedList<Object>();
		boolean l = false;
		boolean r = false;
		boolean isAnd = false;
		boolean isNot = false;

		while (!consumeQuery.isEmpty()) {
			if ("(".equals(consumeQuery.peek())
					|| ")".equals(consumeQuery.peek())) {
				consumeQuery.remove();
			} else if (booleanOperations.isOperator(consumeQuery.peek())) {
				operator = (String)consumeQuery.poll();
				if ("AND".equalsIgnoreCase(operator)) {
					isAnd = true;
				} else if ("OR".equalsIgnoreCase(operator)) {
					isAnd = false;
				} else if ("NOT".equalsIgnoreCase(operator)) {
					isNot = true;
				}
			} else if (!l) {
				dummy = consumeQuery.poll();
				
				if (dummy instanceof String) {
					
					left = getDocumentIdList(dummy);
					//System.out.println(left);
				} else {
					left = (List<Integer>)dummy;
				}
				l = true;
			} else if (!r) {
				dummy = consumeQuery.poll();
				
				if (dummy instanceof String) {
					right = getDocumentIdList(dummy);
					//System.out.println(right);
				} else {
					right = (List<Integer>)dummy;
				}
				r = true;
			}

		}
		
		if (isNot) {
			right = booleanOperations.notLists(right);
		}
		
		if (isAnd) {
			//System.out.println(left);
			return booleanOperations.intersectLists(left, right);
		} else {
			return booleanOperations.unionLists(left, right);
		}
	}

	private List<Integer> getDocumentIdList(Object left) {
		List<Integer> docIds = new ArrayList<Integer>();

		for (SortedMap.Entry<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> pair : finalInvertedMap
				.entrySet()) {
			if(pair.getKey().equals(left)){
			for (AssignmentTwoInvertedFile.DocumentFrequency entry : pair
					.getValue()) {
				docIds.add(entry.doc_id);
			}
			}
		}

		return docIds;
	}

	private String cleanQuery(String q) throws Exception {
		FileContentProcessing fcp = new FileContentProcessing();
		fcp.createStopWordList(Constants.STOP_WORDS_FILE);
		q = fcp.removeQueryStopWords(q.trim(), Arrays.asList("NOT", "AND", "OR", "not", "and", "or" ));

		Stemmer s = new Stemmer();
		q = s.stemLine(q);
		return q;

	}

	public Map<String, List<AssignmentTwoInvertedFile.DocumentFrequency>> processDocuments()
			throws Exception {

		AssignmentTwoInvertedFile runAssignmentTwo = new AssignmentTwoInvertedFile();
		
		finalInvertedMap = runAssignmentTwo.makeInvertedFile();
		
		return finalInvertedMap;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub

		AssignmentThreeBooleanQuery runAssignmentThree = new AssignmentThreeBooleanQuery();
		try {
			long startTime = System.currentTimeMillis();
			System.out.println("Running Assignment Three to output Boolean Query Result."
							+ "\n");

			runAssignmentThree.processBooleanQuery();

			long endTime = System.currentTimeMillis();

			System.out.println("That took " + (endTime - startTime)
					+ " milliseconds" + "\n");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
