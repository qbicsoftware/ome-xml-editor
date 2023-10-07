package de.qbic.xmledit;

import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import net.imagej.ImageJ;
import net.imglib2.type.numeric.RealType;
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
@Plugin(type = Command.class, menuPath = "Plugins>OME-XML-Editor")
public class EditorFijiPlugin<T extends RealType<T>> implements Command {

    /**
     * runs the plugin when started from within FiJi
     */
    @Override
    public void run() {
        DebugTools.enableLogging("INFO");
        Editor myEditor;
        EditorView myEditorView = null;
        EditorController myEditorController;

        myEditor = new Editor();
        myEditorController = new EditorController(myEditorView, myEditor);
        myEditorView = new EditorView(myEditorController);
        myEditorView.setVisible(true);

    }
    /**
     * Starts a new ImageJ session when started from outside Fiji
     */
    public static void main(String[] args) throws Exception {
        final ImageJ ij = new ImageJ();
        ij.command().run(EditorFijiPlugin.class, true);
    }
}
