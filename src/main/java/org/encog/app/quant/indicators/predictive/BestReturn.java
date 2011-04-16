package org.encog.app.quant.indicators.predictive;

import java.util.Map;

import org.encog.app.csv.basic.BaseCachedColumn;
import org.encog.app.csv.basic.FileData;
import org.encog.app.quant.indicators.Indicator;

/**
 * Get the best return.
 */
public class BestReturn extends Indicator {

    /**
     * The name of this indicator.
     */
    public static final String NAME = "PredictBestReturn";

    /**
     * The number of periods this indicator is for.
     */
    private int periods;

    /**
     * @return The number of periods.
     */
    public int getPeriods()
    {
            return periods;
    }

    /**
     * Construct the object.
     * @param periods The number of periods.
     * @param output True, if this indicator is to be predicted.
     */
    public BestReturn(int periods, boolean output)
    {
        super(NAME,false,output);
        this.periods = periods;
        setOutput( output );
    }

    /**
     * Calculate the indicator.
     * @param data The data available to the indicator.
     * @param length The length of the data to calculate.
     */
    public void calculate(Map<String, BaseCachedColumn> data, int length)
    {
        double[] close = data.get(FileData.CLOSE).getData();
        double[] output = this.getData();

        int stop = length - periods;
        for (int i = 0; i < stop; i++)
        {
            double bestReturn = Double.MIN_VALUE;
            double baseClose = close[i];
            for (int j = 1; j <= periods; j++)
            {
                double newClose = close[i + j];
                double rtn = (newClose - baseClose) / baseClose;
                bestReturn = Math.max(rtn, bestReturn);
            }
            output[i] = bestReturn;
        }

        for (int i = length - periods; i < length; i++)
        {
            output[i] = 0;
        }

        this.setBeginningIndex( 0 );
        this.setEndingIndex( length - periods - 1);
    }
	
}
