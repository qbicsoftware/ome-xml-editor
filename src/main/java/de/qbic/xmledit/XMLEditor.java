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
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
        System.out.println("Inside applyChangesToFile");
        loadFile(path);
        if (validateChangeHistory()) {
            exportToOmeTiff(path);
        }
    }
    public void applyChanges(Document dom) {
        System.out.println("Inside applyChanges");
        for (XMLChange c : changeHistory) {
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, dom, dom, query);
        }
    }
    public void applyChange(XMLChange change, Document root, Node n, LinkedList<String> query) {
        System.out.println("---------------------------------------------------------------------");
        System.out.println("Current Node: " + n.getNodeName());
        System.out.println("Remaining Query: " + query.toString());

        // If the query is empty, we are at the node where the new node should be added
        if (change.changeType == "add" && query.size()==0) {

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
    /**
     * Exports the current metadata to an OME-TIFF file
     * @param path the path to the file
     */
    public void exportToOmeTiff(String path) throws IOException, FormatException {
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
        } catch (DependencyException e) {
            myGUI.reportError(e.toString());
        } catch (ServiceException e) {
            myGUI.reportError(e.toString());
        }

        // apply changes to metadata
        Document new_xml_doc = (Document) xml_doc.cloneNode(true);
        applyChanges(new_xml_doc);
        // set metadata
        xmlElement = new_xml_doc.getDocumentElement();
        OMEModel xmlModel = new OMEModelImpl();
        MetadataRoot mdr = null;
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
        myGUI.makeNewTab(myGUI.renderMarkdown(md), "How To Use", myGUI.HELP_SVG);
    }

    public void openAbout() throws IOException {
        String path = "./README.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        myGUI.makeNewTab(myGUI.renderMarkdown(md), "About XML-Editor", myGUI.HELP_SVG);
    }

    public LinkedList<XMLChange> getChangeHistory() {
        return changeHistory;
    }

    public boolean validateChangeHistory() throws TransformerException {
        System.out.println("- - - - - - - - - -");
        Document example_xml_doc = (Document) xml_doc.cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : changeHistory) {
            LinkedList<String> query = new LinkedList<>();
            query.addAll(c.getLocation());
            applyChange(c, example_xml_doc, example_xml_doc, query);
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
        System.out.println("- - - - - - - - - -");
        return changeHistoryValidity;
    }

    public void saveChangeHistory(String path){
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
    public void loadChangeHistory(String path) throws Exception {
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
        // myGUI.makeTree(xmlDoc, simplified);
    }
    public void updateTree() {
        // define a new xml document
        Document new_xml_doc = (Document) xml_doc.cloneNode(true);
        // apply all changes to the original xml
        applyChanges(new_xml_doc);
        // update the tree
        myGUI.updateTree(new_xml_doc, simplified);
    }
    public void loadFile(String path) throws IOException, ServiceException, FormatException, ParserConfigurationException, SAXException {
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
    }
    public void openImage(String path) throws IOException, FormatException, ServiceException, ParserConfigurationException, SAXException {
        loadFile(path);
        // make title from path
        String title = path.substring(path.lastIndexOf("/") + 1);

        if (myGUI.myTree != null) {
            myGUI.updateTree(xml_doc, simplified);
        }
        else {
            myGUI.makeTree(xml_doc, simplified, title);
        }
    }
    public void undoChange() throws MalformedURLException, TransformerException, SAXException {
        if (changeHistory.size() > 0) {
            // remove the last change from the history
            changeHistory.removeLast();
            // define a new xml document
            Document new_xml_doc = (Document) xml_doc.cloneNode(true);
            // apply all changes to the original xml
            for (XMLChange c : changeHistory) {
                System.out.println("apply Change: " + c.changeType);
                LinkedList<String> query = new LinkedList<>();
                query.addAll(c.getLocation());
                applyChange(c, new_xml_doc, new_xml_doc, query);
            }
            myGUI.updateTree(new_xml_doc, simplified);
            myGUI.updateChangeHistoryTab();
        }
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
