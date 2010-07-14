/*
 * Encog(tm) Core v2.5 
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 * 
 * Copyright 2008-2010 by Heaton Research Inc.
 * 
 * Released under the LGPL.
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
 * 
 * Encog and Heaton Research are Trademarks of Heaton Research, Inc.
 * For information on Heaton Research trademarks, visit:
 * 
 * http://www.heatonresearch.com/copyright.html
 */

package org.encog.script.basic.variables.objects;

import java.io.File;

import org.encog.script.basic.variables.BasicObjectVariable;
import org.encog.script.basic.variables.BasicVariable;

/**
 * Object used to read a file.
 *
 */
public class ObjectStdFile extends BasicObjectVariable {

		public ObjectStdFile()
		{
	
		}

		public boolean scan(BasicVariable target)
		{
			return false;
		}
		
		public boolean update()
		{
			return false;
		}
		public boolean execute()
		{
			return false;
		}
		
		public void allocate()
		{
			
		}
		
		void free()
		{
			
		}
		
		public void copy(BasicVariable v)
		{
			
		}
		
		
		public BasicObjectVariable createObject(long num)
		{
			return new ObjectStdFile();
		}
		
		public void createFilter()
		{
			
		}
		
		private File opn;
		private String strFilter; 
		private String strCustomFilter;
		private String strFile;
		private String strFileTitle;
		private String strInitialDir;
		private String strTitle;
		private String strDefExt;

	
}
