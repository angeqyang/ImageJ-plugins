import ij.*;
import ij.plugin.filter.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import java.lang.Math;

public class CFPN_ implements PlugInFilter {

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
		double[] col_avgs = new double[width];

		col_avgs = avg_col(ip); //find 1D array of columns

		//calculate mean of the array
		double sum = 0; //sum of pixel values column
		double avg; //average pixel value of a column

		for (double i: col_avgs) {
			sum = sum + i;
		}
		avg = sum/width; //mean of the array

		//find deviations of array, calculate SD
		double devsum=0; //sum of deviations
		double cfpn; //column FPN same as standard deviation
    double dev;
		double[] columns = new double[width]; //x-axis of plot

    for (int i=0; i<width; i++) {
			dev = Math.pow((col_avgs[i]-avg), 2);
      devsum = devsum + dev;
      columns[i] = i; //building x-axis
		}
		cfpn = devsum/width; //standard deviation

		Plot plot = new Plot("Averages by Column", "Columns", "Means",
		columns, col_avgs);
		plot.show();

		//display CFPN on results table
		ResultsTable rt = ResultsTable.getResultsTable();
		//rt.incrementCounter();
		rt.addValue("CFPN", cfpn);
		rt.show("Results");

	}

  private double[] avg_col(ImageProcessor ip) {

    float sum; //sum of pixel values column
    float avg; //average pixel value of a column
		double[] col_avgs = new double[width]; //array of averages by column

    for (int x=0; x<width; x+=1) {
      sum = 0; //reset with each column
      avg = 0; //reset with each column

      for (int y=0; y<height; y+=1) {
        sum = sum + ip.getPixelValue(x,y);
		  }
      avg = sum/height;
      col_avgs[x] = avg; //building array of means
    }

		return col_avgs;

  }
}
