package dev;
/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

import loci.common.services.ServiceFactory;
import loci.formats.ImageReader;
import loci.formats.meta.IMetadata;
import loci.formats.out.OMETiffWriter;
import loci.formats.services.OMEXMLService;
import ome.xml.meta.MetadataRoot;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Converts the given files to OME-TIFF format.
 */
public class ConvertToOmeTiff {

    public void convertToOmeTiff(String id, Document xml, byte[][] pixelData) throws Exception {
        ImageReader reader = new ImageReader();

        int dot = id.lastIndexOf(".");
        String outId = (dot >= 0 ? id.substring(0, dot) : id) + ".ome.tif";
        System.out.print("Converting " + id + " to " + outId + " ");

        // record metadata to OME-XML format
        ServiceFactory factory = new ServiceFactory();
        OMEXMLService service = factory.getInstance(OMEXMLService.class);
        IMetadata omexmlMeta = service.createOMEXMLMetadata();

        Element xmlElement = xml.getDocumentElement();
        OMEModel xmlModel = new OMEModelImpl();
        MetadataRoot mdr = new OMEXMLMetadataRoot(xmlElement, xmlModel);
        omexmlMeta.setRoot(mdr);

        System.out.println(omexmlMeta.getRoot().toString());

        // this should give me a filled omexmlMeta container



        // reader.setMetadataStore(omexmlMeta);
        // reader.setId(id);

        // configure OME-TIFF writer

        //writer.setCompression("J2K");

        // write out image planes
        /*
        int seriesCount = reader.getSeriesCount();
        for (int s=0; s<seriesCount; s++) {
            reader.setSeries(s);
            writer.setSeries(s);
            int planeCount = reader.getImageCount();
            for (int p=0; p<planeCount; p++) {
                byte[] plane = reader.openBytes(p);
                // write plane to output file
                writer.saveBytes(p, plane);
                System.out.print(".");
            }
        }

        */
        OMETiffWriter writer = new OMETiffWriter();
        writer.setMetadataRetrieve(omexmlMeta);
        writer.setId(outId);
        int sizeC = omexmlMeta.getPixelsSizeC(0).getValue();
        int sizeZ = omexmlMeta.getPixelsSizeZ(0).getValue();
        int sizeT = omexmlMeta.getPixelsSizeT(0).getValue();
        int samplesPerChannel = omexmlMeta.getChannelSamplesPerPixel(0, 0).getValue(); // NEEED TO FIX THIS
        sizeC /= samplesPerChannel;

        int imageCount = sizeC * sizeZ * sizeT;

        for (int image=0; image<imageCount; image++) {
            writer.saveBytes(image, pixelData[image]);
        }

        writer.close();
        reader.close();
        System.out.println(" [done]");
    }

}
