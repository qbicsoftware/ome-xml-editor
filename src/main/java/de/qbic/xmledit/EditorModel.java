package de.qbic.xmledit;

// ---------------------------------------------------------------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------------------------------------------------------------

import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class EditorModel {
    /**
     * This class aims to implement the model layer. It should only act passively and thus only respond to requests from
     * the controller.
     */
    // -----------------------------------------------------------------------------------------------------------------
    // CONSTANTS
    // -----------------------------------------------------------------------------------------------------------------
    private static final Logger LOGGER = LoggerFactory.getLogger(XMLEditor.class);

    // -- Fields --


    private boolean doMeta = true;
    private boolean filter = true;
    private boolean thumbs = false;
    private boolean minmax = false;
    private boolean merge = false;
    private boolean stitch = false;
    private boolean group = true;
    private boolean separate = false;
    private boolean expand = false;
    public boolean omexml = false;
    private boolean cache = false;
    private boolean originalMetadata = true;
    private boolean normalize = false;
    private boolean fastBlit = false;
    private boolean autoscale = false;
    public boolean omexmlOnly = false;
    private boolean validate = true;
    private boolean flat = true;
    public boolean simplified = true;
    private String omexmlVersion = null;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    public int series = 0;
    private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    private String swapOrder = null, shuffleOrder = null;
    private String format = null;
    private String cachedir = null;
    public LinkedList<XMLChange> changeHistory = null;
    public Document xml_doc;
    public Element xmlElement;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();
    private IFormatReader reader;
    private IFormatReader baseReader;
    private MinMaxCalculator minMaxCalc;
    private DimensionSwapper dimSwapper;
    private BufferedImageReader biReader;
    public String schemaPath = "./data/resources/ome.xsd";
    public String id = null;
    public EditorController controller;

    public EditorModel(EditorController cont) {
        controller = cont;
        changeHistory = new LinkedList<>();
    }
    // -----------------------------------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------------------------------
    /**
     *
     */
    public void createReader() {
        reader = new ImageReader();
        baseReader = reader;
    }
    /**
     *
     */
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
    /**
     *
     */
    public void configureReaderPostInit() {
        if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
        if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
    }
    /**
     *
     */
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

        width = reader.getSizeX();
        height = reader.getSizeY();

        int pixelType = reader.getPixelType();

        BufferedImage[] images = new BufferedImage[end - start + 1];
        long s = System.currentTimeMillis();
        long timeLastLogged = s;
        for (int i=start; i<=end; i++) {
            if (!fastBlit) {
                images[i - start] = thumbs ? biReader.openThumbImage(i) :
                        biReader.openImage(i, xCoordinate, yCoordinate, width, height);
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

        LOGGER.info("Deone reading pixel data");

        // output timing results
        float sec = (e - s) / 1000f;
        float avg = (float) (e - s) / images.length;
        LOGGER.info("{}s elapsed ({}ms per plane)", sec, avg);

        // display pixels in image viewer
        return images;
    }
    /**
     *
     */
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

    /**
     *
     */
    public void applyChanges(Document dom) {
        System.out.println("Inside applyChanges");
        for (XMLChange c : changeHistory) {
            applyChange(c, dom, dom, 0);
        }
    }
    /**
     *
     */
    public void applyChange(XMLChange change, Document root, Node currentNode, int positionInQuery) {
        System.out.println("- - - -");
        System.out.println("Current Node: " + currentNode.getNodeName());
        // Some variables to make the code more readable
        LinkedList<XMLNode> query = change.getNodePath();
        boolean queryIsEmpty = query.size() == positionInQuery;
        if (queryIsEmpty) {
            switch (change.getNodeType()) {
                case "element":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new element");
                            Element newElement = root.createElement(change.getToBeChangedNode().getUserObject().toString());
                            newElement.setAttribute("ID", change.getToBeChangedNode().getID());
                            currentNode.appendChild(newElement);
                            break;
                        case "modify":
                            System.out.println("Modifying element");
                            currentNode.setTextContent(change.getToBeChangedNode().getUserObject().toString());
                            break;
                        case "delete":
                            System.out.println("Deleting element");
                            currentNode.getParentNode().removeChild(currentNode);
                            break;
                    }
                    break;

                case "attribute":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new attribute");
                            String attrName = change.getToBeChangedNode().getUserObject().toString();
                            String attrValue = change.getToBeChangedNode().getFirstChild().getUserObject().toString();
                            ((Element) currentNode).setAttribute(attrName, attrValue);
                            break;
                        case "modify":
                            System.out.println("Modifying attribute");
                            String attrName2 = change.getToBeChangedNode().getUserObject().toString();
                            String attrValue2 = change.getToBeChangedNode().getFirstChild().getUserObject().toString();
                            currentNode.getAttributes().getNamedItem(attrName2).setNodeValue(attrValue2);
                            // ((Element) currentNode).setAttribute(attrName2, attrValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting attribute");
                            String attrName3 = change.getToBeChangedNode().getUserObject().toString();
                            ((Element) currentNode).removeAttribute(attrName3);
                            break;
                    }
                    break;

                case "value":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new value???");
                            String attrName = change.getToBeChangedNode().getParent().getUserObject().toString();
                            String attrValue = change.getToBeChangedNode().getUserObject().toString();
                            ((Element) currentNode).setAttribute(attrName, attrValue);
                            break;
                        case "modify":
                            System.out.println("Modifying value");
                            String attrName2 = change.getToBeChangedNode().getParent().getUserObject().toString();
                            String attrValue2 = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.getAttributes().getNamedItem(attrName2).setNodeValue(attrValue2);
                            // ((Element) currentNode).setAttribute(attrName2, attrValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting value???");
                            String attrName3 = change.getToBeChangedNode().getParent().getUserObject().toString();
                            ((Element) currentNode).removeAttribute(attrName3);
                            break;
                    }
                    break;

                case "text":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new text");
                            String newNodeValue = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.setTextContent(newNodeValue);
                            break;
                        case "modify":
                            System.out.println("Modifying text");
                            String newNodeValue2 = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.setTextContent(newNodeValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting text");
                            currentNode.setTextContent("");
                            break;
                    }
                    break;
            }
            return;
        }
        // remainingQuery is not empty --> continue with next remainingQuery item
        else {
            XMLNode currentQueryItem = query.get(positionInQuery);
            String currentQueryItemName = currentQueryItem.getUserObject().toString();
            String currentQueryItemID = currentQueryItem.getID();
            // loop over all child nodes of the current node
            for (int c=0; c<currentNode.getChildNodes().getLength(); c++) {
                // if the name of the child node matches the next remainingQuery item
                if (currentQueryItemName.equals(currentNode.getChildNodes().item(c).getNodeName())) {
                    Node idNode = currentNode.getChildNodes().item(c).getAttributes().getNamedItem("ID");
                    System.out.println("ID Node: " + idNode);
                    System.out.println("Current Query Item ID: " + currentQueryItemID);
                    if ((idNode != null && idNode.getNodeValue().equals(currentQueryItemID)) || currentNode.getNodeName().equals("#document")) {
                        // name and id matches we are at the right node apply the change / go deeper
                        positionInQuery++;
                        applyChange(change, root, currentNode.getChildNodes().item(c), positionInQuery);
                        // return to break out of the recursion
                        return;
                    }
                    else {
                        break;
                    }
                }
            }
            // without the ID requirement
            for (int c=0; c<currentNode.getChildNodes().getLength(); c++) {
                // if the name of the child node matches the next remainingQuery item
                if (currentQueryItemName.equals(currentNode.getChildNodes().item(c).getNodeName())) {
                    // print a warning, that the ID is not found
                    System.out.println("Warning: ID Argument not found, proceeding without ID. This could lead to ambiguity and thus wrong changes.");
                    positionInQuery++;
                    applyChange(change, root, currentNode.getChildNodes().item(c), positionInQuery);
                    // return to break out of the recursion
                    return;
                }
            }
        }
        // remainingQuery not in graph --> print error
        throw new IllegalArgumentException("Query not in graph");
    }
    /**
     *
     */
    public LinkedList<XMLChange> getChangeHistory() {
        return this.changeHistory;
    }
    /**
     *
     */
    public void addChange(XMLChange change) {
        this.changeHistory.add(change);
    }
    /**
     * @return
     */
    public Document getXMLDoc() {
        return this.xml_doc;
    }
    /**
     *
     */
    public void setSchemaPath(String path) {
        this.schemaPath = path;
    }
    /**
     *
     */
    public void setXMLElement(Element ele) {
        this.xmlElement = ele;
    }
    /**
     *
     */
    public void setXMLDoc(Document doc) {
        this.xml_doc = doc;
    }
    /**
     *
     */
    public Element getXmlElement() {
        return this.xmlElement;
    }
    /**
     *
     */
    public IFormatReader getReader() {
        return reader;
    }
    /**
     *
     */
    public void setChangeHistory(LinkedList<XMLChange> changeHistory) {
        this.changeHistory = changeHistory;
    }
    /**
     *
     */
    public void setSimplified(boolean simplified) {
        this.simplified = simplified;
    }


    public String getId() {
        return id;
    }
}
