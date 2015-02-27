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