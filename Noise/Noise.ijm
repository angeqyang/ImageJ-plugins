//choosing and opening folder of images to generate stack
sel_stack = getDirectory("Select a Directory");
run("Image Sequence...", "open=sel_stack");

//name of stack window = name of folder
folder_name = getTitle();

waitForUser("Select a smaller region now to analyze if you wish.\nPress \"OK\" to continue.");

run("Duplicate...", "duplicate range=1-"+nSlices);
new_name = getTitle();

selectWindow(folder_name);
run("Select None");

//standard deviation image and total noise
selectWindow(new_name);
run("Z Project...", "projection=[Standard Deviation]");
run("Measure");

//column temporal noise
selectWindow(new_name);
run("CTN ");

//row temporal noise
selectWindow(new_name);
run("RTN ");

//temporal noise
selectWindow(new_name);
run("TempN ");

//generate average image for calculations
selectWindow(new_name);
run("Z Project...", "projection=[Average Intensity]");

//column fixed pattern noise
selectWindow("AVG_" + new_name);
run("CFPN ");

//row fixed pattern noise
selectWindow("AVG_" + new_name);
run("RFPN ");

//close average image
selectWindow("AVG_" + new_name);
close();
