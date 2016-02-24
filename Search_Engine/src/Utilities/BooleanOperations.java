package Utilities;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;




public class BooleanOperations{
	
	private List<Integer> universe = new ArrayList<Integer>();
	
	public BooleanOperations(){
		for(int i=1; i<=1400; i++) {
			universe.add(i);
		}
	}
	
	 public List<Integer>  intersectLists(List<Integer> p1, List<Integer> p2) {
		 List<Integer> answer = new ArrayList<Integer>();

	        for (Integer t : p1) {
	            if(p2.contains(t)) {
	                answer.add(t);
	            }
	        }
	        
	      

	        return answer;
	    }
	/*public List<Integer> intersectLists(List<Integer> p1, List<Integer> p2){
		
		List<Integer> answer = new ArrayList<Integer>() ;
	
			int i =0;
			int j =0;
			//while(i< p1.size() && j< p1.size()){
			for(int values:p1){
				System.out.print(values + ",");
			}
			System.out.print( "\n");
			for(int values:p2){
				System.out.print(values + ",");
			}
			System.out.print( "\n");
			System.out.println(p1.size());
			System.out.println(p2.size());
		while(p1!=null && p2!=null){
					
			if(p1.get(i)==p2.get(j)){
						answer.add(p1.get(i));
						i++;
						j++;
					}
					else if(p1.get(i)<p2.get(j))						
						i++;
					else
						j++;
					
		} 
			
	return 	answer;
	
	}
	*/
	
	public List<Integer> unionLists(List<Integer> p1, List<Integer> p2){
		List<Integer> answer = new ArrayList<Integer>() ;
		
			if (p1 != null && !p1.isEmpty())
				answer.addAll(p1);
			
			if (p2 != null && !p2.isEmpty())
				answer.addAll(p2);
			
	        Set<Integer> s = new LinkedHashSet<Integer>(answer);
	        answer.removeAll(answer);
	        answer.addAll(s);
	        return answer;
	    }

	public List<Integer> notLists(List<Integer> orig) {
		List<Integer> result = new ArrayList<Integer>();
		for (Integer id:universe) {
			if(!orig.contains(id)) {
				result.add(id);
			}
		}
		
		return result;
	}
	
	
	public boolean isOperator(Object val) {
		if (val instanceof String) {
			String val1 = (String)val;
			if ( "NOT".equalsIgnoreCase(val1)
					|| "AND".equalsIgnoreCase(val1)
					|| "OR".equalsIgnoreCase(val1)) {
				return true;
			}
		}
		return false;
	}
	
}