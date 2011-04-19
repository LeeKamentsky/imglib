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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib;

import mpicbg.imglib.util.Util;

/**
 * Generic implementation of {@link mpicbg.imglib.Iterator Iterator} mapping to
 * abstract {@link #fwd()} and {@link #get()}.
 * 
 * <p>
 * For localization, default implementations are available that all build on
 * the abstract long variant.  For particular cursors, this may be implemented more
 * efficiently saving at least one loop over <em>n</em>. 
 * 
 * @author Tobias Pietzsch, Stephan Preibisch and Stephan Saalfeld
 * 
 * @param <T>
 */
public abstract class AbstractCursor< T > extends AbstractSampler< T > implements Cursor< T >
{
	/**
	 * used internally to forward all localize() versions to the (abstract)
	 * long[] version.
	 */
	final private long[] tmp;
	
	/**
	 * @param n number of dimensions in the {@link Img}.
	 */
	public AbstractCursor( final int n )
	{
		super( n );
		tmp = new long[ n ];
	}

	@Override
	public void remove()
	{
		
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	/**
	 * Highly recommended to override this with a more efficient version.
	 * 
	 * @param steps
	 */
	public void jumpFwd( final long steps )
	{
		for ( long j = 0; j < steps; ++j )
			fwd();
	}

	@Override
	public void localize( float[] pos )
	{
		localize( this.tmp );
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.tmp[ d ];
	}

	@Override
	public void localize( double[] pos )
	{
		localize( this.tmp );
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.tmp[ d ];
	}

	@Override
	public void localize( int[] pos )
	{
		localize( this.tmp );
		for ( int d = 0; d < n; d++ )
			pos[ d ] = ( int )this.tmp[ d ];
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return getLongPosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return ( int )getLongPosition( d );
	}

	@Override
	public String toString()
	{
		localize( tmp );
		return Util.printCoordinates( tmp ) + " = " + get();
	}
	
	@Override
	abstract public AbstractCursor< T > copy();
}
