package helpers;

public class PerfResults {

	public static enum AveragingMethod { MICRO, MACRO };

	private int tp[], fp[], tn[], fn[];
	private long length;
	
	public PerfResults(int tp[], int fp[], int tn[], int fn[], long size) {
		this.tp = tp;
		this.fp = fp;
		this.tn = tn;
		this.fn = fn;
		this.setLength(size);
	}
	
	public double FScore(double beta, AveragingMethod avg) {
		return ((1 + beta * beta) * getPrecision(avg) * getRecall(avg)) / ( beta * beta * getPrecision(avg) + getRecall(avg));
	}

	public double getPrecision(AveragingMethod avg) {
		if (avg == AveragingMethod.MICRO) {
			double sum = 0;
			for (int i = 0; i < length; i++) {
				if ((tp[i] + fp[i])!=0)
					sum += (double) tp[i] / (double) (tp[i] + fp[i]);
			}
			return sum / length;
		} else {
			double stp = 0,sfp = 0;
			for (int i = 0; i < length; i++) {
				stp += tp[i];
				sfp += fp[i];
			}
			return stp / (stp + sfp);
		}
	}

	public double getRecall(AveragingMethod avg) {
		if (avg == AveragingMethod.MICRO) {
			double sum = 0;
			for (int i = 0; i < length; i++) {
				if ((tp[i] + fn[i]) !=0)
					sum += (double) tp[i] / (double) (tp[i] + fn[i]);
			}
			return sum / length;
		} else {
			double stp = 0,sfn = 0;
			for (int i = 0; i < length; i++) {
				stp += tp[i];
				sfn += fn[i];
			}
			return stp / (stp + sfn);
		}
	}
	
	public double getAccuracy(AveragingMethod avg) {
		if (avg == AveragingMethod.MICRO) {
			double sum = 0;
			for (int i = 0; i < length; i++) {
				if ((tp[i] + tn[i] + fp[i] + fn[i]) != 0) {
					double denom = (tp[i] + tn[i] + fp[i] + fn[i]);
					double nomin = (tp[i] + tn[i]);
					double local = nomin / denom;
					sum += local;
				}
			}
			return sum / length;
		} else {
			double stp = 0,sfp = 0, stn = 0, sfn = 0;
			for (int i = 0; i < length; i++) {
				stp += tp[i];
				sfp += fp[i];
				stn += tn[i];
				sfn += fn[i];
			}
			return (stp + stn) / (stp + stn + sfp + sfn);
		}
	}

	public long getLength() {
		return length;
	}

	public void setLength(long size) {
		this.length = size;
	}

}
