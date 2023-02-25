package trash;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import org.apache.xmlbeans.*;
import org.apache.xmlbeans.impl.xsd2inst.SampleXmlUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class XsdToXmlGenerator {
    public static String generateXmlFromXsd(String xsd) throws DependencyException, ServiceException {
        String generatedXML = null;
        try {
            // Parse the XSD schema into an XmlObject
            XmlObject xmlSchema = XmlObject.Factory.parse(xsd);

            ServiceFactory factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);

            IMetadata omexmlMeta = service.createOMEXMLMetadata();

            // Generate an example XML instance from the XSD schema using XMLBeans
            XmlOptions xmlOptions = new XmlOptions();
            // xmlOptions.setLoadLineNumbers(XmlOptions.LOAD_LINE_NUMBERS_IGNORE);
            // xmlOptions.setDocumentType(XmlOptions.VALIDATE_STRICT);
            xmlOptions.setUseDefaultNamespace();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSaveAggressiveNamespaces();
            xmlOptions.setSaveNamespacesFirst(false);
            SchemaType schemaType = xmlSchema.schemaType();

            generatedXML = SampleXmlUtil.createSampleForType(schemaType);
    } catch (XmlException e) {
            throw new RuntimeException(e);
        }

        return generatedXML;
    }
    // create main function to call generatedXmlFromXsd
    public static void main(String[] args) throws ServiceException, DependencyException {
        // read xsd file
        String path = "/home/aaron/Documents/Work/HiWi/QBiC/Metadata_Curation/XML_metadata_editor/data/ome.xsd";
        String xsd = null;
        try {
            // Read in the XSD file as a string
            xsd = new String(Files.readAllBytes(Paths.get(path)));
        } catch (IOException e) {
            System.out.println("Error reading XSD file: " + e.getMessage());
        }

        // call generateXmlFromXsd
        String xml = generateXmlFromXsd(xsd);
        // print xml
        System.out.println("Generated XML:");
        System.out.println(xml);
    }
}