package org.encog.ml.prg.util;

import org.encog.ml.prg.EncogProgram;
import org.junit.Test;

public class TestMapProgram {
	
	@Test
	public void testBasic() {
		EncogProgram prg = new EncogProgram("(((x+(-(-2.8381965325496505)/(-3.21076606827289^8)))+x)+(x+x))");
		MapProgram map = new MapProgram(prg);
		
	}
}
