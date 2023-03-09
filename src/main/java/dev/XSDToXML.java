package dev;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;

public class XSDToXML {

    // A method that takes an XSD file as input and outputs an example XML file
    public static void generateXML(File xsdFile) {
        try {
            // Create a transformer factory
            TransformerFactory factory = TransformerFactory.newInstance();

            // Create a source from the XSD file
            Source source = new StreamSource(xsdFile);

            // Create a result to write the XML file
            Result result = new StreamResult(new File("example.xml"));

            // Create a transformer with the XSD source
            Transformer transformer = factory.newTransformer(source);

            // Transform the source to the result
            transformer.transform(source, result);

            System.out.println("XML file generated successfully.");
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Test the method with an example XSD file
        File xsdFile = new File("/home/aaron/Documents/Work/HiWi/QBiC/Metadata_Curation/XML_metadata_editor/data/ome.xsd");
        generateXML(xsdFile);
    }
}
