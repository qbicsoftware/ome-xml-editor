package de.qbic.xml_edit;

import io.scif.Metadata;
import io.scif.img.SCIFIOImgPlus;
import io.scif.ome.OMEMetadata;
import loci.formats.ome.OMEXMLMetadata;
import net.imagej.Dataset;
import net.imagej.ImgPlus;
import org.scijava.ui.DialogPrompt;
public class OME_XML_Reader {
    public static OMEXMLMetadata read(Dataset data)
    {

        ImgPlus<?> imp = data.getImgPlus();

        if (!(imp instanceof SCIFIOImgPlus)) {
            ij.ui().showDialog("This image has not been opened with SCIFIO.",
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return null;
        }

        SCIFIOImgPlus<?> sciImp = (SCIFIOImgPlus<?>) imp;
        Metadata metadata = sciImp.getMetadata();
        OMEMetadata omeMeta = new OMEMetadata(translatorService.getContext());

        if (!translatorService.translate(metadata, omeMeta, true)) {
            ij.ui().showDialog("Unable to extract OME Metadata",
                    DialogPrompt.MessageType.ERROR_MESSAGE);
            return null;
        }

        OMEXMLMetadata ome = omeMeta.getRoot();

        return ome;
    }

}
