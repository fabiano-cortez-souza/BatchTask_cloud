package com.is.utility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.UUID;
import java.io.BufferedReader;

public class FileGeneration {
	
	public static String[][][] aryList = null;
	private static String[] nameFields;
	
	public static String file(String text, String fileName, boolean increment) throws IOException {
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		
		text = text.replace("${getTimestamp}", dateFormat.format(cal.getTime()));
		text = text.concat("\n");
		BufferedWriter output = null;
        try {
            File file = new File(fileName);
            output = new BufferedWriter(new FileWriter(file,increment));
            output.write(text);
            return "OK";
        } catch ( IOException e ) {
            e.printStackTrace();
            return "KO";
        } finally {
            if ( output != null ) output.close();
        }
    }
	
	public static boolean del(String fileName) {
	      
	      boolean bool = false;
	      
	      try{
	         // create new file
	    	 File file = new File(fileName);
	         
	         // tries to delete a non-existing file
	         bool = file.delete();
    
	      }catch(Exception e){
	         // if any error occurs
	         e.printStackTrace();
	      }
	      
		return bool;
	   }
	
	public static final  String getUnId(){
	    //generate random UUIDs
	    UUID idOne = UUID.randomUUID();
	    return idOne.toString();
	}
	
	public static void log(String[] args) throws IOException {
		Logger logger = Logger.getLogger("MyLog");  
	    FileHandler fh;  

	    try {  

	        // This block configure the logger with handler and formatter  
	        fh = new FileHandler("C:/Leandro/example.txt");  
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();  
	        fh.setFormatter(formatter);  

	        // the following statement is used to log any messages  
	        logger.info("My first log");  

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  

	    logger.info("Hi How r u?");  
    }
	
	public static String[][][] loadFile(String fileName, String delimiter, String[] paramField) {

		try (BufferedReader br = new BufferedReader(new FileReader(fileName)))
		{
			String sCurrentLine;
			// + 1 not found zero variable
			aryList = new String[15000][paramField.length+1][2];
			
			int i = 0;
			while ((sCurrentLine = br.readLine()) != null) {
				
				String[] parts = sCurrentLine.split(delimiter);

				for (int l = 0; l < paramField.length; l++) {
					if(l < paramField.length) {
						
						try {
							aryList[i+1][l+1][1] = parts[l];
						} catch (ArrayIndexOutOfBoundsException e) {
							aryList[i+1][l+1][1] =  "";
						}
						
						try {
							aryList[i+1][l+1][0] = paramField[l];
						} catch (ArrayIndexOutOfBoundsException e) {
							aryList[i+1][l+1][0] = "";
						}

					}
				}
				i++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		} 
		return aryList;
	}

	public static String[] getNameFields() {
		return nameFields;
	}

	public static void setNameFields(String[] nameFields) {
		FileGeneration.nameFields = nameFields;
	}
}
