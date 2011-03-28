package org.encog.util.logging;

import java.io.IOException;

public class EncogLogging {
	
	public static final int LEVEL_DEBUG = 0;
	public static final int LEVEL_INFO = 1;
	public static final int LEVEL_ERROR = 2;
	public static final int LEVEL_CRITICAL = 3;
	
	public static int currentLevel;
	
	public synchronized int getCurrentLevel() {
		return currentLevel;
	}
	
	public synchronized void setCurrentLevel(int level) {
		this.currentLevel = level;
	}
	
	public static void log(int level, String message) {
		
	}
	
	public static void log(int level, Throwable t) { 
		
	}

	public static void log(Throwable t) {
		log(LEVEL_ERROR,t);
		
	}
	
	
}
