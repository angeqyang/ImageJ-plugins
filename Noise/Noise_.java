import ij.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;

public class Noise_ implements PlugIn{

	public void run(String arg) {
		IJ.runMacroFile("Noise.ijm");
	}
}
