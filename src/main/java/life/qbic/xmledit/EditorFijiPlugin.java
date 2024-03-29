package life.qbic.xmledit;

import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Plugin;

import loci.common.DebugTools;
import net.imagej.ImageJ;

@Plugin(type = Command.class, menuPath = "Plugins>OME-XML-Editor")
public class EditorFijiPlugin<T extends RealType<T>> implements Command {

    /**
     * runs the plugin when started from within FiJi
     */
    @Override
    public void run() {
        DebugTools.enableLogging("INFO");
        Editor myEditor;
        EditorView myEditorView = null;
        EditorController myEditorController;

        myEditor = new Editor();
        myEditorController = new EditorController(myEditorView, myEditor);
        myEditorView = new EditorView(myEditorController);
        myEditorView.setVisible(true);

    }
    /**
     * Starts a new ImageJ session when started from outside Fiji
     */
    public static void main(String[] args) throws Exception {
        final ImageJ ij = new ImageJ();
        ij.command().run(EditorFijiPlugin.class, true);
    }
}
