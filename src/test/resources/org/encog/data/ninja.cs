#region Using declarations
using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Drawing;
using System.Drawing.Drawing2D;
using System.Xml.Serialization;
using NinjaTrader.Cbi;
using NinjaTrader.Data;
using NinjaTrader.Gui.Chart;
using System.IO;
using System.Text;
#endregion

// This namespace holds all indicators and is required. Do not change it.
namespace NinjaTrader.Indicator
{
    /// <summary>
    /// Enter the description of your new custom indicator here
    /// </summary>
    [Description("Enter the description of your new custom indicator here")]
    public class EncogExample : Indicator
    {
        #region Variables
        // Wizard generated variables
            private bool export = false; // Default setting for Export
        // User defined variables (add any user defined variables below)
        #endregion

~~MAIN-BLOCK
		private StreamWriter sw;
		
		
        /// <summary>
        /// This method is used to configure the indicator and is called once before any bar data is loaded.
        /// </summary>
        protected override void Initialize()
        {
            Add(new Plot(Color.FromKnownColor(KnownColor.Orange), PlotStyle.Line, "Output"));
            Overlay				= false;
        }
		
		protected double[] ObtainData() {
~~OBTAIN
		}
		
		protected void OpenFile()
		{
			File.Delete(EXPORT_FILENAME);
			sw = File.CreateText(EXPORT_FILENAME);
			StringBuilder line = new StringBuilder();
					
			line.Append("time");
					
			foreach( string n in ENCOG_COLS )
			{
				line.Append(",");
				line.Append(n);
			}
					
			sw.WriteLine(line.ToString());
		}
		
		protected void WriteLine(double[] data)
		{
			StringBuilder line = new StringBuilder();
					
			line.Append(ToDay(Time[0]).ToString() + ToTime(Time[0]).ToString());
					
			foreach( double d in data )
			{
				line.Append(",");	
				line.Append(d);
			}
					
			sw.WriteLine(line.ToString());
		}
				
		public virtual void ActivationTANH(double[] x, int start, int size)
        {
            for (int i = start; i < start + size; i++)
            {
                x[i] = 2.0 / (1.0 + Math.Exp(-2.0 * x[i])) - 1.0;
            }
        }
		
		public virtual void ActivationSigmoid(double[] x, int start, int size)
        {
            for (int i = start; i < start + size; i++)
            {
                x[i] = 1.0d/(1.0d + Math.Exp(-1*x[i]));
            }
        }
		
		public void ActivationElliottSymmetric(double[] x, int start, int size)
        {
            for (int i = start; i < start + size; i++)
            {
                double s = _p[0];
                x[i] = (x[i] * s) / (1 + Math.Abs(x[i] * s));
            }
        }
		
		public void ActivationElliott(double[] x, int start, int size)
        {
            for (int i = start; i < start + size; i++)
            {
                double s = _p[0];
                x[i] = ((x[i]*s)/2)/(1 + Math.Abs(x[i]*s)) + 0.5;
            }
        }
		
		protected internal void ComputeLayer(int currentLayer)
        {
            int inputIndex = _layerIndex[currentLayer];
            int outputIndex = _layerIndex[currentLayer - 1];
            int inputSize = _layerCounts[currentLayer];
            int outputSize = _layerFeedCounts[currentLayer - 1];

            int index = _weightIndex[currentLayer - 1];

            int limitX = outputIndex + outputSize;
            int limitY = inputIndex + inputSize;

            // weight values
            for (int x = outputIndex; x < limitX; x++)
            {
                double sum = 0;
                for (int y = inputIndex; y < limitY; y++)
                {
                    sum += _weights[index++]*_layerOutput[y];
                }
                _layerOutput[x] = sum;
                _layerSums[x] = sum;
            }

			switch(_activation[currentLayer - 1] )
			{
				case 0: // linear
					break;
				case 1:
					ActivationTANH(_layerOutput, outputIndex, outputSize);
					break;
				case 2:
					ActivationSigmoid(_layerOutput, outputIndex, outputSize);
					break;
      			case 3:
					ActivationElliottSymmetric(_layerOutput, outputIndex, outputSize);
					break;
				case 4:
					ActivationElliott(_layerOutput, outputIndex, outputSize);
        			break;
			}
			
            // update context values
            int offset = _contextTargetOffset[currentLayer];

            for (int x = 0; x < _contextTargetSize[currentLayer]; x++)
            {
                _layerOutput[offset + x] = _layerOutput[outputIndex + x];
            }
        }

		
		protected virtual void Compute(double[] input, double[] output)
        {
            int sourceIndex = _layerOutput.Length
                              - _layerCounts[_layerCounts.Length - 1];

			Array.Copy(input,0,_layerOutput,sourceIndex,_inputCount);
			
			for(int i = _layerIndex.Length - 1; i > 0; i--)
			{
				ComputeLayer(i);
			}

			// update context values
			int offset = _contextTargetOffset[0];

			for(int x = 0; x < _contextTargetSize[0]; x++)
			{
				_layerOutput[offset + x] = _layerOutput[x];
			}

			Array.Copy(_layerOutput, 0, output, 0, _outputCount);
        }
        
        protected double Norm(double x,double normalizedHigh, double normalizedLow, double dataHigh, double dataLow)
		{
			return ((x - dataLow) 
				/ (dataHigh - dataLow))
				* (normalizedHigh - normalizedLow) + normalizedLow;
		}
		
		protected double DeNorm(double x,double normalizedHigh, double normalizedLow, double dataHigh, double dataLow) {
			return ((dataLow - dataHigh) * x - normalizedHigh
				* dataLow + dataHigh * normalizedLow)
				/ (normalizedLow - normalizedHigh);
		}
		
        /// <summary>
        /// Called on each bar update event (incoming tick)
        /// </summary>
        protected override void OnBarUpdate()
        {
~~CALC        
			if( export )
			{
				try
				{
					if (CurrentBar == 0)
					{
						OpenFile();
					}
					
					double[] data = ObtainData();
					WriteLine(data);
				}
			
				catch (Exception e)
				{
					Log("Encog Indicator Error: " + e.Message, NinjaTrader.Cbi.LogLevel.Error);				
				}
			}
        }
		
		
		// Necessary to call in order to clean up resources used by the StreamWriter object
		protected override void OnTermination() 
		{
			// Disposes resources used by the StreamWriter
			if (sw != null)
			{
				sw.Dispose();
				sw = null;
			}
		}
		
        #region Properties
        [Browsable(false)]	// this line prevents the data series from being displayed in the indicator properties dialog, do not remove
        [XmlIgnore()]		// this line ensures that the indicator can be saved/recovered as part of a chart template, do not remove
        public DataSeries Output
        {
            get { return Values[0]; }
        }

        [Description("Export data")]
        [GridCategory("Parameters")]
        public bool Export
        {
            get { return export; }
            set { export = value; }
        }
        #endregion
    }
}

#region NinjaScript generated code. Neither change nor remove.
// This namespace holds all indicators and is required. Do not change it.
namespace NinjaTrader.Indicator
{
    public partial class Indicator : IndicatorBase
    {
        private EncogExample[] cacheEncogExample = null;

        private static EncogExample checkEncogExample = new EncogExample();

        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        public EncogExample EncogExample(bool export)
        {
            return EncogExample(Input, export);
        }

        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        public EncogExample EncogExample(Data.IDataSeries input, bool export)
        {
            if (cacheEncogExample != null)
                for (int idx = 0; idx < cacheEncogExample.Length; idx++)
                    if (cacheEncogExample[idx].Export == export && cacheEncogExample[idx].EqualsInput(input))
                        return cacheEncogExample[idx];

            lock (checkEncogExample)
            {
                checkEncogExample.Export = export;
                export = checkEncogExample.Export;

                if (cacheEncogExample != null)
                    for (int idx = 0; idx < cacheEncogExample.Length; idx++)
                        if (cacheEncogExample[idx].Export == export && cacheEncogExample[idx].EqualsInput(input))
                            return cacheEncogExample[idx];

                EncogExample indicator = new EncogExample();
                indicator.BarsRequired = BarsRequired;
                indicator.CalculateOnBarClose = CalculateOnBarClose;
#if NT7
                indicator.ForceMaximumBarsLookBack256 = ForceMaximumBarsLookBack256;
                indicator.MaximumBarsLookBack = MaximumBarsLookBack;
#endif
                indicator.Input = input;
                indicator.Export = export;
                Indicators.Add(indicator);
                indicator.SetUp();

                EncogExample[] tmp = new EncogExample[cacheEncogExample == null ? 1 : cacheEncogExample.Length + 1];
                if (cacheEncogExample != null)
                    cacheEncogExample.CopyTo(tmp, 0);
                tmp[tmp.Length - 1] = indicator;
                cacheEncogExample = tmp;
                return indicator;
            }
        }
    }
}

// This namespace holds all market analyzer column definitions and is required. Do not change it.
namespace NinjaTrader.MarketAnalyzer
{
    public partial class Column : ColumnBase
    {
        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        [Gui.Design.WizardCondition("Indicator")]
        public Indicator.EncogExample EncogExample(bool export)
        {
            return _indicator.EncogExample(Input, export);
        }

        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        public Indicator.EncogExample EncogExample(Data.IDataSeries input, bool export)
        {
            return _indicator.EncogExample(input, export);
        }
    }
}

// This namespace holds all strategies and is required. Do not change it.
namespace NinjaTrader.Strategy
{
    public partial class Strategy : StrategyBase
    {
        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        [Gui.Design.WizardCondition("Indicator")]
        public Indicator.EncogExample EncogExample(bool export)
        {
            return _indicator.EncogExample(Input, export);
        }

        /// <summary>
        /// Enter the description of your new custom indicator here
        /// </summary>
        /// <returns></returns>
        public Indicator.EncogExample EncogExample(Data.IDataSeries input, bool export)
        {
            if (InInitialize && input == null)
                throw new ArgumentException("You only can access an indicator with the default input/bar series from within the 'Initialize()' method");

            return _indicator.EncogExample(input, export);
        }
    }
}
#endregion
