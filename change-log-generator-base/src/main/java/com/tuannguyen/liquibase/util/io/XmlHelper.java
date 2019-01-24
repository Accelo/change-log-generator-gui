package com.tuannguyen.liquibase.util.io;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class XmlHelper {
	public Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder        dBuilder  = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	public void writeDocument(Document document, OutputStream outputStream, int indentSize) throws
	                                                                                        TransformerException {
		StreamResult result = new StreamResult(outputStream);
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer        transformer        = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentSize));
		transformer.transform(new DOMSource(document), result);
	}

	public String prettyXMLString(String xmlString, int indentSize) throws ParserConfigurationException, IOException,
	                                                                       SAXException,
	                                                                       TransformerException,
	                                                                       XPathExpressionException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder        dBuilder  = dbFactory.newDocumentBuilder();
		Document document = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes(
				StandardCharsets.UTF_8)));
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPathExpression xpathExp = xpathFactory.newXPath()
		                                       .compile(
				                                       "//text()[normalize-space(.) = '']");
		NodeList emptyTextNodes = (NodeList)
				xpathExp.evaluate(document, XPathConstants.NODESET);
		for (int i = 0; i < emptyTextNodes.getLength(); i++) {
			Node emptyTextNode = emptyTextNodes.item(i);
			emptyTextNode.getParentNode()
			             .removeChild(emptyTextNode);
		}

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		writeDocument(document, byteArrayOutputStream, indentSize);
		return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
	}
}
