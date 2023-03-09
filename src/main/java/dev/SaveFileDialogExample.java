package dev; /**
 * @(#)SaveFileDialogExample.java 1.0
 * This code is written by www.codejava.net
 *
 */

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class SaveFileDialogExample {
	JTextArea textArea;
	JButton save;
	void initUI() {
		JFrame frame = new JFrame(SaveFileDialogExample.class.getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		textArea = new JTextArea(24, 80);
		save = new JButton("Save to file");
		save.addActionListener(e -> saveToFile());
		frame.add(new JScrollPane(textArea));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(save);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		frame.setSize(500, 400);
		frame.setVisible(true);
	}

	protected void saveToFile() {
		JFileChooser fileChooser = new JFileChooser();
		int retval = fileChooser.showSaveDialog(save);
		if (retval == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			if (file == null) {
				return;
			}
			if (!file.getName().toLowerCase().endsWith(".txt")) {
				file = new File(file.getParentFile(), file.getName() + ".txt");
			}
			try {
				textArea.write(new OutputStreamWriter(new FileOutputStream(file),
						"utf-8"));
				Desktop.getDesktop().open(file);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		new SaveFileDialogExample().initUI();
	}
}