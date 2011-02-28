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
package mpicbg.imglib.img.list;

import java.util.ArrayList;
import java.util.Collection;

import mpicbg.imglib.Interval;
import mpicbg.imglib.IterableRealInterval;
import mpicbg.imglib.img.AbstractImg;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImg;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.IntervalIndexer;

/**
 * 
 * @param <T>
 * @param <A>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class ListImg< T extends Type< T > > extends AbstractImg< T >
{
	final protected int[] step;
	final protected int[] dim;
	
	final ArrayList<T> pixels;
	
	// we have to overwrite those as this can change during the processing
	protected int numPixels, numEntities;

	protected ListImg( final long[] dim, final T type )
	{
		super( dim );

		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int )dim[ d ];

		this.step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.step );
		this.numPixels = ( int ) super.numPixels;
		
		this.pixels = new ArrayList< T >( numPixels );
		
		for ( int i = 0; i < this.numPixels; ++i )
			pixels.add( type.createVariable() );
	}
	
	public ListImg( final Collection<T> collection, final long[] dim )
	{
		super( dim );
		
		this.dim = new int[ n ];
		for ( int d = 0; d < n; ++d )
			this.dim[ d ] = ( int )dim[ d ];

		this.step = new int[ n ];
		IntervalIndexer.createAllocationSteps( this.dim, this.step );
		this.numPixels = ( int ) super.numPixels;
		
		this.pixels = new ArrayList< T >( numPixels );
		this.pixels.addAll( collection );
	}

	public int[] getSteps() { return step.clone(); }

	public int getStep( final int d ) { return step[ d ]; }

	public final int getPos( final int[] l )
	{
		int i = l[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	public final int getPos( final long[] l )
	{
		int i = (int)l[ 0 ];
		for ( int d = 1; d < n; ++d )
			i += l[ d ] * step[ d ];

		return i;
	}

	@Override
	public ListCursor< T > cursor()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public ListLocalizingCursor< T > localizingCursor()
	{
		return new ListLocalizingCursor< T >( this  );
	}

	@Override
	public ListRandomAccess< T > randomAccess()
	{
		return new ListRandomAccess< T >( this );
	}

	@Override
	public ListOutOfBoundsRandomAccess<T> randomAccess( final OutOfBoundsFactory<T, Img< T >> outOfBoundsFactory )
	{
		return new ListOutOfBoundsRandomAccess< T >( this, outOfBoundsFactory );
	}

	@Override
	public ListImgFactory<T> factory() { return new ListImgFactory<T>(); }

	@Override
	public boolean equalIterationOrder( final IterableRealInterval<?> f )
	{
		if ( f.numDimensions() != this.numDimensions() )
			return false;
		
		if ( getClass().isInstance( f ) || ArrayImg.class.isInstance( f ) )
		{
			final Interval a = ( Interval )f;
			for ( int d = 0; d < n; ++d )
				if ( dimension[ d ] != a.dimension( d ) )
					return false;

			return true;
		}

		return false;
	}
}