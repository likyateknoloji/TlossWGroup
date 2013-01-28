package com.likya.tlossw.transform;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.namespace.NamespaceContext;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;



public class YADOM {

    public static Document getDocument(String file) throws Exception {
    	Document doc = null;
	    // Step 1: create a DocumentBuilderFactory
	     DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

	    // Step 2: create a DocumentBuilder
	     DocumentBuilder db = dbf.newDocumentBuilder();
        //System.out.print("PATH = " + ss.getAbsolutePath()); 
	    // Step 3: parse the input file to get a Document object
	     try { 
	    	 doc = db.parse(new FileInputStream(file)); 
	     }
	     catch(FileNotFoundException fnf) {
	     System.out.print(" Dosya okumada hata olustu :" + fnf.getMessage());
	     }

	     return doc;
	   }
    
  public static void dump(Document doc) {
    dumpLoop((Node) doc, "");
  }

  public static void dumpLoop(Node node, String indent) {
	    switch (node.getNodeType()) {
	    case Node.CDATA_SECTION_NODE:
	      System.out.println(indent + "CDATA_SECTION_NODE");
	      break;
	    case Node.COMMENT_NODE:
	      System.out.println(indent + "COMMENT_NODE");
	      break;
	    case Node.DOCUMENT_FRAGMENT_NODE:
	      System.out.println(indent + "DOCUMENT_FRAGMENT_NODE");
	      break;
	    case Node.DOCUMENT_NODE:
	      System.out.println(indent + "DOCUMENT_NODE");
	      break;
	    case Node.DOCUMENT_TYPE_NODE:
	      System.out.println(indent + "DOCUMENT_TYPE_NODE");
	      break;
	    case Node.ELEMENT_NODE:
	      System.out.print(indent + "ELEMENT_NODE");
	      System.out.println(" : "+node.getNodeName());
	      break;
	    case Node.ENTITY_NODE:
	      System.out.println(indent + "ENTITY_NODE");
	      break;
	    case Node.ENTITY_REFERENCE_NODE:
	      System.out.println(indent + "ENTITY_REFERENCE_NODE");
	      break;
	    case Node.NOTATION_NODE:
	      System.out.println(indent + "NOTATION_NODE");
	      break;
	    case Node.PROCESSING_INSTRUCTION_NODE:
	      System.out.println(indent + "PROCESSING_INSTRUCTION_NODE");
	      break;
	    case Node.TEXT_NODE:
	      if( node.getTextContent().toString().trim().compareTo("") != 0 ) {
	        System.out.print(indent + "TEXT_NODE");
	        System.out.println(" : "+node.getTextContent());
	      }
	      break;
	    default:
	      System.out.println(indent + "Unknown node");
	      break;
	    }

	    NodeList list = node.getChildNodes();
	    for (int i = 0; i < list.getLength(); i++){
	      dumpLoop(list.item(i), indent + "   ");
	    }
	  }
  
  public static void printXml(Document doc) {
	    printXmlLoop((Node) doc, "", "");
	  }
  
  public static void printXmlLoop(Node node, String indent, String closedTag) {
    switch (node.getNodeType()) {
    case Node.CDATA_SECTION_NODE:
      System.out.println(indent + "CDATA_SECTION_NODE");
      break;
    case Node.COMMENT_NODE:
      System.out.println(indent + "COMMENT_NODE");
      break;
    case Node.DOCUMENT_FRAGMENT_NODE:
      System.out.println(indent + "DOCUMENT_FRAGMENT_NODE");
      break;
    case Node.DOCUMENT_NODE:
      System.out.println(indent + "DOCUMENT_NODE");
      break;
    case Node.DOCUMENT_TYPE_NODE:
      System.out.println(indent + "DOCUMENT_TYPE_NODE");
      break;
    case Node.ELEMENT_NODE:
      System.out.print(indent + "<");
      System.out.print(node.getNodeName());
      System.out.println(">");
      closedTag = "</"+node.getNodeName()+">";
      break;
    case Node.ENTITY_NODE:
      System.out.println(indent + "ENTITY_NODE");
      break;
    case Node.ENTITY_REFERENCE_NODE:
      System.out.println(indent + "ENTITY_REFERENCE_NODE");
      break;
    case Node.NOTATION_NODE:
      System.out.println(indent + "NOTATION_NODE");
      break;
    case Node.PROCESSING_INSTRUCTION_NODE:
      System.out.println(indent + "PROCESSING_INSTRUCTION_NODE");
      break;
    case Node.TEXT_NODE:
      if( node.getTextContent().toString().trim().compareTo("") != 0 ) {
        System.out.print(indent);
        System.out.println(node.getTextContent()+closedTag);
      }
      break;
    default:
      System.out.println(indent + "Unknown node");
      break;
    }

    NodeList list = node.getChildNodes();
    for (int i = 0; i < list.getLength(); i++){
    	printXmlLoop(list.item(i), indent + "   ", closedTag);
    }
  }
}


class MyErrorHandler implements ErrorHandler {
  public void warning(SAXParseException e) throws SAXException {
    show("Warning", e);
    throw (e);
  }

  public void error(SAXParseException e) throws SAXException {
    show("Error", e);
    throw (e);
  }

  public void fatalError(SAXParseException e) throws SAXException {
    show("Fatal Error", e);
    throw (e);
  }

  private void show(String type, SAXParseException e) {
    System.out.println(type + ": " + e.getMessage());
    System.out.println("Line " + e.getLineNumber() + " Column " + e.getColumnNumber());
    System.out.println("System ID: " + e.getSystemId());
  }
}