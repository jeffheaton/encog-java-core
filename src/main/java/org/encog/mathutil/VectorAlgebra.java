/*
 * Encog(tm) Core v3.1 - Java Version
 * http://www.heatonresearch.com/encog/
 * http://code.google.com/p/encog-java/
 
 * Copyright 2008-2012 Heaton Research, Inc.
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
package org.encog.mathutil;

import java.util.Random;

/**
 * Basic vector algebra operators.
 * Vectors are represented as arrays of doubles.
 * 
 * This class was created to support the calculations 
 * in the PSO algorithm.
 * 
 * This class is thread safe.
 * 
 * Contributed by:
 * Geoffroy Noel
 * https://github.com/goffer-looney 
 * 
 * @author Geoffroy Noel
 */
public class VectorAlgebra {

    static Random rand = new Random(); 

    /**
     * v1 = v1 + v2
     * 
     * @param v1    an array of doubles
     * @param v2    an array of doubles
     */
    public void add(double[] v1, double[] v2) {
        for (int i = 0; i < v1.length; i++) {
            v1[i] += v2[i];
        }
    }

    /**
     * v1 = v1 - v2
     * 
     * @param v1    an array of doubles
     * @param v2    an array of doubles
     */
    public void sub(double[] v1, double[] v2) {
        for (int i = 0; i < v1.length; i++) {
            v1[i] -= v2[i];
        }
    }

    /**
     * v = -v
     * 
     * @param v     an array of doubles
     */
    public void neg(double[] v) {
        for (int i = 0; i < v.length; i++) {
            v[i] = -v[i];
        }
    }

    /**
     * v = k * U(0,1) * v
     * 
     * The components of the vector are multiplied 
     * by k and a random number.
     * A new random number is generated for each 
     * component.    
     * Thread-safety depends on Random.nextDouble()
     * 
     * @param v     an array of doubles.
     * @param k     a scalar.
     */
    public void mulRand(double[] v, double k) {
        for (int i = 0; i < v.length; i++) {
            v[i] *= k * rand.nextDouble();
        }       
    }

    /**
     * v = k * v
     * 
     * The components of the vector are multiplied 
     * by k.
     * 
     * @param v     an array of doubles.
     * @param k     a scalar.
     */
    public void mul(double[] v, double k) {
        for (int i = 0; i < v.length; i++) {
            v[i] *= k;
        }       
    }

    /**
     * dst = src
     * Copy a vector.
     * 
     * @param dst   an array of doubles
     * @param src   an array of doubles
     */
    public void copy(double[] dst, double[] src) {
        System.arraycopy(src, 0, dst, 0, src.length);
    }

    /**
     * v = U(0, 0.1)
     * 
     * @param v     an array of doubles
     */
    public void randomise(double[] v) {
        randomise(v, 0.1);
    }

    /**
     * v = U(-1, 1) * maxValue
     * 
     * Randomise each component of a vector to 
     * [-maxValue, maxValue].
     * thread-safety depends on Random.nextDouble().
     * 
     * @param v     an array of doubles
     */
    public void randomise(double[] v, double maxValue) {
        for (int i = 0; i < v.length; i++) {
            v[i] = (2 * rand.nextDouble() - 1) * maxValue;
        }
    }

    /**
     * For each components, reset their value to maxValue if 
     * their absolute value exceeds it.
     * 
     * @param v         an array of doubles
     * @param maxValue  if -1 this function does nothing
     */
    public void clampComponents(double[] v, double maxValue) {
        if (maxValue != -1) {
            for (int i = 0; i < v.length; i++) {
                if (v[i] > maxValue) v[i] = maxValue;
                if (v[i] < -maxValue) v[i] = -maxValue;
            }
        }
    }

}
