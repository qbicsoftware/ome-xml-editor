package de.qbic.xml_edit;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LabeledTextFieldsSelectableExample {

    private static JPanel selectedPanel;
    private static JButton deleteButton;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Selectable Labeled Text Fields");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(300, 200));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        Border fieldBorder = BorderFactory.createLineBorder(Color.GRAY, 1);

        addLabeledTextField(panel, fieldBorder, "First Name");
        addLabeledTextField(panel, fieldBorder, "Last Name");
        addLabeledTextField(panel, fieldBorder, "Address");
        addLabeledTextField(panel, fieldBorder, "City");
        addLabeledTextField(panel, fieldBorder, "State");
        addLabeledTextField(panel, fieldBorder, "Zip Code");

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (selectedPanel != null) {
                    selectedPanel.setBackground(null);
                    selectedPanel = null;
                    deleteButton = null;
                }
            }
        });

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
    }

    private static void addLabeledTextField(JPanel panel, Border fieldBorder, String labelText) {
        JLabel label = new JLabel(labelText);
        JTextField textField = new JTextField(20);
        textField.setBorder(BorderFactory.createCompoundBorder(fieldBorder, new EmptyBorder(0, 10, 0, 0)));
        label.setLabelFor(textField);
        label.setBorder(BorderFactory.createCompoundBorder(fieldBorder, new EmptyBorder(0, 0, 0, 10)));

        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.add(label, BorderLayout.WEST);
        fieldPanel.add(textField, BorderLayout.CENTER);
        fieldPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        fieldPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    if (selectedPanel != null) {
                        selectedPanel.remove(deleteButton);
                        selectedPanel.setBackground(null);


                    }
                    JPanel clickedPanel = (JPanel) e.getSource();
                    clickedPanel.setBackground(Color.YELLOW);
                    selectedPanel = clickedPanel;

                    // Add a button to delete the labeled text field
                    deleteButton = new JButton("Delete");
                    deleteButton.addActionListener(event -> {
                        panel.remove(clickedPanel);
                        panel.revalidate();
                        panel.repaint();
                    });

                    // Add the button to the right of the labeled text field
                    fieldPanel.add(deleteButton, BorderLayout.EAST);
                    fieldPanel.revalidate();
                    fieldPanel.repaint();
                }
            }
        });

        panel.add(fieldPanel);
    }
}


