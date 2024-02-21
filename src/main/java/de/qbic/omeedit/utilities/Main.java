package de.qbic.omeedit.utilities;

import de.qbic.omeedit.views.CommandLineInterface;
import de.qbic.omeedit.views.GraphicalUserInterface;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("cli")) {
                // Start command line interface
                CommandLineInterface cli = new CommandLineInterface();
                // Assuming there is a method to start the CLI
                cli.start();
            }
            else if (args[0].equals("gui")) {
                // Start graphical user interface
                GraphicalUserInterface gui = new GraphicalUserInterface();
                // Assuming there is a method to start the GUI
                gui.start();
            }
            else {
                System.out.println("Please specify either 'cli' or 'gui' as first argument.");
            }
        }
        else {
            System.out.println("No arguments provided. Please specify either 'cli' or 'gui' as first argument.");
        }
    }
}
