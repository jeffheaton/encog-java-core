package org.encog.util.concurrency.job;

public class JobUnitContext {

	private Object jobUnit;
	private ConcurrentJob owner;
	private int taskNumber;

	public Object getJobUnit() {
		return jobUnit;
	}

	public void setJobUnit(Object jobUnit) {
		this.jobUnit = jobUnit;
	}

	public ConcurrentJob getOwner() {
		return owner;
	}

	public void setOwner(ConcurrentJob owner) {
		this.owner = owner;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public void setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
	}
	
	
	
}
