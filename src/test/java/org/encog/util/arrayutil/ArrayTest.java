/*
 * Encog(tm) Core v3.4 - Java Version
 * http://www.heatonresearch.com/encog/
 * https://github.com/encog/encog-java-core
 
 * Copyright 2008-2017 Heaton Research, Inc.
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
package org.encog.util.arrayutil;

import org.junit.Test;

import static org.junit.Assert.*;

public class ArrayTest {

    @Test
    public void testSwapOnInts() throws Exception {
        int[] input = {1,2,3,4};
        Array.swap(input, 1, 2);
        assertArrayEquals(new int[] {1,3,2,4}, input);
    }

    @Test
    public void testSwapOnBytes() throws Exception {
        byte[] input = {1,2,3,4};
        Array.swap(input, 1, 2);
        assertArrayEquals(new byte[] {1,3,2,4}, input);
    }

    @Test
    public void testSwapOnFloats() throws Exception {
        float[] input = {1,2,3,4};
        Array.swap(input, 1, 2);
        assertArrayEquals(new float[] {1,3,2,4}, input, 0);
    }

    @Test
    public void testSwapOnDoubles() throws Exception {
        double[] input = {1,2,3,4};
        Array.swap(input, 1, 2);
        assertArrayEquals(new double[] {1,3,2,4}, input, 0);
    }

    @Test
    public void testSwapOnObjects() throws Exception {
        String[] input = {"a", "b", "c", "d"};
        Array.swap(input, 1, 2);
        assertArrayEquals(new String[] {"a", "c", "b", "d"}, input);
    }
}
