package org.encog.ml.factory.method;

import org.encog.ml.MLMethod;

public class FeedforwardFactory {

	public MLMethod create(String a, int input, int output) {
		MLMethod result = null;
		boolean done = false;
		int base = 0;
		
		do {
			String part;
			int index = a.indexOf("->",base );
			if( index!=-1 ) {
				part = a.substring(base,index);
				base = index+2;
			} else {
				part = a.substring(base);
				done = true;
			}
			
			System.out.println(part);
			
		} while(!done);
		
		return null;
	}

}
