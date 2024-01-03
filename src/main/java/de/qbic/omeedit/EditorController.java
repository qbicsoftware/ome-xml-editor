package de.qbic.omeedit;

import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.IFormatHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditorController {
    /** This class aims to control or manage the interaction between the View and the Model. It is triggered via the
     * Command or Graphical interfaces (or really any interface implemented). It then calls the appropriate methods to
     * update the Model and the View.
     */
    //------------------------------------------------------------------------------------------------------------------
    // Instantiations
    //------------------------------------------------------------------------------------------------------------------
    private EditorView view;
    private EditorModel model;
    //------------------------------------------------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------------------------------------------------
    public EditorController() {
        model = new EditorModel(this);
    }


    /**
     * Adds Feedback to the Feedback table model
     *
     */
    public void addFeedback(FeedbackStore feedbackStore) {
        String fileName = feedbackStore.getFilePath().substring(feedbackStore.getFilePath().lastIndexOf(File.separator) + 1);
        view.feedBackTableModel.addRow(new Object[]{fileName, Boolean.toString(feedbackStore.getValidity()), Boolean.toString(feedbackStore.getExported())});
        if (feedbackStore.getValidity() & feedbackStore.getExported()) {
            // change color of the row to green
            view.feedBackTableModel.setRowColor(view.feedBackTableModel.getRowCount() - 1, EditorView.PASTEL_GREEN);
        }
        else {
            // change color of the row to red
            view.feedBackTableModel.setRowColor(view.feedBackTableModel.getRowCount() - 1, EditorView.PASTEL_RED);

        }
    }
    /**
     * Adds a change to the change history.
     * @param changeType the type of change that is to be maade on of the following: "add", "modify", "delete"
     * @param toBeChanged the node that is to be changed
     */
    public void makeNewChange(String changeType, XMLNode toBeChanged) throws MalformedURLException, TransformerException, SAXException {
        System.out.println("- - - - Make new change - - - -");

        // update the model
        XMLChange change = new XMLChange(changeType, toBeChanged);
        model.changeHistory.add(change);

        if (toBeChanged.getType().equals("element") && changeType.equals("modify") && toBeChanged.getChildCount() > 0) {
            for (int c = 0; c < toBeChanged.getChildCount(); c++) {
                makeNewChange("add", (XMLNode) toBeChanged.getChildAt(c));
            }
            return;
        }


    }

    /**
     * Sets the schema path
     * @param schemaPath
     */
    public void setSchemaPath(String schemaPath) {
        model.setSchemaPath(schemaPath);
    }
    /**
     * gets all files in the directory and returns them as a set
     * @param dir the directory to get the files from
     * @return a set of all files in the directory
     */
    public Set<String> getFilesInDir(String dir) {
        return Stream.of(new File(dir).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getName)
                .collect(Collectors.toSet());
    }
    /**
     * Applys the current change history to all valid files in the selected folder
     * @param path the path to the folder
     */
    public void applyChangesToFolder(String path) throws Exception {
        System.out.println("Inside applyChangesToFolder");
        // loop over all valid files in the folder
        for (String file : getFilesInDir(path)) {
            System.out.println("###############################################################");
            System.out.println("Current File: " + file);
            // measure time
            long time = System.currentTimeMillis();
            System.out.println("Start time: " + time);
            applyChangesToFile(path + "/" + file);

        }
        System.out.println("Done");
    }
    /**
     * Applys the current change history to the selected file
     * @param path the path to the file
     */
    public void applyChangesToFile(String path) throws Exception {
        FeedbackStore fbStore= new FeedbackStore(path);
        Document newXMLDom =  model.loadFile(path);
        if (validateChangeHistory(newXMLDom)) {
            fbStore.setValidity(true);
            try {
                exportToOmeTiff(path, newXMLDom);
                fbStore.setExported(true);
            }
            catch (Exception e) {
                fbStore.setExported(false);
                fbStore.setExportError(e.getMessage());
            }
        }
        else {
            fbStore.setValidity(false);
        }
        this.addFeedback(fbStore);
        // update teh feedback tabl
    }


    /**
     *
     */
    public LinkedList<XMLChange> getChangeHistory() {
        return model.getChangeHistory();
    }
    /**
     *
     */
    public void updateTree() {
        // define a new xml document
        Document new_xml_doc = (Document) model.getXMLDoc().cloneNode(true);
        // apply all changes to the original xml
        model.applyChanges(new_xml_doc);
        // update the tree
        view.updateTreeTab(new_xml_doc, model.simplified);
    }
    /**
     * @return
     */
    public void undoChange() throws Exception {
        if (!model.getChangeHistory().isEmpty()) {
            // remove the last change from the history
            model.getChangeHistory().removeLast();
            // define a new xml document

            Document new_xml_doc = model.getXMLDoc().cloneNode(true).getOwnerDocument();
            System.out.println("Undoing change");
            // apply all changes to the original xml
            model.applyChanges(new_xml_doc);
        }
    }
    /**
     *
     */
    public void resetChangeHistory() throws Exception {
        model.setChangeHistory(new LinkedList<XMLChange>());
        view.makeChangeHistoryTab();
    }
    /**
     *
     */
    public boolean validateChangeHistory(Document newXMLDom) throws TransformerException {
        System.out.println("- - - - Validating Change History - - - -");
        Document example_xml_doc = (Document) newXMLDom.cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : model.getChangeHistory()) {
            System.out.println("- - - - Applying Change - - - -");
            System.out.println("ToBeChangedNode: " + c.getToBeChangedNode().getUserObject().toString());
            System.out.println("ToBeChangedNode Type: " + c.getNodeType());
            System.out.println("Change Type: " + c.getChangeType());
            model.applyChange(c, example_xml_doc, example_xml_doc, 0);
            try {
                // Element xmlExampleElement = example_xml_doc.getDocumentElement();
                //OMEModel xmlModel = new OMEModelImpl();
                // catch verification errors and print them
                // MetadataRoot mdr = new OMEXMLMetadataRoot(xmlExampleElement, xmlModel);
                // XMLTools.validateXML(XMLTools.getXML(example_xml_doc));
                if (!(new File(model.schemaPath).exists())) {
                    view.popupSchemaHelp();
                }
                String error = XMLValidator.validateOMEXML(XMLTools.getXML(example_xml_doc), model.schemaPath);
                if (error == null){
                    System.out.println("XML is valid");
                    c.setValidity(true);
                    c.setValidationError("OME-XML is valid");
                }
                else {
                    System.out.println("XML is not valid");
                    c.setValidity(false);
                    c.setValidationError(error);
                }
            } catch (Exception e) {
                c.setValidity(false);
                c.setValidationError(e.getMessage());
            }
            changeHistoryValidity = model.getChangeHistory().getLast().getValidity();
        }
        return changeHistoryValidity;
    }
    /**
     *
     */
    public boolean validateChangeHistory() throws TransformerException {
        System.out.println("- - - - Validating Change History - - - -");
        Document example_xml_doc = (Document) model.getXMLDoc().cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : model.getChangeHistory()) {
            System.out.println("- - - - Applying Change - - - -");
            System.out.println("ToBeChangedNode: " + c.getToBeChangedNode().getUserObject().toString());
            System.out.println("ToBeChangedNode Type: " + c.getNodeType());
            System.out.println("Change Type: " + c.getChangeType());
            model.applyChange(c, example_xml_doc, example_xml_doc, 0);
            try {
                // Element xmlExampleElement = example_xml_doc.getDocumentElement();
                //OMEModel xmlModel = new OMEModelImpl();
                // catch verification errors and print them
                // MetadataRoot mdr = new OMEXMLMetadataRoot(xmlExampleElement, xmlModel);
                // XMLTools.validateXML(XMLTools.getXML(example_xml_doc));
                if (!(new File(model.schemaPath).exists())) {
                    view.popupSchemaHelp();
                }
                String error = XMLValidator.validateOMEXML(XMLTools.getXML(example_xml_doc), model.schemaPath);
                if (error == null){
                    System.out.println("XML is valid");
                    c.setValidity(true);
                    c.setValidationError("OME-XML is valid");
                }
                else {
                    System.out.println("XML is not valid");
                    c.setValidity(false);
                    c.setValidationError(error);
                }
            } catch (Exception e) {
                c.setValidity(false);
                c.setValidationError(e.getMessage());
            }
            changeHistoryValidity = model.getChangeHistory().getLast().getValidity();
        }
        return changeHistoryValidity;
    }
    /**
     *
     */
    public Document getXMLDoc() {
        return model.getXMLDoc();
    }

    public void setSimplified(boolean simplified) {
        model.setSimplified(simplified);
    }
    /**
     *
     */
    public void exportToOmeXml(String path) {

    }
    /**
     *
     */
    public void applyChanges(Document doc) {

    }


    public String getId() {
        return model.getId();
    }

    public BufferedImage[] readPixels() throws IOException, FormatException {
        return model.readPixels2();
    }

    public IFormatHandler getReader() {
        return model.getReader();
    }

    public Element getXmlElement() {
        return null;
    }

    public void setXMLElement(Element documentElement) {
    }

    public boolean getSimplified() {
        return model.getSimplified();
    }

    public void setXMLDoc(Document document) {
    }

    public String getOMEXML() {
        return null;
    }

    public Object getSeries() {
        return model.getSeries();
    }

    public void configureReaderPostInit() {
    }

    public void configureReaderPreInit() {
    }

    public void createReader() {
    }

    public void setId(String path) {
    }

    public void addChange(XMLChange c) {
    }

    public void loadChangeHistory(String path) throws Exception {
        try {
            FileInputStream fi = new FileInputStream(new File(path));
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Write objects to file
            boolean moreObjects = true;
            while (moreObjects) {
                XMLChange c = (XMLChange) oi.readObject();
                addChange(c);
                // check if there are more objects
                if (oi.available() == 0) {
                    moreObjects = false;
                }
            }
            oi.close();
            fi.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportToOmeTiff(String path, Document newXMLDom) {
    }
    public void exportToOmeTiff(String path) {
    }

    /**
     *
     */
    public Document openImage(String path) throws Exception {
        // make title from path
        String title = path.substring(path.lastIndexOf("/") + 1);
        Document xml_doc = model.loadFile(path);
        model.setXMLDoc(xml_doc);
        Document new_xml_doc = (Document) xml_doc.cloneNode(true);
        if (!getChangeHistory().isEmpty()) {
            applyChanges(new_xml_doc);
            //view.makeChangeHistoryTab();
        }
        return new_xml_doc;
    }

    public void showCurrentXML() {
    }

    public void openXML(String absolutePath) {
    }

    public void openAbout() {
    }

    public void openTutorial() {
    }

    public void saveChangeHistory(String absolutePath) {
    }

    public void setSeries(Object series) {

    }
}
