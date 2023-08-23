// LICENSE

// PACKAGE
package de.qbic.xmledit;

// IMPORTS

import loci.plugins.config.SpringUtilities;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.intellij.markdown.ast.ASTNode;
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.markdown.html.HtmlGenerator;
import org.intellij.markdown.parser.MarkdownParser;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.border.Border;
import javax.xml.transform.TransformerException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.LinkedList;

// CLASS
public class GUI extends javax.swing.JFrame{

    // Constants %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    static final Color PASTEL_RED = new Color(255, 153, 153);
    static final Color PASTEL_GREEN = new Color(153, 255, 153);
    static final Color PASTEL_BLUE = new Color(153, 153, 255);
    static final Color DARK_GREEN = new Color(30, 150, 30);
    static final Color DARK_BLUE = new Color(30, 30, 150);
    static final Color DARK_RED = new Color(150, 30, 30);
    static final String CHANGE_SVG = "<svg width=\"800\" height=\"800\" viewBox=\"0 0 16 16\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"m13 0-3 3h2.3v9h1.4V3H16ZM5 5.7h6V4.3H5Zm0 3h6V7.3H5Zm0 3h6v-1.4H5ZM3.7 4H2.3v9H0l3 3 3-3H3.7Z\"/></svg>";
    static final String DEFAULT_SVG = "<svg width=\"800\" height=\"800\" viewBox=\"0 0 32 32\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M20.414 2H5v28h22V8.586ZM7 28V4h12v6h6v18Z\" style=\"fill:#c5c5c5\"/></svg>";
    static final String TREE_SVG = "<svg height=\"800\" width=\"800\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\" xml:space=\"preserve\"><path d=\"M21 24h-6v-6h6v6zm-4-2h2v-2h-2v2zm-2 0H6V8H3V0h8v8H8v4h7v2H8v6h7v2zM5 6h4V2H5v4zm16 10h-6v-6h6v6zm-4-2h2v-2h-2v2z\"/></svg>";
    static final String SETTINGS_SVG = "<svg width=\"800\" height=\"800\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\" data-name=\"Line Color\"  class=\"icon line-color\"><circle cx=\"12\" cy=\"12\" r=\"3\" style=\"fill:none;stroke:#2ca9bc;stroke-linecap:round;stroke-linejoin:round;stroke-width:2\"/><path d=\"M20 10h-.59a1 1 0 0 1-.94-.67v0a1 1 0 0 1 .2-1.14l.41-.41a1 1 0 0 0 0-1.42l-1.42-1.43a1 1 0 0 0-1.42 0l-.41.41a1 1 0 0 1-1.14.2h0a1 1 0 0 1-.69-.95V4a1 1 0 0 0-1-1h-2a1 1 0 0 0-1 1v.59a1 1 0 0 1-.67.94h0a1 1 0 0 1-1.14-.2l-.41-.41a1 1 0 0 0-1.42 0L4.93 6.34a1 1 0 0 0 0 1.42l.41.41a1 1 0 0 1 .2 1.14v0a1 1 0 0 1-.94.67H4a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h.59a1 1 0 0 1 .94.67v0a1 1 0 0 1-.2 1.14l-.41.41a1 1 0 0 0 0 1.42l1.41 1.41a1 1 0 0 0 1.42 0l.41-.41a1 1 0 0 1 1.14-.2h0a1 1 0 0 1 .67.94V20a1 1 0 0 0 1 1h2a1 1 0 0 0 1-1v-.59a1 1 0 0 1 .67-.94h0a1 1 0 0 1 1.14.2l.41.41a1 1 0 0 0 1.42 0l1.41-1.41a1 1 0 0 0 0-1.42l-.41-.41a1 1 0 0 1-.2-1.14v0a1 1 0 0 1 .94-.67H20a1 1 0 0 0 1-1V11a1 1 0 0 0-1-1Z\" style=\"fill:none;stroke:#000;stroke-linecap:round;stroke-linejoin:round;stroke-width:2\"/></svg>";
    static final String FILES_SVG = "<svg width=\"800\" height=\"800\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M17.5 0h-9L7 1.5V6H2.5L1 7.5v15.07L2.5 24h12.07L16 22.57V18h4.7l1.3-1.43V4.5L17.5 0zm0 2.12 2.38 2.38H17.5V2.12zm-3 20.38h-12v-15H7v9.07L8.5 18h6v4.5zm6-6h-12v-15H16V6h4.5v10.5z\"/></svg>";
    static final String HELP_SVG = "<svg width=\"800px\" height=\"800px\" viewBox=\"0 0 16 16\" xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" fill=\"none\" stroke=\"#000000\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"1.5\">\n<circle cy=\"8\" cx=\"8\" r=\"6.25\"/>\n<path d=\"m5.75 6.75c0-1 1-2 2.25-2s2.25 1.0335 2.25 2c0 1.5-1.5 1.5-2.25 2m0 2.5v0\"/>\n</svg>";
    static final String FEEDBACK_SVG = "<svg fill=\"#000000\" width=\"800px\" height=\"800px\" viewBox=\"0 0 30 30\" xmlns=\"http://www.w3.org/2000/svg\"><path d=\"M20.5 9s-.3 0-.4.2l-5.4 5.5-.6.3c-.2 0-.4 0-.6-.3L11 12.2c-.6-.5-1.3.2-.8.7l2.4 2.4c.4.4 1 .6 1.4.6.5 0 1-.2 1.4-.6L21 10c.2-.4 0-1-.5-1zm-19-8C.7 1 0 1.7 0 2.5v19c0 .8.7 1.5 1.5 1.5H5v4.5c0 .3 0 .6.2 1 0 .2.4.4.6.5.6 0 1-.2 1.6-.7l5.3-5.3h15.8c.8 0 1.5-.7 1.5-1.5v-19c0-.8-.7-1.5-1.5-1.5zm0 1h27c.3 0 .5.2.5.5v19c0 .3-.2.5-.5.5H12.1l-5.4 5.7c-.5.5-.6 0-.6-.2v-5c0-.3-.2-.5-.5-.5h-4c-.3 0-.5-.2-.5-.5v-19c0-.3.2-.5.5-.5z\"/></svg>";
    static final String UNDO_SVG = "<svg width=\"800px\" height=\"800px\" viewBox=\"0 0 24 24\" xmlns=\"http://www.w3.org/2000/svg\" fill=\"none\"><path stroke=\"#000000\" stroke-linecap=\"round\" stroke-linejoin=\"round\" stroke-width=\"2\" d=\"M4 9v5h5m11 2c-.497-4.5-3.367-8-8-8-2.73 0-5.929 2.268-7.294 5.5\"/></svg>";
    public static final int BASE_UNIT = 8;
    public static final int SCREEN_WIDTH = 128*BASE_UNIT;
    public static final int BUTTON_HEIGHT = 4*BASE_UNIT;
    public static final int BUTTON_WIDTH = 20*BASE_UNIT;
    public static final int SCREEN_HEIGHT = SCREEN_WIDTH*9/16;

    // Variables %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
    public Document xml;
    public XMLEditor edit;
    public XMLTree myTree;
    private JSplitPane splitPane;  // split the window in top and bottom
    private JPanel bottomPanel;    // container panel for the bottom
    private JScrollPane scrollPaneBottom; // makes the text scrollable
    private JScrollPane scrollPaneTop; // makes the text scrollable
    private NodedTextField textField;     // the text
    private JMenuItem openSchemaButton;
    private JMenuItem undoChangeButton;
    private JMenuItem resetChangeHistoryButton;
    private JMenuItem applyChangesToFolderButton;
    private JMenuItem showCurrentXML;
    private JMenuItem showChangeButton;
    private JMenuItem loadChangeButton;
    private JMenuItem saveChangeButton;
    private JMenuItem validateChangeButton;
    private JMenuItem exportOmeTiffButton;
    private JMenuItem exportOmeXmlButton;
    private JMenuItem howToUseButton;
    private JMenuItem aboutButton;
    private JMenuItem setSchemaPath;
    private JCheckBoxMenuItem simplifiedTree;
    private JPanel editPanel;
    private JMenuBar mb;
    private JMenu file, settings, changeHistoryMenu, help;
    private JMenuItem openImage;
    private JMenuItem openXML;
    private static final JPanel titlePanel = new JPanel();
    private static final JTabbedPane tabbedPane = new JTabbedPane();
    private static final JScrollPane changeHistoryPane = new JScrollPane();
    private static final ButtonGroup bg = new ButtonGroup();
    private int labelCount =0;
    private static final JButton addButton = new JButton("Add Node");
    private static final JButton delButton = new JButton("Delete");
    private static final JTextArea validationErrorsField = new JTextArea();
    private static final JPanel changeHistoryWindowPanel = new JPanel();
    private XMLNode selectedNode;
    private XMLNode toBeDeletedNode;
    private final Border fieldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
    public XMLTableModel feedBackTableModel = null;
    public XMLTableModel historyTableModel = null;
    public JTable historyTable = null;
    private JOptionPane schemaHelp = null;
    // create a new panel for the table
    // private JPanel feedbackTablePanel = new JPanel();
    

    public GUI(XMLEditor edit){
        //this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
        this.edit = edit;
        this.setTitle("OME-XML Editor");
        makeUI();
        pack();   // calling pack() at the end, will ensure that every layout and size we just defined gets applied before the stuff becomes visible
    }

    private void makeUI() {
        // INITIALIZE COMPONENTS %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
        mb = new JMenuBar();

        // Menu for the file
        file = new JMenu("File");
        // Menu for the settings
        settings = new JMenu("Settings");
        // Menu for the help
        help = new JMenu("Help");
        // Menu for the change history
        changeHistoryMenu = new JMenu("Change History Menu");
        makeHistoryTable();

        // Button that opens the xml tree of an image
        openImage = new JMenuItem("Open Image");
        // Button that opens an external xml file
        openXML = new JMenuItem("Open XML");
        // Button that shows the current XML
        showCurrentXML = new JMenuItem("Show Current XML");
        // Button that exports the current XML to OmeTiff
        exportOmeTiffButton = new JMenuItem("Export to OmeTiff");
        // Button that exports the current XML to OmeXml
        exportOmeXmlButton = new JMenuItem("Export to OmeXML");
        // Button that opens the schema
        openSchemaButton = new JMenuItem("Open Schema");
        // Button that adds a tab to the tabbed pane that shows the changes currently loaded in the change history
        showChangeButton = new JMenuItem("Show Change History");
        // Button that loads a change History from a file
        loadChangeButton = new JMenuItem("Load Change History");
        // Button that saves the current change History to a file
        saveChangeButton = new JMenuItem("Save Change History");
        // Button that validates the current change History
        validateChangeButton = new JMenuItem("Validate Change History");
        // Button that undoes the last change
        undoChangeButton = new JMenuItem("Undo Last Change");
        // Button that applies the current change history to all files in a folder
        applyChangesToFolderButton = new JMenuItem("Apply History to Folder");
        // Button that resets the change history
        resetChangeHistoryButton = new JMenuItem("Reset Change History");
        // Button that opens the tutorial
        howToUseButton = new JMenuItem("How To Use");
        // Button that add the about page to the tabbed pane
        aboutButton = new JMenuItem("About XML-Editor");
        //Button that sets the schema path
        setSchemaPath = new JMenuItem("Set Schema Path");
        // simplifiedTree checkbox
        simplifiedTree = new JCheckBoxMenuItem("Simplified Tree");
        simplifiedTree.setSelected(true);

        initializeAddButton();
        initializeDelButton();

        splitPane = new JSplitPane();
        splitPane.setName("XML-Editor");

        bottomPanel = new JPanel();
        scrollPaneBottom = new JScrollPane();
        textField = new NodedTextField();
        scrollPaneTop = new JScrollPane();

        editPanel = new JPanel();

        tabbedPane.setUI(new MyTabbedPaneUI(BUTTON_HEIGHT));
        tabbedPane.setFocusable(false);

        scrollPaneTop.setOpaque(true);
        scrollPaneTop.setBackground(Color.WHITE);
        scrollPaneTop.setForeground(Color.WHITE);
        tabbedPane.setOpaque(true);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setForeground(Color.WHITE);
        splitPane.setOpaque(true);
        splitPane.setBackground(Color.WHITE);
        splitPane.setForeground(Color.WHITE);

        // ADD ACTION LISTENERS ########################################################################################
        textField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String newText = textField.getText();
                try {
                    makeNewChange("edit", textField.getNode());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                textField.getNode().setUserObject(newText);
            }
        });

        // undo the last change in the change history
        undoChangeButton.addActionListener(e -> {
            try {
                edit.undoChange();
            } catch (MalformedURLException | TransformerException | SAXException ex) {
                throw new RuntimeException(ex);
            }
        });

        // apply the current change history to all files in a folder
        applyChangesToFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select Folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int userSelection = chooser.showOpenDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File folder = chooser.getSelectedFile();
                try {
                    System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
                    System.out.println("Folder: " + folder.getAbsolutePath());
                    giveFeedback();
                    Thread.sleep(2000);
                    edit.applyChangesToFolder(folder.getAbsolutePath());

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // reset the change history
        resetChangeHistoryButton.addActionListener(e -> {
            edit.resetChangeHistory();
        });

        // show change profile action listener
        showChangeButton.addActionListener(e -> {
            try {
                makeChangeHistoryTab();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // save change history action listener
        saveChangeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Change History");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                try {
                    edit.saveChangeHistory(fileToSave.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // load change history action listener
        loadChangeButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Load Change history");
            int userSelection = chooser.showOpenDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = chooser.getSelectedFile();
                try {
                    edit.loadChangeHistory(fileToLoad.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // validate change history action listener
        validateChangeButton.addActionListener(e -> {
            try {
                edit.validateChangeHistory(edit.xml_doc);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        // Opens the tutorial
        howToUseButton.addActionListener(e -> {
            try {
                edit.openTutorial();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // opens the about page
        aboutButton.addActionListener(e -> {
            try {
                edit.openAbout();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        // set schema path

        setSchemaPath.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Set Schema Path");
            int userSelection = chooser.showOpenDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToLoad = chooser.getSelectedFile();
                try {
                    edit.setSchemaPath(fileToLoad.getAbsolutePath());
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        openImage.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    edit.openImage(chooser.getSelectedFile().getAbsolutePath());
                    makeChangeHistoryTab();
                    // focus the xml tab
                    tabbedPane.setSelectedIndex(0);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Opens a new XML file in the topPanel of the GUI, in a new tab
        openXML.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    edit.openXML(chooser.getSelectedFile().getAbsolutePath());
                } catch (Exception ex) {
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

        exportOmeTiffButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to OmeTiff");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                try {
                    edit.exportToOmeTiff(fileToSave.getPath(), edit.xml_doc);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        exportOmeXmlButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Export to OmeXml");
            int userSelection = chooser.showSaveDialog(splitPane);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = chooser.getSelectedFile();
                try {
                    edit.exportToOmeXml(fileToSave.getPath(), edit.xml_doc);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Add an action listener to the JCheckBoxMenuItem
        simplifiedTree.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Get the source of the event
                JCheckBoxMenuItem source = (JCheckBoxMenuItem) e.getSource();
                // Get the selection state of the checkbox
                boolean selected = source.isSelected();
                // Perform some action based on the selection state
                if (selected) {
                    edit.simplified = true;
                    edit.updateTree();
                }
                else {
                    edit.simplified = false;
                    edit.updateTree();
                }
            }
        });

        // SET DIMENSIONS ##############################################################################################
        textField.setSize(WIDTH, BUTTON_HEIGHT);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        // SET LAYOUTS #################################################################################################
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,

        SpringLayout argPaneLayout = new SpringLayout();
        editPanel.setLayout(argPaneLayout);

        changeHistoryPane.setLayout(new ScrollPaneLayout());

        // SET BORDERS #################################################################################################


        // SET ICONS FOR MENU ITEMS ####################################################################################
        openImage.setIcon(loadSvgAsImageIcon(FILES_SVG));
        openXML.setIcon(loadSvgAsImageIcon(FILES_SVG));
        showCurrentXML.setIcon(loadSvgAsImageIcon(FILES_SVG));
        exportOmeTiffButton.setIcon(loadSvgAsImageIcon(FILES_SVG));
        exportOmeXmlButton.setIcon(loadSvgAsImageIcon(FILES_SVG));
        openSchemaButton.setIcon(loadSvgAsImageIcon(FILES_SVG));
        setSchemaPath.setIcon(loadSvgAsImageIcon(SETTINGS_SVG));

        showChangeButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        loadChangeButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        saveChangeButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        validateChangeButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        applyChangesToFolderButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        resetChangeHistoryButton.setIcon(loadSvgAsImageIcon(CHANGE_SVG));


        undoChangeButton.setIcon(loadSvgAsImageIcon(UNDO_SVG));

        howToUseButton.setIcon(loadSvgAsImageIcon(HELP_SVG));
        aboutButton.setIcon(loadSvgAsImageIcon(HELP_SVG));

        file.setIcon(loadSvgAsImageIcon(FILES_SVG));
        changeHistoryMenu.setIcon(loadSvgAsImageIcon(CHANGE_SVG));
        settings.setIcon(loadSvgAsImageIcon(SETTINGS_SVG));
        help.setIcon(loadSvgAsImageIcon(HELP_SVG));

        // POPULATE COMPONENTS #########################################################################################
        getContentPane().add(splitPane);

        // add menu to menu bar
        mb.add(file);
        mb.add(settings);
        mb.add(changeHistoryMenu);
        mb.add(help);

        // add menu items to file menu
        file.add(openImage);
        file.add(openXML);
        file.add(openSchemaButton);
        file.add(showCurrentXML);
        file.add(exportOmeTiffButton);
        file.add(exportOmeXmlButton);

        // add menu items to settings menu
        settings.add(simplifiedTree);
        settings.add(setSchemaPath);

        // add menu items to change history menu
        changeHistoryMenu.add(showChangeButton);
        changeHistoryMenu.add(loadChangeButton);
        changeHistoryMenu.add(saveChangeButton);
        changeHistoryMenu.add(validateChangeButton);
        changeHistoryMenu.add(undoChangeButton);
        changeHistoryMenu.add(applyChangesToFolderButton);
        changeHistoryMenu.add(resetChangeHistoryButton);

        // add menu item to tutorial menu
        help.add(howToUseButton);
        help.add(aboutButton);

        // add menu bar to frame
        this.add(mb);
        this.setJMenuBar(mb);
        bottomPanel.add(scrollPaneBottom);

        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(SCREEN_HEIGHT/2);
        splitPane.setTopComponent(tabbedPane);
        splitPane.setBottomComponent(bottomPanel);

    }
    /**
     * Creates a popup window that helps the user setting a valid schema path
     */
    public void popupSchemaHelp() {
        schemaHelp = new JOptionPane();
        JOptionPane.showMessageDialog(this, "Please set a valid schema path in the settings menu.", "Schema Path", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Takes a markdown string and transforms it into a html string. Then returns a JScrollPane
     * that shows the html.
     * @param md markdown string
     * @return JComponent that shows the html
     */
    public JComponent renderMarkdown(String md) {
        final String src = md;
        final MarkdownFlavourDescriptor flavour = new GFMFlavourDescriptor();
        final ASTNode parsedTree = new MarkdownParser(flavour).buildMarkdownTreeFromString(src);
        final String html = new HtmlGenerator(src, parsedTree, flavour, false).generateHtml();
        JEditorPane editor = new JEditorPane();
        JScrollPane scrollPane = new JScrollPane(editor);
        editor.setContentType("text/html");
        editor.setText(html);
        return scrollPane;
    }

    /**
     * Creates a change history tab in the tabbed pane
     */
    public void makeChangeHistoryTab() throws Exception {
        // create changer history window panel
        changeHistoryWindowPanel.setLayout(new BoxLayout(changeHistoryWindowPanel, BoxLayout.Y_AXIS));
        // add the change history pane to the change history window panel
        changeHistoryWindowPanel.add(changeHistoryPane);
        // Create Header entrys for the list of changes, so the user knows which entry is what
        makeHistoryTable();
        // add a textfield to the change history panel to display validation errors
        validationErrorsField.setEditable(false);
        validationErrorsField.setLineWrap(true);
        validationErrorsField.setWrapStyleWord(true);
        validationErrorsField.setSize(WIDTH, BUTTON_HEIGHT);
        // add a border to the validation errors textfield
        makePanelBorder(validationErrorsField);
        // add the validation errors to the change history window panel
        changeHistoryWindowPanel.add(validationErrorsField);
        // add the changes to the table model
        addChangesToTable(historyTableModel);
        // add the table to the change history pane
        changeHistoryPane.add(historyTable);
        // set the view port of the change history pane to the table
        changeHistoryPane.setViewportView(historyTable);
        // add the change panel to the tabbed pane
        makeNewTab(changeHistoryWindowPanel, "Change History", CHANGE_SVG);
    }

    /**
     * Initializes the change history table
     */
    public void makeHistoryTable() {
        // create an array of column names
        String[] columnNames = {"Index", "Change Type", "Location", "Node Type"};
        // create a default table model with no data
        historyTableModel = new XMLTableModel(columnNames, 0);
        // create a JTable with the model
        historyTable = new JTable(historyTableModel);
        // make the table cells editable
        historyTable.setDefaultEditor(Object.class, null);
        historyTable.setDefaultRenderer(Object.class, new XMLTableRenderer());
        // add a mouse listener to handle clicks on the table
        historyTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // get the row and column of the clicked cell
                int row = historyTable.rowAtPoint(e.getPoint());
                int col = historyTable.columnAtPoint(e.getPoint());
                // get the value of the clicked cell
                Object value = historyTable.getValueAt(row, col);
                // do something with the value (for example, print it)
                System.out.println("Clicked on: " + value);
            }
        });
    }

    /**
     * adds changes to the table
     */
    public void addChangesToTable(XMLTableModel model) throws TransformerException {
        // get the change history
        LinkedList<XMLChange> changeHistory = edit.getChangeHistory();
        edit.validateChangeHistory(edit.xml_doc);
        int index = 0;
        for (XMLChange c : changeHistory) {
            model.addRow(new Object[]{index, c.getChangeType(), c.getLocation(), c.getNodeType()});
            if (c.getValidity()) {
                model.setRowColor(index, PASTEL_GREEN);
                validationErrorsField.setText("No validation errors in most recent change found.");
                // set the text color to green
                validationErrorsField.setForeground(DARK_GREEN);

            }
            else if (!c.getValidity()) {
                model.setRowColor(index, PASTEL_RED);
                validationErrorsField.setText(c.getValidationError());
                // set the text color to red
                validationErrorsField.setForeground(DARK_RED);
            }
            else {
                model.setRowColor(index, PASTEL_BLUE);
            }
            index++;
        }
    }

    /**
     * Update the change history tab with the new changes
     */
    public void updateChangeHistoryTab() throws TransformerException {
        // empty the table model
        historyTableModel.setRowCount(0);
        // add new data to the table model
        addChangesToTable(historyTableModel);
        changeHistoryPane.add(historyTable);
        changeHistoryPane.setViewportView(historyTable);
    }

    /**
     * Creates a new table in a new tab that shows the user which files from the specified folder
     * (apply change to folder) have been changed and which have not.
     *
     */
    public void giveFeedback() {
        // create the array of column names for the table
        String[] columnNames = {"File Name", "Validation", "Exported"};
        // create a default table model with no data
        feedBackTableModel = new XMLTableModel(columnNames, 0);
        // create a JTable with the model
        JTable table = new JTable(feedBackTableModel);
        // make the table cells editable
        table.setDefaultEditor(Object.class, null);
        table.setDefaultRenderer(Object.class, new XMLTableRenderer());
        // add a mouse listener to handle clicks on the table
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                // get the row and column of the clicked cell
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                // get the value of the clicked cell
                Object value = feedBackTableModel.getValueAt(row, col);

                // do something with the value (for example, print it)
                System.out.println("Clicked on: " + value);
            }
        });

        // set the layout of the panel to a box layout
        //feedbackTablePanel.setLayout(new BoxLayout(feedbackTablePanel, BoxLayout.Y_AXIS));
        // set dimensions of the panel
        //feedbackTablePanel.setPreferredSize(new Dimension(feedbackTablePanel.getWidth(), feedbackTablePanel.getHeight()));
        // create a scroll pane for the table
        JScrollPane feedbackScrollPane = new JScrollPane(table);
        // set dimensions of the scroll pane
        feedbackScrollPane.setPreferredSize(new Dimension(feedbackScrollPane.getWidth(), feedbackScrollPane.getHeight()));
        // set viewport view to the table
        feedbackScrollPane.setViewportView(table);
        // add the scroll pane to the panel
        //feedbackTablePanel.add(feedbackScrollPane);
        // add the panel to the tabbed pane
        makeNewTab(feedbackScrollPane, "Feedback", FEEDBACK_SVG);
    }

    /**
     * Creates a new table in a new tab that shows the user which files from the specified folder
     * (apply change to folder) have been changed and which have not.
     *
     */
    public void addFeedback(FeedbackStore feedbackStore) {
        String fileName = feedbackStore.getFilePath().substring(feedbackStore.getFilePath().lastIndexOf(File.separator) + 1);
        feedBackTableModel.addRow(new Object[]{fileName, Boolean.toString(feedbackStore.getValidity()), Boolean.toString(feedbackStore.getExported())});
        if (feedbackStore.getValidity() & feedbackStore.getExported()) {
            // change color of the row to green
            feedBackTableModel.setRowColor(feedBackTableModel.getRowCount() - 1, PASTEL_GREEN);
        }
        else {
            // change color of the row to red
            feedBackTableModel.setRowColor(feedBackTableModel.getRowCount() - 1, PASTEL_RED);

        }
    }

    /**
     * Reports errors that occur in a little pop-up in the gui.
     */
    public void reportError(String exception) {
        JOptionPane.showMessageDialog(this, "Error: " + exception, "Error", JOptionPane.ERROR_MESSAGE);

    }

    /**
     * Adds a border to the specified panel with the specified title.
     * @param p the panel to which the border will be added
     * @param title the title of the border
     */
    public void makeTitledBorder(JPanel p, String title) {
        // create a compound border with a line border and an empty border
        Border line = BorderFactory.createLineBorder(Color.GRAY);
        Border margin = BorderFactory.createEmptyBorder(BASE_UNIT, BASE_UNIT, BASE_UNIT, BASE_UNIT);
        Border compound = BorderFactory.createCompoundBorder(line, margin);

        // create a titled border with the specified title
        Border titleBorder = BorderFactory.createTitledBorder(title);
        Border compound2 = BorderFactory.createCompoundBorder(compound, titleBorder);

        // set the border of this component
        p.setBorder(compound2);
        p.setOpaque(true);
    }

    /**
     * Adds a border to the specified panel
     * @param p the component to which the border will be added
     */
    public void makePanelBorder(JComponent p) {
        // create a compound border with a line border and an empty border
        Border line = BorderFactory.createLineBorder(Color.GRAY);
        Border margin = BorderFactory.createEmptyBorder(BASE_UNIT/2, BASE_UNIT/2, BASE_UNIT/2, BASE_UNIT/2);
        Border compound = BorderFactory.createCompoundBorder(line, margin);

        // set the border of this component
        p.setBorder(compound);
        p.setOpaque(true);
    }

    /**
     * Adds a border to the specified panel
     * @param p the component to which the border will be added
     */
    public void makeLineBorder(JComponent p) {
        // create a compound border with a line border and an empty border
        Border line = BorderFactory.createLineBorder(Color.GRAY, 1);
        // set the border of this component
        p.setBorder(line);
        p.setOpaque(true);
    }

    /**
     * Adds a change to the change history.
     * @param changeType the type of change that is to be maade on of the following: "add", "modify", "delete"
     * @param toBeChanged the node that is to be changed
     */
    public void makeNewChange(String changeType, XMLNode toBeChanged) throws MalformedURLException, TransformerException, SAXException {
        System.out.println("- - - - Make new change - - - -");
        XMLChange change = new XMLChange(changeType, toBeChanged);
        edit.changeHistory.add(change);

        if (toBeChanged.getType().equals("element") && changeType.equals("modify") && toBeChanged.getChildCount() > 0) {
            for (int c = 0; c < toBeChanged.getChildCount(); c++) {
                makeNewChange("add", (XMLNode) toBeChanged.getChildAt(c));
            }
            return;
        }
        // update the change history tab if it is opened

        if (tabbedPane.indexOfComponent(changeHistoryWindowPanel) != -1) {
            updateChangeHistoryTab();
        }
    }

    /**
     * Creates a new xml-viewer tab.
     * @param dom the xml dom that is to be displayed in the tab
     * @param simplified whether the xml-view should be simplified (only show elements) or not (show all nodes)
     * @param title the title of the new xml-viewer tab
     */
    public void makeNewTreeTab(Document dom, boolean simplified, String title) throws TransformerException {
        // create a new XMLTree from the DOM
        myTree = new XMLTree(dom, simplified);
        myTree.setOpaque(true);
        myTree.setShowsRootHandles(false);
        myTree.putClientProperty("JTree.lineStyle", "None");
        myTree.setDragEnabled(true);
        // add a listener to the jTree
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                // get the selected node
                selectedNode = (XMLNode) myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });

        // add the jTree to a new panel and add it to the tabbedPane
        scrollPaneTop.setViewportView(myTree);

        makeNewTab(scrollPaneTop, title, TREE_SVG);
    }

    /**
     * Updates the xml-viewer tab with the specified dom tree,
     * @param dom the new xml dom that is to be displayed in the tab
     * @param simplified whether the xml-view should be simplified (only show elements) or not (show all nodes)
     */
    public void updateTree(Document dom, boolean simplified){
        // create a new XMLTree from the DOM
        myTree = new XMLTree(dom, simplified);
        myTree.setOpaque(true);
        myTree.setShowsRootHandles(false);
        myTree.putClientProperty("JTree.lineStyle", "None");
        myTree.setDragEnabled(true);
        // add a listener to the jTree
        myTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                // get the selected node
                selectedNode = (XMLNode) myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });

        // add the jTree to a new panel and add it to the tabbedPane
        scrollPaneTop.setViewportView(myTree);
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
                selectedNode = (XMLNode) myTree.getLastSelectedPathComponent();
                updateEditPanel();
            }
        });
        JScrollPane currentScrollPane = new JScrollPane();
        currentScrollPane.setViewportView(currentTree);
        makeNewTab(currentScrollPane, title, TREE_SVG);
    }

    /**
     * Helper function called by multiple methods to create new tabs.
     * @param p the component that is to be added to a new tab
     * @param title the title of the new xml-viewer tab
     * @param svgCode the svg code that is to be used for the tab icon
     */
    public void makeNewTab(JComponent p, String title, String svgCode) {
        if (svgCode == null) {
            svgCode = DEFAULT_SVG;
        }
        p.setSize(p.getWidth(), BUTTON_HEIGHT);
        tabbedPane.addTab(title, p);
        // get index of new tab
        int index = tabbedPane.getTabCount()-1;
        MouseListener close = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int componentPos = tabbedPane.indexOfTabComponent(e.getComponent().getParent());
                System.out.println("Closing tab " + componentPos);
                tabbedPane.remove(componentPos);
            }
        };
        final CloseButton closeButton = new CloseButton(title,  loadSvgAsImageIcon(svgCode), close);
        closeButton.setOpaque(false);
        closeButton.setPreferredSize(new Dimension(BUTTON_WIDTH, 20));
        // closeButton.setFocusable(false);
        // set panel as tab component
        tabbedPane.setTabComponentAt(index, closeButton);
        // put newly created tab in front
        tabbedPane.setSelectedIndex(index);
    }

    /**
     * Loads an svg code as an ImageIcon
     * @param svgCode the svg code that is to be loaded
     * @return the ImageIcon that was created from the svg code
     */
    public ImageIcon loadSvgAsImageIcon(String svgCode) {
        // create a transcoder input from the svg code
        TranscoderInput input = new TranscoderInput(new StringReader(svgCode));

        // create an image transcoder that converts svg to image
        MyImageTranscoder trans = new MyImageTranscoder();
        // try to transcode the input and catch exceptions
        try {
            trans.transcode(input, null);
        } catch (TranscoderException e) {
            e.printStackTrace();
        }
        // get the image from the transcoder and create an ImageIcon from it#
        Image image = trans.getImage();
        ImageIcon icon = new ImageIcon(image);

        // return the ImageIcon
        return icon;
    }

    /**
     * Updates the editPanel, which shows the attributes and text nodes of the currently selected element node.
     */
    public void updateEditPanel() {
        // reset the editPanel
        labelCount = 0;
        editPanel.removeAll();
        editPanel.revalidate();
        editPanel.repaint();

        // identify the currently selected element node and add it as title button (the children will be added in addTitleButton())
        if (selectedNode.getType().equals("attribute") ||
                selectedNode.getType().equals("text") ||
                selectedNode.getType().equals("value")) {

            selectedNode = selectedNode.getParent();
            makeEditPanel();
        }
        else {
            makeEditPanel();
        }
        JPanel spacer = new JPanel();
        labelCount++;
        editPanel.add(spacer);
        spacer.setPreferredSize(new Dimension(0, spacer.getHeight()));
        SpringUtilities.makeCompactGrid(editPanel, labelCount, 1,0,0,0, 0);
        scrollPaneBottom.setViewportView(editPanel);
    }

    /**
     * Creates the editPanel, which shows the attributes and text nodes of the currently selected element node.
     */
    private void makeEditPanel() {
        // make border for the editPanel
        // makePanelBorder(editPanel);
        // add the Element Button to the editPanel
        addElementButton();
        // draw the children of the element node
        for (int c=0; c<selectedNode.getChildCount();c++) {
            XMLNode child = (XMLNode) selectedNode.getChildAt(c);
            if (child.getType().equals("attribute")) {
                XMLNode childChild = child.getFirstChild();
                addAttributeButton(child.getUserObject().toString(), childChild);
            }
            else if (child.getType().equals("value")) {
                addAttributeButton(selectedNode.getUserObject().toString(), child);
            }
            else if (child.getType().equals("text")){
                addTextButton(child);
            }
        }
    }

    /**
     * Adds a title button to the editPanel at the top. If clicked, an add and a delete button will pop up.
     */
    private void addElementButton() {
        // reset the titlePanel
        titlePanel.removeAll();
        titlePanel.revalidate();
        titlePanel.repaint();
        // create the title Panel
        titlePanel.setLayout(new BorderLayout(0,0));
        // create a new title button in the editPanel, if clicked an add and a delete button will pop up
        JToggleButton titleButton = new JToggleButton(selectedNode.getUserObject().toString());
        titleButton.setBorder(fieldBorder);
        // set the preferred size of the title button to the width of the title panel and the height of the standard button height
        Dimension d = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        addButton.setPreferredSize(d);
        delButton.setPreferredSize(d);
        titleButton.setPreferredSize(d);
        titlePanel.setPreferredSize(d);
        // sett the maximum size of the title button (and add and del button) to the width of the title panel and the height of the standard button height
        titleButton.setMaximumSize(d);
        addButton.setMaximumSize(d);
        delButton.setMaximumSize(d);
        titlePanel.setMaximumSize(d);
        // set the minimum size of the title button (and add and del button) to the width of the title panel and the height of the standard button height
        titleButton.setMinimumSize(d);
        addButton.setMinimumSize(d);
        delButton.setMinimumSize(d);
        titlePanel.setMinimumSize(d);
        // add the title button to the title panel
        titlePanel.add(titleButton, BorderLayout.CENTER);
        editPanel.add(titlePanel);
        labelCount +=1;
        // add to button group so only one attribute / title can be selected at a time
        bg.add(titleButton);
        // add a listener to the button to detect selection and show the add and delete button

        titleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // set the to be deleted node to the currently selected element node
                toBeDeletedNode = selectedNode;
                // add the buttons to the title panel
                titlePanel.add(delButton, BorderLayout.EAST);
                titlePanel.add(addButton, BorderLayout.WEST);
                titlePanel.revalidate();
                titlePanel.repaint();
            }
        });

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
        makeLineBorder(attrButton);
        // make new textfield for the attribute
        NodedTextField textField = new NodedTextField(node);
        // give the textfield  a border
        makeLineBorder(textField);
        // add to button group so only one attribute can be selected at a time
        bg.add(attrButton);
        // change the background color of the textfield when it is selected
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(PASTEL_BLUE);
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
                titlePanel.remove(addButton);
                // set the to be deleted node to the currently selected attribute
                toBeDeletedNode = node.getParent();
                // add the delete button to the attributePanel
                attributePanel.add(delButton, BorderLayout.EAST);
                attributePanel.revalidate();
                attributePanel.repaint();
            }
        });
        // set the size of the buttons and the textfield
        Dimension d = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        attributePanel.setPreferredSize(d);
        attrButton.setPreferredSize(d);
        delButton.setPreferredSize(d);
        // set the minimum size of the buttons and the textfield
        attributePanel.setMinimumSize(d);
        attrButton.setMinimumSize(d);
        delButton.setMinimumSize(d);
        // set the maximum size of the buttons and the textfield
        attributePanel.setMaximumSize(d);
        attrButton.setMaximumSize(d);
        delButton.setMaximumSize(d);

        // add the components to the panel and the panel to the editPanel
        attributePanel.add(attrButton, BorderLayout.WEST);
        attributePanel.add(textField, BorderLayout.CENTER);
        editPanel.add(attributePanel);

        // increase the labelCount so the layout can be updated
        labelCount +=1;
    }

    /**
     * Adds a text button to the editPanel
     * @param node
     */
    private void addTextButton(XMLNode node) {
        // add a new text to the editPanel consisting of a label, a textfield and a delete button in a new panel
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout(0,0));
        NodedTextField textField = new NodedTextField(node);
        // give the textfield  a border
        makeLineBorder(textField);

        // add a listener to the textfield to dtetect if it is selected
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBackground(PASTEL_BLUE);
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
                    titlePanel.remove(addButton);

                    // set the to be deleted node to the currently selected text
                    toBeDeletedNode = node;

                    // add the delete button to the textPanel
                    textPanel.add(delButton, BorderLayout.EAST);
                    textPanel.revalidate();
                    textPanel.repaint();
                }
            }
        });

        // set the size of the buttons and the textfield
        Dimension d = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        textPanel.setPreferredSize(d);
        delButton.setPreferredSize(d);
        // set the minimum size of the buttons and the textfield
        textPanel.setMinimumSize(d);
        delButton.setMinimumSize(d);
        // set the maximum size of the buttons and the textfield
        textPanel.setMaximumSize(d);
        delButton.setMaximumSize(d);

        // add the components to the panel and the panel to the editPanel
        //textPanel.add(textButton, BorderLayout.WEST);
        textPanel.add(textField, BorderLayout.CENTER);
        editPanel.add(textPanel);

        // increase the labelCount so the layout can be updated
        labelCount +=1;
    }

    /**
     * Initializes the add button
     */
    private void initializeAddButton() {
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
            attrField.setPreferredSize(new Dimension(attrField.getWidth(), BUTTON_HEIGHT));
            valField.setPreferredSize(new Dimension(valField.getWidth(), BUTTON_HEIGHT));
            textField.setPreferredSize(new Dimension(textField.getWidth(), BUTTON_HEIGHT));
            elementField.setPreferredSize(new Dimension(elementField.getWidth(), BUTTON_HEIGHT));
            elementIDField.setPreferredSize(new Dimension(elementIDField.getWidth(), BUTTON_HEIGHT));

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
                    selectedNode.add(newAttrNode);

                    // add the new node to the change history
                    try {
                        makeNewChange("add", newAttrNode);
                    } catch (MalformedURLException | TransformerException | SAXException e) {
                        throw new RuntimeException(e);
                    }

                    // add the new node to the argument panel
                    addAttributeButton(newAttrNode.getUserObject().toString(), newValNode);

                    // redraw the argument panel
                    updateEditPanel();
                    myTree.updateUI();
                }
                else if (selection.equals("text")) {
                    // create new XMLNode and add it to the selected node
                    XMLNode newTextNode = new XMLNode();
                    newTextNode.setUserObject(textField.getText());
                    newTextNode.setType("text");
                    selectedNode.add(newTextNode);

                    // add the new node to the change history
                    try {
                        makeNewChange("add", newTextNode);
                    } catch (MalformedURLException | SAXException | TransformerException e) {
                        throw new RuntimeException(e);
                    }

                    // add the new node to the argument panel
                    addAttributeButton(newTextNode.getUserObject().toString(), newTextNode);

                    // redraw the argument panel
                    updateEditPanel();
                    myTree.updateUI();
                }
                else if (selection.equals("element")) {
                    // create new XMLNode and add it to the selected node
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
                    // redraw the argument panel
                    updateEditPanel();
                    System.out.println(selectedNode.getChildCount());
                    // redraw the tree
                    XMLTreeModel model = (XMLTreeModel) myTree.getModel();
                    model.insertNodeInto(newElementNode, selectedNode, selectedNode.getChildCount());
                    System.out.println(selectedNode.getChildCount());
                    myTree.updateUI();
                    // add the new node to the change history
                    try {
                        makeNewChange("add", newElementNode);
                    }
                    catch (MalformedURLException | TransformerException | SAXException e) {
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

    /**
     *
     */
    private void initializeDelButton() {
        // remove all the action listeners from the delete button
        for (ActionListener al : delButton.getActionListeners()) {
            delButton.removeActionListener(al);
        }
        // add action listener to delete button
        delButton.addActionListener(event -> {
            System.out.println("Delete button pressed");
            System.out.println("To be Deleted node: " + toBeDeletedNode.getUserObject());

            // remove the node from the tree
            try {
                makeNewChange("delete", toBeDeletedNode);
            } catch (MalformedURLException | TransformerException | SAXException e) {
                throw new RuntimeException(e);
            }
            XMLNode parentNode = toBeDeletedNode.getParent();
            selectedNode = parentNode;
            parentNode.remove(toBeDeletedNode);

            myTree.updateUI();
            updateEditPanel();
        });
    }
}

