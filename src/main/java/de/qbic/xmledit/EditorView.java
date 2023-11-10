// PACKAGE
package de.qbic.xmledit;

// ---------------------------------------------------------------------------------------------------------------------
// IMPORTS
// ---------------------------------------------------------------------------------------------------------------------
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

public class EditorView extends javax.swing.JFrame {
    /** This class defines the visual representation of the OME-Editor.<
     *
     */
    // -----------------------------------------------------------------------------------------------------------------
    // Constants
    // -----------------------------------------------------------------------------------------------------------------
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
    //-----------------------------------------------------------------------------------------------------------------
    // Instantiate Components
    //-----------------------------------------------------------------------------------------------------------------
    public Document xml;
    public XMLTree myTree;
    public JSplitPane splitPane;  // split the window in top and bottom
    private JPanel bottomPanel;    // container panel for the bottom
    public JScrollPane scrollPaneBottom; // makes the text scrollable
    public JScrollPane scrollPaneTop; // makes the text scrollable
    public NodedTextField textField;     // the text
    private JMenuItem openSchemaButton;
    public JMenuItem undoChangeButton;
    public JMenuItem resetChangeHistoryButton;
    public JMenuItem applyChangesToFolderButton;
    public JMenuItem showCurrentXML;
    public JMenuItem showChangeButton;
    public JMenuItem loadChangeButton;
    public JMenuItem saveChangeButton;
    public JMenuItem validateChangeButton;
    public JMenuItem exportOmeTiffButton;
    public JMenuItem exportOmeXmlButton;
    public JMenuItem howToUseButton;
    public JMenuItem aboutButton;
    public JMenuItem setSchemaPath;
    public JCheckBoxMenuItem simplifiedTree;
    public JPanel editPanel;
    private JMenuBar mb;
    private JMenu file, settings, changeHistoryMenu, help;
    public JMenuItem openImage;
    public JMenuItem openXML;
    public static final JPanel titlePanel = new JPanel();
    public static final JTabbedPane tabbedPane = new JTabbedPane();
    public static final JScrollPane changeHistoryPane = new JScrollPane();
    public static final ButtonGroup bg = new ButtonGroup();
    public int labelCount =0;
    public static final JButton addButton = new JButton("Add Node");
    public static final JButton delButton = new JButton("Delete");
    public static final JTextArea validationErrorsField = new JTextArea();
    public static final JPanel changeHistoryWindowPanel = new JPanel();
    public XMLNode selectedNode;
    public XMLNode toBeDeletedNode;
    private final Border fieldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);
    public XMLTableModel feedBackTableModel = null;
    public XMLTableModel historyTableModel = null;
    public JTable historyTable = null;
    private JOptionPane schemaHelp = null;

    // create a new panel for the table
    // private JPanel feedbackTablePanel = new JPanel();

    // -----------------------------------------------------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------------------------------------------------
    public EditorView() {
        // INITIALIZE COMPONENTS ---------------------------------------------------------------------------------------
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



        // -------------------------------------------------------------------------------------------------------------
        // SET LAYOUT + DIMENSIONS
        // -------------------------------------------------------------------------------------------------------------
        textField.setSize(WIDTH, BUTTON_HEIGHT);
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS)); // BoxLayout.Y_AXIS will arrange the content vertically
        getContentPane().setLayout(new GridLayout());  // the default GridLayout is like a grid with 1 column and 1 row,

        SpringLayout argPaneLayout = new SpringLayout();
        editPanel.setLayout(argPaneLayout);

        changeHistoryPane.setLayout(new ScrollPaneLayout());

        //--------------------------------------------------------------------------------------------------------------
        // SET BORDERS
        //--------------------------------------------------------------------------------------------------------------

        //--------------------------------------------------------------------------------------------------------------
        // SET ICONS FOR MENU ITEMS
        //--------------------------------------------------------------------------------------------------------------
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

        //--------------------------------------------------------------------------------------------------------------
        // POPULATE COMPONENTS
        //--------------------------------------------------------------------------------------------------------------
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

        // -------------------------------------------------------------------------------------------------------------
        // DISPLAY
        // -------------------------------------------------------------------------------------------------------------
        this.pack();

    }
    // -----------------------------------------------------------------------------------------------------------------
    // METHODS
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * Reports errors that occur in a little pop-up in the gui.
     */
    public void reportError(String exception) {
        JOptionPane.showMessageDialog(this, "Error: " + exception, "Error", JOptionPane.ERROR_MESSAGE);

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
     * Creates a new Feedback tab in the tabbed pane
     */
    public void makeFeedbackTab() { // originally called giveFeedback
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
     * Adds a title button to the editPanel at the top. If clicked, an add and a delete button will pop up.
     */
    public void addElementButton() {
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

    public void showXMLTree(Document exampleXmlDoc, String nodeName) {
    }

    public void makeChangeHistoryTab() {
    }

    public void makeNewTreeTab(Document newXmlDoc, Object simplified, String title) {
    }

    public void updateTree() {
    }

    public void updateTreeTab(Document newXmlDoc, Object simplified) {
    }


    /**
     * Initializes the add button
     */



}
