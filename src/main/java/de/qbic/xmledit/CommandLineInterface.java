package de.qbic.xmledit;

public class CommandLineInterface {
    /** This class aims to implement the input layer via a commandline interface. It should only contain methods
     * which are needed for communication between the View and the Controller. The View itself should be implemented in
     * a separate class.
     */
    //------------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    //------------------------------------------------------------------------------------------------------------------
    EditorController controller;
    CommandLineInterface(EditorController contr) {
        controller = contr;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------------------------------------------------

    public void exampleMethod() {
        // controller.exampleMethod();
    }
}
