package com.tuannguyen.liquibase.util.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class XmlHelperTest
{
	private XmlHelper xmlHelper;

	@Before
	public void setup()
	{
		xmlHelper = new XmlHelper();
	}

	@Test
	public void getDocument_givenValidFile_shouldReturnCorrectElement()
			throws URISyntaxException, IOException, SAXException, ParserConfigurationException
	{
		File file = new File(getClass().getResource("/prospect.xml").toURI());
		System.out.println(Files.lines(file.toPath()).collect(Collectors.toList()));
		Document document = xmlHelper.getDocument(file);
		assertThat(((Element) document.getElementsByTagName("createView").item(0)).getAttribute("schemaName"),
				equalTo("accelo"));
	}

	@Test
	public void writeDocument_givenValidXML_shouldOutputCorrectly()
			throws Exception
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		// root elements
		Document doc = documentBuilder.newDocument();
		Element rootElement = doc.createElement("company");
		doc.appendChild(rootElement);

		// staff elements
		Element staff = doc.createElement("Staff");
		rootElement.appendChild(staff);

		// set attribute to staff element
		Attr attr = doc.createAttribute("id");
		attr.setValue("1");
		staff.setAttributeNode(attr);

		// firstname elements
		Element firstname = doc.createElement("firstname");
		firstname.appendChild(doc.createTextNode("tuan"));
		staff.appendChild(firstname);

		// lastname elements
		Element lastname = doc.createElement("lastname");
		lastname.appendChild(doc.createTextNode("nguyen"));
		staff.appendChild(lastname);

		// nickname elements
		Element nickname = doc.createElement("nickname");
		nickname.appendChild(doc.createTextNode("vdtn359"));
		staff.appendChild(nickname);

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		xmlHelper.writeDocument(doc, outStream, 2);
		assertThat(outStream.toString(), equalTo("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n" +
				"<company>\n" +
				"  <Staff id=\"1\">\n" +
				"    <firstname>tuan</firstname>\n" +
				"    <lastname>nguyen</lastname>\n" +
				"    <nickname>vdtn359</nickname>\n" +
				"  </Staff>\n" +
				"</company>\n"));
	}
}
