package de.qbic.xml_edit;

import loci.common.services.ServiceFactory;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.ome.OMEXMLMetadata;
import loci.formats.services.OMEXMLService;

public class Test1 {
    public static void main(String[] args) throws Exception {
        // parse command line arguments
        if (args.length < 1) {
            System.err.println("Usage: java GetMetadata imageFile [seriesNo]");
            System.exit(1);
        }

        String id = args[0];
        int series = args.length > 1 ? Integer.parseInt(args[1]) : 0;

        // create OME-XML metadata store
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata meta = service.createOMEXMLMetadata();
        OMEXMLMetadata data = service.createOMEXMLMetadata();

        // create format reader
        IFormatReader reader = new ImageReader();
        reader.setMetadataStore(meta);

        // initialize file
        System.out.println("Initializing " + id);
        reader.setId(id);

        int seriesCount = reader.getSeriesCount();
        if (series < seriesCount) reader.setSeries(series);
        series = reader.getSeries();
        System.out.println("\tImage series = " + series + " of " + seriesCount);

    }
}
