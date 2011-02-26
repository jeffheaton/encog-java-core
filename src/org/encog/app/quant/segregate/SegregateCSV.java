package org.encog.app.quant.segregate;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.encog.app.quant.QuantError;
import org.encog.app.quant.basic.BasicFile;
import org.encog.app.quant.basic.LoadedRow;
import org.encog.util.csv.CSVFormat;
import org.encog.util.csv.ReadCSV;

/**
 * This class is used to segregate a CSV file into several sub-files.  This can
 * be used to create training and evaluation datasets. 
 */
public class SegregateCSV extends BasicFile {
	/**
	 * @return The segregation targets.
	 */
	public List<SegregateTargetPercent> getTargets() {
		return this.targets;
	}

	/**
	 * The segregation targets.
	 */
	private List<SegregateTargetPercent> targets = new ArrayList<SegregateTargetPercent>();

	/**
	 * Validate that the data is correct.
	 */
	private void validate()
    {
        validateAnalyzed();

        if (targets.size() < 1)
        {
            throw new QuantError("There are no segregation targets.");
        }

        if (targets.size() < 2)
        {
            throw new QuantError("There must be at least two segregation targets.");
        }

        int total = 0;
        for (SegregateTargetPercent p : this.targets)
        {
            total += p.getPercent();
        }

        if (total != 100)
        {
            throw new QuantError("Target percents must equal 100.");
        }
    }

	/**
	 * Balance the targets.
	 */
	private void balanceTargets()
    {
        SegregateTargetPercent smallestItem = null;
        int numberAssigned = 0;

        // first try to assign as many as can be assigned
        for (SegregateTargetPercent p : this.targets)
        {
                SegregateTargetPercent stp = (SegregateTargetPercent)p;

                // assign a number of records to this 
                double percent = stp.getPercent() / 100.0;
                int c = (int)(this.getRecordCount() * percent);
                stp.setNumberRemaining( c );

                // track the smallest group
                if (smallestItem == null || smallestItem.getPercent() > stp.getPercent())
                {
                    smallestItem = stp;
                }

                numberAssigned += c;
            
        }

        // see if there are any remaining items
        int remain = this.getRecordCount() - numberAssigned;

        // if there are extras, just add them to the largest group
        if (remain > 0)
        {
            smallestItem.setNumberRemaining( smallestItem.getNumberRemaining()+ remain);
        }
    }
	
	/**
	 * Analyze the input file.
	 * @param inputFile The input file.
	 * @param headers The headers.
	 * @param format The format of the input file.
	 */
	public void analyze(String inputFile, boolean headers, CSVFormat format) {
		this.setInputFilename( inputFile );
		this.setExpectInputHeaders( headers );
		this.setInputFormat( format );

		this.setAnalyzed( true);

		performBasicCounts();

		balanceTargets();
	}

	/**
	 * Process the input file and segregate into the output files.
	 */
	public void process()
    {
        validate();

        ReadCSV csv = new ReadCSV(this.getInputFilename(), this.isExpectInputHeaders(), this.getInputFormat());
        resetStatus();
        for (SegregateTargetPercent target : this.targets)
        {
                PrintWriter tw = this.prepareOutputFile(target.getFilename());

                while (target.getNumberRemaining() > 0 && csv.next() && !this.shouldStop())
                {
                    updateStatus(false);
                    LoadedRow row = new LoadedRow(csv);
                    writeRow(tw, row);
                    target.setNumberRemaining(target.getNumberRemaining()-1);
                }

                tw.close();                
        }
        reportDone(false);
        csv.close();
    }
}
