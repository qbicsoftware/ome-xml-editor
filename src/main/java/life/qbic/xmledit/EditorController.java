package life.qbic.xmledit;

import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.FormatException;
import loci.formats.ImageWriter;
import loci.formats.meta.IMetadata;
import loci.formats.services.OMEXMLService;
import net.imagej.ImageJ;
import ome.xml.meta.OMEXMLMetadataRoot;
import ome.xml.model.OMEModel;
import ome.xml.model.OMEModelImpl;
import ome.xml.model.enums.EnumerationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EditorController {
    //------------------------------------------------------------------------------------------------------------------
    // Instantiations
    //------------------------------------------------------------------------------------------------------------------
    public EditorView view;
    public Editor editor;
    //------------------------------------------------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------------------------------------------------
    public EditorController(EditorView MyView, Editor MyEditor) {
        editor = MyEditor;
        view = MyView;
    }
    /**
     * Takes a table model and adds changes stored in the change history to it
     */
    public void addChangesToTable(XMLTableModel model) throws TransformerException {
        // get the change history
        LinkedList<XMLChange> changeHistory = editor.getChangeHistory();
        validateChangeHistory(editor.xml_doc);
        int index = 0;
        for (XMLChange c : changeHistory) {
            model.addRow(new Object[]{index, c.getChangeType(), c.getLocation(), c.getNodeType()});
            if (c.getValidity()) {
                model.setRowColor(index, EditorView.PASTEL_GREEN);
                view.validationErrorsField.setText("No validation errors in most recent change found.");
                // set the text color to green
                view.validationErrorsField.setForeground(EditorView.DARK_GREEN);

            }
            else{
                model.setRowColor(index, EditorView.PASTEL_RED);
                view.validationErrorsField.setText(c.getValidationError());
                // set the text color to red
                view.validationErrorsField.setForeground(EditorView.DARK_RED);
            }
            index++;
        }
    }
    /**
     * Update the change history tab with the new changes
     */
    public void updateChangeHistoryTab() throws TransformerException {
        // empty the table model
        view.historyTableModel.setRowCount(0);
        // add new data to the table model
        addChangesToTable(view.historyTableModel);
        view.changeHistoryPane.add(view.historyTable);
        view.changeHistoryPane.setViewportView(view.historyTable);
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
        editor.changeHistory.add(change);

        if (toBeChanged.getType().equals("element") && changeType.equals("modify") && toBeChanged.getChildCount() > 0) {
            for (int c = 0; c < toBeChanged.getChildCount(); c++) {
                makeNewChange("add", (XMLNode) toBeChanged.getChildAt(c));
            }
            return;
        }

        // update the view
        if (view.tabbedPane.indexOfComponent(view.changeHistoryWindowPanel) != -1) {
            updateChangeHistoryTab();
        }
    }

    /**
     * Sets the schema path
     * @param schemaPath
     */
    public void setSchemaPath(String schemaPath) {
        editor.setSchemaPath(schemaPath);
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
        Document newXMLDom =  loadFile(path);
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
     * Exports the current metadata to an OME-TIFF file
     * @param path the path to the file
     */
    public void exportToOmeTiff(String path, Document newXML) throws IOException, FormatException {

        System.out.println("Inside exportToOmeTiff");
        System.out.println("Path: " + path);
        // time measurement
        System.out.println("Start export time"+ System.currentTimeMillis());
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.tif";
        // record metadata to OME-XML format
        ServiceFactory factory = null;
        IMetadata omexmlMeta = null;
        try {
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            omexmlMeta = service.createOMEXMLMetadata();
        } catch (DependencyException | ServiceException e) {
            view.reportError(e.toString());
        }
        System.out.println("After initialisation time"+ System.currentTimeMillis());

        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        editor.applyChanges(new_xml_doc);
        System.out.println("Applied changes time"+ System.currentTimeMillis());
        // set metadata
        editor.setXMLElement(new_xml_doc.getDocumentElement());
        OMEModel xmlModel = new OMEModelImpl();
        // MetadataRoot mdr = null;
        OMEXMLMetadataRoot mdr = null;
        // OME test = new OME();
        //OME.getChildrenByTagName();
        try {
            mdr = new OMEXMLMetadataRoot(editor.getXmlElement(), xmlModel);
        } catch (EnumerationException e) {
            view.reportError(e.toString());
        }
        omexmlMeta.setRoot(mdr);
        // define writer
        //OMETiffWriter writer = new OMETiffWriter();
        //BufferedImageWriter biwriter = new BufferedImageWriter(writer);

        // read pixels
        editor.getReader().setId(editor.id);
        BufferedImage[] pixelData = editor.readPixels2();
        editor.getReader().close();
        // write pixels
        //biwriter.setMetadataRetrieve(omexmlMeta);
        System.out.println("Writing to: " + outPath);
        //biwriter.setId(outPath);
        System.out.println("set metadata time"+ System.currentTimeMillis());
        System.out.println("pixelData.length: " + pixelData.length);
        System.out.println("pixelData[0].getWidth(): " + pixelData[0].getWidth());
        System.out.println("pixelData[0].getHeight(): " + pixelData[0].getHeight());


        ImageConverter converter = new ImageConverter();
        converter.testConvert(new ImageWriter(), omexmlMeta, editor.id, outPath);

        //for (int i = 0; i < pixelData.length; i++) {
        //    biwriter.saveImage(i, pixelData[i]);
        //}
        System.out.println("wrote pixels time"+ System.currentTimeMillis());
        // close writer
        //writer.close();
        //biwriter.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
        System.out.println("Done time"+ System.currentTimeMillis());
    }
    /**
     * Exports the current metadata to an OME-TIFF file
     * @param path the path to the file
     */
    public void exportToOmeTiff(String path) throws IOException, FormatException {

        System.out.println("Inside exportToOmeTiff");
        System.out.println("Path: " + path);
        // time measurement
        System.out.println("Start export time"+ System.currentTimeMillis());
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.tif";
        // record metadata to OME-XML format
        ServiceFactory factory = null;
        IMetadata omexmlMeta = null;
        try {
            factory = new ServiceFactory();
            OMEXMLService service = factory.getInstance(OMEXMLService.class);
            omexmlMeta = service.createOMEXMLMetadata();
        } catch (DependencyException | ServiceException e) {
            view.reportError(e.toString());
        }
        System.out.println("After initialisation time"+ System.currentTimeMillis());

        // apply changes to metadata
        Document new_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        editor.applyChanges(new_xml_doc);
        System.out.println("Applied changes time"+ System.currentTimeMillis());
        // set metadata
        editor.setXMLElement(new_xml_doc.getDocumentElement());
        OMEModel xmlModel = new OMEModelImpl();
        // MetadataRoot mdr = null;
        OMEXMLMetadataRoot mdr = null;
        // OME test = new OME();
        //OME.getChildrenByTagName();
        try {
            mdr = new OMEXMLMetadataRoot(editor.getXmlElement(), xmlModel);
        } catch (EnumerationException e) {
            view.reportError(e.toString());
        }
        omexmlMeta.setRoot(mdr);
        // define writer
        //OMETiffWriter writer = new OMETiffWriter();
        //BufferedImageWriter biwriter = new BufferedImageWriter(writer);

        // read pixels
        editor.getReader().setId(editor.id);
        BufferedImage[] pixelData = editor.readPixels2();
        editor.getReader().close();
        // write pixels
        //biwriter.setMetadataRetrieve(omexmlMeta);
        System.out.println("Writing to: " + outPath);
        //biwriter.setId(outPath);
        System.out.println("set metadata time"+ System.currentTimeMillis());
        System.out.println("pixelData.length: " + pixelData.length);
        System.out.println("pixelData[0].getWidth(): " + pixelData[0].getWidth());
        System.out.println("pixelData[0].getHeight(): " + pixelData[0].getHeight());


        ImageConverter converter = new ImageConverter();
        converter.testConvert(new ImageWriter(), omexmlMeta, editor.id, outPath);

        //for (int i = 0; i < pixelData.length; i++) {
        //    biwriter.saveImage(i, pixelData[i]);
        //}
        System.out.println("wrote pixels time"+ System.currentTimeMillis());
        // close writer
        //writer.close();
        //biwriter.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
        System.out.println("Done time"+ System.currentTimeMillis());
    }
    /**
     * Exports the current metadata to an OME-XML file
     * @param path the path to the file
     * @throws Exception
     */
    public void exportToOmeXml(String path, Document newXML) throws Exception {
        System.out.println("Inside exportToOmeXml");
        System.out.println("Path: " + path);
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.xml";
        // apply changes to metadata
        Document new_xml_doc = (Document) newXML.cloneNode(true);
        editor.applyChanges(new_xml_doc);
        // write to file
        System.out.println("Writing to: " + outPath);
        FileOutputStream xmlOutStream = new FileOutputStream(outPath);
        XMLTools.writeXML(xmlOutStream, new_xml_doc);
        xmlOutStream.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
    }
    /**
     * Exports the current metadata to an OME-XML file
     * @param path the path to the file
     * @throws Exception
     */
    public void exportToOmeXml(String path) throws Exception {
        System.out.println("Inside exportToOmeXml");
        System.out.println("Path: " + path);
        // define output path
        int dot = path.lastIndexOf(".");
        String outPath = (dot >= 0 ? path.substring(0, dot) : path) + "_edited_" + ".ome.xml";
        // apply changes to metadata
        Document new_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        editor.applyChanges(new_xml_doc);
        // write to file
        System.out.println("Writing to: " + outPath);
        FileOutputStream xmlOutStream = new FileOutputStream(outPath);
        XMLTools.writeXML(xmlOutStream, new_xml_doc);
        xmlOutStream.close();
        // refocus gui
        view.setVisible(true);
        System.out.println("[done]");
    }
    /**
     *
     */
    public void showCurrentXML() throws Exception {
        Document example_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);

        editor.applyChanges(example_xml_doc);

        System.out.println(XMLTools.indentXML(XMLTools.getXML(example_xml_doc)));
        System.out.println("All Changes applied");
        view.showXMLTree(example_xml_doc, example_xml_doc.getNodeName());
        view.setVisible(true);
    }
    public void openTutorial() throws IOException {
        String path = "./data/resources/HowToUse.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        view.makeNewTab(view.renderMarkdown(md), "How To Use", GUI.HELP_SVG);
    }
    /**
     * Opens the about tab
     */
    public void openAbout() throws IOException {
        String path = "./README.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        view.makeNewTab(view.renderMarkdown(md), "About XML-Editor", GUI.HELP_SVG);
    }
    /**
     *
     */
    public LinkedList<XMLChange> getChangeHistory() {
        return editor.getChangeHistory();
    }
    /**
     *
     */
    public void saveChangeHistory(String path){
        try {
            FileOutputStream f = new FileOutputStream(new File(path));
            ObjectOutputStream o = new ObjectOutputStream(f);
            // Write objects to file
            for (XMLChange c : editor.getChangeHistory()) {
                o.writeObject(c);
            }
            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     */
    public void loadChangeHistory(String path) throws Exception {
        try {
            FileInputStream fi = new FileInputStream(new File(path));
            ObjectInputStream oi = new ObjectInputStream(fi);
            // Write objects to file
            boolean moreObjects = true;
            while (moreObjects) {
                XMLChange c = (XMLChange) oi.readObject();
                editor.addChange(c);
                // check if there are more objects
                if (oi.available() == 0) {
                    moreObjects = false;
                }
            }
            oi.close();
            fi.close();
            // apply changes to the view port and show the change history
            view.makeChangeHistoryTab();
            updateTree();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     *
     */
    public void updateTree() {
        // define a new xml document
        Document new_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        // apply all changes to the original xml
        editor.applyChanges(new_xml_doc);
        // update the tree
        view.updateTreeTab(new_xml_doc, editor.simplified);
    }
    /**
     *
     */
    public Document loadFile(String path) throws IOException, ServiceException, FormatException, ParserConfigurationException, SAXException {
        String[] args = new String[2];
        args[0] = path; // the id parameter
        args[1] = "-omexml-only";
        editor.omexmlOnly = true;
        editor.omexml = true;
        editor.id = path;

        editor.createReader();
        editor.configureReaderPreInit();
        editor.getReader().setId(path);
        editor.configureReaderPostInit();

        editor.getReader().setSeries(editor.series);

        String xml = editor.getOMEXML();
        editor.getReader().close();
        return XMLTools.parseDOM(xml);
    }
    /**
     *
     */
    public void openImage(String path) throws Exception {
        // make title from path
        String title = path.substring(path.lastIndexOf("/") + 1);
        editor.setXMLDoc(loadFile(path));
        Document new_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        if (!editor.getChangeHistory().isEmpty()) {
            editor.applyChanges(new_xml_doc);
            view.makeChangeHistoryTab();
        }
        view.makeNewTreeTab(new_xml_doc, editor.simplified, title);
    }
    /**
     * Opens an external XML file and applies loaded changes to it
     * @param path the path to the file
     * @throws Exception
     */
    public void openXML(String path) throws Exception {
        // make tab title from path
        String title = path.substring(path.lastIndexOf("/") + 1);
        // read the file
        editor.setXMLDoc(XMLTools.parseDOM(new File(path)));
        // apply changes to metadata
        Document new_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        if (!editor.getChangeHistory().isEmpty()) {
            editor.applyChanges(new_xml_doc);
            view.makeChangeHistoryTab();
        }
        view.makeNewTreeTab(new_xml_doc, editor.simplified, title);
    }
    /**
     *
     */
    public void undoChange() throws Exception {
        if (!editor.getChangeHistory().isEmpty()) {
            // remove the last change from the history
            editor.getChangeHistory().removeLast();
            // define a new xml document
            Document new_xml_doc = (Document) editor.getXmlElement().cloneNode(true);
            // apply all changes to the original xml
            editor.applyChanges(new_xml_doc);
            view.updateTreeTab(new_xml_doc, editor.simplified);
            view.makeChangeHistoryTab();
        }
    }
    /**
     *
     */
    public void resetChangeHistory() throws Exception {
        editor.setChangeHistory(new LinkedList<XMLChange>());
        try {
            view.makeChangeHistoryTab();
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *
     */
    public void startWithGUI() {
        final ImageJ ij = new ImageJ();
        ij.command().run(EditorFijiPlugin.class, true);
    }
    /**
     *
     */
    public boolean validateChangeHistory(Document newXMLDom) throws TransformerException {
        System.out.println("- - - - Validating Change History - - - -");
        Document example_xml_doc = (Document) newXMLDom.cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : editor.getChangeHistory()) {
            System.out.println("- - - - Applying Change - - - -");
            System.out.println("ToBeChangedNode: " + c.getToBeChangedNode().getUserObject().toString());
            System.out.println("ToBeChangedNode Type: " + c.getNodeType());
            System.out.println("Change Type: " + c.getChangeType());
            editor.applyChange(c, example_xml_doc, example_xml_doc, 0);
            try {
                // Element xmlExampleElement = example_xml_doc.getDocumentElement();
                //OMEModel xmlModel = new OMEModelImpl();
                // catch verification errors and print them
                // MetadataRoot mdr = new OMEXMLMetadataRoot(xmlExampleElement, xmlModel);
                // XMLTools.validateXML(XMLTools.getXML(example_xml_doc));
                if (!(new File(editor.schemaPath).exists())) {
                    view.popupSchemaHelp();
                }
                String error = XMLValidator.validateOMEXML(XMLTools.getXML(example_xml_doc), editor.schemaPath);
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
            changeHistoryValidity = editor.getChangeHistory().getLast().getValidity();
        }
        return changeHistoryValidity;
    }
    /**
     *
     */
    public boolean validateChangeHistory() throws TransformerException {
        System.out.println("- - - - Validating Change History - - - -");
        Document example_xml_doc = (Document) editor.getXMLDoc().cloneNode(true);
        boolean changeHistoryValidity= true;
        for (XMLChange c : editor.getChangeHistory()) {
            System.out.println("- - - - Applying Change - - - -");
            System.out.println("ToBeChangedNode: " + c.getToBeChangedNode().getUserObject().toString());
            System.out.println("ToBeChangedNode Type: " + c.getNodeType());
            System.out.println("Change Type: " + c.getChangeType());
            editor.applyChange(c, example_xml_doc, example_xml_doc, 0);
            try {
                // Element xmlExampleElement = example_xml_doc.getDocumentElement();
                //OMEModel xmlModel = new OMEModelImpl();
                // catch verification errors and print them
                // MetadataRoot mdr = new OMEXMLMetadataRoot(xmlExampleElement, xmlModel);
                // XMLTools.validateXML(XMLTools.getXML(example_xml_doc));
                if (!(new File(editor.schemaPath).exists())) {
                    view.popupSchemaHelp();
                }
                String error = XMLValidator.validateOMEXML(XMLTools.getXML(example_xml_doc), editor.schemaPath);
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
            changeHistoryValidity = editor.getChangeHistory().getLast().getValidity();
        }
        return changeHistoryValidity;
    }
    /**
     *
     */
    public void setSimplified(boolean simplified) {
        editor.setSimplified(simplified);
    }
}
