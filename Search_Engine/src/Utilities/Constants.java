package Utilities;

import java.io.File;
import java.io.IOException;

public class Constants {
	
	
	public static String startingPath;
	 static {
	    String path = null;
	    try {
	      path = new File(".").getCanonicalPath();
	    } catch (IOException e) {
	      // do whatever you have to do
	    	e.printStackTrace();
	    }
	    startingPath = path;
	 }
	 
	public static final String STOP_WORDS_FILE = startingPath + "//InputFile//stop.words";
	public static final String SOURCE_FILE = startingPath + "//InputFile//cran.all.1400";
	public static final String OUTPUT_FILE = startingPath + "//OutputFile//AssignmentOneOutput.txt";
	
	
	public static final String TWO_OUTPUT_FILE = startingPath + "//OutputFile//AssignmentTwoOutput.txt";
	
	public static final Integer TOTAL_NO_OF_DOCS = 1400;
	
	public static final String QUERY_SOURCE_FILE = startingPath + "//InputFile//cran.qry";
	public static final String OUTPUT_FILE_ASSIGNMENTFOUR = startingPath + "//OutputFile//AssignmentFourOutput.txt";
	public static final String QUERY_RELEVENCY_FILE = startingPath + "//InputFile//cranqrel";
}

//F:\Google Drive\UNLV\Installed_Project\Search_Engine