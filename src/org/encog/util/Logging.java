package org.encog.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Logging {
	
	
	public static Logger getRootLogger()
	{
		Logger logger = LogManager.getLogManager().getLogger("");
		return logger;
	}
	
	public static Handler getConsoleHandler()
	{
		Handler[] handlers = Logging.getRootLogger().getHandlers();
		for(int i=0;i<handlers.length;i++)
		{
			if( handlers[i] instanceof ConsoleHandler )
				return handlers[i];
		}
		return null;
	}
	
	public static void setConsoleLevel(Level level)
	{
		getConsoleHandler().setLevel(level);
	}
	
	public static void stopConsoleLogging()
	{
		setConsoleLevel(Level.OFF);
	}
}
