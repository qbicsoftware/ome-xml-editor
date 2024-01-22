package de.qbic.omeedit.models;

// ---------------------------------------------------------------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------------------------------------------------------------

import de.qbic.omeedit.utilities.XMLChange;
import de.qbic.omeedit.deprecated.XMLEditor;
import de.qbic.omeedit.utilities.XMLNode;
import de.qbic.omeedit.controllers.EditorController;
import loci.common.DebugTools;
import loci.common.Location;
import loci.common.services.DependencyException;
import loci.common.services.ServiceException;
import loci.common.services.ServiceFactory;
import loci.common.xml.XMLTools;
import loci.formats.*;
import loci.formats.gui.AWTImageTools;
import loci.formats.gui.BufferedImageReader;
import loci.formats.in.DynamicMetadataOptions;
import loci.formats.in.MetadataLevel;
import loci.formats.meta.MetadataRetrieve;
import loci.formats.meta.MetadataStore;
import loci.formats.services.OMEXMLService;
import loci.formats.services.OMEXMLServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

public class EditorModel {
    /**
     * This class aims to implement the model layer. It should only act passively and thus only respond to requests from
     * the controller.
     */
    // -----------------------------------------------------------------------------------------------------------------
    // CONSTANTS
    // -----------------------------------------------------------------------------------------------------------------
    // -- Fields --

    private boolean doMeta = true;
    private boolean filter = true;
    private boolean minmax = false;
    private boolean merge = false;
    private boolean stitch = false;
    private boolean group = true;
    private boolean separate = false;
    private boolean expand = false;
    public boolean omexml = false;
    private boolean cache = false;
    private boolean originalMetadata = true;
    private boolean normalize = false;
    private boolean autoscale = false;
    public boolean omexmlOnly = false;
    private boolean validate = true;
    private boolean flat = true;
    public boolean simplified = true;
    private String omexmlVersion = null;
    private int start = 0;
    private int end = Integer.MAX_VALUE;
    public int series = 0;
    private int xCoordinate = 0, yCoordinate = 0, width = 0, height = 0;
    private String format = null;
    private String cachedir = null;
    public LinkedList<XMLChange> changeHistory = null;
    public Document xmlDoc;
    public Element xmlElement;
    private DynamicMetadataOptions options = new DynamicMetadataOptions();
    private MinMaxCalculator minMaxCalc;
    private DimensionSwapper dimSwapper;

    private String schemaPath = "./data/resources/ome.xsd";
    public String id = null;
    public EditorController controller;

    // -----------------------------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    // -----------------------------------------------------------------------------------------------------------------
    public EditorModel(EditorController cont) {
        controller = cont;
        changeHistory = new LinkedList<>();
    }
    // -----------------------------------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------------------------------
    /**
     *
     */
    public void applyChanges(Document dom) {
        System.out.println("Inside applyChanges");
        for (XMLChange c : changeHistory) {
            applyChange(c, dom, dom, 0);
        }
    }
    /**
     *
     */
    public void applyChange(XMLChange change, Document root, Node currentNode, int positionInQuery) {
        System.out.println("- - - -");
        System.out.println("Current Node: " + currentNode.getNodeName());
        // Some variables to make the code more readable
        LinkedList<XMLNode> query = change.getNodePath();
        boolean queryIsEmpty = query.size() == positionInQuery;
        if (queryIsEmpty) {
            switch (change.getNodeType()) {
                case "element":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new element");
                            Element newElement = root.createElement(change.getToBeChangedNode().getUserObject().toString());
                            newElement.setAttribute("ID", change.getToBeChangedNode().getID());
                            currentNode.appendChild(newElement);
                            break;
                        case "modify":
                            System.out.println("Modifying element");
                            currentNode.setTextContent(change.getToBeChangedNode().getUserObject().toString());
                            break;
                        case "delete":
                            System.out.println("Deleting element");
                            currentNode.getParentNode().removeChild(currentNode);
                            break;
                    }
                    break;

                case "attribute":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new attribute");
                            String attrName = change.getToBeChangedNode().getUserObject().toString();
                            String attrValue = change.getToBeChangedNode().getFirstChild().getUserObject().toString();
                            ((Element) currentNode).setAttribute(attrName, attrValue);
                            break;
                        case "modify":
                            System.out.println("Modifying attribute");
                            String attrName2 = change.getToBeChangedNode().getUserObject().toString();
                            String attrValue2 = change.getToBeChangedNode().getFirstChild().getUserObject().toString();
                            currentNode.getAttributes().getNamedItem(attrName2).setNodeValue(attrValue2);
                            // ((Element) currentNode).setAttribute(attrName2, attrValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting attribute");
                            String attrName3 = change.getToBeChangedNode().getUserObject().toString();
                            ((Element) currentNode).removeAttribute(attrName3);
                            break;
                    }
                    break;

                case "value":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new value???");
                            String attrName = change.getToBeChangedNode().getParent().getUserObject().toString();
                            String attrValue = change.getToBeChangedNode().getUserObject().toString();
                            ((Element) currentNode).setAttribute(attrName, attrValue);
                            break;
                        case "modify":
                            System.out.println("Modifying value");
                            String attrName2 = change.getToBeChangedNode().getParent().getUserObject().toString();
                            String attrValue2 = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.getAttributes().getNamedItem(attrName2).setNodeValue(attrValue2);
                            // ((Element) currentNode).setAttribute(attrName2, attrValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting value???");
                            String attrName3 = change.getToBeChangedNode().getParent().getUserObject().toString();
                            ((Element) currentNode).removeAttribute(attrName3);
                            break;
                    }
                    break;

                case "text":
                    switch (change.getChangeType()) {
                        case "add":
                            System.out.println("Adding new text");
                            String newNodeValue = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.setTextContent(newNodeValue);
                            break;
                        case "modify":
                            System.out.println("Modifying text");
                            String newNodeValue2 = change.getToBeChangedNode().getUserObject().toString();
                            currentNode.setTextContent(newNodeValue2);
                            break;
                        case "delete":
                            System.out.println("Deleting text");
                            currentNode.setTextContent("");
                            break;
                    }
                    break;
            }
            return;
        }
        // remainingQuery is not empty --> continue with next remainingQuery item
        else {
            XMLNode currentQueryItem = query.get(positionInQuery);
            String currentQueryItemName = currentQueryItem.getUserObject().toString();
            String currentQueryItemID = currentQueryItem.getID();
            // loop over all child nodes of the current node
            for (int c=0; c<currentNode.getChildNodes().getLength(); c++) {
                // if the name of the child node matches the next remainingQuery item
                if (currentQueryItemName.equals(currentNode.getChildNodes().item(c).getNodeName())) {
                    Node idNode = currentNode.getChildNodes().item(c).getAttributes().getNamedItem("ID");
                    System.out.println("ID Node: " + idNode);
                    System.out.println("Current Query Item ID: " + currentQueryItemID);
                    if ((idNode != null && idNode.getNodeValue().equals(currentQueryItemID)) || currentNode.getNodeName().equals("#document")) {
                        // name and id matches we are at the right node apply the change / go deeper
                        positionInQuery++;
                        applyChange(change, root, currentNode.getChildNodes().item(c), positionInQuery);
                        // return to break out of the recursion
                        return;
                    }
                    else {
                        break;
                    }
                }
            }
            // without the ID requirement
            for (int c=0; c<currentNode.getChildNodes().getLength(); c++) {
                // if the name of the child node matches the next remainingQuery item
                if (currentQueryItemName.equals(currentNode.getChildNodes().item(c).getNodeName())) {
                    // print a warning, that the ID is not found
                    System.out.println("Warning: ID Argument not found, proceeding without ID. This could lead to ambiguity and thus wrong changes.");
                    positionInQuery++;
                    applyChange(change, root, currentNode.getChildNodes().item(c), positionInQuery);
                    // return to break out of the recursion
                    return;
                }
            }
        }
        // remainingQuery not in graph --> print error
        throw new IllegalArgumentException("Query not in graph");
    }
    /**
     *
     */
    public LinkedList<XMLChange> getChangeHistory() {
        return this.changeHistory;
    }
    /**
     *
     */
    public void addChange(XMLChange change) {
        this.changeHistory.add(change);
    }
    /**
     * @return
     */
    public Document getXMLDoc() {
        return this.xmlDoc;
    }
    /**
     *
     */
    public void setSchemaPath(String path) {
        this.schemaPath = path;
    }
    /**
     *
     */

    public void setXMLElement(Element ele) {
        this.xmlElement = ele;
    }
    /**
     *
     */
    public void setXMLDoc(Document doc) {
        System.out.println("Inside setXMLDoc");
        this.xmlDoc = doc;
        System.out.println("XMLDoc: " + this.xmlDoc);
    }
    /**
     *
     */
    public void setChangeHistory(LinkedList<XMLChange> changeHistory) {
        this.changeHistory = changeHistory;
    }
    /**
     *
     */
    public void setSimplified(boolean simplified) {
        this.simplified = simplified;
    }


    public String getId() {
        return id;
    }

    public Object getSeries() {
        return series;
    }
    public boolean getSimplified() {
        return simplified;
    }

    public Element getXmlElement() {
        return xmlElement;
    }

    public String getSchemaPath() {
        return schemaPath;
    }

    public void removeLastChange() {
        this.changeHistory.removeLast();
    }
}
