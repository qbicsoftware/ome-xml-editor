package life.qbic.xmledit;

import net.imagej.ImageJ;

public class Main {
  /**
   * Starts a new ImageJ session when started from outside Fiji
   */
  public static void main(String[] args) {
    final ImageJ ij = new ImageJ();
    ij.command().run(XMLEditor.class, true);
  }
}
