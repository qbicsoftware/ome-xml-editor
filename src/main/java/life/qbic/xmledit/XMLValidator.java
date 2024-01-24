package life.qbic.xmledit;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;
import java.net.URL;

public class XMLValidator {
    public static String validateOMEXML(String omexml, String xsdPath) {
        try {
            // Create a schema factory and a URL for the XSD schema
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            URL xsdURL = new File(xsdPath).toURI().toURL();

            // Create a schema object from the URL
            Schema schema = schemaFactory.newSchema(xsdURL);
            System.out.println(schema.toString());

            // Create a validator object from the schema
            Validator validator = schema.newValidator();

            // Create a source object from the OME-XML string
            Source source = new StreamSource(new StringReader(omexml));

            // Validate the source against the schema
            validator.validate(source);

            // If no exception is thrown, return true
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            // If any exception is thrown, return false
            return e.getMessage();
        }
    }
}
