package de.qbic.xml_edit;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;

public class Main {

    public static void main(String[] args) {
        JFrame f = new JFrame();

        f.setSize(300, 500);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel pan = new JPanel(new GridLayout(1, 1));
        XmlJTree myTree = new XmlJTree(null);
        f.add(new JScrollPane(myTree));
        JButton button = new JButton("Choose file");
        button.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("XML file",
                    "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                myTree.setPath(chooser.getSelectedFile().getAbsolutePath());
            }
        });
        pan.add(button);
        f.add(pan, BorderLayout.SOUTH);
        f.setVisible(true);
    }
}

class XmlJTree extends JTree {

    DefaultTreeModel dtModel = null;

    public XmlJTree(String filePath) {
        if (filePath != null)
            setPath(filePath);
    }

    public void setPath(String filePath) {
        Node root = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            System.out.println(doc);
            root = (Node) doc.getDocumentElement();
            System.out.println(root);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Can't parse file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (root != null) {
            dtModel = new DefaultTreeModel(builtTreeNode((DefaultMutableTreeNode) root));
            this.setModel(dtModel);
        }
    }

    private DefaultMutableTreeNode builtTreeNode(DefaultMutableTreeNode root) {
        DefaultMutableTreeNode dmtNode;
        DefaultMutableTreeNode dmtNode2;

        dmtNode2 = new DefaultMutableTreeNode();

        dmtNode = new DefaultMutableTreeNode(root.getUserObject());

        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode tempNode = (DefaultMutableTreeNode) root.getChildAt(i);
            if (tempNode.getChildCount() > 0) {
                dmtNode.add(builtTreeNode(tempNode));
            }
        }

        return dmtNode;
    }

}
