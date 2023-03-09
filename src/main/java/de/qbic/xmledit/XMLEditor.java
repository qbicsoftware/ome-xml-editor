// License

// Package
package de.qbic.xmledit;

// Imports

import loci.common.DataTools;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.gui.BufferedImageWriter;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.RealType;
import ome.xml.meta.MetadataRoot;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// Class
@Plugin(type = Command.class, menuPath = "Plugins>XML-Editor")
public class XMLEditor<T extends RealType<T>> implements Command {

    // -- Constants --
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLEditor.class);

    // -- Fields --

    private String id = null;
    private boolean doMeta = true;
    private boolean filter = true;
    private boolean thumbs = false;
    private boolean minmax = false;
    private boolean merge = false;
    private boolean stitch = false;
    private boolean group = true;
    private boolean separate = false;
    private boolean expand = false;
    private boolean omexml = false;
    private boolean cache = false;
    private boolean originalMetadata = true;
    private boolean normalize = false;
    private boolean fastBlit = false;
    private boolean autoscale = false;
    private boolean omexmlOnly = false;
    private boolean validate = true;
    private boolean flat = true;
    private String omexmlVersion = null;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    private int series = 0;
    private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    private String swapOrder = null, shuffleOrder = null;
    private String format = null;
    private String cachedir = null;
    public LinkedList<XMLChange> changeHistory;
    public Document xml_doc;
    public Element xmlElement;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();
    private IFormatReader reader;
    private IFormatReader baseReader;
    private MinMaxCalculator minMaxCalc;
    private DimensionSwapper dimSwapper;
    private BufferedImageReader biReader;
    private GUI myGUI;

    // -- ImageInfo methods --
    public void createReader() {
        if (reader != null) return; // reader was set programmatically
        if (format != null) {
            // create reader of a specific format type
            try {
                Class<?> c = Class.forName("loci.formats.in." + format + "Reader");
                reader = (IFormatReader) c.newInstance();
            }
            catch (ClassNotFoundException exc) {
                LOGGER.warn("Unknown reader: {}", format);
                LOGGER.debug("", exc);
            }
            catch (InstantiationException exc) {
                LOGGER.warn("Cannot instantiate reader: {}", format);
                LOGGER.debug("", exc);
            }
            catch (IllegalAccessException exc) {
                LOGGER.warn("Cannot access reader: {}", format);
                LOGGER.debug("", exc);
            }
        }
        if (reader == null) reader = new ImageReader();
        baseReader = reader;
    }
    public void configureReaderPreInit() throws FormatException, IOException {
        if (omexml) {
            reader.setOriginalMetadataPopulated(originalMetadata);
            try {
                ServiceFactory factory = new ServiceFactory();
                OMEXMLService service = factory.getInstance(OMEXMLService.class);
                reader.setMetadataStore(
                        service.createOMEXMLMetadata(null, omexmlVersion));
            }
            catch (DependencyException de) {
                throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
            }
            catch (ServiceException se) {
                throw new FormatException(se);
            }
        }

        // check file format
        if (reader instanceof ImageReader) {
            // determine format
            ImageReader ir = (ImageReader) reader;
            if (new Location(id).exists()) {
                LOGGER.info("Checking file format [{}]", ir.getFormat(id));
            }
        }
        else {
            // verify format
            LOGGER.info("Checking {} format [{}]", reader.getFormat(),
                    reader.isThisType(id) ? "yes" : "no");
        }

        LOGGER.info("Initializing reader");
        if (stitch) {
            reader = new FileStitcher(reader, true);
            Location f = new Location(id);
            String pat = null;
            if (!f.exists()) {
                ((FileStitcher) reader).setUsingPatternIds(true);
                pat = id;
            }
            else {
                pat = FilePattern.findPattern(f);
            }
            if (pat != null) id = pat;
        }
        if (expand) reader = new ChannelFiller(reader);
        if (separate) reader = new ChannelSeparator(reader);
        if (merge) reader = new ChannelMerger(reader);
        if (cache) {
            if (cachedir != null) {
                reader  = new Memoizer(reader, 0, new File(cachedir));
            } else {
                reader = new Memoizer(reader, 0);
            }
        }
        minMaxCalc = null;
        if (minmax || autoscale) reader = minMaxCalc = new MinMaxCalculator(reader);
        dimSwapper = null;
        if (swapOrder != null || shuffleOrder != null) {
            reader = dimSwapper = new DimensionSwapper(reader);
        }
        reader = biReader = new BufferedImageReader(reader);

        reader.close();
        reader.setNormalized(normalize);
        reader.setMetadataFiltered(filter);
        reader.setGroupFiles(group);
        options.setMetadataLevel(
                doMeta ? MetadataLevel.ALL : MetadataLevel.MINIMUM);
        options.setValidate(validate);
        reader.setMetadataOptions(options);
        reader.setFlattenedResolutions(flat);
    }
    public void configureReaderPostInit() {
        if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
        if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
    }
    public BufferedImage[] readPixels2() throws FormatException, IOException {
        String seriesLabel = reader.getSeriesCount() > 1 ?
                (" series #" + series) : "";
        LOGGER.info("");

        int num = reader.getImageCount();
        if (start < 0) start = 0;
        if (start >= num) start = num - 1;
        if (end < 0) end = 0;
        if (end >= num) end = num - 1;
        if (end < start) end = start;

        LOGGER.info("Reading{} pixel data ({}-{})",
                new Object[] {seriesLabel, start, end});

        int sizeX = reader.getSizeX();
        int sizeY = reader.getSizeY();
        if (width == 0) width = sizeX;
        if (height == 0) height = sizeY;

        int pixelType = reader.getPixelType();

        BufferedImage[] images = new BufferedImage[end - start + 1];
        long s = System.currentTimeMillis();
        long timeLastLogged = s;
        for (int i=start; i<=end; i++) {
            if (!fastBlit) {
                images[i - start] = thumbs ? biReader.openThumbImage(i) :
                        biReader.openImage(i, xCoordinate, yCoordinate, width, height);
            }
            else {
                byte[] b = thumbs ? reader.openThumbBytes(i) :
                        reader.openBytes(i, xCoordinate, yCoordinate, width, height);
                Object pix = DataTools.makeDataArray(b,
                        FormatTools.getBytesPerPixel(pixelType),
                        FormatTools.isFloatingPoint(pixelType),
                        reader.isLittleEndian());
                Double min = null, max = null;

                if (autoscale) {
                    Double[] planeMin = minMaxCalc.getPlaneMinimum(i);
                    Double[] planeMax = minMaxCalc.getPlaneMaximum(i);
                    if (planeMin != null && planeMax != null) {
                        min = planeMin[0];
                        max = planeMax[0];
                        for (int j=1; j<planeMin.length; j++) {
                            if (planeMin[j].doubleValue() < min.doubleValue()) {
                                min = planeMin[j];
                            }
                            if (planeMax[j].doubleValue() > max.doubleValue()) {
                                max = planeMax[j];
                            }
                        }
                    }
                }
                else if (normalize) {
                    min = Double.valueOf(0);
                    max = Double.valueOf(1);
                }

                if (normalize) {
                    if (pix instanceof float[]) {
                        pix = DataTools.normalizeFloats((float[]) pix);
                    }
                    else if (pix instanceof double[]) {
                        pix = DataTools.normalizeDoubles((double[]) pix);
                    }
                }
                if (thumbs) {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    sizeX, sizeY, reader.isInterleaved(), false, min, max),
                            sizeX, sizeY, FormatTools.isSigned(pixelType));
                }
                else {
                    images[i - start] = AWTImageTools.makeImage(ImageTools.make24Bits(pix,
                                    width, height, reader.isInterleaved(), false, min, max),
                            width, height, FormatTools.isSigned(pixelType));
                }
            }
            if (images[i - start] == null) {
                LOGGER.warn("\t************ Failed to read plane #{} ************", i);
            }
            if (reader.isIndexed() && reader.get8BitLookupTable() == null &&
                    reader.get16BitLookupTable() == null)
            {
                LOGGER.warn("\t************ no LUT for plane #{} ************", i);
            }

            // check for pixel type mismatch
            int pixType = AWTImageTools.getPixelType(images[i - start]);
            if (pixType != pixelType && pixType != pixelType + 1 && !fastBlit) {
                LOGGER.info("\tPlane #{}: pixel type mismatch: {}/{}",
                        new Object[] {i, FormatTools.getPixelTypeString(pixType),
                                FormatTools.getPixelTypeString(pixelType)});
            }
            else {
                // log number of planes read every second or so
                long t = System.currentTimeMillis();
                if (i == end || (t - timeLastLogged) / 1000 > 0) {
                    int current = i - start + 1;
                    int total = end - start + 1;
                    int percent = 100 * current / total;
                    LOGGER.info("\tRead {}/{} planes ({}%)", new Object[] {
                            current, total, percent
                    });
                    timeLastLogged = t;
                }
            }
        }
        long e = System.currentTimeMillis();

        LOGGER.info("[done]");

        // output timing results
        float sec = (e - s) / 1000f;
        float avg = (float) (e - s) / images.length;
        LOGGER.info("{}s elapsed ({}ms per plane)", sec, avg);

        // display pixels in image viewer
        return images;
    }
    public String getOMEXML() throws MissingLibraryException, ServiceException, IOException, ParserConfigurationException, SAXException {
        String xml = "";
        LOGGER.info("");
        MetadataStore ms = reader.getMetadataStore();

        if (baseReader instanceof ImageReader) {
            baseReader = ((ImageReader) baseReader).getReader();
        }

        OMEXMLService service;
        try {
            ServiceFactory factory = new ServiceFactory();
            service = factory.getInstance(OMEXMLService.class);
        }
        catch (DependencyException de) {
            throw new MissingLibraryException(OMEXMLServiceImpl.NO_OME_XML_MSG, de);
        }
        String version = service.getOMEXMLVersion(ms);
        if (version == null) LOGGER.info("Generating OME-XML");
        else {
            LOGGER.info("Generating OME-XML (schema version {})", version);
        }
        if (ms instanceof MetadataRetrieve) {
            if (omexmlOnly) {
                DebugTools.setRootLevel("INFO");
            }
            xml = service.getOMEXML((MetadataRetrieve) ms);
            //LOGGER.info("First XML-Output");
            //LOGGER.info("{}", XMLTools.indentXML(xml, xmlSpaces, true));


            if (omexmlOnly) {
                DebugTools.setRootLevel("OFF");
            }


        }
        else {
            LOGGER.info("The metadata could not be converted to OME-XML.");
            if (omexmlVersion == null) {
                LOGGER.info("The OME-XML Java library is probably not available.");
            }
            else {
                LOGGER.info("{} is probably not a legal schema version.",
                        omexmlVersion);
            }
        }
        return xml;
    }
    public void applyChange(XMLChange change, Document root, Node n, LinkedList<String> query) {
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Current Node: " + n.getNodeName());
        System.out.println("Remaining Query: " + query.toString());

        // If the query is empty, we are at the node where the new node should be added
        if (change.changeType == "add" && query.size()==0) {
            System.out.println("Adding new Node");

            if (change.getNewValue().startsWith("@")) {
                // Adding a new Attribute node and its value
                System.out.println("adding new attribute");

                String attrName = change.getNewValue().split("=")[0].replace("@", "");
                String attrValue = change.getNewValue().split("=")[1].replace(":", "");
                ((Element) n).setAttribute(attrName, attrValue);
                return;
            }
            else if (change.getNewValue().startsWith("#")) {
                // Adding a new Text Node
                System.out.println("adding new text");

                String newNodeValue = change.getNewValue().replace("#", "");
                n.setTextContent(newNodeValue);
                return;
            }
            else {
                // Adding a new Element Node
                System.out.println("adding new element");

                n.appendChild(root.createElement(change.getNewValue()));
                return;
            }
        }
        // if the query has only left 1 item, then we are at the node to be edited
        else if (change.changeType == "edit" && query.size()<=2) {
            System.out.println("Editing Node");

            if (query.get(0).startsWith("@")) {
                // Editing an Attribute node
                System.out.println("editing attribute");

                String oldNodeName = query.get(0).replace("@", "");
                String newNodeValue = change.getNewValue();
                n.getAttributes().getNamedItem(oldNodeName).setNodeValue(newNodeValue);
                return;
            }
            else if (query.get(0).startsWith(":")) {
                // Editing the value of an attribute node
                System.out.println("editing value");
                return;
            }
            else if (query.get(0).startsWith("#")) {
                // Editing a Text Node
                System.out.println("editing text");

                String newNodeValue = change.getNewValue();
                n.setTextContent(newNodeValue);
                return;
            }
            else if (query.size()==0) {
                // Editing an Element Node
                System.out.println("editing element");
                return;
            }
        }
        // If the query is empty, we are at the node that is to be deleted
        else if (change.changeType == "del") {
            if (query.size()>0){
                System.out.println("Deleting Node: " + n.getNodeName());
                if (query.get(0).startsWith("@")) {
                    // Deleting an Attribute node
                    System.out.println("deleting attribute");
                    String oldNodeName = query.get(0).replace("@", "");
                    n.getAttributes().removeNamedItem(oldNodeName);
                    return;
                }
                else if (query.get(0).startsWith(":")) {
                    // Deleting the value of an attribute node
                    System.out.println("deleting value");
                    String oldNodeName = query.get(0).replace(":", "");
                    return;
                }
                else if (query.get(0).startsWith("#")) {
                    // Deleting a Text Node
                    n.setTextContent("");
                    System.out.println("deleting text");
                    return;
                }
            }
            else if (query.size()==0){
                // Deleting an Element Node
                n.getParentNode().removeChild(n);
                System.out.println("deleting element");
                return;
            }
        }
        // query is not empty --> continue with next query item
        if (query.size()>0) {
            for (int c=0; c<n.getChildNodes().getLength(); c++) {
                if (query.get(0).equals(n.getChildNodes().item(c).getNodeName())) {
                    query.remove(0);
                    applyChange(change, root, n.getChildNodes().item(c), query);
                    return;
                }
            }
        }


        // query not in graph --> print error
        System.out.println("Query couldnt be found, no change was made");
    }
    public void exportToOmeTiff(String outPath) throws Exception {

        int dot = outPath.lastIndexOf(".");
        String outId = (dot >= 0 ? outPath.substring(0, dot) : outPath) + ".ome.tif";
        System.out.println("Converting " + outPath + " to " + outId + " ");

        // record metadata to OME-XML format
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata omexmlMeta = service.createOMEXMLMetadata();


        System.out.println("XML SCHEMA: ");
        System.out.println(XMLTools.indentXML(omexmlMeta.getRoot().toString()));

        for (XMLChange c : changeHistory) {
            System.out.println("apply Change: " + c.changeType);
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, xml_doc, xml_doc, query);
            System.out.println("One Change applied");
            // validate XML
            System.out.println(XMLTools.validateXML(XMLTools.getXML(xml_doc)));
            System.out.println(XMLTools.indentXML(XMLTools.getXML(xml_doc)));
        }
        xmlElement = xml_doc.getDocumentElement();
        OMEModel xmlModel = new OMEModelImpl();
        MetadataRoot mdr = new OMEXMLMetadataRoot(xmlElement, xmlModel);
        omexmlMeta.setRoot(mdr);

        BufferedImageWriter biwriter = new BufferedImageWriter(new OMETiffWriter());
        reader.setId(id);
        BufferedImage[] pixelData = readPixels2();


        /*
        writer.setMetadataRetrieve(omexmlMeta);
        writer.setId("test.ome.tif");

        byte[][] imageInByte = getByteArrays(pixelData);
        System.out.println("byte length: " + imageInByte.length);
        for (int p=0; p<imageInByte.length;p++) {
            writer.saveBytes(p, imageInByte[p]);
        }
         */

        biwriter.setMetadataRetrieve(omexmlMeta);
        biwriter.setId(outId);

        for (int i = 0; i < pixelData.length; i++) {
            biwriter.saveImage(i, pixelData[i]);
        }

        biwriter.close();
        System.out.println("[done]");
    }
    public void showCurrentXML() throws Exception {
        // record metadata to OME-XML format
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata omexmlMeta = service.createOMEXMLMetadata();

        System.out.println("XML SCHEMA: ");
        System.out.println(XMLTools.indentXML(omexmlMeta.getRoot().toString()));
        Document example_xml_doc = (Document) xml_doc.cloneNode(true);

        for (XMLChange c : changeHistory) {
            System.out.println("apply Change: " + c.changeType);
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, example_xml_doc, example_xml_doc, query);
        }
        System.out.println(XMLTools.indentXML(XMLTools.getXML(example_xml_doc)));
        System.out.println("All Changes applied");
        myGUI.showXMLTree(example_xml_doc, "Current XML");
        myGUI.setVisible(true);
    }
    public LinkedList<XMLChange> getChangeHistory() {
        return changeHistory;
    }

    public void validateChangeHistory() throws TransformerException, MalformedURLException, SAXException {
        Document example_xml_doc = (Document) xml_doc.cloneNode(true);
        for (XMLChange c : changeHistory) {
            System.out.println("apply Change: " + c.changeType);
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, example_xml_doc, example_xml_doc, query);
            boolean val = XMLValidator.validateOMEXML(XMLTools.getXML(example_xml_doc), "/home/aaron/Documents/Work/HiWi/QBiC/Metadata_Curation/XML_metadata_editor/data/ome.xsd");
            System.out.println("Validation: " + val);
            System.out.println(XMLTools.validateXML(XMLTools.getXML(example_xml_doc)));
            if (val) {
                c.setValidity(true);
            } else {
                c.setValidity(false);
            }
        }
    }

    public void saveChangeProfile(String path){
        // save each change as a new line to file
        // each line contains the modification type, the location and the new content seperated by tabs
        // for identification the file is called path/ + change + date + time + .txt
        String history = "";
        for (XMLChange c : changeHistory) {
            String line = c.changeType + "\t" + c.getLocation().toString() + "\t" + c.getNewValue();
            history += line + "\n";
        }
        try {
            Files.write(Paths.get(path + ".ch"), history.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadChangeProfile(String path) throws Exception {
        // load each change from file
        // each line contains the modification type, the location and the new content seperated by tabs
        // for identification the file is called path/ + change + date + time + .txt
        List<String> lines = Files.readAllLines(Paths.get(path));
        for (String line : lines) {
            String[] parts = line.replace("\n", "").split("\t");
            String modificationType = parts[0];
            String location = parts[1];
            String newContent = parts[2];
            LinkedList<String> locationList = new LinkedList<>();
            locationList.addAll(Arrays.asList(location.split(",")));
            XMLChange c = new XMLChange(modificationType, locationList, newContent);
            changeHistory.add(c);
        }
    }
    public void openSchema() throws Exception {
        XMLSchemaEditor schemaEditor = new XMLSchemaEditor();
        String xmlString = schemaEditor.createExampleXML();
        Document xmlDoc = XMLTools.parseDOM(xmlString);
        myGUI.makeTree(xmlDoc);
    }
    public void openXML(String path) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        xml_doc = db.parse(new File(path));
        String xml = XMLTools.getXML(xml_doc);
        System.out.println(xml);
        changeHistory = new LinkedList<>();
        myGUI.makeTree(xml_doc);
    }

    public void openImage(String path) throws IOException, FormatException, ServiceException, ParserConfigurationException, SAXException {
        String[] args = new String[2];
        args[0] = path; // the id parameter
        args[1] = "-omexml-only";
        omexmlOnly = true;
        omexml = true;
        id = path;

        createReader();

        configureReaderPreInit();
        reader.setId(path);
        configureReaderPostInit();

        reader.setSeries(series);

        String xml = getOMEXML();
        reader.close();
        xml_doc = XMLTools.parseDOM(xml);
        myGUI.makeTree(xml_doc);
    }
    public void testEdit() {
        changeHistory = new LinkedList<>();
        myGUI = new GUI(this);
        myGUI.setVisible(true);
    }
    @Override
    public void run() {
        DebugTools.enableLogging("INFO");
        new XMLEditor().testEdit();
    }
    public static void main(String[] args) throws Exception {
        final ImageJ ij = new ImageJ();
        ij.command().run(XMLEditor.class, true);
    }
}