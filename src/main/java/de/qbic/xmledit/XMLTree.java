// Build the XML Tree for GUI + ImageInfo

package de.qbic.xmledit;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.swing.tree.TreeModel;

class XMLTree extends JTree {

    XMLTreeModel dtModel;

    public XMLTree(Document xml, boolean simplified) {
        if (xml != null)
            setTree(xml);
        if (simplified) {
            this.setCellRenderer(new XMLTreeRendererSimplified(this));
        } else {
            this.setCellRenderer(new XMLTreeRenderer());
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
