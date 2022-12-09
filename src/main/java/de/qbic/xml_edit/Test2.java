package de.qbic.xml_edit;

import loci.formats.gui.XMLWindow;
import net.imagej.ImageJ;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
public class Test2 {
    public static void main(String[] args) {
        final ImageJ ij = new ImageJ();
        final File file = ij.ui().chooseFile(null, "open");
        XMLWindow w = new XMLWindow();
        try {
            w.setXML(file);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
