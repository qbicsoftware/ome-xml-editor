package dev;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class XMLViewer extends JFrame {

    private JTree xmlTree;

    public XMLViewer() {
        setTitle("XML Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);

        xmlTree = new JTree();

        JScrollPane scrollPane = new JScrollPane(xmlTree);
        getContentPane().add(scrollPane);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.addActionListener(e -> loadXMLFile());
        fileMenu.add(openMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void loadXMLFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                Document doc = factory.newDocumentBuilder().parse(file);
                DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(doc.getDocumentElement().getNodeName());
                addNodes(doc.getDocumentElement(), rootNode);
                xmlTree.setModel(new DefaultTreeModel(rootNode));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void addNodes(Node parentNode, DefaultMutableTreeNode treeNode) {
        NodeList list = parentNode.getChildNodes();
        for (int i = 0; i < list.getLength(); i++) {
            Node node = list.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(node.getNodeName());
                treeNode.add(childTreeNode);
                addNodes(node, childTreeNode);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            XMLViewer viewer = new XMLViewer();
            viewer.setVisible(true);
        });
    }
}

