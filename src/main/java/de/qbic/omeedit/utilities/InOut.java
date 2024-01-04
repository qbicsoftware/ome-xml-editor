package de.qbic.omeedit.utilities;


import de.qbic.omeedit.controllers.EditorController;
import de.qbic.omeedit.views.EditorView;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.ImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import ome.xml.model.enums.EnumerationException;
import org.w3c.dom.Document;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;

public interface InOut {
    EditorView view = null;
    EditorController controller = null;
    // Input
    public void loadImage(String path) throws Exception;
    public void loadChangeHistory(String path) throws Exception;
    public void applyChangeHistory(String path)throws Exception;
    // Output
    public void saveImage(String path) throws IOException, FormatException, Exception;
    public default void exportToOmeTiff(String path, Document newXML) throws Exception {

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
    public default void exportToOmeTiff(String path) throws Exception {
        exportToOmeTiff(path, controller.getXMLDoc());
    }

    /**
     * Exports the current metadata to an OME-XML file
     * @param path the path to the file
     */
    public default void exportToOmeXml(String path) throws Exception {
        exportToOmeXml(path, controller.getXMLDoc());
    }
    /**
     * Exports the current metadata to an OME-XML file
     * @param path the path to the file
     */
    public default void exportToOmeXml(String path, Document newXML) throws Exception {
        System.out.println("Inside exportToOmeXml");
        System.out.println("Path: " + path);
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.xml";
        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        controller.applyChanges(new_xml_doc);
        // write to file
        System.out.println("Writing to: " + outPath);
        FileOutputStream xmlOutStream = new FileOutputStream(outPath);
        XMLTools.writeXML(xmlOutStream, new_xml_doc);
        xmlOutStream.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
    }

}
