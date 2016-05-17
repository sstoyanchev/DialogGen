package com.interactions;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.ArrayList;

import javassist.convert.Transformer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.SAXException;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

public class OpenDialFormXML {
	

    
	String NLUtype;
	
	String greeting;

	String [] fName;

	String [] fType;
	
	String [] mutuallyExclusive;

	String [] mustFollow;
	
	String [] nlg;
	
	String [] b_optional;

	
	public String getGreeting() {
		return greeting;
	}
	
	public String getNLUtype() {
		return NLUtype;
	}

	public void setNLUtype(String nLUtype) {
		NLUtype = nLUtype;
	}


	public void setGreeting(String greeting) {
		this.greeting = greeting;
	}
	
	public String[] getfName() {
		return fName;
	}

	public void setfName(String[] fName) {
		this.fName = fName;
	}


	public String[] getfType() {
		return fType;
	}


	public void setfType(String[] fType) {
		this.fType = fType;
	}

	public String[] getMutuallyExclusive() {
		return mutuallyExclusive;
	}


	public void setMutuallyExclusive(String[] mutuallyExclusive) {
		this.mutuallyExclusive = mutuallyExclusive;
	}


	public String[] getMustFollow() {
		return mustFollow;
	}


	public void setMustFollow(String[] mustFollow) {
		this.mustFollow = mustFollow;
	}


	public String[] getNlg() {
		return nlg;
	}


	public void setNlg(String[] nlg) {
		this.nlg = nlg;
	}


	public String[] getB_optional() {
		return b_optional;
	}


	public void setB_optional(String[] b_optional) {
		this.b_optional = b_optional;
	}


	public String toString()
	{
		String s = "";
		for(int i = 0; i < fName.length; i++)
		{
			s+=fName[i] + " ";
		}
		
		s+="; b_optional: ";
		if(b_optional!=null)
		for(int i = 0; i < b_optional.length; i++)
		{
			s+=b_optional[i] + ",";
		}
		return s;
	}
	
	
	private final String SLOT_NAMES_ALL_XPATH = "/domain/initialstate/variable[@id='Slots']/value";
	private final String SLOT_NAMES_OPTIONAL_XPATH = "/domain/initialstate/variable[@id='optional']/value";
	private final String SLOT_PAIRS_PRECEDENCE_XPATH = "/domain/initialstate/variable[@id='precedence']/value";
	private final String SLOT_PAIRS_EXCLUSIVE_XPATH = "/domain/initialstate/variable[@id='exclusive']/value";
	
	private final String FILL_SLOT_XML_XPATH = "/domain/model[@trigger='entities']";
	private final String NLG_SLOT_XML_XPATH = "/domain/model[@trigger='a_m' and @type='nlg']/rule";
	private final String FILL_TEMPLATE_FNAME = "src/main/resources/xml/oneSlotFillTemplate.xml";
	private final String NLG_TEMPLATE_FNAME = "src/main/resources/xml/oneSlotNlgTemplate.xml";
	private final String SETTING_TEMPLATE_FNAME = "src/main/resources/xml/nluSettings.xml";
	private final String NLG_INITIAL_GREETING = "/domain/model[@trigger='a_m' and @type='nlg']/rule/case/effect/set[@type='greeting']/@value";

			
    
	
	/**
	 * in java 8 could just use String.join method
	 * @return
	 */
	private String getNames()
	{
		String s = "";
		for(int i = 0; i < fName.length; i++)
		{
			s+=fName[i] ;
			if (i!=fName.length-1) s+=",";
		}
		return"[" +  s + "]";
	}
	
	/**
	 * get list of names that are optional
	 * @return
	 */
	private String getOptionalNames()
	{
		String s = "";
		for(int i = 0; i < b_optional.length; i++)
		{
			if(b_optional[i].equals("YES"))
				 s+="("+fName[i] + ")";
		}
		//hack for the last comma
		s = s.replaceAll("\\)\\(","),(");
		s = s.replaceAll("\\(","");
		s = s.replaceAll("\\)","");
		return"[" +  s + "]";
	}	
	
	/**
	 * get list of names that are optional
	 * @return
	 */
	private String getPrecedencePairs()
	{
		String s = "";
		for(int i = 0; i < mustFollow.length; i++)
		{
			System.out.println("must follow i:"+mustFollow[i]);
			if(!mustFollow[i].equals("NONE"))
				s+="("+ mustFollow[i] + "," + fName[i] + ")";
		}
		s = s.replaceAll("\\)\\(","),(");
		return"[" +  s + "]";
	}		

	/**
	 * get list of names that are optional
	 * @return
	 */
	private String getExclusivePairs()
	{
		String s = "";
		ArrayList<String> added = new ArrayList<String>();
		for(int i = 0; i < mutuallyExclusive.length; i++)
		{
			if(!mutuallyExclusive[i].equals("NONE") && !added.contains(fName[i] ) )
			{
				added.add(mutuallyExclusive[i]);
				s+="(" + fName[i] + "," + mutuallyExclusive[i] + ")";
			}
		}
		s = s.replaceAll("\\)\\(","),(");
		return"[" +  s + "]";
	}
	



	/**
	 * replace node in the dom corresponding to @xpathstr with @val
	 * @param xpathstr
	 * @param val
	 * @return
	 * @throws XPathExpressionException 
	 */
	private void insertNodeValue(Document xmlDocument, String xpathstr, String val) throws XPathExpressionException
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		//read a string value
		//String test = xPath.compile(xpathstr).evaluate(xmlDocument);
		//System.out.println("Found node:" + test); 		
		//read an xml node using xpath
		Node node = (Node) xPath.compile(xpathstr).evaluate(xmlDocument, XPathConstants.NODE);
		
		if(node==null)
		{
			throw new XPathExpressionException("ERROR: SLOT_NAMES xpath not found in template " + xpathstr );
		}
		
		node.setTextContent(val);
		//read a string value
		
		System.out.println("after replace with " + val +  ":" + xPath.compile(xpathstr).evaluate(xmlDocument)); 
		
	}
	
	/**
	 * Inserts ruleDoc into xmlDocument at xpath
	 * @param xmlDocument
	 * @param ruleDoc
	 * @param xpath
	 * @throws XPathExpressionException 
	 */
	private void insertFillOutRules(Document xmlDocument, Document ruleDoc, String xpath) throws XPathExpressionException
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node nodeInDoc = (Node) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODE);
		Node nodeToInsert = (Node) ruleDoc.getDocumentElement();
		if(nodeInDoc==null)
			throw new XPathExpressionException("ERROR:  xpath not found in template " + xpath );
		if(nodeToInsert==null)
			throw new XPathExpressionException("ERROR: nodeToInsert is null "  );
		System.out.println("insertFillOutRules: xpath=" + xpath + " node=" + nodeInDoc.getNodeValue());
		Node newnode = nodeToInsert.cloneNode(true);
		
		    
		xmlDocument.adoptNode(newnode);
		nodeInDoc.appendChild(newnode);
		
	}
	
	/**
	 * remove "type" attribute
	 * @param xmlDocument
	 * @throws XPathExpressionException 
	 */
	private void cleanUpOpenDialForm(Document xmlDocument) throws XPathExpressionException
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node modeltype = (Node) xPath.compile("/domain/model[@type]").evaluate(xmlDocument, XPathConstants.NODE);
		System.out.println("document=" + xmlDocument + "; typeatt = " + modeltype);
		if (modeltype!=null)
		{
			((Element)modeltype).removeAttribute("type");
			System.out.println("removing typeatt");
		}
		Node n = (Node) xPath.compile("/domain/model/rule/case/effect/set[@type]").evaluate(xmlDocument, XPathConstants.NODE);
		System.out.println("document=" + xmlDocument + "; typeatt = " + n);
		if (n!=null)
		{
			((Element)n).removeAttribute("type");
			System.out.println("removing type");
		}
		
	}
	
/**
 *  inserts value into .value attribyte of xpath in document
 * @param xmlDocument
 * @param xpath
 * @param value
 * @throws XPathExpressionException
 */
	private void insertValue(Document xmlDocument,  String xpath, String value) throws XPathExpressionException
	{
		XPath xPath =  XPathFactory.newInstance().newXPath();
		Node nodeInDoc = (Node) xPath.compile(xpath).evaluate(xmlDocument, XPathConstants.NODE);
		
		if(nodeInDoc==null)
			throw new XPathExpressionException("ERROR:  xpath not found in template " + xpath );
		nodeInDoc.setNodeValue(value);
		System.out.println("Setting " + value + " on " + nodeInDoc.getNodeName());
	}	
	
	/**
	 *  inserts value into .value attribyte of xpath in document
	 * @param xmlDocument
	 * @param xpath
	 * @param value
	 * @throws XPathExpressionException
	 */
		private void insertSettings(Document xmlDocument,   Document settingsDoc, String value) throws XPathExpressionException
		{
			XPath xPath =  XPathFactory.newInstance().newXPath();
			Node nodeInDoc = (Node) xPath.compile("/domain").evaluate(xmlDocument, XPathConstants.NODE);
			Node nodeToInsert = (Node) xPath.compile("/nlu/settings[@id='"+value+"']").evaluate(settingsDoc, XPathConstants.NODE);
			
			
			if(nodeInDoc==null)
				throw new XPathExpressionException("ERROR: insertSettings()  xpath not found in template ");
			if(nodeToInsert==null)
				throw new XPathExpressionException("ERROR: insertSettings() nodeToInsert is null for value " + value  );
			
			Node newnode = nodeToInsert.cloneNode(true);
			xmlDocument.adoptNode(newnode);
			nodeInDoc.appendChild(newnode);
		}	
	
	//replace with "slot is {slot}"
	private String getNamesPresentation()
	{
		String s = "";
		for(int i = 0; i < fName.length; i++)
		{
			s+=fName[i] + " is {" + fName[i] + "}";
		}
		return s;

	}

	/**

	 * @param fname
	 * @return
	 * @throws IOException
	 */
	private String readInFile( String fname) throws IOException
	{
		File file = new File(fname);
		FileInputStream fis = new FileInputStream(file);
		byte[] data = new byte[(int) file.length()];
		fis.read(data);
		fis.close();
	
		String str = new String(data, "UTF-8");

		return str;
		
	}
	
	/**
	 * 
	 * opens template file and fills the values
	 * using xml dom library
	 * writes output file to fout
	 * @param fin is an input xml file
	 * @param fout is an output xml file
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws XPathExpressionException 
	 * @throws TransformerException 
	 */
	public void fillTemplate(String fin, String fout) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException, TransformerException
	{
		DocumentBuilderFactory builderFactory =
		        DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		builder = builderFactory.newDocumentBuilder();
		Document xmlDocument = builder.parse(new FileInputStream(fin));
		Document xmlSettingsDoc = builder.parse(new FileInputStream(SETTING_TEMPLATE_FNAME));
		
		System.out.println(this.toString());
		System.out.println("xml doc=" + xmlDocument);
	 
		insertNodeValue(xmlDocument, SLOT_NAMES_ALL_XPATH, getNames() );
		insertNodeValue(xmlDocument, SLOT_NAMES_OPTIONAL_XPATH, getOptionalNames());
		insertNodeValue(xmlDocument, SLOT_PAIRS_PRECEDENCE_XPATH, getPrecedencePairs());
		insertNodeValue(xmlDocument, SLOT_PAIRS_EXCLUSIVE_XPATH, getExclusivePairs());
		
		insertSettings(xmlDocument, xmlSettingsDoc, NLUtype);
		
		insertValue(xmlDocument, NLG_INITIAL_GREETING, greeting);
		
		for(int i = 0; i < fName.length; i++)
		{
			String str = readInFile(FILL_TEMPLATE_FNAME ).replaceAll("#_FIELD_NAME_#", fName[i]).replaceAll("#_FIELD_TYPE_#", fType[i]);
			insertFillOutRules(xmlDocument,  builder.parse(new ByteArrayInputStream(str.getBytes())), FILL_SLOT_XML_XPATH);
		}

		for(int i = 0; i < fName.length; i++)
		{
			String str = readInFile(NLG_TEMPLATE_FNAME).replaceAll("#_FIELD_NAME_#", fName[i]).replaceAll("#_FIELD_NLG_#", nlg[i]);
			insertFillOutRules(xmlDocument,  builder.parse(new ByteArrayInputStream(str.getBytes())), NLG_SLOT_XML_XPATH);
		}
		
		cleanUpOpenDialForm(xmlDocument);
		//write the output file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();		
		DOMSource source = new DOMSource(xmlDocument);
		StreamResult result = new StreamResult(new File(fout));
		transformer.transform(source, result);

	}


}

