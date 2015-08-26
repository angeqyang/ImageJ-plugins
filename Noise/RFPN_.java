import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import java.lang.Math;

public class RFPN_ implements PlugInFilter {

	ImagePlus imp;
	ImageProcessor ip;
	int width;
	int height;



	public int setup(String arg, ImagePlus imp) {
		//IJ.register(Average_Oversampled.class);

  	if (IJ.versionLessThan("1.32c"))
			return DONE;

		imp.unlock();
    this.imp = imp;
    this.width = imp.getWidth();
  	this.height = imp.getHeight();
    return (DOES_ALL);
	}


	public void run(ImageProcessor ip) {
		this.ip = ip;
		double[] row_avgs = new double[height];

		row_avgs = avg_row(ip); //find 1D array of rows

		//calculate mean of the array
		double sum = 0; //sum of values of the array
		double avg; //average of values of the array

		for (double i: row_avgs) {
			sum = sum + i;
		}
		avg = sum/height; //mean of the array

		//find deviations of array, calculate SD
		double devsum=0; //sum of deviations
		double rfpn; //row FPN same as standard deviation
    double dev;
		double[] rows = new double[height]; //x-axis of plot

    for (int i=0; i<height; i++) {
			dev = Math.pow((row_avgs[i]-avg), 2);
      devsum = devsum + dev;
      rows[i] = i; //building x-axis
		}
		rfpn = devsum/height; //standard deviation

		Plot plot = new Plot("Averages by Row", "Rows", "Means",
		rows, row_avgs);
		plot.show();

		//display RFPN on results table
		ResultsTable rt = ResultsTable.getResultsTable();
		//rt.incrementCounter();
		rt.addValue("RFPN", rfpn);
		rt.show("Results");

	}

  private double[] avg_row(ImageProcessor ip) {

    float sum; //sum of pixel values column
    float avg; //average pixel value of a column
		double[] row_avgs = new double[height]; //array of averages by row

    for (int y=0; y<height; y+=1) {
      sum = 0; //reset with each row
      avg = 0; //reset with each row

      for (int x=0; x<width; x+=1) {
        sum = sum + ip.getPixelValue(x,y);
		  }
      avg = sum/width;
      row_avgs[y] = avg; //building array of means
    }

		return row_avgs;

  }
}
