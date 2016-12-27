package com.is.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConnectionPool {

	static Map<String,String> params = new LinkedHashMap<>();
	static String fileName;
	static String fileNamePath = "connection/"; 

	public static Map<String, String> loadCfg(String Name) throws FileNotFoundException {
		// reader file config
		try {

			fileName = fileNamePath.concat(Name + ".xml");

			File f = new File(fileName);
			if(f.exists() && f.isFile()) {


				// xml reader file config
				File fXmlFile2 = new File(fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile2);

				// load list
				NodeList nList = doc.getElementsByTagName("connection");
				// loop parse
				for (int temp = 0; temp < nList.getLength(); temp++) {

					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						// load param
						NodeList nListParam = eElement.getElementsByTagName("param");
						// load to set param
						for (int tempParam = 0; tempParam < nListParam.getLength(); tempParam++) {
							Node nNodeParam = nListParam.item(tempParam);
							if (nNodeParam.getNodeType() == Node.ELEMENT_NODE) {
								// load element
								Element eElementParam = (Element) nNodeParam;
								// set param
								params.put(eElementParam.getAttribute("name"), eElementParam.getTextContent());
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
		//System.exit(0);
	}
}
