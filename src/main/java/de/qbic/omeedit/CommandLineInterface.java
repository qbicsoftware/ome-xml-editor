package de.qbic.omeedit;

import loci.formats.FormatException;

import java.io.IOException;

public class CommandLineInterface implements InOut{
    /** This class aims to implement the input layer via a commandline interface. It should only contain methods
     * which are needed for communication between the View and the Controller. The View itself should be implemented in
     * a separate class.
     */
    //------------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    //------------------------------------------------------------------------------------------------------------------
    EditorController controller;
    public CommandLineInterface() {
        controller = new EditorController();
    }

    //------------------------------------------------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------------------------------------------------

    public void exampleMethod() {
        // controller.exampleMethod();
    }

    @Override
    public void loadImage(String path) throws Exception {

    }

    @Override
    public void loadChangeHistory(String path) throws Exception {

    }

    @Override
    public void applyChangeHistory(String path) throws Exception {

    }

    @Override
    public void saveImage(String path) throws IOException, FormatException {

    }
}
