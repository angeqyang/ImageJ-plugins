import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import java.lang.Math;

public class RTN_ implements PlugInFilter {

	ImagePlus imp;
	ImageProcessor ip;
	int width;
	int height;
  int nslices;
  boolean pre = true;
  float[][] slicerows;



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
      this.slicerows = new float[nslices][height];
      this.pre = false;
      return (DOES_ALL+DOES_STACKS+FINAL_PROCESSING);

    } else { //find SD after finding means of rows
      float sum; //sum of pixel values row
      float avg; //average pixel value of a row
      double devsum; //sum of deviations
      double devavg; //standard deviation
      double[] rows = new double[height]; //x-axis of plot
      double[] sdevs = new double[height]; //y-axis of plot

      for (int i=0; i<height; i+=1) {
        sum = 0; //reset with each row
        avg = 0; //reset with each row
        devsum = 0; //reset with each row
        devavg = 0; //reset with each row
        rows[i] = i; //building x-axis

        for (int s=0; s<nslices; s+=1) { //sum of row means
          sum = sum + slicerows[s][i];
  		  }
        avg = sum/nslices; //mean of one row across all slices

        for (int s=0; s<nslices; s+=1) {//standard deviation
          devsum = devsum + Math.pow((slicerows[s][i]-avg),2); //square diff
  		  }
        devavg = Math.sqrt(devsum/nslices);

        sdevs[i] = devavg; //building y-axis
      }

      Plot plot = new Plot("Average SD by Row", "Rows", "Standard Deviation",
      rows, sdevs);
      plot.show();

      //calculate RTN
      double rtn;
      double sdevssum = 0;
      for (double sd: sdevs) {
        sdevssum = sdevssum + sd;
      }
      rtn = sdevssum/height;

      //display RTN on results table
      ResultsTable rt = Analyzer.getResultsTable();
      //rt.incrementCounter();
			rt.addValue("Row Temporal Noise", rtn);
      rt.show("Results");

      this.pre = false;

      return DONE;
    }

	}

	public void run(ImageProcessor ip) {
		this.ip = ip;

		avg_row(ip);
	}

  void avg_row(ImageProcessor ip) {

    float sum; //sum of pixel values of a row
    float avg; //average pixel value of a row
    float[] sliceavgs = new float[height]; //means across rows of one slice
    int sliceNumber = ip.getSliceNumber()-1; //slice number

    for (int y=0; y<height; y+=1) {
      sum = 0; //reset with each row
      avg = 0; //reset with each row

      for (int x=0; x<width; x+=1) {
        sum = sum + ip.getPixelValue(x,y);
		  }
      avg = sum/width;
      sliceavgs[y] = avg; //building array of means
    }

    this.slicerows[sliceNumber] = sliceavgs; //add this slice's means to array

  }
}
