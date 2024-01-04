// Build the XML Tree for GUI + ImageInfo

package de.qbic.omeedit.utilities;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.TreeModel;

public class XMLTree extends JTree {

    XMLTreeModel dtModel;
    private final boolean simplified;

    public XMLTree(Document xml, boolean simplified) {
        this.simplified = simplified;
        if (xml != null)
            setTree(xml);

        if (simplified) {

            this.setCellRenderer(new XMLTreeRendererSimplified());
            this.setShowsRootHandles(false);
            this.setDragEnabled(true);
        } else {
            this.setCellRenderer(new XMLTreeRenderer());
            this.setShowsRootHandles(false);
            this.setDragEnabled(true);
        }
    }
    @Override
    public TreeModel getModel() {
        return dtModel;
    }

    public void setTree(Document xml) {
        Node root;
        try {
            root = xml;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Can't parse file", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (root != null) {
            dtModel = new XMLTreeModel(builtTreeNode(root.getFirstChild()));
            dtModel.activateFilter(simplified);
            dtModel.reload();
            this.setModel(dtModel);
        }
    }

    private XMLNode builtTreeNode(Node root) {
        XMLNode dmtNode;
        dmtNode = new XMLNode(root.getNodeName());

        if (root.getNodeType() == 1) {

            String text;

            if (root.hasAttributes()) {
                int numAttrs = root.getAttributes().getLength();

                for (int i = 0; i < numAttrs; i++){
                    Attr attr = (Attr) root.getAttributes().item(i);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    XMLNode atrNode = new XMLNode(attrName);
                    atrNode.setType("attribute");
                    XMLNode valNode = new XMLNode(attrValue);
                    valNode.setType("value");
                    atrNode.add(valNode);
                    dmtNode.add(atrNode);
                }
            }
        }
        else if (root.getNodeType() == 2) {
            System.out.println("Node Type 2");
        }
        else if (root.getNodeType() == 3) {
            dmtNode = new XMLNode(root.getTextContent());
            dmtNode.setType("text");
        }

        NodeList nodeList = root.getChildNodes();

        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            dmtNode.add(builtTreeNode(tempNode));

        }

        return dmtNode;
    }
}
