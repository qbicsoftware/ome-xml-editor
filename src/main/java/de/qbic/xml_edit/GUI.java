// Mandatory for ImageInfo

package de.qbic.xml_edit;

import org.w3c.dom.Document;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Enumeration;

public class GUI extends javax.swing.JFrame{
    public static int SCREEN_WIDTH = 1000;

    public static Editor Edit;
    public static int SCREEN_HEIGHT = SCREEN_WIDTH*9/16;
    public static int BUTTON_HEIGHT = 25;
    public Document xml;
    public XmlJTree myTree;
    // these are the components we need.
    private final JSplitPane splitPane;  // split the window in top and bottom
    private final JPanel topPanel;       // container panel for the top
    private final JPanel bottomPanel;    // container panel for the bottom
    private final JScrollPane scrollPaneBottom; // makes the text scrollable
    private final JScrollPane scrollPaneTop; // makes the text scrollable
    private final JTextField textField;     // the text
    private final JPanel contentPanel;      // under the text a container for all the input elements
    private final JButton saveButton;         // and a save  xml changes button
    private final JButton openButton;         // open new image file button
    private final JButton validateXMLButton;         // validate the current xml button
    private final JButton exportButton;         // export xml and image to ome.tif button


    public GUI(Editor edit){
        //this.Edit = edit;
        this.setTitle("XML-Editor");

        // first, lets create the containers:
        // the splitPane devides the window in two components (here: top and bottom)
        // users can then move the devider and decide how much of the top component
        // and how much of the bottom component they want to see.
        myTree = new XmlJTree(edit.xml_doc);
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged(evt);
            }
        });

        splitPane = new JSplitPane();
        splitPane.setName("XML-Editor");

        topPanel = new JPanel();
        bottomPanel = new JPanel();

        scrollPaneBottom = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        textField = new JTextField();      // this text area will be put inside the scrollPane
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String new_text = textField.getText();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
                System.out.println(myTree.getAnchorSelectionPath() + "\t" + new_text);
                edit.changeHistory.put(myTree.getAnchorSelectionPath().toString(), new_text);
                node.setUserObject(new_text);
            }
        });
        scrollPaneTop = new JScrollPane();  // this scrollPane is used to make the text area scrollable

        contentPanel = new JPanel();

        saveButton = new JButton("Print Change Profile");

        saveButton.addActionListener(e -> {
            System.out.println(edit.changeHistory);
        });

        openButton = new JButton("Open");
        openButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file",
                    "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                myTree.setPath(chooser.getSelectedFile().getAbsolutePath());
            }
        });

        validateXMLButton = new JButton("Show Schema");
        validateXMLButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Show Schema");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();

                try {
                    edit.getSchema();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                DefaultMutableTreeNode myRoot = (DefaultMutableTreeNode) myTree.getModel().getRoot();
                Enumeration en = myRoot.preorderEnumeration();
                while(en.hasMoreElements()){
                }
            }
        });

        exportButton = new JButton("Export to OmeTiff");
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

        // now lets define the default size of our window and its layout:
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));     // let's open the window with a default size of 400x400 pixels
        // the contentPane is the container that holds all our components
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,
        // we only add one element to the window itself
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window

        // let's configure our splitPane:
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(0.5);                    // the initial position of the divider is 200 (our window is 400 pixels high)
        splitPane.setTopComponent(topPanel);                  // at the top we want our "topPanel"
        splitPane.setBottomComponent(bottomPanel);            // and at the bottom we want our "bottomPanel"

        // our topPanel doesn't need anymore for this example. Whatever you want it to contain, you can add it here
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically

        bottomPanel.add(scrollPaneBottom);                // first we add the scrollPane to the bottomPanel, so it is at the top
        scrollPaneBottom.setViewportView(textField);       // the scrollPane should make the textArea scrollable, so we define the viewport
        bottomPanel.add(contentPanel);                // then we add the inputPanel to the bottomPanel, so it under the scrollPane / textArea

        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
        topPanel.add(scrollPaneTop);
        scrollPaneTop.setViewportView(myTree);


        // let's set the maximum size of the inputPanel, so it doesn't get too big when the user resizes the window

        openButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        saveButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        exportButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));
        validateXMLButton.setPreferredSize(new Dimension(BUTTON_HEIGHT, BUTTON_HEIGHT));

        contentPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, exportButton.getHeight() * 4));     // we set the max height to 75 and the max width to (almost) unlimited
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

        contentPanel.add(openButton);
        contentPanel.add(saveButton);
        contentPanel.add(validateXMLButton);
        contentPanel.add(exportButton);


        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }
    public void jTree1ValueChanged( TreeSelectionEvent tse ) {
        //String node = tse.getNewLeadSelectionPath().getLastPathComponent().toString();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) myTree.getLastSelectedPathComponent();
        textField.setText((String) node.getUserObject());
    }
}
