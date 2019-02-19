package com.tuannguyen.liquibase.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlHelper
{
	public Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	public void writeDocument(Document document, OutputStream outputStream, int indentSize)
			throws Exception
	{
		outputStream.write(prettyXMLDocument(document, indentSize).getBytes());
	}

	public String prettyXMLString(String xml, int indentSize)
			throws Exception
	{

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));

		return prettyXMLDocument(doc, indentSize);
	}

	public static String prettyXMLDocument(Document document, int indentSize)
			throws IOException, XPathExpressionException, TransformerException
	{
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
		StreamResult result = new StreamResult(byteArrayOutputStream);
		// write the content into xml file
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indentSize));
		transformer.transform(new DOMSource(document), result);

		return byteArrayOutputStream.toString(StandardCharsets.UTF_8.name());
	}
}
