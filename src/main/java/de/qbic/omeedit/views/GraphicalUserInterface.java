package de.qbic.omeedit.views;

import de.qbic.omeedit.controllers.EditorController;
import loci.common.xml.XMLTools;
import loci.plugins.config.SpringUtilities;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;

import javax.swing.*;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

import de.qbic.omeedit.utilities.*;

public class GraphicalUserInterface implements InOut {
    /** This class aims to implement the input layer via a graphical user interface. It should only contain methods
     * which are needed for communication between the View and the Controller. The View itself should be implemented in
     * a separate class.
     */
    //------------------------------------------------------------------------------------------------------------------
    // Initialisations and Instantiations
    //------------------------------------------------------------------------------------------------------------------
    private EditorController controller;
    private EditorView view;
    //------------------------------------------------------------------------------------------------------------------
    // Constructor
    //------------------------------------------------------------------------------------------------------------------
    public GraphicalUserInterface() {
        view = new EditorView();
        view.setVisible(true);
        view.setDefaultCloseOperation(view.EXIT_ON_CLOSE);
        System.out.println("view initialised");
        initializeAddButton();
        System.out.println("add initialised");
        initializeDelButton();
        System.out.println("del initialised");
        controller = new EditorController();
        System.out.println("Controller initialised");
        // -------------------------------------------------------------------------------------------------------------
        // ADD ACTION LISTENERS
        // -------------------------------------------------------------------------------------------------------------
        /** Textfield
         * @return
         */
        view.textField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newText = view.textField.getText();
                try {
                    makeNewChange("edit", view.textField.getNode());
                    // update the view
                    if (view.tabbedPane.indexOfComponent(view.changeHistoryWindowPanel) != -1) {
                        updateChangeHistoryTab();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                view.textField.getNode().setUserObject(newText);
            }
        });
        /**
         *
         */
        view.undoChangeButton.addActionListener(e -> {
            try {
                undoChange();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        /**
         * apply the current change history to all files in a folder
         */
        view.applyChangesToFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int userSelection = chooser.showOpenDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File folder = chooser.getSelectedFile();
                try {
                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    System.out.println("Folder: " + folder.getAbsolutePath());
                    view.makeFeedbackTab();
                    Thread.sleep(2000);
                    controller.applyChangesToFolder(folder.getAbsolutePath());

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // reset the change history
        view.resetChangeHistoryButton.addActionListener(e -> {
            try {
                controller.resetChangeHistory();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // show change profile action listener
        view.showChangeButton.addActionListener(e -> {
            try {
                view.makeChangeHistoryTab();
                // add the changes to the table model
                addChangesToTable(view.historyTableModel);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // save change history action listener
        view.saveChangeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Change History");
            int userSelection = chooser.showSaveDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                try {
                    controller.saveChangeHistory(fileToSave.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // load change history action listener
        view.loadChangeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load Change history");
            int userSelection = chooser.showOpenDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = chooser.getSelectedFile();
                try {
                    controller.loadChangeHistory(fileToLoad.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // validate change history action listener
        view.validateChangeButton.addActionListener(e -> {
            try {
                controller.validateChangeHistory();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Opens the tutorial
        view.howToUseButton.addActionListener(e -> {
            try {
                controller.openTutorial();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // opens the about page
        view.aboutButton.addActionListener(e -> {
            try {
                controller.openAbout();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // set schema path

        view.setSchemaPath.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Set Schema Path");
            int userSelection = chooser.showOpenDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = chooser.getSelectedFile();
                try {
                    controller.setSchemaPath(fileToLoad.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        view.openImageButton.addActionListener(e -> {
            System.out.println("Open Image button pressed");
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String path = chooser.getSelectedFile().getAbsolutePath();
                try {
                    loadImage(path);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        view.openXML.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File fileToOpen = chooser.getSelectedFile();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.openXML(fileToOpen.getAbsolutePath());
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            get();
                            System.out.println("Successfully opened " + fileToOpen.getPath());
                        } catch (Exception ex) {
                            System.out.println("Error opening " + fileToOpen.getPath());
                            view.reportError("Error opening " + fileToOpen.getPath());
                            throw new RuntimeException(ex);
                        }
                    }
                };
                worker.execute();
            }
        });

        // Opens the Current XML in the topPanel of the GUI, in a new tab
        view.showCurrentXML.addActionListener(e -> {
            try {
                controller.showCurrentXML();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        view.exportOmeTiffButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to OmeTiff");
            int userSelection = chooser.showSaveDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.exportToOmeTiff(fileToSave.getPath());
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            System.out.println("Successfully exported to " + fileToSave.getPath());
                            view.reportSuccess("Successfully exported to " + fileToSave.getPath());
                            System.out.println("Successfully exported to " + fileToSave.getPath());
                        } catch (Exception ex) {
                            System.out.println("Error exporting to " + fileToSave.getPath());
                            throw new RuntimeException(ex);
                        }
                    }
                };
                worker.execute();
            }
        });

        view.exportOmeXmlButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to OmeXml");
            int userSelection = chooser.showSaveDialog(view.splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        controller.exportToOmeXml(fileToSave.getPath());
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            get();
                            System.out.println("Successfully exported to " + fileToSave.getPath());
                            view.reportSuccess("Successfully exported to " + fileToSave.getPath());
                            System.out.println("Successfully exported to " + fileToSave.getPath());
                        } catch (Exception ex) {
                            System.out.println("Error exporting to " + fileToSave.getPath());
                            throw new RuntimeException(ex);
                        }
                    }
                };
                worker.execute();
            }
        });

        // Add an action listener to the JCheckBoxMenuItem
        view.simplifiedTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the source of the event
                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                // Get the selection state of the checkbox
                boolean selected = source.isSelected();
                // Perform some action based on the selection state
                controller.setSimplified(selected);
                controller.updateTree();
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    // Methods for the Controller
    //------------------------------------------------------------------------------------------------------------------


    @Override
    public void loadImage(String path) throws Exception {
        controller.openImage(path);
        view.makeChangeHistoryTab();
        // add the changes to the table model
        addChangesToTable(view.historyTableModel);
        String title = path.substring(path.lastIndexOf("/") + 1);
        makeNewTreeTab(controller.getXMLDoc(), controller.getSimplified(), title);

    }

    @Override
    public void loadChangeHistory(String path) throws Exception {
        controller.loadChangeHistory(path);
        // apply changes to the view port and show the change history
        view.makeChangeHistoryTab();
        // add the changes to the table model
        addChangesToTable(view.historyTableModel);
        view.updateTree();

    }
    
    @Override
    public void applyChangeHistory(String path) throws Exception {
        controller.applyChangesToFile(path);
    }

    @Override
    public void saveImage(String path) throws Exception {
            controller.exportToOmeTiff(path);

    }
    // -----------------------------------------------------------------------------------------------------------------
    // Methods for the View
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Takes a table model and adds changes stored in the change history to it
     */
    public void addChangesToTable(XMLTableModel model) throws Exception {
        // get the change history
        LinkedList<XMLChange> changeHistory = controller.getChangeHistory();

        controller.validateChangeHistory(controller.getXMLDoc());
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
     * Adds an attribute to the editPanel
     * @param labelText
     * @param node
     */
    private void addAttributeButton(String labelText, XMLNode node) {
        // add a new attribute to the editPanel consisting of a label, a textfield and a delete button in a new panel
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new BorderLayout(0,0));
        JToggleButton attrButton = new JToggleButton(labelText);
        // set the unselected background color of the button to gray
        attrButton.setBackground(Color.LIGHT_GRAY);
        // set the border of the attribute Button
        view.makeLineBorder(attrButton);
        // make new textfield for the attribute
        NodedTextField textField = new NodedTextField(node);
        // give the textfield  a border
        view.makeLineBorder(textField);
        // add to button group so only one attribute can be selected at a time
        view.bg.add(attrButton);
        // change the background color of the textfield when it is selected
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(view.PASTEL_BLUE);
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField.setBackground(Color.WHITE);
                textField.setText(textField.getNode().getUserObject().toString());
            }
        });

        // add a listener to the textfield to detect changes
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!textField.getNode().getUserObject().equals(textField.getText())) {
                    textField.getNode().setUserObject(textField.getText());
                    try {
                        makeNewChange("modify" , textField.getNode());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("change detected");
                }
                textField.getParent().requestFocus();
            }
        });
        // add a listener to the button to detect selection and show the delete button
        attrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // remove the add button from the titlePanel
                view.titlePanel.remove(view.addButton);
                // set the to be deleted node to the currently selected attribute
                view.toBeDeletedNode = node.getParent();
                // add the delete button to the attributePanel
                attributePanel.add(view.delButton, BorderLayout.EAST);
                attributePanel.revalidate();
                attributePanel.repaint();
            }
        });
        // set the size of the buttons and the textfield
        Dimension d = new Dimension(view.BUTTON_WIDTH, view.BUTTON_HEIGHT);
        attributePanel.setPreferredSize(d);
        attrButton.setPreferredSize(d);
        view.delButton.setPreferredSize(d);
        // set the minimum size of the buttons and the textfield
        attributePanel.setMinimumSize(d);
        attrButton.setMinimumSize(d);
        view.delButton.setMinimumSize(d);
        // set the maximum size of the buttons and the textfield
        attributePanel.setMaximumSize(d);
        attrButton.setMaximumSize(d);
        view.delButton.setMaximumSize(d);

        // add the components to the panel and the panel to the editPanel
        attributePanel.add(attrButton, BorderLayout.WEST);
        attributePanel.add(textField, BorderLayout.CENTER);
        view.editPanel.add(attributePanel);

        // increase the labelCount so the layout can be updated
        view.labelCount +=1;
    }
    /**
     * Creates the editPanel, which shows the attributes and text nodes of the currently selected element node.
     */
    private void makeEditPanel() {
        // make border for the editPanel
        // makePanelBorder(editPanel);
        // add the Element Button to the editPanel
        view.addElementButton();
        // draw the children of the element node
        for (int c=0; c<view.selectedNode.getChildCount();c++) {
            XMLNode child = (XMLNode) view.selectedNode.getChildAt(c);
            if (child.getType().equals("attribute")) {
                XMLNode childChild = child.getFirstChild();
                addAttributeButton(child.getUserObject().toString(), childChild);
            }
            else if (child.getType().equals("value")) {
                addAttributeButton(view.selectedNode.getUserObject().toString(), child);
            }
            else if (child.getType().equals("text")){
                addTextButton(child);
            }
        }
    }
    /**
     * Updates the editPanel, which shows the attributes and text nodes of the currently selected element node.
     */
    public void updateEditPanel() {
        // reset the editPanel
        view.labelCount = 0;
        view.editPanel.removeAll();
        view.editPanel.revalidate();
        view.editPanel.repaint();

        // identify the currently selected element node and add it as title button (the children will be added in addTitleButton())
        if (view.selectedNode.getType().equals("attribute") ||
                view.selectedNode.getType().equals("text") ||
                view.selectedNode.getType().equals("value")) {

            view.selectedNode = view.selectedNode.getParent();
            makeEditPanel();
        }
        else {
            makeEditPanel();
        }
        JPanel spacer = new JPanel();
        view.labelCount++;
        view.editPanel.add(spacer);
        spacer.setPreferredSize(new Dimension(0, spacer.getHeight()));
        SpringUtilities.makeCompactGrid(view.editPanel, view.labelCount, 1,0,0,0, 0);
        view.scrollPaneBottom.setViewportView(view.editPanel);
    }

    private void initializeAddButton() {
        view.addButton.addActionListener(event -> {
            // create optionPane that is a pane containing two panels.
            // A radio button panel on the left and a panel for each type of node on the right
            // The right panel is only visible when the corresponding radio button is selected
            JOptionPane optionPane = new JOptionPane();
            optionPane.setLayout(new BorderLayout(10, 10));
            optionPane.setPreferredSize(new Dimension(600, 150));

            // create left panel for radio buttons
            JPanel leftPanel = new JPanel();
            leftPanel.setLayout(new GridLayout(3, 1));
            leftPanel.setPreferredSize(new Dimension(200, 150));

            // create panel for the right part of the optionPane and create new SpringLayout
            JPanel rightPanel = new JPanel();
            SpringLayout rightPanelLayout = new SpringLayout();
            rightPanel.setLayout(rightPanelLayout);
            rightPanel.setPreferredSize(new Dimension(400, 150));

            // add leftPanel and rightPanel to optionPane
            optionPane.add(leftPanel, BorderLayout.WEST);
            optionPane.add(rightPanel, BorderLayout.CENTER);

            // make my titled border for both panels
            view.makeTitledBorder(leftPanel, "Choose Node Type");
            view.makeTitledBorder(rightPanel, "Node Details");

            // create radio buttons for "Attribute", "Text" and "Element"
            JRadioButton attrRadio = new JRadioButton("Attribute");
            JRadioButton textRadio = new JRadioButton("Text");
            JRadioButton elementRadio = new JRadioButton("Element");

            // set action command for each radio button
            attrRadio.setActionCommand("attribute");
            textRadio.setActionCommand("text");
            elementRadio.setActionCommand("element");

            // add radio buttons to leftPanel
            leftPanel.add(attrRadio);
            leftPanel.add(textRadio);
            leftPanel.add(elementRadio);

            // group the radio buttons
            ButtonGroup group = new ButtonGroup();
            group.add(attrRadio);
            group.add(textRadio);
            group.add(elementRadio);

            // create textfields for each type of node
            JTextField attrField = new JTextField(1);
            JTextField valField = new JTextField(1);
            JTextField textField = new JTextField(1);
            JTextField elementField = new JTextField(1);
            JTextField elementIDField = new JTextField(1);

            // create label for each textfield
            JLabel attrLabel = new JLabel("Attribute:");
            JLabel valLabel = new JLabel("Value:");
            JLabel textLabel = new JLabel("Text:");
            JLabel elementLabel = new JLabel("Element:");
            JLabel elementIDLabel = new JLabel("ID:");

            // set preferred width for each textfield to textfield width and height to BUTTON_HEIGHT
            attrField.setPreferredSize(new Dimension(attrField.getWidth(), view.BUTTON_HEIGHT));
            valField.setPreferredSize(new Dimension(valField.getWidth(), view.BUTTON_HEIGHT));
            textField.setPreferredSize(new Dimension(textField.getWidth(), view.BUTTON_HEIGHT));
            elementField.setPreferredSize(new Dimension(elementField.getWidth(), view.BUTTON_HEIGHT));
            elementIDField.setPreferredSize(new Dimension(elementIDField.getWidth(), view.BUTTON_HEIGHT));

            // set labels for each textfield
            attrLabel.setLabelFor(attrField);
            valLabel.setLabelFor(valField);
            textLabel.setLabelFor(textField);
            elementLabel.setLabelFor(elementField);
            elementIDLabel.setLabelFor(elementIDField);

            // set default selection
            attrRadio.setSelected(true);
            rightPanel.add(attrLabel);
            rightPanel.add(attrField);
            rightPanel.add(valLabel);
            rightPanel.add(valField);
            SpringUtilities.makeCompactGrid(rightPanel, 2, 2, 5, 5, 5, 5);

            // add actionlistener to radio buttons, upon selection, change the right panel to the selected type
            attrRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // reset rightPanel
                    rightPanel.removeAll();

                    // add labels and textfields to rightPanel
                    rightPanel.add(attrLabel);
                    rightPanel.add(attrField);
                    rightPanel.add(valLabel);
                    rightPanel.add(valField);

                    // Lay out the panel by defining SpringUtilities constraints
                    SpringUtilities.makeCompactGrid(rightPanel, rightPanel.getComponentCount()/2, 2, 5, 5, 5, 5);

                    // revalidate and repaint
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            });
            textRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // reset rightPanel
                    rightPanel.removeAll();

                    // add labels and textfields to rightPanel
                    rightPanel.add(textLabel);
                    rightPanel.add(textField);

                    // Lay out the panel by defining SpringUtilities constraints
                    SpringUtilities.makeCompactGrid(rightPanel, rightPanel.getComponentCount()/2, 2, 5, 5, 5, 5);

                    // revalidate and repaint
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            });
            elementRadio.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // reset rightPanel
                    rightPanel.removeAll();

                    // add labels and textfields to rightPanel
                    rightPanel.add(elementLabel);
                    rightPanel.add(elementField);
                    rightPanel.add(elementIDLabel);
                    rightPanel.add(elementIDField);

                    // Lay out the panel by defining SpringUtilities constraints
                    SpringUtilities.makeCompactGrid(rightPanel, rightPanel.getComponentCount()/2, 2, 5, 5, 5, 5);

                    // revalidate and repaint
                    rightPanel.revalidate();
                    rightPanel.repaint();
                }
            });

            int confirmed = JOptionPane.showConfirmDialog(null, optionPane,
                    "Create new Node", JOptionPane.OK_CANCEL_OPTION);

            if (confirmed == JOptionPane.OK_OPTION) {
                String selection = group.getSelection().getActionCommand();
                if (selection.equals("attribute")) {
                    // create new XMLNode and add it to the selected node
                    XMLNode newAttrNode = new XMLNode();
                    XMLNode newValNode = new XMLNode();
                    newAttrNode.add(newValNode);
                    newAttrNode.setUserObject(attrField.getText());
                    newValNode.setUserObject(valField.getText());
                    newAttrNode.setType("attribute");
                    newValNode.setType("value");
                    view.selectedNode.add(newAttrNode);

                    // add the new node to the change history
                    try {
                        makeNewChange("add", newAttrNode);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // add the new node to the argument panel
                    addAttributeButton(newAttrNode.getUserObject().toString(), newValNode);

                    // redraw the argument panel
                    updateEditPanel();
                    view.myTree.updateUI();
                }
                else if (selection.equals("text")) {
                    // create new XMLNode and add it to the selected node
                    XMLNode newTextNode = new XMLNode();
                    newTextNode.setUserObject(textField.getText());
                    newTextNode.setType("text");
                    view.selectedNode.add(newTextNode);

                    // add the new node to the change history
                    try {
                        makeNewChange("add", newTextNode);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // add the new node to the argument panel
                    addAttributeButton(newTextNode.getUserObject().toString(), newTextNode);

                    // redraw the argument panel
                    updateEditPanel();
                    view.myTree.updateUI();
                }
                else if (selection.equals("element")) {
                    // create new XMLNode and add it to the selected node
                    XMLNode newElementNode = makeXmlNode(elementField, elementIDField);
                    // redraw the argument panel
                    updateEditPanel();
                    System.out.println(view.selectedNode.getChildCount());
                    // redraw the tree
                    XMLTreeModel model = (XMLTreeModel) view.myTree.getModel();
                    model.insertNodeInto(newElementNode, view.selectedNode, view.selectedNode.getChildCount());
                    System.out.println(view.selectedNode.getChildCount());
                    view.myTree.updateUI();
                    // add the new node to the change history
                    try {
                        makeNewChange("add", newElementNode);
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }
            }
            // make sure the option pane closes when the user clicks cancel
            else {
                optionPane.setVisible(false);
            }
        });
    }

    @NotNull
    private static XMLNode makeXmlNode(JTextField elementField, JTextField elementIDField) {
        XMLNode newElementNode = new XMLNode();
        newElementNode.setUserObject(elementField.getText());
        newElementNode.setType("element");
        // create a new ID node for the new element
        XMLNode newIDNode = new XMLNode();
        newIDNode.setUserObject("ID");
        newIDNode.setType("attribute");
        // create a new ID value node for the new element
        XMLNode newIDValNode = new XMLNode();
        newIDValNode.setUserObject(elementIDField.getText());
        newIDValNode.setType("value");
        // add the ID value node to the ID node
        newIDNode.add(newIDValNode);
        // add the ID node to the new element node
        newElementNode.add(newIDNode);
        return newElementNode;
    }

    /**
     *
     */
    private void initializeDelButton() {
        // remove all the action listeners from the delete button
        for (ActionListener al : view.delButton.getActionListeners()) {
            view.delButton.removeActionListener(al);
        }
        // add action listener to delete button
        view.delButton.addActionListener(event -> {
            System.out.println("Delete button pressed");
            System.out.println("To be Deleted node: " + view.toBeDeletedNode.getUserObject());

            // remove the node from the tree
            try {
                makeNewChange("delete", view.toBeDeletedNode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            XMLNode parentNode = view.toBeDeletedNode.getParent();
            view.selectedNode = parentNode;
            parentNode.remove(view.toBeDeletedNode);

            view.myTree.updateUI();
            updateEditPanel();
        });
    }
    /**
     * makeNewChange call to the controller to also update the view.
     */
    public void makeNewChange(String changeType, XMLNode node) throws Exception {
         controller.makeNewChange(changeType, node);
        // update the view
        if (view.tabbedPane.indexOfComponent(view.changeHistoryWindowPanel) != -1) {
            updateChangeHistoryTab();
        }
    }


    /**
     * Adds a text button to the editPanel
     * @param node
     */
    public void addTextButton(XMLNode node) {
        // add a new text to the editPanel consisting of a label, a textfield and a delete button in a new panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0,0));
        NodedTextField textField = new NodedTextField(node);
        // give the textfield  a border
        view.makeLineBorder(textField);

        // add a listener to the textfield to dtetect if it is selected
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(view.PASTEL_BLUE);
            }
            @Override
            public void focusLost(FocusEvent e) {
                textField.setBackground(Color.WHITE);
                textField.setText(textField.getNode().getUserObject().toString());
            }
        });

        // add a listener to the textfield to detect changes
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (!textField.getNode().getUserObject().equals(textField.getText())) {
                    textField.getNode().setUserObject(textField.getText());
                    try {
                        makeNewChange("modify" , textField.getNode());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("change detected");
                }
                textField.getParent().requestFocus();
            }
        });

        // add a mouse listener to the textfield to detect selection and show the delete button
        textField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // remove the add button from the titlePanel
                    view.titlePanel.remove(view.addButton);

                    // set the to be deleted node to the currently selected text
                    view.toBeDeletedNode = node;

                    // add the delete button to the textPanel
                    textPanel.add(view.delButton, BorderLayout.EAST);
                    textPanel.revalidate();
                    textPanel.repaint();
                }
            }
        });

        // set the size of the buttons and the textfield
        Dimension d = new Dimension(view.BUTTON_WIDTH, view.BUTTON_HEIGHT);
        textPanel.setPreferredSize(d);
        view.delButton.setPreferredSize(d);
        // set the minimum size of the buttons and the textfield
        textPanel.setMinimumSize(d);
        view.delButton.setMinimumSize(d);
        // set the maximum size of the buttons and the textfield
        textPanel.setMaximumSize(d);
        view.delButton.setMaximumSize(d);

        // add the components to the panel and the panel to the editPanel
        //textPanel.add(textButton, BorderLayout.WEST);
        textPanel.add(textField, BorderLayout.CENTER);
        view.editPanel.add(textPanel);

        // increase the labelCount so the layout can be updated
        view.labelCount +=1;
    }
    /**
     * Creates a new xml-viewer tab.
     * @param dom the xml dom that is to be displayed in the tab
     * @param simplified whether the xml-view should be simplified (only show elements) or not (show all nodes)
     * @param title the title of the new xml-viewer tab
     */
    public void makeNewTreeTab(Document dom, boolean simplified, String title) throws TransformerException {
        // create a new XMLTree from the DOM
        view.myTree = new XMLTree(dom, simplified);
        view.myTree.setOpaque(true);
        view.myTree.setShowsRootHandles(false);
        view.myTree.putClientProperty("JTree.lineStyle", "None");
        view.myTree.setDragEnabled(true);
        // add a listener to the jTree
        view.myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                // get the selected node
                view.selectedNode = (XMLNode) view.myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });

        // add the jTree to a new panel and add it to the tabbedPane
        view.scrollPaneTop.setViewportView(view.myTree);

        view.makeNewTab(view.scrollPaneTop, title, view.TREE_SVG);
    }
    /**
     * Updates the xml-viewer tab with the specified dom tree,
     * @param dom the new xml dom that is to be displayed in the tab
     * @param simplified whether the xml-view should be simplified (only show elements) or not (show all nodes)
     */
    public void updateTreeTab(Document dom, boolean simplified){ // originally called updateTree
        // create a new XMLTree from the DOM
        view.myTree = new XMLTree(dom, simplified);
        view.myTree.setOpaque(true);
        view.myTree.setShowsRootHandles(false);
        view.myTree.putClientProperty("JTree.lineStyle", "None");
        view.myTree.setDragEnabled(true);
        // add a listener to the jTree
        view.myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                // get the selected node
                view.selectedNode = (XMLNode) view.myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });

        // add the jTree to a new panel and add it to the tabbedPane
        view.scrollPaneTop.setViewportView(view.myTree);
    }
    /**
     * Creates a new xml-viewer tab. This version does not allow the user to edit the xml.
     * @param dom the xml dom that is to be displayed in the tab
     * @param title the title of the new xml-viewer tab
     */
    public void showXMLTree(Document dom, String title) {
        XMLTree currentTree = new XMLTree(dom, false);
        currentTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                // get the selected node
                view.selectedNode = (XMLNode) view.myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });
        JScrollPane currentScrollPane = new JScrollPane();
        currentScrollPane.setViewportView(currentTree);
        view.makeNewTab(currentScrollPane, title, view.TREE_SVG);
    }

    public void openTutorial() throws IOException {
        String path = "./data/resources/HowToUse.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        view.makeNewTab(view.renderMarkdown(md), "How To Use", EditorView.HELP_SVG);
    }
    /**
     * Opens the about tab
     */
    public void openAbout() throws IOException {
        String path = "./README.md";
        String md = new String(Files.readAllBytes(Paths.get(path)));
        view.makeNewTab(view.renderMarkdown(md), "About XML-Editor", EditorView.HELP_SVG);
    }
    /**
     *
     */
    public void saveChangeHistory(String path){
        try {
            FileOutputStream f = new FileOutputStream(new File(path));
            ObjectOutputStream o = new ObjectOutputStream(f);
            // Write objects to file
            for (XMLChange c : controller.getChangeHistory()) {
                o.writeObject(c);
            }
            o.close();
            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    
    public void undoChange() throws Exception {
        controller.undoChange();
        Document updatedXMLDoc = controller.getXMLDoc();
        updateTreeTab(updatedXMLDoc, controller.getSimplified());
        view.makeChangeHistoryTab();
        // add the changes to the table model
        addChangesToTable(view.historyTableModel);
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
        controller.setXMLDoc(XMLTools.parseDOM(new File(path)));
        // apply changes to metadata
        Document new_xml_doc = (Document) controller.getXMLDoc().cloneNode(true);
        if (!controller.getChangeHistory().isEmpty()) {
            controller.applyChanges(new_xml_doc);
            view.makeChangeHistoryTab();
        }
        makeNewTreeTab(new_xml_doc, controller.getSimplified(), title);
    }
    /**
     *
     */
    public void setSimplified(boolean simplified) {
        controller.setSimplified(simplified);
    }
    /**
     *
     */
    public void showCurrentXML() throws Exception {
        Document example_xml_doc = (Document) controller.getXMLDoc().cloneNode(true);

        controller.applyChanges(example_xml_doc);

        System.out.println(XMLTools.indentXML(XMLTools.getXML(example_xml_doc)));
        System.out.println("All Changes applied");
        view.showXMLTree(example_xml_doc, example_xml_doc.getNodeName());
        view.setVisible(true);
    }




    /**
     * Update the change history tab with the new changes
     */
    public void updateChangeHistoryTab() throws Exception {
        // empty the table model
        view.historyTableModel.setRowCount(0);
        // add new data to the table model
        addChangesToTable(view.historyTableModel);
        view.changeHistoryPane.add(view.historyTable);
        view.changeHistoryPane.setViewportView(view.historyTable);
    }

}
