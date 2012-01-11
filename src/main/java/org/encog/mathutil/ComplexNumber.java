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

/**
 * A complex number class.  This class is based on source code by
 * 
 * Andrew G. Bennett, Department of Mathematics
 * Kansas State University
 * 
 * The original version can be found here:
 * 
 * http://www.math.ksu.edu/~bennett/jomacg/c.html
 *
 */
public class ComplexNumber {

	/**
	 * The real part.
	 */
	private final double x;
	
	/**
	 * The imaginary part.
	 */
	private final double y;

	/**
	    Constructs the complex number z = u + i*v
	    @param u Real part
	    @param v Imaginary part
	*/
	public ComplexNumber(double u, double v) {
		x = u;
		y = v;
	}

	/**
	 * Create a complex number from another complex number.
	 * @param other The other complex number. 
	 */
	public ComplexNumber(ComplexNumber other) {
		this.x = other.getReal();
		this.y = other.getImaginary();
	}

	/**
	    Real part of this Complex number 
	    (the x-coordinate in rectangular coordinates).
	    @return Re[z] where z is this Complex number.
	*/
	public double getReal() {
		return x;
	}

	/**
	    Imaginary part of this Complex number 
	    (the y-coordinate in rectangular coordinates).
	    @return Im[z] where z is this Complex number.
	*/
	public double getImaginary() {
		return y;
	}

	/**
	    Modulus of this Complex number
	    (the distance from the origin in polar coordinates).
	    @return |z| where z is this Complex number.
	*/
	public double mod() {
		if (x != 0 || y != 0) {
			return Math.sqrt(x * x + y * y);
		} else {
			return 0d;
		}
	}

	/**
	    Argument of this Complex number 
	    (the angle in radians with the x-axis in polar coordinates).
	    @return arg(z) where z is this Complex number.
	*/
	public double arg() {
		return Math.atan2(y, x);
	}

	/**
	    Complex conjugate of this Complex number
	    (the conjugate of x+i*y is x-i*y).
	    @return z-bar where z is this Complex number.
	*/
	public ComplexNumber conj() {
		return new ComplexNumber(x, -y);
	}

	/**
	    Addition of Complex numbers (doesn't change this Complex number).
	    <br>(x+i*y) + (s+i*t) = (x+s)+i*(y+t).
	    @param w is the number to add.
	    @return z+w where z is this Complex number.
	*/
	public ComplexNumber plus(ComplexNumber w) {
		return new ComplexNumber(x + w.getReal(), y + w.getImaginary());
	}

	/**
	    Subtraction of Complex numbers (doesn't change this Complex number).
	    <br>(x+i*y) - (s+i*t) = (x-s)+i*(y-t).
	    @param w is the number to subtract.
	    @return z-w where z is this Complex number.
	*/
	public ComplexNumber minus(ComplexNumber w) {
		return new ComplexNumber(x - w.getReal(), y - w.getImaginary());
	}

	/**
	    Complex multiplication (doesn't change this Complex number).
	    @param w is the number to multiply by.
	    @return z*w where z is this Complex number.
	*/
	public ComplexNumber times(ComplexNumber w) {
		return new ComplexNumber(x * w.getReal() - y * w.getImaginary(), x * w.getImaginary() + y
				* w.getReal());
	}

	/**
	    Division of Complex numbers (doesn't change this Complex number).
	    <br>(x+i*y)/(s+i*t) = ((x*s+y*t) + i*(y*s-y*t)) / (s^2+t^2)
	    @param w is the number to divide by
	    @return new Complex number z/w where z is this Complex number  
	*/
	public ComplexNumber div(ComplexNumber w) {
		double den = Math.pow(w.mod(), 2);
		return new ComplexNumber((x * w.getReal() + y * w.getImaginary()) / den, (y
				* w.getReal() - x * w.getImaginary())
				/ den);
	}

	/**
	    Complex exponential (doesn't change this Complex number).
	    @return exp(z) where z is this Complex number.
	*/
	public ComplexNumber exp() {
		return new ComplexNumber(Math.exp(x) * Math.cos(y), Math.exp(x)
				* Math.sin(y));
	}

	/**
	    Principal branch of the Complex logarithm of this Complex number.
	    (doesn't change this Complex number).
	    The principal branch is the branch with -pi < arg <= pi.
	    @return log(z) where z is this Complex number.
	*/
	public ComplexNumber log() {
		return new ComplexNumber(Math.log(this.mod()), this.arg());
	}

	/**
	    Complex square root (doesn't change this complex number).
	    Computes the principal branch of the square root, which 
	    is the value with 0 <= arg < pi.
	    @return sqrt(z) where z is this Complex number.
	*/
	public ComplexNumber sqrt() {
		double r = Math.sqrt(this.mod());
		double theta = this.arg() / 2;
		return new ComplexNumber(r * Math.cos(theta), r * Math.sin(theta));
	}

	// Real cosh function (used to compute complex trig functions)
	private double cosh(double theta) {
		return (Math.exp(theta) + Math.exp(-theta)) / 2;
	}

	// Real sinh function (used to compute complex trig functions)
	private double sinh(double theta) {
		return (Math.exp(theta) - Math.exp(-theta)) / 2;
	}

	/**
	    Sine of this Complex number (doesn't change this Complex number).
	    <br>sin(z) = (exp(i*z)-exp(-i*z))/(2*i).
	    @return sin(z) where z is this Complex number.
	*/
	public ComplexNumber sin() {
		return new ComplexNumber(cosh(y) * Math.sin(x), sinh(y) * Math.cos(x));
	}

	/**
	    Cosine of this Complex number (doesn't change this Complex number).
	    <br>cos(z) = (exp(i*z)+exp(-i*z))/ 2.
	    @return cos(z) where z is this Complex number.
	*/
	public ComplexNumber cos() {
		return new ComplexNumber(cosh(y) * Math.cos(x), -sinh(y) * Math.sin(x));
	}

	/**
	    Hyperbolic sine of this Complex number 
	    (doesn't change this Complex number).
	    <br>sinh(z) = (exp(z)-exp(-z))/2.
	    @return sinh(z) where z is this Complex number.
	*/
	public ComplexNumber sinh() {
		return new ComplexNumber(sinh(x) * Math.cos(y), cosh(x) * Math.sin(y));
	}

	/**
	    Hyperbolic cosine of this Complex number 
	    (doesn't change this Complex number).
	    <br>cosh(z) = (exp(z) + exp(-z)) / 2.
	    @return cosh(z) where z is this Complex number.
	*/
	public ComplexNumber cosh() {
		return new ComplexNumber(cosh(x) * Math.cos(y), sinh(x) * Math.sin(y));
	}

	/**
	    Tangent of this Complex number (doesn't change this Complex number).
	    <br>tan(z) = sin(z)/cos(z).
	    @return tan(z) where z is this Complex number.
	*/
	public ComplexNumber tan() {
		return (this.sin()).div(this.cos());
	}

	/**
	    Negative of this complex number (chs stands for change sign). 
	    This produces a new Complex number and doesn't change 
	    this Complex number.
	    <br>-(x+i*y) = -x-i*y.
	    @return -z where z is this Complex number.
	*/
	public ComplexNumber chs() {
		return new ComplexNumber(-x, -y);
	}

	/**
	    String representation of this Complex number.
	    @return x+i*y, x-i*y, x, or i*y as appropriate.
	*/
	public String toString() {
		if (x != 0 && y > 0) {
			return x + " + " + y + "i";
		}
		if (x != 0 && y < 0) {
			return x + " - " + (-y) + "i";
		}
		if (y == 0) {
			return String.valueOf(x);
		}
		if (x == 0) {
			return y + "i";
		}
		// shouldn't get here (unless Inf or NaN)
		return x + " + i*" + y;

	}

}
