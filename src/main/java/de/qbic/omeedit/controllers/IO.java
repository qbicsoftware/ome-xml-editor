package de.qbic.omeedit.controllers;


import de.qbic.omeedit.utilities.ImageConverter;
import de.qbic.omeedit.views.EditorView;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import ome.xml.model.enums.EnumerationException;
import org.w3c.dom.Document;
import loci.formats.gui.BufferedImageReader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Logger;

import de.qbic.omeedit.utilities.*;
import org.w3c.dom.Element;

public class IO {
    // -----------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    // -----------------------------------------------------------------------------------------------------------------
    EditorView view = null;
    EditorController controller = null;
    Logger LOGGER = Logger.getLogger(IO.class.getName());

    //

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
    //private String format = null;
    private String cachedir = null;
    public LinkedList<XMLChange> changeHistory = new LinkedList<>();
    public Document xml_doc;
    public Element xmlElement;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();
    private IFormatReader reader;
    private IFormatReader baseReader;
    private MinMaxCalculator minMaxCalc;
    private DimensionSwapper dimSwapper;
    private BufferedImageReader biReader;
    public String schemaPath = "./data/resources/ome.xsd";

    // -----------------------------------------------------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------------------------------------------------
    /**
     *
     */
    public void createReader() {
        System.out.println("Inside createReader");
        reader = new ImageReader();
        baseReader = reader;
    }
    /**
     *
     */
    public void configureReaderPreInit2() throws Exception {
        reader.setOriginalMetadataPopulated(true);
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

        // check file format
        if (reader instanceof ImageReader) {
            // determine format
            ImageReader ir = (ImageReader) reader;
        }

        reader = biReader = new BufferedImageReader(reader);
        reader.close();
        // reader.setNormalized(normalize);
        reader.setMetadataFiltered(filter);
        reader.setGroupFiles(group);
        options.setMetadataLevel(MetadataLevel.ALL);
        options.setValidate(validate);
        reader.setMetadataOptions(options);
        reader.setFlattenedResolutions(flat);
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
                LOGGER.info("Checking file format [{}]");
            }
        }
        else {
            // verify format
            LOGGER.info("Checking {} format [{}]"
            );
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
     * load an image and returns the metadata as a xml
     * @param path the path to the image
     */
    public Document loadFile(String path) throws Exception {
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
    public void configureReaderPostInit() {
        if (swapOrder != null) dimSwapper.swapDimensions(swapOrder);
        if (shuffleOrder != null) dimSwapper.setOutputOrder(shuffleOrder);
    }

    /**
     *
     */
    public BufferedImage[] readPixels2() throws Exception {

        String seriesLabel = reader.getSeriesCount() > 1 ?
                (" series #" + series) : "";


        int num = reader.getImageCount();
        if (start < 0) start = 0;
        if (start >= num) start = num - 1;
        if (end < 0) end = 0;
        if (end >= num) end = num - 1;
        if (end < start) end = start;


        width = reader.getSizeX();
        height = reader.getSizeY();

        int pixelType = reader.getPixelType();

        BufferedImage[] images = new BufferedImage[end - start + 1];
        long s = System.currentTimeMillis();
        long timeLastLogged = s;
        for (int i=start; i<=end; i++) {
            // check for pixel type mismatch
            int pixType = AWTImageTools.getPixelType(images[i - start]);

        }
        // display pixels in image viewer
        return images;
    }
    /**
     *
     */
    public String getOMEXML() throws Exception {
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
            LOGGER.info("Generating OME-XML (schema version {})");
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
                LOGGER.info("{} is probably not a legal schema version."
                );
            }
        }
        return xml;
    }
    public void exportToOmeTiff(String path, Document newXML) throws Exception {

        System.out.println("Inside exportToOmeTiff");
        System.out.println("Path: " + path);
        // time measurement
        System.out.println("Start export time"+ System.currentTimeMillis());
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
            view.reportError(e.toString());
        }
        System.out.println("After initialisation time"+ System.currentTimeMillis());

        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        controller.applyChanges(new_xml_doc);
        System.out.println("Applied changes time"+ System.currentTimeMillis());
        // set metadata
        controller.setXMLElement(new_xml_doc.getDocumentElement());
        OMEModel xmlModel = new OMEModelImpl();
        // MetadataRoot mdr = null;
        OMEXMLMetadataRoot mdr = null;
        // OME test = new OME();
        //OME.getChildrenByTagName();
        try {
            mdr = new OMEXMLMetadataRoot(controller.getXmlElement(), xmlModel);
        } catch (EnumerationException e) {
            view.reportError(e.toString());
        }
        omexmlMeta.setRoot(mdr);
        // define writer
        //OMETiffWriter writer = new OMETiffWriter();
        //BufferedImageWriter biwriter = new BufferedImageWriter(writer);

        // read pixels
        reader.setId(controller.getId());
        BufferedImage[] pixelData = controller.readPixels();
        reader.close();
        // write pixels
        //biwriter.setMetadataRetrieve(omexmlMeta);
        System.out.println("Writing to: " + outPath);
        //biwriter.setId(outPath);
        System.out.println("set metadata time"+ System.currentTimeMillis());
        System.out.println("pixelData.length: " + pixelData.length);
        System.out.println("pixelData[0].getWidth(): " + pixelData[0].getWidth());
        System.out.println("pixelData[0].getHeight(): " + pixelData[0].getHeight());


        ImageConverter converter = new ImageConverter();
        converter.testConvert(new ImageWriter(), omexmlMeta, controller.getId(), outPath);

        //for (int i = 0; i < pixelData.length; i++) {
        //    biwriter.saveImage(i, pixelData[i]);
        //}
        System.out.println("wrote pixels time"+ System.currentTimeMillis());
        // close writer
        //writer.close();
        //biwriter.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
        System.out.println("Done time"+ System.currentTimeMillis());
    }
    /**
     * Exports the current metadata to an OME-TIFF file
     * @param path the path to the file
     */
    public void exportToOmeTiff(String path) throws Exception {
        exportToOmeTiff(path, controller.getXMLDoc());
    }

}
