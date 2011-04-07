package org.encog.app.analyst;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.encog.app.analyst.report.AnalystReport;
import org.encog.app.analyst.wizard.AnalystWizard;
import org.encog.util.TempDir;
import org.encog.util.file.FileUtil;

public class TestAnalystWizard extends TestCase {
	public final TempDir TEMP_DIR = new TempDir();
	
	public void testIrisWizard() throws Exception {
		File rawFile = TEMP_DIR.createFile("iris_raw.csv");
		FileUtil.copyResource("org/encog/data/iris.csv", rawFile);
		
		File analystFile = TEMP_DIR.createFile("iris.ega");
		
		EncogAnalyst encog = new EncogAnalyst();
		encog.setMaxIteration(1);
		//encog.addAnalystListener(new ConsoleAnalystListener());
		AnalystWizard wiz = new AnalystWizard(encog);
		
		wiz.wizard(rawFile, false, AnalystFileFormat.DECPNT_COMMA);

		/*encog.executeTask("task-full");
		
		encog.save(analystFile);
		
		AnalystReport report = new AnalystReport(encog);
		report.produceReport(TEMP_DIR.createFile("report.html"));*/
		
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		TEMP_DIR.dispose();
	}
}
