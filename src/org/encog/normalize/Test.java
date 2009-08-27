package org.encog.normalize;

import java.io.File;

import org.encog.StatusReportable;

public class Test implements StatusReportable {
	
	public void run()
	{
		File inFile = new File("c:\\data\\covtype.data");
		File outFile = new File("c:\\data\\output.csv");
		InputField a;
		InputField b;
		InputField c;
		
		Normalization norm = new Normalization();
		norm.setReport(this);
		norm.setTarget(new NormalizationTargetCSV(outFile));
		norm.addInputField(a = new InputFieldCSV(inFile,0));
		norm.addInputField(b = new InputFieldCSV(inFile,1));
		norm.addInputField(c = new InputFieldCSV(inFile,2));
		norm.process();
		System.out.println( a.getMin() + "-" + a.getMax() );
		System.out.println( b.getMin() + "-" + b.getMax() );
	}
	
	public static void main(String[] args)
	{
		Test test = new Test();
		test.run();
	}

	@Override
	public void report(int total, int current, String message) {
		System.out.println( current + " " + message );
		
	}
}
