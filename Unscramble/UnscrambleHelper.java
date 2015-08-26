import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import java.lang.Math;
import java.lang.String.*;
import java.util.Arrays;

public class UnscrambleHelper implements PlugInFilter {
  ImagePlus imp;
	ImageProcessor ip;
	int width; //dimensions of original image
	int height;
  int x0; //dimensions of original repeating block
  int y0;
  int x1; //dimensions of destination repeating block
  int y1;
  int xd; //dimensions of destination image
  int yd;
  int[][][] newDest; //(x,y) are pixels in original, values are [x,y] dest pixels
  float[][] values; //pixel values of original image
  float[][] destination; //pixel values of destination image
  ImagePlus result;
  ImageStack destStack;
  String title;

	public int setup(String arg, ImagePlus imp) {

  	if (IJ.versionLessThan("1.32c"))
			return DONE;

		imp.unlock();
    this.imp = imp;
    this.width = imp.getWidth();
  	this.height = imp.getHeight();
    this.title = imp.getTitle();

    input(); //getting user input, map pixels between repeating blocks
    //place in setup so plugin could run on a stack

    xd = (width/x0)*x1; //find dimensions of destination image
    yd = (height/y0)*y1;

    result = new ImagePlus();
    result.setTitle(title+" scrambled");
    destStack = new ImageStack(xd, yd);

    return (DOES_ALL+DOES_STACKS);
	}


	public void run(ImageProcessor ip) {
		this.ip = ip;

    values = new float[width][height]; //source image



    destination = new float[xd][yd];

    buildValues(); //getting pixel values from original image

    blockByBlock(); //rearranging pixels, forming array of destination image

    buildStack(); //creating stack
    result.setStack(destStack);

    result.show();

  }

  /* Gets input from users, including dimensions of repeating block in original
  image, dimensions of repating block in destination image, and mappings of pixels
  from repeating block in original to repeating block in destination
  */
  private void input() {
    GenericDialog dims = new GenericDialog("Dimensions of Repeating Units");
    dims.addMessage("x0 and y0 refer to the dimensions of the repeating unit in the\nsource image, while x1 and y1 refer to the dimensions\nof the repeating unit in the destination image");
    dims.addNumericField("x0", 0, 0);
    dims.addNumericField("y0", 0, 0);
    dims.addNumericField("x1", 0, 0);
    dims.addNumericField("y1", 0, 0);
    dims.showDialog();

    x0 = Math.round((float) dims.getNextNumber());
    y0 = Math.round((float) dims.getNextNumber());
    x1 = Math.round((float) dims.getNextNumber());
    y1 = Math.round((float) dims.getNextNumber());

    newDest = new int[x0][y0][];

    for (int y=0; y<y0; y++) {
      GenericDialog nums = new GenericDialog("Row " + y); //get numbers row-by-row
      nums.addMessage("For each specified pixel from the source, enter its (x, y) coordinates in its destination block\n\nPLEASE NOTE: x increases to the right, y increases downwards");

      for (int x=0; x<x0; x++) { //setting up number fields
        nums.addNumericField("(" + x + ", " + y + ")", 0, 0, 3, "x");
        nums.addNumericField("", 0, 0, 3, "y");
      }

      nums.showDialog();

      for (int x=0; x<x0; x++) { //build new_dest array
        int[] dest = new int[2];
        dest[0] = Math.round((float) nums.getNextNumber());
        dest[1] = Math.round((float) nums.getNextNumber());
        newDest[x][y] = dest;
      }
    }
  }

  /*Gets all the pixel values of the original image and stores them in the
  values array*/
  private void buildValues() {
    for (int x=0; x<width; x+=1) {
      for (int y=0; y<height; y+=1) {
        values[x][y] = ip.getPixelValue(x,y);
		  }
    }
  }

  /*Breaks down original image by its repeating blocks, then rearranges the
  pixel values and builds the destination image block by block*/
  private void blockByBlock(){
    for (int block_x = 0; block_x < (width/x0); block_x++) {
      for (int block_y = 0; block_y < (height/y0); block_y++){
        smallBlock(block_x, block_y);
      }
    }
  }

  /*Rearranges values within a small block from original to destination image
  (helper function)*/
  private void smallBlock(int block_x, int block_y) {
    //go through all pixels in this small block
    for (int x=0; x<x0; x++) {
      for (int y=0; y<y0; y++) {
        //calculate actual location in original image
        int original_x = (block_x * x0) + x;
        int original_y = (block_y * y0) + y;

        //get value from values
        float val = values[original_x][original_y];

        //get new destination
        int[] block_pt = newDest[x][y];
        int new_x = (block_x * x1) + block_pt[0];
        int new_y = (block_y * y1) + block_pt[1];

        //build destination array
        destination[new_x][new_y] = val;
      }
    }
  }


  public float[] mode(float[][] arr) {
    int w = arr.length;
    int h = arr[0].length;
    int size = w * h;
    float[] vector = new float[size];

      for (int y = 0; y < h; ++y) {
          // tiny change 1: proper dimensions
          for (int x = 0; x < w; x++) {
              // tiny change 2: actually store the values
              vector[x + y*w]= arr[x][y];
          }

      }
      return vector;
  }

    private void buildStack() {
      destStack.addSlice(title, mode(destination));
    }
}
