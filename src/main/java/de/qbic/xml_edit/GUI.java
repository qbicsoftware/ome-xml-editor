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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;

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
    private JTextField textField;     // the text
    private JPanel contentPanelLower;      // under the text a container for all the input elements
    private JPanel contentPanelHigher;
    private JButton addNodeButton;         // and a save  xml changes button
    private JButton delNodeButton;         // and a save  xml changes button
    private JButton saveButton;         // and a save  xml changes button
    private JButton openSchemaButton;         // open new image file button
    private JButton openImageButton;
    private JButton validateXMLButton;         // validate the current xml button
    private JButton exportButton;         // export xml and image to ome.tif button
    private JPanel argumentPane;
    private JMenuBar mb;
    private JMenu file, settings, help;

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
        splitPane = new JSplitPane();
        splitPane.setName("XML-Editor");
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        scrollPaneBottom = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        contentPanelLower = new JPanel();
        contentPanelHigher = new JPanel();
        textField = new JTextField();      // this text area will be put inside the scrollPane
        scrollPaneTop = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        argumentPane = new JPanel();


        saveButton = new JButton("Print Change Profile");
        openSchemaButton = new JButton("Open Schema");
        openImageButton = new JButton("Open Image");
        validateXMLButton = new JButton("Show Schema");
        exportButton = new JButton("Export to OmeTiff");
        addNodeButton = new JButton("Add New Node");
        delNodeButton = new JButton("Delete Node");


        // ADD ACTION LISTENERS ########################################################################################
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String new_text = textField.getText();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
                System.out.println(myTree.getAnchorSelectionPath() + "\t" + new_text);
                edit.changeHistory.put(myTree.getAnchorSelectionPath().toString(), new_text);
                node.setUserObject(new_text);
            }
        });

        saveButton.addActionListener(e -> {
            System.out.println(edit.changeHistory);
        });

        addNodeButton.addActionListener(e -> {
            System.out.println(edit.changeHistory);
        });

        delNodeButton.addActionListener(e -> {
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

        openImageButton.addActionListener(e -> {
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
        openImageButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        openSchemaButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        saveButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        exportButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        validateXMLButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));

        addNodeButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        delNodeButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));

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
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        topPanel.setOpaque(true);
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder("XML FILE VIEW"));

        // POPULATE COMPONENTS #########################################################################################
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window
        mb.add(file);
        mb.add(settings);
        mb.add(help);
        this.add(mb);
        this.setJMenuBar(mb);
        bottomPanel.add(scrollPaneBottom);                // first we add the scrollPane to the bottomPanel, so it is at the top
        bottomPanel.add(contentPanelLower);                // then we add the inputPanel to the bottomPanel, so it under the scrollPane / textArea
        topPanel.add(scrollPaneTop);
        topPanel.add(contentPanelHigher);

        contentPanelHigher.add(addNodeButton);
        contentPanelHigher.add(delNodeButton);

        contentPanelLower.add(openImageButton);
        contentPanelLower.add(openSchemaButton);
        contentPanelLower.add(saveButton);
        contentPanelLower.add(validateXMLButton);
        contentPanelLower.add(exportButton);

        // let's configure our splitPane:
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(SCREEN_HEIGHT/2);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(topPanel);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(bottomPanel);            // and at the bottom we want our "bottomPanel"

    }
    public void makeTree(Document dom){
        myTree = new XmlJTree(dom);
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });
        scrollPaneTop.setViewportView(myTree);
    }
    public void jTree1ValueChanged(TreeSelectionEvent tse ) {
        int labelCount =0;
        argumentPane.removeAll();
        XmlNode node = (XmlNode) myTree.getLastSelectedPathComponent();
        argumentPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        argumentPane.setOpaque(true);
        argumentPane.setBackground(Color.WHITE);
        argumentPane.setBorder(BorderFactory.createTitledBorder(node.getUserObject().toString()));

        if (node.getUserObject().toString().startsWith("@")) {
            DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getFirstChild();
            addLabeledTextField(node.getUserObject().toString(), child);
            labelCount +=1;
        }
        else if (node.getUserObject().toString().startsWith("#")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addLabeledTextField(parent.getUserObject().toString(), node);
            labelCount +=1;
        }
        else if (node.getUserObject().toString().startsWith(":")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addLabeledTextField(parent.getUserObject().toString(), node);
            labelCount +=1;
        }
        else {
            for (int c=0; c<node.getChildCount();c++) {
                XmlNode child = (XmlNode) node.getChildAt(c);
                if (child.getUserObject().toString().startsWith("@")) {
                    DefaultMutableTreeNode childChild = (DefaultMutableTreeNode) child.getFirstChild();
                    addLabeledTextField(child.getUserObject().toString(), childChild);
                    labelCount +=1;
                }
                else if (child.getUserObject().toString().startsWith(":")) {
                    addLabeledTextField(node.getUserObject().toString(), child);
                    labelCount +=1;
                }
                else if (child.getUserObject().toString().startsWith("#")) {
                    addLabeledTextField(node.getUserObject().toString(), child);
                    labelCount +=1;
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
        JTextField textField = new JTextField(selectedNode.getUserObject().toString());
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String new_text = textField.getText();
                // System.out.println(myTree.getAnchorSelectionPath() + "\t" + new_text);
                System.out.println(selectedNode.getPath().toString());
                edit.changeHistory.put(myTree.getAnchorSelectionPath().toString(), new_text);
                selectedNode.setUserObject(new_text);
            }
        });
        textField.setBorder(BorderFactory.createCompoundBorder(fieldBorder, new EmptyBorder(0, 10, 0, 0)));

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
                boolean clicked = false;
                if (e.getButton() == MouseEvent.BUTTON1) {
                    JPanel clickedPanel = (JPanel) e.getSource();
                    clickedPanel.setBackground(Color.LIGHT_GRAY);

                    // Add a button to delete the labeled text field
                    JButton deleteButton = new JButton("Delete");
                    deleteButton.addActionListener(event -> {
                        argumentPane.remove(clickedPanel);
                        argumentPane.revalidate();
                        argumentPane.repaint();
                    });

                    // Add the button to the right of the labeled text field
                    fieldPanel.add(deleteButton, BorderLayout.EAST);
                    fieldPanel.revalidate();
                    fieldPanel.repaint();
                }
            }
        });
        argumentPane.add(fieldPanel);
    }
}
