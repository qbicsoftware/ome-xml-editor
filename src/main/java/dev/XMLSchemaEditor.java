package dev;

import org.openmicroscopy.schemas.ome._2016_06.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

public class XMLSchemaEditor {
    private OME defineExampleXML() {
        // create the ObjectFactory
        ObjectFactory factory = new ObjectFactory();

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
    public OME unmarshall() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(OME.class);
        return (OME) context.createUnmarshaller()
                .unmarshal(new FileReader("./book.xml"));
    }

    public String createExampleXML() throws JAXBException {

        JAXBContext context = JAXBContext.newInstance("org.openmicroscopy.schemas.ome._2016_06");
        Marshaller marshaller = context.createMarshaller();

        String exampleXML = "";

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        StringWriter sw = new StringWriter();
        marshaller.marshal(defineExampleXML(), sw);

        return sw.toString();
    }
    public static void main(String[] args) throws JAXBException {
        XMLSchemaEditor editor = new XMLSchemaEditor();
        System.out.println(editor.createExampleXML());
    }
}
