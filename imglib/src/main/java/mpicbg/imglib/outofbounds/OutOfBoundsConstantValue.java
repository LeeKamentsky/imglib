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
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.Interval;
import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.Util;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class OutOfBoundsConstantValue< T extends Type< T > > implements OutOfBounds< T >
{
	final protected T value;
	
	final protected RandomAccess< T > sampler;
	
	final protected int n;
	
	final protected long[] dimension, min, max, position;
	
	final protected boolean[] dimIsOutOfBounds;
	
	protected boolean isOutOfBounds = false;
	
	protected OutOfBoundsConstantValue( final OutOfBoundsConstantValue< T > outOfBounds )
	{
		this.value = outOfBounds.value.copy();
		this.sampler = outOfBounds.sampler.copy();
		n = outOfBounds.n;
		dimension = new long[ n ];
		min = new long[ n ];
		max = new long[ n ];
		position = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
		for ( int d = 0; d < n; ++d )
		{
			dimension[ d ] = outOfBounds.dimension[ d ];
			min[ d ] = outOfBounds.min[ d ];
			max[ d ] = outOfBounds.max[ d ];
			position[ d ] = outOfBounds.position[ d ];
			dimIsOutOfBounds[ d ] = outOfBounds.dimIsOutOfBounds[ d ];
		}
	}
	
	public < F extends Interval & RandomAccessible< T > > OutOfBoundsConstantValue( final F f, final T value )
	{
		this.sampler = f.randomAccess();
		this.value = value;
		n = f.numDimensions();
		dimension = new long[ n ];
		f.dimensions( dimension );
		min = new long[ n ];
		f.min( min );
		max = new long[ n ];
		f.max( max );
		position = new long[ n ];
		dimIsOutOfBounds = new boolean[ n ];
	}
		
	final private void checkOutOfBounds()
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( dimIsOutOfBounds[ d ] )
			{
				isOutOfBounds = true;
				return;
			}
		}
		isOutOfBounds = false;
	}
	
	
	/* Dimensionality */
	
	@Override
	public int numDimensions(){ return n; }
	
	
	/* OutOfBounds */
	
	@Override
	public boolean isOutOfBounds()
	{
		checkOutOfBounds();
		return isOutOfBounds;
	}

	
	/* Sampler */
	
	@Override
	final public T get()
	{
		//System.out.println( getLocationAsString() + " " + isOutOfBounds );
		if ( isOutOfBounds )
			return value;
		else
			return sampler.get();
	}
	
	@Override
	final public OutOfBoundsConstantValue< T > copy()
	{
		return new OutOfBoundsConstantValue< T >( this );
	}
	
	
	/* RasterLocalizable */
	
	@Override
	public void localize( final float[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final double[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}

	@Override
	public void localize( final int[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = ( int )this.position[ d ];
	}
	
	@Override
	public void localize( final long[] pos )
	{
		for ( int d = 0; d < n; d++ )
			pos[ d ] = this.position[ d ];
	}
	
	@Override
	public float getFloatPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public double getDoublePosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public int getIntPosition( final int dim ){ return ( int )position[ dim ]; }

	@Override
	public long getLongPosition( final int dim ){ return position[ dim ]; }
	
	@Override
	public String toString() { return Util.printCoordinates( position ) + " = " + get(); }
	
	
	/* RasterPositionable */
	
	@Override
	public void fwd( final int dim )
	{
		final boolean wasOutOfBounds = isOutOfBounds;
		final long p = ++position[ dim ];
		if ( p == 0 )
		{
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
		}
		else if ( p == dimension[ dim ] )
		{
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
			return;
		}
		
		if ( isOutOfBounds ) return;
		if ( wasOutOfBounds )
			sampler.setPosition( position );
		else
			sampler.fwd( dim );
	}
	
	@Override
	public void bck( final int dim )
	{
		final boolean wasOutOfBounds = isOutOfBounds;
		final long p = position[ dim ]--;
		if ( p == 0 )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		else if ( p == dimension[ dim ] )
		{
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
		}
		
		if ( isOutOfBounds ) return;
		if ( wasOutOfBounds )
			sampler.setPosition( position );
		else
			sampler.bck( dim );
	}
	
	@Override
	public void move( final int distance, final int dim )
	{
		if ( distance > 0 )
		{
			for ( int i = 0; i < distance; ++i )
				fwd( dim );
		}
		else
		{
			for ( int i = -distance; i > 0; --i )
				bck( dim );
		}
	}
	
	@Override
	public void move( final long distance, final int dim )
	{
		move( ( int )distance, dim );
	}
	
	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			move( localizable.getIntPosition( d ), d );
	}
	
	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}
	
	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
			move( distance[ d ], d );
	}
	
	@Override
	public void setPosition( final int position, final int dim )
	{
		this.position[ dim ] = position;
		if ( position < 0 || position >= dimension[ dim ] )
			dimIsOutOfBounds[ dim ] = isOutOfBounds = true;
		else
		{
			final boolean wasOutOfBounds = isOutOfBounds;
			dimIsOutOfBounds[ dim ] = false;
			checkOutOfBounds();
			
			if ( isOutOfBounds ) return;
			if ( wasOutOfBounds )
				sampler.setPosition( this.position );
			else
				sampler.setPosition( position, dim );
		}
	}
	
	@Override
	public void setPosition( final long position, final int dim )
	{
		setPosition( ( int )position, dim );
	}
	
	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
			setPosition( localizable.getIntPosition( d ), d );
	}
	
	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}
	
	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < position.length; ++d )
			setPosition( position[ d ], d );
	}
}
