/*
 * Encog(tm) Core v3.3 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2014 Heaton Research, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *   
 * For more information on Heaton Research copyrights, licenses 
 * and trademarks visit:
 * http://www.heatonresearch.com/copyright
 */
package org.encog.util.text;

import java.io.UnsupportedEncodingException;

import org.encog.Encog;
import org.encog.EncogError;

public class DoubleString {
	
	public final static double FRAC_SHIFT = 100000000.0;
	public final static int[] MULT = { 0x10000, 0x100, 0x01 };
	
	public static String convertDoubleToString(double[] target, int index) {
		boolean wholeMode = true;
		int currentIndex = index;
		int multIndex = 0;
		int len = (int)target[currentIndex];
		int encodedLen = (int)((target[currentIndex]-len) * FRAC_SHIFT);
		byte[] encoded = new byte[encodedLen];
		
		currentIndex++;
		int assembled = (int)target[currentIndex];
		
		for(int i=0;i<encoded.length;i++) {
			encoded[i] = (byte)(assembled/MULT[multIndex]);
			assembled-=target[currentIndex]*MULT[multIndex];
			multIndex++;
			if(multIndex==MULT.length) {
				multIndex=0;
				if( wholeMode ) {
					double dec = (encoded[currentIndex] - ((int)encoded[currentIndex]));
					assembled = (int)(dec * FRAC_SHIFT);
					wholeMode = false;
				} else {
					wholeMode = true;
					assembled = (int)target[currentIndex++];
				}
			}
		}
		
		try {
			return new String(encoded, Encog.DEFAULT_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}
	
	public static int convertStringToDouble(String str, double[] target, int targetIndex) {
		try {
			
			int multIndex = 0;
			int assemble = 0;
			boolean wholeMode = true;
			
			byte[] encoded = str.getBytes(Encog.DEFAULT_ENCODING);
			int outputIndex = targetIndex;
			
			int len = (encoded.length/6);
			if( (encoded.length%6)>0 ) {
				len++;
			}
			
			target[outputIndex++] = (encoded.length/FRAC_SHIFT) + len;
			
			for(int i=0;i<encoded.length;i++) {
				assemble+=encoded[i]*MULT[multIndex++];
				if( multIndex>=3 ) {
					if( wholeMode ) {
						target[outputIndex] = assemble;
					} else {
						target[outputIndex++] += assemble/FRAC_SHIFT;
					}
					multIndex = 0;
					wholeMode=!wholeMode;
					assemble = 0;
				}
			}
			
			if( wholeMode ) {
				target[outputIndex] = assemble;
			} else {
				target[outputIndex++] += assemble/FRAC_SHIFT;
			}
			
			return (outputIndex - targetIndex)+1;
		} catch (UnsupportedEncodingException e) {
			throw new EncogError(e);
		}
	}
	
	public static void main(String[] args) {
		String str = "This is a test";
		double[] target = new double[1024];
		
		int len = DoubleString.convertStringToDouble(str, target, 0);
		for(int i=0;i<len;i++) {
			System.out.println(target[i]);
		}
		
		System.out.println(DoubleString.convertDoubleToString(target, 0));
	}
}
