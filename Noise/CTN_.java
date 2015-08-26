import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import java.lang.Math;

public class CTN_ implements PlugInFilter {

	ImagePlus imp;
	ImageProcessor ip;
	int width;
	int height;
  int nslices;
  boolean pre = true;
  float[][] slicecols;



	public int setup(String arg, ImagePlus imp) {
		//IJ.register(Average_Oversampled.class);

  	if (IJ.versionLessThan("1.32c"))
			return DONE;

    if (this.pre) { //before finding means

      imp.unlock();
      this.imp = imp;
      this.nslices = imp.getNSlices();
      this.width = imp.getWidth();
  		this.height = imp.getHeight();
      this.slicecols = new float[nslices][width];
      this.pre = false;
      return (DOES_ALL+DOES_STACKS+FINAL_PROCESSING);

    } else { //find SD after finding means of columns
      float sum; //sum of pixel values column
      float avg; //average pixel value of a column
      double devsum; //sum of deviations
      double devavg; //standard deviation
      double[] columns = new double[width]; //x-axis of plot
      double[] sdevs = new double[width]; //y-axis of plot

      for (int i=0; i<width; i+=1) {
        sum = 0; //reset with each column
        avg = 0; //reset with each column
        devsum = 0; //reset with each column
        devavg = 0; //reset with each column
        columns[i] = i; //building x-axis

        for (int s=0; s<nslices; s+=1) { //sum of column means
          sum = sum + slicecols[s][i];
  		  }
        avg = sum/nslices; //mean of one column across all slices

        for (int s=0; s<nslices; s+=1) {//standard deviation
          devsum = devsum + Math.pow((slicecols[s][i]-avg),2); //square diff
  		  }
        devavg = Math.sqrt(devsum/nslices);

        sdevs[i] = devavg; //building y-axis
      }

      Plot plot = new Plot("Average SD by Column", "Columns", "Standard Deviation",
      columns, sdevs);
      plot.show();

      //calculate CTN
      double ctn;
      double sdevssum = 0;
      for (double sd: sdevs) {
        sdevssum = sdevssum + sd;
      }
      ctn = sdevssum/width;

      //display CTN on results table
      ResultsTable rt = ResultsTable.getResultsTable();
      //rt.incrementCounter();
      rt.addValue("Column Temporal Noise", ctn);
      rt.show("Results");

      this.pre = false;

      return DONE;
    }

	}

	public void run(ImageProcessor ip) {
		this.ip = ip;

		avg_col(ip);
	}

  void avg_col(ImageProcessor ip) {

    float sum; //sum of pixel values column
    float avg; //average pixel value of a column
    float[] sliceavgs = new float[width]; //means across columns of one slice
    int sliceNumber = ip.getSliceNumber()-1; //slice number

    for (int x=0; x<width; x+=1) {
      sum = 0; //reset with each column
      avg = 0; //reset with each column

      for (int y=0; y<height; y+=1) {
        sum = sum + ip.getPixelValue(x,y);
		  }
      avg = sum/height;
      sliceavgs[x] = avg; //building array of means
    }

    this.slicecols[sliceNumber] = sliceavgs; //add this slice's means to array

  }
}
