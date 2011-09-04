/*

Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.operation.unary.complex;

import net.imglib2.ops.ComplexOutput;
import net.imglib2.ops.UnaryOperation;
import net.imglib2.ops.Complex;
import net.imglib2.ops.operation.binary.complex.ComplexDivide;
import net.imglib2.ops.operation.binary.complex.ComplexMultiply;
import net.imglib2.ops.operation.binary.complex.ComplexSubtract;
import net.imglib2.ops.operation.unary.complex.ComplexExp;

/**
 * 
 * @author Barry DeZonia
 *
 */
public class ComplexSin extends ComplexOutput implements UnaryOperation<Complex,Complex> {

	private static final ComplexExp expFunc = new ComplexExp();
	private static final ComplexSubtract subFunc = new ComplexSubtract();
	private static final ComplexMultiply mulFunc = new ComplexMultiply();
	private static final ComplexDivide divFunc = new ComplexDivide();
	private static final Complex twoI = Complex.createCartesian(0,2);
	private static final Complex I = Complex.createCartesian(0,1);
	private static final Complex minusI = Complex.createCartesian(0,-1);
	
	private final Complex IZ;
	private final Complex minusIZ;
	private final Complex expIZ;
	private final Complex expMinusIZ;
	private final Complex diff;
	
	public ComplexSin() {
		IZ = new Complex();
		minusIZ = new Complex();
		expIZ = new Complex();
		expMinusIZ = new Complex();
		diff = new Complex();
	}
	
	@Override
	public void compute(Complex input, Complex output) {
		mulFunc.compute(input, I, IZ);
		mulFunc.compute(input, minusI, minusIZ);
		expFunc.compute(IZ, expIZ);
		expFunc.compute(minusIZ, expMinusIZ);
		subFunc.compute(expIZ, expMinusIZ, diff);
		divFunc.compute(diff, twoI, output);
	}
}