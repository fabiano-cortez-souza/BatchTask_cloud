package com.tratamento.vmd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.is.utility.*;

public class Tratamento {

	public static String Commands, Command, Param, checkMsg;
	public static String[] paramField, allResponse;
	public static String[][][] arrayList;
	public static ArrayList<String> setList = new ArrayList<String>();
	public static String paramFile, paramName, paramDriver, paramType, paramString, paramUser, paramPass, paramTimeloop, paramTimestart = null;
	private static Scanner scanner;
	public static String checkContinue = "OK";
	public static int setStep = 0;
	public static String response = "";

	public static void main(final String[] args) {

		String paramLoop, paramSeconds;

		try {
			paramSeconds = args[1];
			paramLoop = "1";
		} catch (ArrayIndexOutOfBoundsException e) {
			paramSeconds = "1";
			paramLoop = "0";
		}
		
		final int close = Integer.parseInt(paramLoop);
		int x = Integer.parseInt(paramSeconds);
		final Timer t = new Timer();
		t.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				System.out.println("START NOW: " + new Date());
				new Tratamento();
				Tratamento.readerFileCfg(args[0]);
				if(close == 0) t.cancel();
			}

		}, 0, x*1000 );
	}

	static void readerFileCfg(String fileName) {
		// reader file config
		try {
			System.out.println(fileName);
			// xml reader file config
			File fXmlFile2 = new File(fileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile2);

			// load list
			NodeList nList = doc.getElementsByTagName("list");
			// loop parse
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;
					// load param
					NodeList nListParam = eElement.getElementsByTagName("param");
					// load to set param
					loadParam(nListParam);
					// get file content data
					System.out.println(paramFile);
					// load list send content
					if(paramType.equals("jdbc")){
						// define query file
						String content = setContentFile(paramFile);
						// print file content
						System.out. println("------------------------ QUERY ------------------------");
						//print date
						currentDate();
						System.out.println(content);
						// replace content for variable
						content = replaceComand(content, nListParam);
						System.out.println("-------------------- QUERY REPLACE --------------------");
						//print date
						currentDate();
						System.out.println(content);
						arrayList = loadJdbc(content);
					}else if(paramType.equals("file")){

						arrayList = FileGeneration.loadFile(paramFile, paramString, paramField);

					}
					// loop comand to set list commands
					NodeList nListOper = eElement.getElementsByTagName("action");
					// loop data
					for (int i = 1; i < arrayList.length; i++) {
						allResponse = new String[99];
								
						if(arrayList[i][1][0] != null){
							// set check continue OK next data
							checkContinue = "OK";
							System.out.println("----- <DATA BEGIN> ----- " + i);
							
							
							// LOOP COMAND
							for (int tempOper = 0; tempOper < nListOper.getLength(); tempOper++) {
								//check Continue
								if(checkContinue.equals("OK")){
									//print date
									currentDate();
									System.out.print("STEP: ----------------- > ");
									System.out.println(tempOper);
									
									Node nNodeOper = nListOper.item(tempOper);
									if (nNodeOper.getNodeType() == Node.ELEMENT_NODE) {
										Element eElementOper = (Element) nNodeOper;
										//System.out.println("Oper : " + eElementOper.getAttribute("type") + " , script : " + eElementOper.getTextContent() + " param : " + eElementOper.getAttribute("param"));
										Commands = eElementOper.getTextContent();
										Param = eElementOper.getAttribute("param");
										
										Commands = setContentFile(Commands);
										Commands = replaceComand(Commands, nListParam);
										//print date
										currentDate();
										// for loop parameter
										System.out.print("PARAM DATA = ");
										for (int j = 1; j < arrayList[i].length; j++) {
											// variable is not null
											if (!arrayList[i][j][0].equals(null)) {
												System.out.print(arrayList[i][j][0] + " = " + arrayList[i][j][1] + " , ");
												// set replace variable
												String contentReplace = "${"+arrayList[i][j][0]+"}";
												// replace variable to value
												Commands = Commands.replace(contentReplace,arrayList[i][j][1]);
											}
										}
										// replace command last response
										Commands = Commands.replace("${getLastResponse}",response);
										Commands = replaceResponse(Commands);
										Commands = replaceSet(Commands);
										
										System.out.println("END PARAM DATA;");
										//print date
										currentDate();
										// command send
										System.out.println("Type = " + eElementOper.getAttribute("type"));
										//print date
										currentDate();
										// param 
										System.out.println("PARAM = " + eElementOper.getAttribute("param"));
										//print date
										currentDate();
										System.out.println("COMMAND = " + Commands);
										//print date
										currentDate();
										System.out.println("CONTINUE = " + eElementOper.getAttribute("continue"));
										//print date
										currentDate();
										System.out.println("CHECK = " + eElementOper.getAttribute("check"));
										//print date
										currentDate();
										// exec command and set response
										if (eElementOper.getAttribute("type").equals("command")) sendCommand(Commands);
										// exec command and set response
										if (eElementOper.getAttribute("type").equals("sqlplus")) sendSqlplus(Commands);
										// exec command and set response
										if (eElementOper.getAttribute("type").equals("file")) fileGeneration(Commands);
										// command and set response
										if (eElementOper.getAttribute("type").equals("print")) response = Commands;
										// set
										if (eElementOper.getAttribute("type").equals("set")) setList.add(Param + "=" + Commands);
										// sleep
										if (eElementOper.getAttribute("type").equals("sleep")) sleep(Commands);
										// select
										if (eElementOper.getAttribute("type").equals("select"))  commandSelect(Commands, Param);
										// update, delete, insert param
										if (eElementOper.getAttribute("type").equals("update") ||
												eElementOper.getAttribute("type").equals("delete") ||
													eElementOper.getAttribute("type").equals("insert") ) commandUpdate(Commands, Param);

										// print response
										System.out.println("RESPONSE = " + response);
										allResponse[tempOper]=response;
										// check continue
										checkContinue = eElementOper.getAttribute("continue");
										// check Response
										if(  checkResponse(response, eElementOper.getAttribute("check")) ) {
											// set 
											//setStep = 0;
											// set step command and checkContinue OK
											//setStep = Integer.parseInt(eElementOper.getAttribute("nextStep"));
											if( setStep > 0){
												tempOper = (setStep-1) ;
												checkContinue = "OK";
												//print date
												currentDate();
												System.out.println("MSG_CHECK = " + checkMsg);
												//print date
												currentDate();
												System.out.println("SET_STEP = " + setStep);
											}
										}
										//print date
										currentDate();
										System.out.println(checkContinue);
									}
								}
							}
							System.out.println("----- <DATA END> ----- " + i);
							// clear list set
							setList.clear();
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.exit(0);
	}


	// replace param
	public static String replaceComand (String Comands, NodeList nListParam){
		for (int tempInfo = 0; tempInfo < nListParam.getLength(); tempInfo++) {
			Node nNodeInfo = nListParam.item(tempInfo);
			if (nNodeInfo.getNodeType() == Node.ELEMENT_NODE) {
				Element eElementInfo = (Element) nNodeInfo;
				//System.out.println(eElementInfo.getAttribute("name"));
				String contentReplace = "${"+eElementInfo.getAttribute("name")+"}";
				Comands = Comands.replace(contentReplace,eElementInfo.getTextContent());
			}
		}

		return Comands;
	}
	
	public static String replaceSet(String Comands) {
		// Display values.
		
		for (String value : setList) {
		    String[] arraySet = value.split("=", 2); 
		    Comands = Comands.replace("${" + arraySet[0] + "}" ,arraySet[1]);
		}
		return Comands;
	}
	
	// replace all responses
	public static String replaceResponse (String Comands){
		for (int v = 0; v < allResponse.length; v++) {
			try {
					if( allResponse[v] != null )
						Comands = Comands.replace("${getResponse["+v+"]}",allResponse[v]);

			} catch (NullPointerException e) {
				continue;
			}
		}
		return Comands;
	}
	
	// load set param
	public static void loadParam (NodeList nListParam) throws FileNotFoundException, DOMException{
		for (int tempParam = 0; tempParam < nListParam.getLength(); tempParam++) {
			Node nNodeParam = nListParam.item(tempParam);
			if (nNodeParam.getNodeType() == Node.ELEMENT_NODE) {
				// load element
				Element eElementParam = (Element) nNodeParam;
				// set param connection
				if(eElementParam.getAttribute("name").equals("connection")){
					
					for (Map.Entry<String,String> param : ConnectionPool.loadCfg(eElementParam.getTextContent()).entrySet()) {
						//System.out.println(param.getKey() + " = " + param.getValue());
						setParam(param.getKey(), param.getValue());
			        }
				}
				// set param
				setParam(eElementParam.getAttribute("name"), eElementParam.getTextContent());
			}
		}
	}

	// replace param
	public static String[][][] loadJdbc (String content) throws Exception{

		DBQuery.JDBC_DRIVER = paramDriver;
		DBQuery.DB_URL = paramString;
		DBQuery.USER = paramUser;
		DBQuery.PASS = paramPass;
		DBQuery.QUERY = content;
		DBQuery.consult();

		return DBQuery.aryList;

	}
	// set param
	private static void setParam(String param, String value){

		if( param.equals("file") ) 
			paramFile = value;
		if( param.equals("driver") ) 
			paramDriver = value;
		if( param.equals("name") ) 
			paramName = value;
		if( param.equals("type") ) 
			paramType = value;
		if( param.equals("string") )
			paramString = value;
		if( param.equals("user") )
			paramUser = value;
		if( param.equals("pass") )
			paramPass = value;
		if( param.equals("timeloop") )
			paramTimeloop = value;
		if( param.equals("timestart") )
			paramTimestart = value;
		if( param.equals("field") ) 
			paramField = value.split(";");
	}

	private static void sendCommand(String command){

		String s = null;
		response = "";

		try {
			// using the Runtime exec method:
			Process p = Runtime.getRuntime().exec(new String[]{"bash","-c",command});

			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				response = response.concat(s);
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				response = response.concat(s);
			}
		}
		catch (IOException e) {
			response = "exception happened - here's what I know: ";
			e.printStackTrace();
		}

		//System.out.println(response);
	}


	private static void sendSqlplus(String command) throws IOException{

		response = "";
		String s, file, sql, unId = null;
		unId = FileGeneration.getUnId();
		// file name
		file = "log/" + unId + ".sql";
		sql = "SET PAGESIZE 0 FEEDBACK OFF VERIFY OFF HEADING OFF ECHO OFF \n";
		sql = "SET SERVEROUTPUT ON \n";
		sql = sql.concat(command);
		sql = sql.concat(";");
		sql = sql.concat("\n");
		sql = sql.concat("commit;");
		sql = sql.concat("\n");
		sql = sql.concat("exit;");
		sql = sql.concat("\n");
		// generate file tmp
		FileGeneration.file(sql, file , false);

		try {
			// using the Runtime exec method:
			Process p = Runtime.getRuntime().exec("sqlplus -s "+ Param +" @"+ file);

			BufferedReader stdInput = new BufferedReader(new
					InputStreamReader(p.getInputStream()));

			BufferedReader stdError = new BufferedReader(new
					InputStreamReader(p.getErrorStream()));

			// read the output from the command
			while ((s = stdInput.readLine()) != null) {
				response = response.concat(s);
			}

			// read any errors from the attempted command
			while ((s = stdError.readLine()) != null) {
				response = response.concat(s);
			}
		}
		catch (IOException e) {
			response = "exception happened - here's what I know: ";
			e.printStackTrace();
		}
		// delete file tmp
		FileGeneration.del(file);
	}

	private static void fileGeneration(String command) throws IOException{
		boolean increment = false;
		String[] split = command.split("[\\[\\]]");
		if(split[5].equals("true")) increment = true;
		response=FileGeneration.file(split[1], split[3], increment);
	}
	
	// ---------------- sleep
	public static void sleep(String value) throws InterruptedException {
			Thread.sleep(setToNumber(value) * 1000);
			response = "OK";
	}
	// select command
		public static void commandSelect (String content, String connection) throws Exception{
			commandDbLoad(connection);
			DBQuery.JDBC_DRIVER = paramDriver;
			DBQuery.DB_URL = paramString;
			DBQuery.USER = paramUser;
			DBQuery.PASS = paramPass;
			DBQuery.QUERY = content;
			response = DBQuery.select();
		}
	// update command
	public static void commandUpdate (String content, String connection) throws Exception{
		commandDbLoad(connection);
		DBQuery.JDBC_DRIVER = paramDriver;
		DBQuery.DB_URL = paramString;
		DBQuery.USER = paramUser;
		DBQuery.PASS = paramPass;
		DBQuery.QUERY = content;
		response = Integer.toString(DBQuery.update());
	}
	// load connection command
	public static void commandDbLoad (String connection) throws Exception{
		for (Map.Entry<String,String> param : ConnectionPool.loadCfg(connection).entrySet()) {
			setParam(param.getKey(), param.getValue());
        }
	}
	
	public static void currentDate() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		Calendar cal = Calendar.getInstance();
		System.out.print(dateFormat.format(cal.getTime()));
	}

	public static boolean checkResponse(String response, String check) {
		boolean resp=false;
		try {
			String[] split, validate;
			setStep=0;
			split = check.split("[\\[\\]]");
			for (int i = 1; i < split.length; i++) {
				// check response false
				if(!resp){
					// clear var
					validate = null;
					// set validade split
					validate = split[i].split("[{}]",3);

/*					System.out.print("Response: ");
					System.out.print(validate[0]);
					System.out.print("  ");
					System.out.print(validate[2]);
					System.out.print(" SET STEP : ");
					System.out.println(validate[1]);*/
					
					// check oper validate
					switch(validate[0]){
					case "!=" :
						// check diff check in response
						if(!response.equals(validate[2])) {
							//set resp true
							resp=true;
							// set msg contains
							checkMsg=validate[2];
							// set next step for msg
							setStep=Integer.parseInt(validate[1]);
						}
						break; //optional
					case "=" :
						// check equals check in response
						if(response.equals(validate[2])) {
							//set resp true
							resp=true;
							// set msg contains
							checkMsg=validate[2];
							// set next step for msg 
							setStep=Integer.parseInt(validate[1]);
						}
						break; //optional
					case "-" :
						// check > check in response
						if((setToNumber(response) <= setToNumber(validate[2]))) {
							//set resp true
							resp=true;
							// set msg contains
							checkMsg=validate[2];
							// set next step for msg
							setStep=Integer.parseInt(validate[1]);
						}
						break;
					case "+" :
						// check > check in response
						if((setToNumber(response) >= setToNumber(validate[2]))) {
							//set resp true
							resp=true;
							// set msg contains
							checkMsg=validate[2];
							// set next step for msg
							setStep=Integer.parseInt(validate[1]);
						}
						break;
						//You can have any number of case statements.
					default : //Optional
						// check contains check in response	
						if(response.contains(validate[2])) {
							//set resp true
							resp=true;
							// set msg contains
							checkMsg=validate[2];
							// set next step for msg
							setStep=Integer.parseInt(validate[1]);
						}
					}
				}
				i++;
			}
		} catch (NumberFormatException e) {

		}
		return resp;
	}
	// ---------------- set string to Number
	public static int setToNumber(String value) {
		int valueNumber;
		try {
			valueNumber = Integer.parseInt(value);
		} catch (NumberFormatException e) {
			valueNumber = 0;
		}
		return valueNumber;
	}
	// set content file existe
	public static String setContentFile(String content) throws FileNotFoundException {
		try {
			File f = new File(content);
			if(f.exists() && f.isFile()) {
				scanner = new Scanner(new File(content));
				content = scanner.useDelimiter("\\Z").next();
			}
		} catch (FileNotFoundException e) {
			
		}
		return content;
	} 
}
