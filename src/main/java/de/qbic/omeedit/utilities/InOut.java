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
