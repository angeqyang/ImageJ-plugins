import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import ij.io.*;
import java.awt.*;

public class Unscramble_ implements PlugIn{

	public void run(String arg) {
    DirectoryChooser prompt = new DirectoryChooser("Select a Folder");
    String selected = prompt.getDirectory();

    FolderOpener opener = new FolderOpener();
    ImagePlus imp = opener.openFolder(selected);

    imp.show();

		GenericDialog gd = new GenericDialog("Split Image?");
    gd.addMessage("Are the pixels going to be split into two images?");
    gd.enableYesNoCancel();
    gd.showDialog();

		if (gd.wasCanceled()) {
			return;
		} else if (gd.wasOKed()) {
      IJ.runPlugIn(imp, "UnscrambleHelper2", "");
		} else {
      IJ.runPlugIn(imp, "UnscrambleHelper", "");
		}
	}
}
