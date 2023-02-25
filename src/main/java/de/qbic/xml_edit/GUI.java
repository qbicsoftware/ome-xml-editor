// Mandatory for ImageInfo

package de.qbic.xml_edit;

import loci.common.services.ServiceException;
import loci.formats.FormatException;
import loci.plugins.config.SpringUtilities;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
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
    public XMLEditor edit;
    public static int SCREEN_HEIGHT = SCREEN_WIDTH*9/16;
    public static int BUTTON_HEIGHT = 25;
    public Document xml;
    public XMLTree myTree;
    // these are the components we need.
    private JSplitPane splitPane;  // split the window in top and bottom
    private JPanel topPanel;       // container panel for the top
    private JPanel bottomPanel;    // container panel for the bottom
    private JScrollPane scrollPaneBottom; // makes the text scrollable
    private JScrollPane scrollPaneTop; // makes the text scrollable
    private NodedTextField textField;     // the text
    private JPanel contentPanelHigher;
    private JMenuItem saveButton;         // and a save  xml changes button
    private JMenuItem openSchemaButton;         // open new image file button
    private JMenuItem showCurrentXML;
    private JMenuItem validateXMLButton;         // validate the current xml button
    private JMenuItem exportButton;         // export xml and image to ome.tif button
    private JPanel argumentPanel;
    private JMenuBar mb;
    private JMenu file, settings, help;
    private JMenuItem openImage;
    private static JPanel selectedPanel;
    private static JPanel titlePanel = new JPanel();
    private static JButton deleteButton;
    private static JTabbedPane tabbedPane = new JTabbedPane();
    private static ButtonGroup bg = new ButtonGroup();
    int labelCount =0;
    private static JButton addButton = new JButton("Add Node");
    private static JButton delButton = new JButton("Delete");

    Border fieldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);


    public GUI(XMLEditor edit){

        this.edit = edit;
        this.setTitle("XML-XMLEditor");
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
        showCurrentXML = new JMenuItem("Show Current XML");
        exportButton = new JMenuItem("Export to OmeTiff");
        openSchemaButton = new JMenuItem("Open Schema");
        saveButton = new JMenuItem("Print Change Profile");
        validateXMLButton = new JMenuItem("Show Schema");

        splitPane = new JSplitPane();
        splitPane.setName("XML-XMLEditor");
        topPanel = new JPanel();
        bottomPanel = new JPanel();
        scrollPaneBottom = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        contentPanelHigher = new JPanel();
        textField = new NodedTextField();      // this text area will be put inside the scrollPane
        scrollPaneTop = new JScrollPane();  // this scrollPane is used to make the text area scrollable
        argumentPanel = new JPanel();

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
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    edit.openSchema(chooser.getSelectedFile().getAbsolutePath());
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

        // Opens the Current XML in the topPanel of the GUI, in a new tab
        showCurrentXML.addActionListener(e -> {
            try {
                edit.showCurrentXML();
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

        // SET LAYOUTS #################################################################################################
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,

        contentPanelHigher.setLayout(new BoxLayout(contentPanelHigher, BoxLayout.X_AXIS));
        SpringLayout argPaneLayout = new SpringLayout();
        argumentPanel.setLayout(argPaneLayout);

        // SET BORDERS #################################################################################################
        makeStandardBorder(argumentPanel);

        // POPULATE COMPONENTS #########################################################################################
        getContentPane().add(splitPane);               // due to the GridLayout, our splitPane will now fill the whole window
        mb.add(file);
        file.add(openImage);
        file.add(showCurrentXML);
        file.add(exportButton);
        file.add(openSchemaButton);
        mb.add(settings);
        mb.add(help);
        help.add(saveButton);
        help.add(validateXMLButton);

        this.add(mb);
        this.setJMenuBar(mb);
        bottomPanel.add(scrollPaneBottom);                // first we add the scrollPane to the bottomPanel, so it is at the top



        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);  // we want it to split the window vertically
        splitPane.setDividerLocation(SCREEN_HEIGHT/2);                    // the initial position of the divider is 200 (our window is 400 pixels high)

        splitPane.setTopComponent(tabbedPane);
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
    // creates a new Tab
    public void makeTitledBorder(JPanel p, String title) {

        // create a compound border with a line border and an empty border
        Border line = BorderFactory.createLineBorder(Color.GRAY);
        Border margin = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border compound = BorderFactory.createCompoundBorder(line, margin);

        // create a titled border with the specified title
        Border titleBorder = BorderFactory.createTitledBorder(title);
        Border compound2 = BorderFactory.createCompoundBorder(compound, titleBorder);

        // set the border of this component
        p.setBorder(compound2);
        p.setOpaque(true);
    }
    public void makeStandardBorder(JPanel p) {

        // create a compound border with a line border and an empty border
        Border line = BorderFactory.createLineBorder(Color.GRAY);
        Border margin = BorderFactory.createEmptyBorder(5, 5, 5, 5);
        Border compound = BorderFactory.createCompoundBorder(line, margin);

        // set the border of this component
        p.setBorder(compound);
        p.setOpaque(true);
    }

    public void makeHistoryEntry(XMLNode originNode, String modification, String newText) {
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
    public void makeHistoryEntry(XMLNode originNode, String modification, XMLNode toBeAdded) {
        if (modification == "add") {
            if (toBeAdded.getUserObject().toString().startsWith("@")) {
                System.out.println("change \"Addition\" added to history");
                String path = Arrays.toString(originNode.getPath());
                LinkedList<String> location = new LinkedList<>();
                location.addAll(Arrays.asList(path.replace("[", "").replace("]", "").split(", ")));
                XMLChange change = new XMLChange(modification, location);
                String newText = toBeAdded.getUserObject().toString()+ "=" +toBeAdded.getFirstChild().getUserObject().toString();
                change.setNewContent(newText);
                edit.changeHistory.add(change);
                return;
            }
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
    public void makeHistoryEntry(XMLNode originNode, String modification) {
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
        myTree = new XMLTree(dom);
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged();
            }
        });
        scrollPaneTop.setViewportView(myTree);
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.add(scrollPaneTop);
        makeStandardBorder(topPanel);
        tabbedPane.addTab("XML-Tree", topPanel);
    }
    // gets called from XMLEditor.showCurrentXML() and creates a new tab with the current XML after changes were applied
    public void showXMLTree(Document dom, String title) {
        XMLTree currentTree = new XMLTree(dom);
        currentTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                jTree1ValueChanged();
            }
        });
        JScrollPane currentScrollPane = new JScrollPane();
        currentScrollPane.setViewportView(currentTree);
        tabbedPane.addTab(title, currentScrollPane);
        // add close button to tab
        int index = tabbedPane.getTabCount() - 1;
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel("Updated XML-Tree");
        JButton btnClose = new JButton("x");

        btnClose.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setPreferredSize(new Dimension(17, 17));
        btnClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tabbedPane.remove(index);
            }
        });
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 2;
        pnlTab.add(lblTitle, gbc);
        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);
        tabbedPane.setTabComponentAt(index, pnlTab);
    }

    public void jTree1ValueChanged() {
        labelCount = 0;
        argumentPanel.removeAll();
        argumentPanel.revalidate();
        argumentPanel.repaint();
        XMLNode node = (XMLNode) myTree.getLastSelectedPathComponent();
        makeStandardBorder(argumentPanel);

        if (node.getUserObject().toString().startsWith("@")) {
            XMLNode child = node.getFirstChild();
            // addLabeledTextField(node.getUserObject().toString(), child);
            addAttribute(node.getUserObject().toString(), child);
        }
        else if (node.getUserObject().toString().startsWith("#")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addAttribute(parent.getUserObject().toString(), node);
        }
        else if (node.getUserObject().toString().startsWith(":")){
            DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
            addAttribute(parent.getUserObject().toString(), node);
        }
        else {
            addTitleButton(node);
        }
        SpringUtilities.makeCompactGrid(argumentPanel, labelCount, 1,5,5,5, 5);
        scrollPaneBottom.setViewportView(argumentPanel);
        textField.setText((String) node.getUserObject());
    }
    public void addAttribute(String labelText, XMLNode node) {
        JPanel attributePanel = new JPanel();
        attributePanel.setLayout(new BorderLayout(5,5));
        JToggleButton attrButton = new JToggleButton(labelText);
        NodedTextField textField = new NodedTextField(node);
        bg.add(attrButton);
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newText = textField.getText();
                makeHistoryEntry(textField.getNode(),"edit" , newText);
                textField.getNode().setUserObject(newText);
                System.out.println("change detected");
            }
        });
        attrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();

                delButton.addActionListener(event -> {
                    makeHistoryEntry(node, "del");
                    XMLNode attrNode = node;
                    XMLNode parentNode = node.getParent();
                    parentNode.remove(attrNode);
                    argumentPanel.remove(attributePanel);
                    labelCount -= 1;
                    SpringUtilities.makeCompactGrid(argumentPanel, labelCount, 1,5,5,5, 5);
                    scrollPaneBottom.setViewportView(argumentPanel);
                });
                if (selected) {
                    attributePanel.add(delButton, BorderLayout.EAST);
                    // remove addButton from its Panel
                    titlePanel.remove(addButton);
                    attributePanel.repaint();
                    argumentPanel.revalidate();
                    argumentPanel.repaint();
                }
                else if (!selected) {
                    attributePanel.repaint();
                    argumentPanel.revalidate();
                    argumentPanel.repaint();
                }
            }
        });
        attributePanel.setPreferredSize(new Dimension(400, 50));
        attrButton.setPreferredSize(new Dimension(150, attrButton.getHeight()));
        delButton.setPreferredSize(new Dimension(150, delButton.getHeight()));

        attributePanel.add(attrButton, BorderLayout.WEST);
        attributePanel.add(textField, BorderLayout.CENTER);
        argumentPanel.add(attributePanel);
        labelCount +=1;
    }
    private void addTitleButton(XMLNode node) {
        titlePanel.removeAll();
        titlePanel.revalidate();
        titlePanel.repaint();
        titlePanel.setLayout(new BorderLayout(5,5));
        JToggleButton titleButton = new JToggleButton(node.getUserObject().toString());
        titleButton.setMinimumSize(new Dimension(BUTTON_HEIGHT*5, BUTTON_HEIGHT*5));
        bg.add(titleButton);
        titleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton = (AbstractButton) e.getSource();
                boolean selected = abstractButton.getModel().isSelected();
                addButton.addActionListener(event -> {

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
                    makeTitledBorder(leftPanel, "Choose Node Type");
                    makeTitledBorder(rightPanel, "Node Details");

                    // create radio buttons for "Attribute", "Text" and "Folder"
                    JRadioButton attrRadio = new JRadioButton("Attribute");
                    JRadioButton textRadio = new JRadioButton("Text");
                    JRadioButton folderRadio = new JRadioButton("Folder");

                    // set action command for each radio button
                    attrRadio.setActionCommand("attribute");
                    textRadio.setActionCommand("text");
                    folderRadio.setActionCommand("folder");

                    // add radio buttons to leftPanel
                    leftPanel.add(attrRadio);
                    leftPanel.add(textRadio);
                    leftPanel.add(folderRadio);

                    // group the radio buttons
                    ButtonGroup group = new ButtonGroup();
                    group.add(attrRadio);
                    group.add(textRadio);
                    group.add(folderRadio);

                    // create textfields for each type of node
                    JTextField attrField = new JTextField(5);
                    JTextField valField = new JTextField(5);
                    JTextField textField = new JTextField(5);
                    JTextField folderField = new JTextField(5);

                    // create label for each textfield
                    JLabel attrLabel = new JLabel("Attribute:");
                    JLabel valLabel = new JLabel("Value:");
                    JLabel textLabel = new JLabel("Text:");
                    JLabel folderLabel = new JLabel("Folder:");

                    // set preferred width for each textfield to textfield width and height to 30
                    attrField.setPreferredSize(new Dimension(attrField.getWidth(), 30));
                    valField.setPreferredSize(new Dimension(valField.getWidth(), 30));
                    textField.setPreferredSize(new Dimension(textField.getWidth(), 30));
                    folderField.setPreferredSize(new Dimension(folderField.getWidth(), 30));

                    // set preferred width for each label to 100 and height to 30
                    attrLabel.setPreferredSize(new Dimension(100, 30));
                    valLabel.setPreferredSize(new Dimension(100, 30));
                    textLabel.setPreferredSize(new Dimension(100, 30));
                    folderLabel.setPreferredSize(new Dimension(100, 30));

                    // set labels for each textfield
                    attrLabel.setLabelFor(attrField);
                    valLabel.setLabelFor(valField);
                    textLabel.setLabelFor(textField);
                    folderLabel.setLabelFor(folderField);

                    // set default selection
                    attrRadio.setSelected(true);
                    rightPanel.add(attrLabel);
                    rightPanel.add(attrField);
                    rightPanel.add(valLabel);
                    rightPanel.add(valField);
                    SpringUtilities.makeCompactGrid(rightPanel, 2, 2, 5, 5, 5, 5);

                    // add actionlistener to radio buttons, upon selection, change the panel to the selected type
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
                            SpringUtilities.makeCompactGrid(rightPanel, 2, 2, 5, 5, 5, 5);

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
                            SpringUtilities.makeCompactGrid(rightPanel, 1, 2, 5, 5, 5, 5);

                            // revalidate and repaint
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    });
                    folderRadio.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            // reset rightPanel
                            rightPanel.removeAll();

                            // add labels and textfields to rightPanel
                            rightPanel.add(folderLabel);
                            rightPanel.add(folderField);

                            // Lay out the panel by defining SpringUtilities constraints
                            SpringUtilities.makeCompactGrid(rightPanel, 1, 2, 5, 5, 5, 5);

                            // revalidate and repaint
                            rightPanel.revalidate();
                            rightPanel.repaint();
                        }
                    });

                    int confirmed = JOptionPane.showConfirmDialog(null, optionPane,
                            "Create new Node", JOptionPane.OK_CANCEL_OPTION);

                    if (confirmed == JOptionPane.OK_OPTION) {
                        String selection = group.getSelection().getActionCommand();
                        if (selection == "attribute") {
                            XMLNode newNodeAttr = new XMLNode();
                            XMLNode newNodeValue = new XMLNode();
                            newNodeAttr.add(newNodeValue);
                            newNodeAttr.setUserObject("@"+attrField.getText().replace("@", ""));
                            newNodeValue.setUserObject(":"+valField.getText().replace(":", ""));
                            node.add(newNodeAttr);
                            makeHistoryEntry(node, "add", (XMLNode) newNodeAttr);
                            addAttribute(newNodeAttr.getUserObject().toString(), (XMLNode) newNodeValue);
                            SpringUtilities.makeCompactGrid(argumentPanel, labelCount, 1,5,5,5, 5);
                            scrollPaneBottom.setViewportView(argumentPanel);
                        }
                        else if (selection == "text") {
                            XMLNode newNode = new XMLNode();
                            newNode.setUserObject(textField.getText());
                            node.add(newNode);
                            makeHistoryEntry(node, "add", (XMLNode) newNode);
                            addAttribute(newNode.getUserObject().toString(), (XMLNode) newNode);
                        }
                        else if (selection == "folder") {
                            XMLNode newNode = new XMLNode();
                            newNode.setUserObject(folderField.getText());
                            node.add(newNode);
                            makeHistoryEntry(node, "add", (XMLNode) newNode);
                            addTitleButton(newNode);
                        }
                    }
                });
                delButton.addActionListener(event -> {
                    makeHistoryEntry(node, "del");
                    XMLNode attrNode = node;
                    XMLNode parentNode = node.getParent();
                    parentNode.remove(attrNode);
                    argumentPanel.remove(titlePanel);
                    labelCount -= 1;
                    SpringUtilities.makeCompactGrid(argumentPanel, labelCount, 1,5,5,5, 5);
                    scrollPaneBottom.setViewportView(argumentPanel);
                });

                if (selected) {
                    titlePanel.add(delButton, BorderLayout.EAST);
                    titlePanel.add(addButton, BorderLayout.WEST);
                    titlePanel.repaint();
                    argumentPanel.revalidate();
                    argumentPanel.repaint();
                }
                else if (!selected) {
                    titlePanel.remove(addButton);
                    titlePanel.repaint();
                    argumentPanel.revalidate();
                    argumentPanel.repaint();
                }
            }
        });
        titleButton.setBorder(fieldBorder);
        titlePanel.setPreferredSize(new Dimension(400, 50));
        addButton.setPreferredSize(new Dimension(150, addButton.getHeight()));
        delButton.setPreferredSize(new Dimension(150, delButton.getHeight()));
        titleButton.setPreferredSize(new Dimension(titleButton.getWidth(), titleButton.getHeight()));

        titlePanel.add(titleButton, BorderLayout.CENTER);
        argumentPanel.add(titlePanel);
        labelCount +=1;
        for (int c=0; c<node.getChildCount();c++) {
            XMLNode child = (XMLNode) node.getChildAt(c);
            if (child.getUserObject().toString().startsWith("@")) {
                DefaultMutableTreeNode childChild = (DefaultMutableTreeNode) child.getFirstChild();
                addAttribute(child.getUserObject().toString(), (XMLNode) childChild);
            }
            else if (child.getUserObject().toString().startsWith(":")) {
                addAttribute(node.getUserObject().toString(), child);
            }
            else if (child.getUserObject().toString().startsWith("#")) {
                addAttribute(node.getUserObject().toString(), child);
            }
        }
    }
}
