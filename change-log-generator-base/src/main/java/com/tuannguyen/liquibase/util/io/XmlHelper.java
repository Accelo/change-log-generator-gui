package com.tuannguyen.liquibase.util.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XmlHelper
{
	public Document getDocument(File file) throws ParserConfigurationException, IOException, SAXException
	{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		return dBuilder.parse(file);
	}

	public void writeDocument(Document document, OutputStream outputStream, int indentSize)
			throws IOException
	{
		outputStream.write(prettyXMLDocument(document, indentSize).getBytes());
	}

	public String prettyXMLString(String xml, int indentSize)
			throws IOException, SAXException, ParserConfigurationException
	{

		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = db.parse(new InputSource(new StringReader(xml)));

		return prettyXMLDocument(doc, indentSize);
	}

	public static String prettyXMLDocument(Document document, int indentSize)
			throws IOException
	{
		OutputFormat format = new OutputFormat(document);
		format.setIndenting(true);
		format.setIndent(indentSize);
		format.setLineWidth(Integer.MAX_VALUE);
		Writer outxml = new StringWriter();
		XMLSerializer serializer = new XMLSerializer(outxml, format);
		serializer.serialize(document);

		return outxml.toString();
	}
}
