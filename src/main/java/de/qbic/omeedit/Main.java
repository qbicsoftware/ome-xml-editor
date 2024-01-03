package de.qbic.omeedit;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            if (args[0].equals("cli")) {
                // Start command line interface
                CommandLineInterface cli = new CommandLineInterface();
            }
            else if (args[0].equals("gui")) {
                // Start graphical user interface
                GraphicalUserInterface gui = new GraphicalUserInterface();
                // gui.startView();
            }
            else {
                System.out.println("Please specify either 'cli' or 'gui' as first argument.");
            }
        }

    }
}
