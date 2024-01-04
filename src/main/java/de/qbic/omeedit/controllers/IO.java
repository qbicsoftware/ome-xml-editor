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

import de.qbic.omeedit.utilities.*;
public class IO {
    // -----------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    // -----------------------------------------------------------------------------------------------------------------
    EditorView view = null;
    EditorController controller = null;
    public IFormatReader reader = null;
    private IFormatReader baseReader = null;
    private BufferedImageReader biReader;
    private String omexmlVersion = null;
    private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    public int series = 0;
    public String id = null;
    private boolean validate = true;
    private boolean flat = true;
    private boolean filter = true;
    private MinMaxCalculator minMaxCalc;
    private boolean group = true;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();
    // -----------------------------------------------------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Export the current image to an OME-TIFF file, using the SPECIFIED ome xml.
     */
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
            mdr = new OMEXMLMetadataRoot(controller.getXMLDoc().getDocumentElement(), xmlModel);
        } catch (EnumerationException e) {
            view.reportError(e.toString());
        }
        omexmlMeta.setRoot(mdr);
        // define writer
        //OMETiffWriter writer = new OMETiffWriter();
        //BufferedImageWriter biwriter = new BufferedImageWriter(writer);

        // read pixels
        controller.getReader().setId(controller.getId());
        BufferedImage[] pixelData = controller.readPixels();
        controller.getReader().close();
        // write pixels
        //biwriter.setMetadataRetrieve(omexmlMeta);
        System.out.println("Writing to: " + outPath);
        //biwriter.setId(outPath);
        System.out.println("set metadata time"+ System.currentTimeMillis());
        System.out.println("pixelData.length: " + pixelData.length);
        System.out.println("pixelData[0].getWidth(): " + pixelData[0].getWidth());
        System.out.println("pixelData[0].getHeight(): " + pixelData[0].getHeight());


        ImageConverter converter = new ImageConverter();
        ImageWriter writer = new ImageWriter();
        converter.testConvert(writer, omexmlMeta, controller.getId(), outPath);
        writer.close();

        //for (int i = 0; i < pixelData.length; i++) {
        //    biwriter.saveImage(i, pixelData[i]);
        //}
        System.out.println("wrote pixels time"+ System.currentTimeMillis());
        // close writer
        //writer.close();
        //biwriter.close();
        return;
    }
    /**
     * Export the current image to an OME-TIFF file, using the CURRENTLY LOADED ome xml.
     */
    public void exportToOmeTiff(String path) throws Exception {
        exportToOmeTiff(path, controller.getXMLDoc());
    }
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
    public void configureReaderPreInit() throws Exception {
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
    /**
     * load an image and returns the metadata as a xml
     * @param path the path to the image
     */
    public Document loadFile(String path) throws Exception {
        System.out.println("Inside loadFile");
        id = path;
        System.out.println("Path: " + path);

        createReader();
        configureReaderPreInit();
        reader.setId(path);

        reader.setSeries(series);

        String xml = getOMEXML();
        reader.close();

        Document xmlDoc = XMLTools.parseDOM(xml);
        System.out.println("xmlDoc: " + xmlDoc);
        return xmlDoc;
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
        if (ms instanceof MetadataRetrieve) {
            xml = service.getOMEXML((MetadataRetrieve) ms);
        }
        return xml;
    }
}
