package de.qbic.omeedit.views;

import de.qbic.omeedit.controllers.EditorController;
import loci.formats.FormatException;

import java.io.IOException;

public class CommandLineInterface implements InOut, UserInterface {
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


    @Override
    public void loadImage(String path) throws Exception {

    }

    @Override
    public void loadChangeHistory(String path) throws Exception {

    }
    public void start() {
    // Initialize the controller
    controller = new EditorController();
}

    @Override
    public void applyChangeHistory(String path) throws Exception {

    }

    @Override
    public void saveImage(String path) throws IOException, FormatException {

    }
}
