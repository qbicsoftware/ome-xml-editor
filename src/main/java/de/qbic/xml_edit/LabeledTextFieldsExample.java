package de.qbic.xml_edit;

import javax.swing.*;

public class LabeledTextFieldsExample extends JFrame {

    public LabeledTextFieldsExample() {
        setTitle("Labeled Text Fields Example");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createTitledBorder("Personal Information"));
        SpringLayout layout = new SpringLayout();
        panel.setLayout(layout);

        addLabeledTextField(panel, "First Name:");
        addLabeledTextField(panel, "Last Name:");
        addLabeledTextField(panel, "Address:");
        addLabeledTextField(panel, "City:");
        addLabeledTextField(panel, "State:");
        addLabeledTextField(panel, "Zip:");

        setContentPane(panel);
        pack();
        setVisible(true);
    }

    private void addLabeledTextField(JPanel panel, String labelName) {
        JLabel label = new JLabel(labelName);
        JTextField textField = new JTextField();
        panel.add(label);
        panel.add(textField);

        SpringLayout layout = (SpringLayout) panel.getLayout();

        // Set constraints for label
        SpringLayout.Constraints labelConstraints = layout.getConstraints(label);
        labelConstraints.setX(Spring.constant(5));
        if (panel.getComponentCount() == 2) {
            labelConstraints.setY(Spring.constant(5));
        } else {
            SpringLayout.Constraints prevTextFieldConstraints =
                    layout.getConstraints(panel.getComponent(panel.getComponentCount() - 3));
            labelConstraints.setY(Spring.sum(prevTextFieldConstraints.getConstraint(SpringLayout.SOUTH),
                    Spring.constant(5)));
        }

        // Set constraints for text field
        SpringLayout.Constraints textFieldConstraints = layout.getConstraints(textField);
        textFieldConstraints.setX(Spring.sum(labelConstraints.getConstraint(SpringLayout.EAST),
                Spring.constant(5)));
        textFieldConstraints.setY(labelConstraints.getY());
        textFieldConstraints.setConstraint(SpringLayout.EAST, Spring.constant(0));
        textFieldConstraints.setWidth(Spring.constant(150));
    }

    public static void main(String[] args) {
        new LabeledTextFieldsExample();
    }
}
