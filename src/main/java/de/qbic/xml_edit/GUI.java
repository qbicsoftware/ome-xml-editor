package de.qbic.xml_edit;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeCellEditor;

public class GUI {

    public GUI() {
        JFrame frame = new JFrame("Demo");
        DefaultMutableTreeNode node = new DefaultMutableTreeNode("Products");
        DefaultMutableTreeNode node1 = new DefaultMutableTreeNode("Clothing");
        DefaultMutableTreeNode node2 = new DefaultMutableTreeNode("Electronics");
        DefaultMutableTreeNode node3 = new DefaultMutableTreeNode("Home Decor");
        DefaultMutableTreeNode node4 = new DefaultMutableTreeNode("Furniture");
        node.add(node1);
        node.add(node2);
        node.add(node3);
        node.add(node4);
        DefaultMutableTreeNode one = new DefaultMutableTreeNode("Shirt");
        DefaultMutableTreeNode two = new DefaultMutableTreeNode("Trousers");
        DefaultMutableTreeNode three = new DefaultMutableTreeNode("Jeans");
        DefaultMutableTreeNode four = new DefaultMutableTreeNode("Mobiles");
        DefaultMutableTreeNode five = new DefaultMutableTreeNode("Camera");
        DefaultMutableTreeNode six = new DefaultMutableTreeNode("Tablet");
        DefaultMutableTreeNode seven = new DefaultMutableTreeNode("Paintings");
        DefaultMutableTreeNode eight = new DefaultMutableTreeNode("Showpieces");
        DefaultMutableTreeNode nine = new DefaultMutableTreeNode("Wardrobes");
        DefaultMutableTreeNode ten = new DefaultMutableTreeNode("Bean Bags");
        node1.add(one);
        node1.add(two);
        node1.add(three);
        node2.add(four);
        node2.add(five);
        node2.add(six);
        node3.add(seven);
        node3.add(eight);
        node4.add(nine);
        node4.add(ten);
        JTree tree = new JTree(node);
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        tree.putClientProperty("JTree.lineStyle", "Angled");
        JTextField textField = new JTextField();
        TreeCellEditor editor = new DefaultCellEditor(textField);
        tree.setEditable(true);
        tree.setCellEditor(editor);
        tree.setRowHeight(25);
        frame.add(tree);
        frame.setSize(600,450);
        frame.setVisible(true);
    }

    public DefaultMutableTreeNode build_node(String tree) {
        DefaultMutableTreeNode node = new DefaultMutableTreeNode(tree.split(" ")[0]);
        for (String entity : tree.split("\n")) {
            DefaultMutableTreeNode new_node = new DefaultMutableTreeNode(entity);
            node.add(new_node);
        }

        return node;
    }

    public static void main(String[] args) {
        new GUI();
    }
}
