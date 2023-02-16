// Mandatory for ImageInfo

package de.qbic.xml_edit;

import loci.common.services.ServiceException;
import loci.formats.FormatException;
import loci.plugins.config.SpringUtilities;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;

public class GUI extends javax.swing.JFrame{


    public static int SCREEN_WIDTH = 1000;
    public Editor edit;
    public static int SCREEN_HEIGHT = SCREEN_WIDTH*9/16;
    public static int BUTTON_HEIGHT = 25;
    public Document xml;
    public XmlJTree myTree;
    // these are the components we need.
    private JSplitPane splitPane;  // split the window in top and bottom
    private JPanel topPanel;       // container panel for the top
    private JPanel bottomPanel;    // container panel for the bottom
    private JScrollPane scrollPaneBottom; // makes the text scrollable
    private JScrollPane scrollPaneTop; // makes the text scrollable
    private NodedTextField textField;     // the text
    private JPanel contentPanelLower;      // under the text a container for all the input elements
    private JPanel contentPanelHigher;
    private JMenuItem saveButton;         // and a save  xml changes button
    private JMenuItem openSchemaButton;         // open new image file button
    private JMenuItem printCurrentXML;
    private JMenuItem validateXMLButton;         // validate the current xml button
    private JMenuItem exportButton;         // export xml and image to ome.tif button
    private JPanel argumentPane;
    private JMenuBar mb;
    private JMenu file, settings, help;
    private JMenuItem openImage;
    private static JPanel selectedPanel;
    private static JButton deleteButton;
    int labelCount =0;

    JButton addButton = new JButton("Add Node");


    Border fieldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);


    public GUI(Editor edit){

        this.edit = edit;
        this.setTitle("XML-Editor");
        makeMenu();
        // first, lets create the containers:
        // the splitPane devides the window in two components (here: top and bottom)
        // users can then move the devider and decide how much of the top component
        // and how much of the bottom component they want to see.

        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }
    private void makeMenu() {

        // INITIALIZE COMPONENTS #######################################################################################
        mb = new JMenuBar();
        file = new JMenu("File");
        settings = new JMenu("Settings");
        help = new JMenu("Help");
        openImage = new JMenuItem("Open Image");
        printCurrentXML = new JMenuItem("Print Current XML");
        exportButton = new JMenuItem("Export to OmeTiff");
        openSchemaButton = new JMenuItem("Open Schema");
        saveButton = new JMenuItem("Print Change Profile");
        validateXMLButton = new JMenuItem("Show Schema");

        splitPane = new JSplitPane();
        splitPane.setName("XML-Editor");
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        scrollPaneBottom = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        contentPanelLower = new JPanel();
        contentPanelHigher = new JPanel();
        textField = new NodedTextField();      // this text area will be put inside the scrollPane
        scrollPaneTop = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        argumentPane = new JPanel();


        // ADD ACTION LISTENERS ########################################################################################
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newText = textField.getText();
                makeHistoryEntry(textField.getNode(), "edit", newText);
                textField.getNode().setUserObject(newText);
            }
        });
        saveButton.addActionListener(e -> {
            System.out.println(edit.changeHistory);
        });
        openSchemaButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file",
                    "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    edit.readSchema(chooser.getSelectedFile().getAbsolutePath());
                }
                catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        openImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    edit.readImage(chooser.getSelectedFile().getAbsolutePath());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                } catch (FormatException ex) {
                    throw new RuntimeException(ex);
                } catch (ServiceException ex) {
                    throw new RuntimeException(ex);
                } catch (ParserConfigurationException ex) {
                    throw new RuntimeException(ex);
                } catch (SAXException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        printCurrentXML.addActionListener(e -> {
            try {
                edit.printOMEXML();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        validateXMLButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Show Schema");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();

                try {
                    edit.readSchema(fileToSave.getPath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                DefaultMutableTreeNode myRoot = (DefaultMutableTreeNode) myTree.getModel().getRoot();
                Enumeration en = myRoot.preorderEnumeration();
                while(en.hasMoreElements()){
                }
            }
        });
        exportButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to OmeTiff");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();

                try {
                    edit.exportToOmeTiff(fileToSave.getPath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                DefaultMutableTreeNode myRoot = (DefaultMutableTreeNode) myTree.getModel().getRoot();
                Enumeration en = myRoot.preorderEnumeration();
                while(en.hasMoreElements()){
                }
            }
        });

        // SET DIMENSIONS ##############################################################################################

        textField.setSize(WIDTH, BUTTON_HEIGHT);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        contentPanelLower.setMaximumSize(new Dimension(Integer.MAX_VALUE, exportButton.getHeight() * 4));

        // SET LAYOUTS #################################################################################################
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        contentPanelHigher.setLayout(new BoxLayout(contentPanelHigher, BoxLayout.X_AXIS));
        contentPanelLower.setLayout(new BoxLayout(contentPanelLower, BoxLayout.X_AXIS));
        SpringLayout argPaneLayout = new SpringLayout();
        argumentPane.setLayout(argPaneLayout);

        // SET BORDERS #################################################################################################
        makeStandardBorder(topPanel);
        makeStandardBorder(bottomPanel);
        makeStandardBorder(contentPanelLower);
        makeStandardBorder(argumentPane);

        // POPULATE COMPONENTS #########################################################################################
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window
        mb.add(file);
        file.add(openImage);
        file.add(printCurrentXML);
        file.add(exportButton);
        file.add(openSchemaButton);
        mb.add(settings);
        mb.add(help);
        help.add(saveButton);
        help.add(validateXMLButton);

        this.add(mb);
        this.setJMenuBar(mb);
        bottomPanel.add(scrollPaneBottom);                // first we add the scrollPane to the bottomPanel, so it is at the top

        topPanel.add(scrollPaneTop);

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(SCREEN_HEIGHT/2);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(topPanel);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(bottomPanel);            // and at the bottom we want our "bottomPanel"

        splitPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedPanel != null) {
                    selectedPanel.setBackground(null);
                    selectedPanel.remove(deleteButton);
                    selectedPanel.repaint();
                }
            }
        });
    }

    public void makeStandardBorder(JPanel p) {
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.setOpaque(true);
        p.setBackground(Color.WHITE);
    }

    public void makeHistoryEntry(DefaultMutableTreeNode originNode, String modification, String newText) {
        if (modification == "edit") {
            System.out.println("change \"Edit\" added to history");
            String path = Arrays.toString(originNode.getPath());
            LinkedList<String> location = new LinkedList<>();
            location.addAll(Arrays.asList(path.replace("[", "").replace("]", "").split(", ")));

            System.out.println("Path: " + path);
            System.out.println("New Text: " + newText);
            System.out.println("added new location: " + location.toString());

            XMLChange change = new XMLChange(modification, location);
            change.setNewContent(newText);
            edit.changeHistory.add(change);
        }
        else System.out.println("No change was added");
    }
    public void makeHistoryEntry(DefaultMutableTreeNode originNode, String modification, DefaultMutableTreeNode toBeAdded) {
        if (modification == "add") {
            System.out.println("change \"Addition\" added to history");
            String path = Arrays.toString(originNode.getPath());
            LinkedList<String> location = new LinkedList<>();
            location.addAll(Arrays.asList(path.replace("[", "").replace("]", "").split(", ")));

            XMLChange change = new XMLChange(modification, location);
            change.setNewContent(toBeAdded.getUserObject().toString());
            edit.changeHistory.add(change);
            if (toBeAdded.getChildCount()>0){
                for (int c=0; c< toBeAdded.getChildCount(); c++){
                    makeHistoryEntry(toBeAdded, "add", (XMLNode) toBeAdded.getChildAt(c));
                }
            }
        }
        else System.out.println("No change was added");
    }
    public void makeHistoryEntry(DefaultMutableTreeNode originNode, String modification) {
        if (modification == "del") {
            System.out.println("change \"Deletion\" added to history");
            String path = Arrays.toString(originNode.getPath());
            LinkedList<String> location = new LinkedList<>();
            location.addAll(Arrays.asList(path.replace("[", "").replace("]", "").split(", ")));

            XMLChange change = new XMLChange(modification, location);
            edit.changeHistory.add(change);
        }
    }
    public void makeTree(Document dom){
        myTree = new XmlJTree(dom);
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged();
            }
        });
        scrollPaneTop.setViewportView(myTree);
    }
    public void jTree1ValueChanged() {
        labelCount = 0;
        argumentPane.removeAll();
        XMLNode node = (XMLNode) myTree.getLastSelectedPathComponent();
        makeStandardBorder(argumentPane);
        if (node.getUserObject().toString().startsWith("@")) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getFirstChild();
            addLabeledTextField(node.getUserObject().toString(), child);
        }
        else if (node.getUserObject().toString().startsWith("#")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addLabeledTextField(parent.getUserObject().toString(), node);
        }
        else if (node.getUserObject().toString().startsWith(":")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addLabeledTextField(parent.getUserObject().toString(), node);
        }
        else {
            JPanel titlePanel = new JPanel();
            titlePanel.setLayout(new BorderLayout());
            JToggleButton titleButton = new JToggleButton(node.getUserObject().toString());
            titleButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AbstractButton abstractButton = (AbstractButton) e.getSource();
                    boolean selected = abstractButton.getModel().isSelected();

                    addButton.addActionListener(event -> {

                        JTextField xField = new JTextField(5);
                        JTextField yField = new JTextField(5);

                        JPanel myPanel = new JPanel();
                        myPanel.add(new JLabel("Name:"));
                        myPanel.add(xField);
                        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
                        myPanel.add(new JLabel(":Value:"));
                        myPanel.add(yField);

                        int confirmed = JOptionPane.showConfirmDialog(null, myPanel,
                                "Please Enter Name and Value", JOptionPane.OK_CANCEL_OPTION);
                        if (confirmed == JOptionPane.OK_OPTION) {
                            DefaultMutableTreeNode newNodeLabel = new DefaultMutableTreeNode();
                            DefaultMutableTreeNode newNodeValue = new DefaultMutableTreeNode();
                            newNodeLabel.add(newNodeValue);
                            newNodeLabel.setUserObject(xField.getText());
                            newNodeValue.setUserObject(yField.getText());
                            node.add(newNodeLabel);
                            makeHistoryEntry(node, "add", newNodeLabel);


                            addLabeledTextField(newNodeValue.getUserObject().toString(), newNodeLabel);
                            SpringUtilities.makeCompactGrid(argumentPane, labelCount, 1,5,5,5, 5);
                            scrollPaneBottom.setViewportView(argumentPane);
                        }
                    });

                    if (selected) {
                        titlePanel.add(addButton, BorderLayout.EAST);
                        titlePanel.repaint();
                        argumentPane.revalidate();
                        argumentPane.repaint();

                    }
                    else if (!selected) {
                        titlePanel.remove(addButton);
                        titlePanel.repaint();
                        argumentPane.revalidate();
                        argumentPane.repaint();
                    }
                }
            });
            titlePanel.add(titleButton, BorderLayout.WEST);
            argumentPane.add(titlePanel);
            labelCount +=1;
            for (int c=0; c<node.getChildCount();c++) {
                XMLNode child = (XMLNode) node.getChildAt(c);
                if (child.getUserObject().toString().startsWith("@")) {
                    DefaultMutableTreeNode childChild = (DefaultMutableTreeNode) child.getFirstChild();
                    addLabeledTextField(child.getUserObject().toString(), childChild);
                }
                else if (child.getUserObject().toString().startsWith(":")) {
                    addLabeledTextField(node.getUserObject().toString(), child);
                }
                else if (child.getUserObject().toString().startsWith("#")) {
                    addLabeledTextField(node.getUserObject().toString(), child);
                }
            }
        }
        SpringUtilities.makeCompactGrid(argumentPane, labelCount, 1,5,5,5, 5);
        scrollPaneBottom.setViewportView(argumentPane);
        textField.setText((String) node.getUserObject());
    }
    private void addLabeledTextField(String labelText, DefaultMutableTreeNode selectedNode) {
        JLabel label = new JLabel(labelText);
        label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
        NodedTextField textField = new NodedTextField((XMLNode) selectedNode);
        textField.setBorder(BorderFactory.createCompoundBorder(fieldBorder, new EmptyBorder(0, 10, 0, 0)));
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newText = textField.getText();
                makeHistoryEntry(textField.getNode(),"edit" , newText);
                textField.getNode().setUserObject(newText);
                System.out.println("change detected");
            }
        });

        label.setLabelFor(textField);
        label.setBorder(BorderFactory.createCompoundBorder(fieldBorder, new EmptyBorder(0, 0, 0, 10)));

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        fieldPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selectedPanel != null) {
                        selectedPanel.setBackground(null);
                        selectedPanel.remove(deleteButton);
                    }
                    JPanel clickedPanel = (JPanel) e.getSource();
                    clickedPanel.setBackground(Color.LIGHT_GRAY);
                    selectedPanel = clickedPanel;

                    // Add a button to delete the labeled text field
                    deleteButton = new JButton("Delete");
                    deleteButton.addActionListener(event -> {
                        argumentPane.remove(clickedPanel);
                        labelCount -= 1;
                        SpringUtilities.makeCompactGrid(argumentPane, labelCount, 1,5,5,5, 5);
                        scrollPaneBottom.setViewportView(argumentPane);
                    });
                    // Add the button to the right of the labeled text field
                    fieldPanel.add(deleteButton, BorderLayout.EAST);
                    fieldPanel.revalidate();
                    fieldPanel.repaint();
                }
            }
        });
        argumentPane.add(fieldPanel);
        labelCount +=1;
    }
}
