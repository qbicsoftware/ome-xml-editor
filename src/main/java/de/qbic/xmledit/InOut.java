package de.qbic.xmledit;

import loci.formats.FormatException;

import java.io.IOException;

public interface InOut {
    // Input
    public void loadImage(String path) throws Exception;
    public void loadChangeHistory(String path) throws Exception;
    public void applyChangeHistory(String path)throws Exception;
    // Output
    public void saveImage(String path) throws IOException, FormatException;

}
