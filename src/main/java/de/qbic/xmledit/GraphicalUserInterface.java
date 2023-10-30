package de.qbic.xmledit;

public class GraphicalUserInterface {
    /** This class aims to implement the input layer via a graphical user interface. It should only contain methods
     * which are needed for communication between the View and the Controller. The View itself should be implemented in
     * a separate class.
     */
    //------------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    //------------------------------------------------------------------------------------------------------------------
    EditorController controller;
    GraphicalUserInterface(EditorController contr) {
        controller = contr;
    }

    //------------------------------------------------------------------------------------------------------------------
    // Methods
    //------------------------------------------------------------------------------------------------------------------

    public void exampleMethod() {
        // if button x pressed: controller.exampleMethod();
    }
}
