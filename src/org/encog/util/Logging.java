/*
 * Encog Artificial Intelligence Framework v2.x
 * Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2009, Heaton Research Inc., and individual contributors.
 * See the copyright.txt in the distribution for a full listing of 
 * individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.encog.util;

import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.slf4j.LoggerFactory;

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

	public static void allConsoleLogging() {
		setConsoleLevel(Level.FINEST);
		getRootLogger().setLevel(Level.FINEST);
		
	}
}
