/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.interpolation.randomaccess;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.RealRandomAccess;
import mpicbg.imglib.position.transform.Floor;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.util.IntervalIndexer;

/**
 * 
 * @param <T>
 *
 * @author Tobias Pietzsch, Stephan Preibisch and Stephan Saalfeld
 */
public class NLinearInterpolator< T extends NumericType< T > > extends Floor< RandomAccess< T > > implements RealRandomAccess< T >
{
	/**
	 * Index into {@link weights} array.
	 * 
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * 
	 * <p>
	 * This index is the corresponding gray code bit pattern which will select
	 * the correct corresponding weight.
	 * 
	 * <p>
	 * {@see http://en.wikipedia.org/wiki/Gray_code}
	 */
	protected int code;

	/**
	 *  Weights for each pixel of the <em>2x2x...x2</em> hypercube
	 *  of pixels participating in the interpolation.
	 *  
	 *  Indices into this array are arranged in the standard iteration
	 *  order (as provided by {@link IntervalIndexer#positionToIndex}).
	 *  Element 0 refers to position <em>(0,0,...,0)</em>,
	 *  element 1 refers to position <em>(1,0,...,0)</em>,
	 *  element 2 refers to position <em>(0,1,...,0)</em>, etc.
	 */
	final protected double[] weights;

	final protected T accumulator;

	final protected T tmp;

	protected NLinearInterpolator( final NLinearInterpolator< T > interpolator )
	{
		super( interpolator.target.copy() );
		
		weights = interpolator.weights.clone();		
		code = interpolator.code;
		accumulator = interpolator.accumulator.copy();
		tmp = interpolator.tmp.copy();
		
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = interpolator.position[ d ];
			floor[ d ] = interpolator.floor[ d ];
		}
	}
	
	protected NLinearInterpolator( final RandomAccessible< T > randomAccessible, final T type )
	{
		super( randomAccessible.randomAccess() );
		weights = new double [ 1 << n ];		
		code = 0;
		accumulator = type.createVariable();
		tmp = type.createVariable();
	}

	protected NLinearInterpolator( final RandomAccessible< T > randomAccessible )
	{
		this( randomAccessible, randomAccessible.randomAccess().get() );
	}

	/**
	 * Fill the {@link weights} array.
	 * 
	 * <p>
	 * Let <em>w_d</em> denote the fraction of a pixel at which the sample
	 * position <em>p_d</em> lies from the floored position <em>pf_d</em> in
	 * dimension <em>d</em>. That is, the value at <em>pf_d</em> contributes
	 * with <em>(1 - w_d)</em> to the sampled value; the value at
	 * <em>( pf_d + 1 )</em> contributes with <em>w_d</em>.
	 * 
	 * <p>
	 * At every pixel, the total weight results from multiplying the weights of
	 * all dimensions for that pixel. That is, the "top-left" contributing pixel
	 * (position floored in all dimensions) gets assigned weight
	 * <em>(1-w_0)(1-w_1)...(1-w_n)</em>.
	 * 
	 * <p>
	 * We work through the weights array starting from the highest dimension.
	 * For the highest dimension, the first half of the weights contain the
	 * factor <em>(1 - w_n)</em> because this first half corresponds to floored
	 * pixel positions in the highest dimension. The second half contain the
	 * factor <em>w_n</em>. In this first step, the first weight of the first
	 * half gets assigned <em>(1 - w_n)</em>. The first element of the second
	 * half gets assigned <em>w_n</em>
	 * 
	 * <p>
	 * From their, we work recursively down to dimension 0. That is, each half
	 * of weights is again split recursively into two partitions. The first
	 * element of the second partitions is the first element of the half
	 * multiplied with <em>(w_d)</em>. The first element of the first partitions
	 * is multiplied with <em>(1 - w_d)</em>.
	 * 
	 * <p>
	 * When we have reached dimension 0, all weights will have a value assigned.
	 */
	protected void fillWeights()
	{
		weights[ 0 ] = 1.0d;

		for ( int d = n - 1; d >= 0; --d )
		{
			final double w    = position[ d ] - target.getLongPosition( d );
			final double wInv = 1.0d - w;
			final int wInvIndexIncrement = 1 << d;
			final int loopCount = 1 << ( n - 1 - d );
			final int baseIndexIncrement = wInvIndexIncrement * 2;
			int baseIndex = 0;
			for (int i = 0; i < loopCount; ++i )
			{
				weights[ baseIndex + wInvIndexIncrement ] = weights[ baseIndex ] * w;
				weights[ baseIndex ] *= wInv;
				baseIndex += baseIndexIncrement;
			}
		}
//		printWeights();
//		System.out.println();
	}
	
	/**
	 * Get the interpolated value at the current position.
	 * 
	 * <p>
	 * To visit the pixels that contribute to an interpolated value, we move in
	 * a (binary-reflected) Gray code pattern, such that only one dimension of
	 * the target position is modified per move.
	 * 
	 * <p>
	 * {@see http://en.wikipedia.org/wiki/Gray_code}
	 */
	@Override
	public T get()
	{
		fillWeights();

		accumulator.set( target.get() );
		accumulator.mul( weights[ 0 ] );

		code = 0;
		graycodeFwdRecursive( n - 1 );
		target.bck( n - 1 );
		
		return accumulator;
	}
	
	@Override
	public NLinearInterpolator< T > copy()
	{
		return new NLinearInterpolator< T >( this );
	}

	final private void graycodeFwdRecursive ( int dimension )
	{
		if ( dimension == 0 )
		{
			target.fwd ( 0 );
			code += 1;
			accumulate();
		}
		else
		{
			graycodeFwdRecursive ( dimension - 1 );
			target.fwd ( dimension );
			code += 1 << dimension;
			accumulate();
			graycodeBckRecursive ( dimension - 1 );
		}
	}

	final private void graycodeBckRecursive ( int dimension )
	{
		if ( dimension == 0 )
		{
			target.bck ( 0 );
			code -= 1;
			accumulate();
	}
		else
		{
			graycodeFwdRecursive ( dimension - 1 );
			target.bck ( dimension );
			code -= 1 << dimension;
			accumulate();
			graycodeBckRecursive ( dimension - 1 );
		}
	}

	/**
	 * multiply current target value with current weight and add to accumulator.
	 */
	final private void accumulate()
	{
		tmp.set( target.get() );
		tmp.mul( weights[ code ] );
		accumulator.add( tmp );
//		System.out.print( "accumulating value at " + target );
//		System.out.print( "with weights [" );
//		printCode();
//		System.out.printf( "] = %f" + "\n", weights[ code ] );
	}

	@SuppressWarnings( "unused" )
	final private void printWeights()
	{
		for ( int i = 0; i < weights.length; ++i )
			System.out.printf("weights [ %2d ] = %f\n", i, weights[ i ] );
	}
	
	@SuppressWarnings( "unused" )
	final private void printCode()
	{
		final int maxbits = 4;
		String binary = Integer.toBinaryString( code );
		for ( int i = binary.length(); i < maxbits; ++i )
			System.out.print("0");
		System.out.print ( binary );
	}

	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}
}
