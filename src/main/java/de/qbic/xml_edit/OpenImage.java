package de.qbic.xml_edit;

import loci.plugins.LociImporter;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;


import java.io.File;

@Plugin(type = Command.class, menuPath = "Plugins>OpenImage")
public class OpenImage<T extends RealType<T>> implements Command {

    @Parameter
    private ImageJ ij;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;

    @Override
    public void run() {
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            new LociImporter().run(file.getPath());

        }
    }

    /**
     * This main function serves for development purposes.
     * It allows you to run the plugin immediately out of
     * your integrated development environment (IDE).
     *
     * @param args whatever, it's ignored
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        // create the ImageJ application context with all available services
        final ImageJ ij = new ImageJ();

        ij.ui().showUI();


        // ask the user for a file to open
        final File file = ij.ui().chooseFile(null, "open");

        if (file != null) {
            // load the dataset
            final Dataset dataset = ij.scifio().datasetIO().open(file.getPath());

            // show the image
            ij.ui().show(dataset);

            // invoke the plugin
            ij.command().run(OpenImage.class, true);
        }
    }
}
