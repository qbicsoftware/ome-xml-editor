package de.qbic.xml_edit;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.formats.*;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;

public class test3 {
    public static void main(String[] args) throws Exception {
        // create a reader that will automatically handle any supported format
        IFormatReader reader = new ImageReader();
        // tell the reader where to store the metadata from the dataset
        MetadataStore metadata;

        ServiceFactory factory;
        try {
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            metadata = service.createOMEXMLMetadata();
        } catch (DependencyException exc) {
            throw new FormatException("Could not create OME-XML store.", exc);
        } catch (ServiceException exc) {
            throw new FormatException("Could not create OME-XML store.", exc);
        }

        reader.setMetadataStore(metadata);
        // initialize the dataset
        reader.setId("/home/aaron/Pictures/Screenshots/reconstructed_mrc.png");
        // create a writer that will automatically handle any supported output format
        IFormatWriter writer = new ImageWriter();
        // give the writer a MetadataRetrieve object, which encapsulates all of the
        // dimension information for the dataset (among many other things)
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        writer.setMetadataRetrieve(service.asRetrieve(reader.getMetadataStore()));
        // initialize the writer
        writer.setId("/home/aaron/Pictures/Screenshots/mrc.xml");
        for (int series = 0; series < reader.getSeriesCount(); series++) {
            reader.setSeries(series);
            writer.setSeries(series);

            for (int image = 0; image < reader.getImageCount(); image++) {
                writer.saveBytes(image, reader.openBytes(image));
            }
        }
        reader.close();
        writer.close();
    }
}
