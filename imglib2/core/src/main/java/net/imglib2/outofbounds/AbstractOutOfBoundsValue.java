/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.outofbounds;

import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;
import net.imglib2.util.Util;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public abstract class AbstractOutOfBoundsValue< T extends Type< T > > implements OutOfBounds< T >
{
	final protected RandomAccess< T > sampler;
	
	final protected int n;
	
	final protected long[] dimension, min, max, position;
	
	final protected boolean[] dimIsOutOfBounds;
	
	protected boolean isOutOfBounds = false;
	
	protected AbstractOutOfBoundsValue( final AbstractOutOfBoundsValue< T > outOfBounds )
	{
		this.sampler = outOfBounds.sampler.copyRandomAccess();
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
	
	public < F extends Interval & RandomAccessible< T > > AbstractOutOfBoundsValue( final F f, final T value )
	{
		this.sampler = f.randomAccess();
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
		if ( position < min[ dim ] || position > max[ dim ] )
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
