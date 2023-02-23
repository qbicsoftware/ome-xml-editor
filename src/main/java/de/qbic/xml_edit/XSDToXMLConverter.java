package de.qbic.xml_edit;

import loci.common.xml.XMLTools;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;

public class XSDToXMLConverter {
    public static void main(String[] args) throws Exception {
        // Load the XSD file
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new File("/home/aaron/Documents/Work/HiWi/QBiC/Metadata_Curation/XML_metadata_editor/data/ome.xsd"));

        // Create a new XML document based on the XSD
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setSchema(schema);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        System.out.println(XMLTools.getXML(document));

        // Create the root element based on the XSD
        Element root = document.createElementNS("http://example.com", "example");
        document.appendChild(root);

        // Add child elements to the root based on the XSD
        addElement(document, root, "stringElement");
        addElement(document, root, "intElement");
        addElement(document, root, "booleanElement");

        // Write the XML document to a file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File("example.xml"));
        transformer.transform(source, result);
    }

    private static void addElement(Document document, Element parent, String elementName) {
        Element element = document.createElementNS("http://example.com", elementName);
        parent.appendChild(element);
    }
}
