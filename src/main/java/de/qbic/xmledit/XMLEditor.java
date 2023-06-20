// License

// Package
package de.qbic.xmledit;

// Imports

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
import ome.xml.model.enums.EnumerationException;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public boolean simplified = true;
    private String omexmlVersion = null;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    private int series = 0;
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
    private GUI myGUI;

    // -- ImageInfo methods --
    public void createReader() {
        reader = new ImageReader();
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
     * gets all files in the directory and returns them as a set
     * @param dir the directory to get the files from
     * @return a set of all files in the directory
     */
    public Set<String> getFilesInDir(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
    /**
     * Applys the current change history to all valid files in the selected folder
     * @param path the path to the folder
     */
    public void applyChangesToFolder(String path) throws Exception {
        System.out.println("Inside applyChangesToFolder");
        // loop over all valid files in the folder
        for (String file : getFilesInDir(path)) {
            System.out.println("###############################################################");
            System.out.println("Current File: " + file);
            applyChangesToFile(path + "/" + file);
        }
        System.out.println("Done");
    }
    /**
     * Applys the current change history to the selected file
     * @param path the path to the file
     */
    public void applyChangesToFile(String path) throws Exception {
        FeedbackStore fbStore= new FeedbackStore(path);
        Document newXMLDom =  loadFile(path);
        if (validateChangeHistory(newXMLDom)) {
            fbStore.setValidity(true);
            try {
                exportToOmeTiff(path, newXMLDom);
                fbStore.setExported(true);
            }
            catch (Exception e) {
                fbStore.setExported(false);
                fbStore.setExportError(e.getMessage());
            }
        }
        else {
            fbStore.setValidity(false);
        }
        myGUI.addFeedback(fbStore);
    }
    public boolean validateChangeHistory(Document newXMLDom) throws TransformerException {
        System.out.println("- - - - Validating Change History - - - -");
        Document example_xml_doc = (Document) newXMLDom.cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : changeHistory) {
            System.out.println("- - - - Applying Change - - - -");
            System.out.println("ToBeChangedNode: " + c.getToBeChangedNode().getUserObject().toString());
            System.out.println("ToBeChangedNode Type: " + c.getNodeType());
            System.out.println("Change Type: " + c.getChangeType());
            applyChange(c, example_xml_doc, example_xml_doc, 0);
            try {
                Element xmlExampleElement = example_xml_doc.getDocumentElement();
                OMEModel xmlModel = new OMEModelImpl();
                // catch verification errors and print them
                MetadataRoot mdr = new OMEXMLMetadataRoot(xmlExampleElement, xmlModel);
                c.setValidity(true);
            } catch (Exception e) {
                c.setValidity(false);
                c.setValidationError(e.getMessage());
            }
            changeHistoryValidity = changeHistory.getLast().getValidity();
        }
        return changeHistoryValidity;
    }

    public void applyChanges(Document dom) {
        System.out.println("Inside applyChanges");
        for (XMLChange c : changeHistory) {
            applyChange(c, dom, dom, 0);
        }
    }
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
     * Exports the current metadata to an OME-TIFF file
     * @param path the path to the file
     */
    public void exportToOmeTiff(String path, Document newXML) throws IOException, FormatException {
        System.out.println("Inside exportToOmeTiff");
        System.out.println("Path: " + path);
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.tif";
        // record metadata to OME-XML format
        ServiceFactory factory = null;
        IMetadata omexmlMeta = null;
        try {
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            omexmlMeta = service.createOMEXMLMetadata();
        } catch (DependencyException | ServiceException e) {
            myGUI.reportError(e.toString());
        }

        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        applyChanges(new_xml_doc);
        // set metadata
        xmlElement = new_xml_doc.getDocumentElement();
        OMEModel xmlModel = new OMEModelImpl();
        // MetadataRoot mdr = null;
        OMEXMLMetadataRoot mdr = null;
        // OME test = new OME();
        //OME.getChildrenByTagName();
        try {
            mdr = new OMEXMLMetadataRoot(xmlElement, xmlModel);
        } catch (EnumerationException e) {
            myGUI.reportError(e.toString());
        }
        omexmlMeta.setRoot(mdr);
        // define writer
        OMETiffWriter writer = new OMETiffWriter();
        BufferedImageWriter biwriter = new BufferedImageWriter(writer);
        // read pixels
        reader.setId(id);
        BufferedImage[] pixelData = readPixels2();
        reader.close();
        // write pixels
        biwriter.setMetadataRetrieve(omexmlMeta);
        System.out.println("Writing to: " + outPath);
        biwriter.setId(outPath);
        for (int i = 0; i < pixelData.length; i++) {
            biwriter.saveImage(i, pixelData[i]);
        }
        // close writer
        writer.close();
        biwriter.close();
        // refocus gui
        myGUI.setVisible(true);
        System.out.println("[done]");
    }

    /**
     * Exports the current metadata to an OME-XML file
     * @param path the path to the file
     * @throws Exception
     */
    public void exportToOmeXml(String path, Document newXML) throws Exception {
        System.out.println("Inside exportToOmeXml");
        System.out.println("Path: " + path);
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.xml";
        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        applyChanges(new_xml_doc);
        // write to file
        System.out.println("Writing to: " + outPath);
        FileOutputStream xmlOutStream = new FileOutputStream(outPath);
        XMLTools.writeXML(xmlOutStream, new_xml_doc);
        xmlOutStream.close();
        // refocus gui
        myGUI.setVisible(true);
        System.out.println("[done]");
    }
    public void showCurrentXML() throws Exception {
        Document example_xml_doc = (Document) xml_doc.cloneNode(true);

        applyChanges(example_xml_doc);

        System.out.println(XMLTools.indentXML(XMLTools.getXML(example_xml_doc)));
        System.out.println("All Changes applied");
        myGUI.showXMLTree(example_xml_doc, example_xml_doc.getNodeName());
        myGUI.setVisible(true);
    }
    public void openTutorial() throws IOException {
        String path = "./data/resources/HowToUse.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        myGUI.makeNewTab(myGUI.renderMarkdown(md), "How To Use", GUI.HELP_SVG);
    }

    public void openAbout() throws IOException {
        String path = "./README.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        myGUI.makeNewTab(myGUI.renderMarkdown(md), "About XML-Editor", GUI.HELP_SVG);
    }

    public LinkedList<XMLChange> getChangeHistory() {
        return changeHistory;
    }



    public void saveChangeHistory(String path){
        try {
            FileOutputStream f = new FileOutputStream(new File(path));
            ObjectOutputStream o = new ObjectOutputStream(f);
            // Write objects to file
            for (XMLChange c : changeHistory) {
                o.writeObject(c);
            }
            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadChangeHistory(String path) throws Exception {
        try {
            FileInputStream fi = new FileInputStream(new File(path));
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Write objects to file
            boolean moreObjects = true;
            while (moreObjects) {
                XMLChange c = (XMLChange) oi.readObject();
                changeHistory.add(c);
                // check if there are more objects
                if (oi.available() == 0) {
                    moreObjects = false;
                }
            }
            oi.close();
            fi.close();
            // apply changes to the view port and show the change history
            myGUI.makeChangeHistoryTab();
            updateTree();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void updateTree() {
        // define a new xml document
        Document new_xml_doc = (Document) xml_doc.cloneNode(true);
        // apply all changes to the original xml
        applyChanges(new_xml_doc);
        // update the tree
        myGUI.updateTree(new_xml_doc, simplified);
    }
    public Document loadFile(String path) throws IOException, ServiceException, FormatException, ParserConfigurationException, SAXException {
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
        return XMLTools.parseDOM(xml);
    }
    public void openImage(String path) throws IOException, FormatException, ServiceException, ParserConfigurationException, SAXException, TransformerException {

        // make title from path
        String title = path.substring(path.lastIndexOf("/") + 1);
        xml_doc = loadFile(path);
        Document new_xml_doc = (Document) xml_doc.cloneNode(true);
        if (changeHistory.size() > 0) {
            applyChanges(new_xml_doc);
            myGUI.updateChangeHistoryTab();
        }
        myGUI.makeNewTreeTab(new_xml_doc, simplified, title);

    }
    public void undoChange() throws MalformedURLException, TransformerException, SAXException {
        if (changeHistory.size() > 0) {
            // remove the last change from the history
            changeHistory.removeLast();
            // define a new xml document
            Document new_xml_doc = (Document) xml_doc.cloneNode(true);
            // apply all changes to the original xml
            applyChanges(new_xml_doc);
            myGUI.updateTree(new_xml_doc, simplified);
            myGUI.updateChangeHistoryTab();
        }
    }
    public void testEdit() {
        changeHistory = new LinkedList<>();
        myGUI = new GUI(this);
        myGUI.setVisible(true);
        OMEModel xmlModel = new OMEModelImpl();
        System.out.println("Mode Objects: " + xmlModel.getModelObjects().toString());
    }
    public void resetChangeHistory() {
        changeHistory = new LinkedList<>();
        try {
            myGUI.updateChangeHistoryTab();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
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
