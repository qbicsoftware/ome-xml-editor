package dev;

import org.apache.xmlbeans.*;
import org.openmicroscopy.schemas.ome._2016_06.ObjectFactory;
import org.openmicroscopy.schemas.ome._2016_06.*;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;

public class XMLBeans  {
    public XMLBeans() {
    }
    private static OME defineExampleXML() {
        // create the ObjectFactory
        org.openmicroscopy.schemas.ome._2016_06.ObjectFactory factory = new ObjectFactory();

        // Create the root OME object and set all attributes
        OME ome = new OME();
        ome.setCreator("Bing");
        ome.setUUID("1234-5678-90AB-CDEF");

        // Create an image object
        Image image = new Image();
        image.setID("1234");
        image.setName("Test");

        // Create a pixels object
        Pixels pixels = new Pixels();
        pixels.setID("5678");
        pixels.setSizeX(512);
        pixels.setSizeY(512);
        pixels.setSizeZ(1);
        pixels.setSizeC(1);
        pixels.setSizeT(1);

        // Create a channel object
        Channel channel = new Channel();
        channel.setID("9012");
        channel.setName("Test");

        // Create a detector object
        Detector detector = new Detector();
        detector.setID("3456");
        detector.setModel("Test");
        detector.setManufacturer("Test");
        detector.setSerialNumber("Test");
        detector.setLotNumber("Test");

        // Create a light source object
        LightSource lightSource = new LightSource();
        lightSource.setID("7890");
        lightSource.setModel("Test");
        lightSource.setManufacturer("Test");
        lightSource.setSerialNumber("Test");
        lightSource.setLotNumber("Test");

        // Create a filter object
        Filter filter = new Filter();
        filter.setID("CDEF");
        filter.setModel("Test");
        filter.setManufacturer("Test");
        filter.setSerialNumber("Test");
        filter.setLotNumber("Test");

        // Create Project object
        Project project = new Project();
        project.setID("1234");
        project.setName("Test");

        // Create Dataset object
        Dataset dataset = new Dataset();
        dataset.setID("5678");
        dataset.setName("Test");

        // add the objects to the root ome
        ome.getImage().add(image);
        ome.getProject().add(project);
        ome.getDataset().add(dataset);
        pixels.getChannel().add(channel);

        return ome;
    }
    public static String createExampleXML() throws XmlException, IOException {

        // Load the schema file
        File schemaFile = new File("data/ome.xsd");
        // Parse the schema into a SchemaTypeSystem object
        SchemaTypeSystem sts = XmlBeans.compileXsd(new XmlObject[] { XmlObject.Factory.parse(schemaFile) }, XmlBeans.getBuiltinTypeSystem(), null);
        // Create a SchemaTypeLoader that can load and resolve types from the SchemaTypeSystem
        SchemaTypeLoader stl = XmlBeans.typeLoaderUnion(new SchemaTypeLoader[] { sts, XmlBeans.getContextTypeLoader() });
        // Get the SchemaType object for the root element of the XML document
        SchemaType rootType = stl.findDocumentType(new QName("http://www.openmicroscopy.org/Schemas/OME/2016-06", "OME"));
        // Create an empty XML document of that type
        XmlObject xobj = XmlObject.Factory.newInstance();
        // Change the type of the XML document to the root type
        xobj = xobj.changeType(rootType);
        // Populate the XML document with data using the generated setter methods
        xobj.Factory.parse(defineExampleXML().toString());
        // Create an XmlOptions object to specify the output format and encoding
        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setSavePrettyPrintIndent(4);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setCharacterEncoding("UTF-8");
        // Save the XML document to a StringWriter
        StringWriter sw = new StringWriter();
        xobj.save(sw, xmlOptions);

        return sw.toString();
    }

    public static void main(String[] args) throws XmlException, IOException {
        System.out.println(createExampleXML());

    }
}
