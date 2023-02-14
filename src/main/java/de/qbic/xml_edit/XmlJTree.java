// Build the XML Tree for GUI + ImageInfo

package de.qbic.xml_edit;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

class XmlJTree extends JTree {

    XMLTreeModel dtModel = null;

    public XmlJTree(Document xml) {
        if (xml != null)
            setTree(xml);
    }


    public void setPath(String filePath) {
        Node root;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(filePath);
            root = doc.getDocumentElement();
            System.out.println(root.getNodeName());
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

    private XmlNode builtTreeNode(Node root) {
        XmlNode dmtNode;
        dmtNode = new XmlNode(root.getNodeName());

        if (root.getNodeType() == 1) {

            String text;

            if (root.hasAttributes()) {
                int numAttrs = root.getAttributes().getLength();

                for (int i = 0; i < numAttrs; i++){
                    Attr attr = (Attr) root.getAttributes().item(i);
                    String attrName = attr.getNodeName();
                    String attrValue = attr.getNodeValue();
                    XmlNode atrNode = new XmlNode("@"+attrName);
                    XmlNode valNode = new XmlNode(":"+attrValue);
                    atrNode.add(valNode);
                    dmtNode.add(atrNode);
                }
            }
        }
        else if (root.getNodeType() == 2) {
            System.out.println("Node Type 2");
        }
        else if (root.getNodeType() == 3) {
            dmtNode = new XmlNode("#"+root.getTextContent());
        }

        NodeList nodeList = root.getChildNodes();


        for (int count = 0; count < nodeList.getLength(); count++) {
            Node tempNode = nodeList.item(count);
            dmtNode.add(builtTreeNode(tempNode));

        }
        return dmtNode;
    }
}
