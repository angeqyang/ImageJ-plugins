import ij.*;
import ij.text.*;
import ij.plugin.*;
import ij.process.*;
import ij.gui.*;
import java.awt.*;
import ij.measure.*;
import ij.measure.ResultsTable;
import java.lang.Math;

public class TempN_ implements PlugIn {

  public void run(String arg) {
    ResultsTable rt = ResultsTable.getResultsTable();

    int last_row = rt.getCounter()-1;

    double totn = rt.getValue("Mean", last_row);
    double ctn = rt.getValue("Column Temporal Noise", last_row);
    double rtn = rt.getValue("Row Temporal Noise", last_row);
    double tempn = findTempN(totn, ctn, rtn);

    //rt.incrementCounter();
    rt.addValue("Total Noise", totn);
    //rt.addValue("Column Temporal Noise", ctn);
    //rt.addValue("Row Temporal Noise", rtn);
    rt.addValue("Temporal Noise", tempn);
    rt.show("Results");

  }

  private double findTempN(double totn, double ctn, double rtn) {
    double diff = Math.pow(totn, 2) - Math.pow(ctn, 2) - Math.pow(rtn, 2);
    return Math.sqrt(diff);
  }
}
