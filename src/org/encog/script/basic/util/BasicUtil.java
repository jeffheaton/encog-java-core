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

package org.encog.script.basic.util;

import org.encog.script.basic.error.BasicError;
import org.encog.script.basic.error.ErrorNumbers;
import org.encog.script.basic.keys.BasicKey;
import org.encog.script.basic.keys.KeyNames;
import org.encog.script.basic.variables.BasicTypes;
import org.encog.script.basic.variables.BasicVariable;

/**
 * Scripting utilities used by Encog script.
 */
public class BasicUtil {
	
	public static BasicKey findKeyword(String token)
	{
		for( BasicKey key : BasicKey.getKeys())
		{
			if( key.getName().equals(token))
				return key;
		}
		return null;
	}
	
	public static BasicKey  findKeyword(KeyNames token)
	{
		for( BasicKey key : BasicKey.getKeys())
		{
			if( key.getId()==token)
				return key;
		}
		return null;
	}
	public static void doInput(String str,String cap)
	{
		
	}
	
	public static int findKeyword(String str,String key)
	{
		boolean quote=false;
		int wptr;
		int rtn = 0;
		String cmp;

		cmp = key.toUpperCase();

		StringBuilder word = new StringBuilder();
		
		int index = 0;
		
		while(index<str.length())
		{
			char ch = str.charAt(index);
			
			if(ch=='\"')
			{
				quote=!quote;
				index++;
				continue;
			}

			if(quote)
			{
				index++;
				continue;
			}

			if( (ch=='\t') || (ch==' ') || (ch==13) || (ch==10) )
			{
				index++;
				if(word.toString().equals(cmp))
					return rtn;
				continue;
			}
			
			word.append(ch);
			rtn++;
			index++;
		}

		return -1;
	}



	public static String basicToUpper(String l) {
		StringBuilder result = new StringBuilder();
		boolean inQuote = false;
		
		for(int i=0;i<l.length();i++)
		{
			char ch = l.charAt(i);
			if( ch=='\"')
				inQuote = !inQuote;
			
			if( !inQuote )
				ch = Character.toUpperCase(ch);
			
			result.append(ch);
		}
		
		return result.toString();
	}
	
	public static int basicIndexOf(String str, int start, String srch)
	{
		boolean quote=false;
		int searchIndex = 0;
		
		for(int sourceIndex = start; sourceIndex<str.length()-srch.length(); sourceIndex++)
		{
			char ch = str.charAt(sourceIndex);
			char ch2 = srch.charAt(searchIndex);
			
			if( ch==34 )
				quote = !quote;
			
			if( !quote )
			{
				if( Character.toUpperCase(ch)==Character.toUpperCase(ch2))
				{
					searchIndex++;
					if( searchIndex==srch.length())
						return sourceIndex - srch.length();
				}
				else
					searchIndex = 0;
			}
		}
		
		return -1;
	}
}
