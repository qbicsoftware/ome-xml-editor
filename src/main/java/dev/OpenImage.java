package dev;// not Needed

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;

import java.io.File;
import java.io.IOException;

@Plugin(type = Command.class, menuPath = "Plugins>OpenImage")
public class OpenImage<T extends RealType<T>> implements Command {

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;


    /*
            ImporterOptions options = null;

        try {
            options = new ImporterOptions();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        options.setId(path);
        options.setShowOMEXML(true);
     */

    @Override
    public void run() {
        File file = ij.ui().chooseFile(null, "open");
        String path = file.getPath();


        IFormatReader reader = new ImageReader();
        OMEXMLMetadata metadata;

        ServiceFactory factory;
        try {
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            metadata = service.createOMEXMLMetadata();
            System.out.println("test" + "\n" + metadata);
        } catch (DependencyException exc) {
            try {
                throw new FormatException("Could not create OME-XML store.", exc);
            } catch (FormatException e) {
                throw new RuntimeException(e);
            }
        } catch (ServiceException exc) {
            try {
                throw new FormatException("Could not create OME-XML store.", exc);
            } catch (FormatException e) {
                throw new RuntimeException(e);
            }
        }

        reader.setMetadataStore(metadata);
        // initialize the dataset
        try {
            reader.setId(path);
        } catch (FormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



        // create a writer that will automatically handle any supported output format
        IFormatWriter writer = new ImageWriter();
        // give the writer a MetadataRetrieve object, which encapsulates all the
        // dimension information for the dataset (among many other things)
        OMEXMLService service = null;
        try {
            service = factory.getInstance(OMEXMLService.class);
        } catch (DependencyException e) {
            throw new RuntimeException(e);
        }
        writer.setMetadataRetrieve(service.asRetrieve(reader.getMetadataStore()));
        // initialize the writer
        try {
            writer.setId("/home/aaron/Desktop/test.xml");
        } catch (FormatException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (int series=0; series<reader.getSeriesCount(); series++) {
            reader.setSeries(series);
            try {
                writer.setSeries(series);
            } catch (FormatException e) {
                throw new RuntimeException(e);
            }

            for (int image=0; image<reader.getImageCount(); image++) {
                try {
                    writer.saveBytes(image, reader.openBytes(image));
                } catch (FormatException e) {
                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // create the ImageJ application context with all available services

        final ImageJ ij = new ImageJ();
        ij.command().run(OpenImage.class, true);
    }
}
