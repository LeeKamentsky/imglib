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
package mpicbg.imglib.img.planar;

import mpicbg.imglib.AbstractLocalizingCursorInt;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.type.NativeType;

/**
 * Localizing Iterator for a {@link PlanarImg PlanarContainers}
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class PlanarLocalizingCursor< T extends NativeType< T > > extends AbstractLocalizingCursorInt< T > implements Cursor< T >, PlanarImg.PlanarContainerSampler
{
	protected final T type;

	protected final PlanarImg< T, ? > container;

	protected final int lastIndex, lastSliceIndex;
	protected int sliceIndex;

	/**
	 * The current index of the type.
	 * It is faster to duplicate this here than to access it through type.getIndex(). 
	 */
	protected int index;

	/**
	 * Maximum of the {@link PlanarImg} in every dimension.
	 * This is used to check isOutOfBounds().
	 */
	protected final int[] max;
	
	protected PlanarLocalizingCursor( final PlanarLocalizingCursor< T > cursor )
	{
		super( cursor.numDimensions() );
		
		container = cursor.container;
		this.type = container.createLinkedType();
		
		lastIndex = cursor.lastIndex;
		lastSliceIndex = cursor.lastSliceIndex;
		
		max = new int[ n ];
		for( int d = 0; d < n; ++d )
		{
			max[ d ] = cursor.max[ d ];
			position[ d ] = cursor.position[ d ];
		}

		sliceIndex = cursor.sliceIndex;
		index = cursor.index;
		
		type.updateContainer( this );
		type.updateIndex( index );
	}
	
	public PlanarLocalizingCursor( final PlanarImg< T, ? > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.container = container;

		lastIndex = ( ( n > 1 ) ? container.dimensions[ 1 ] : 1 )  *  container.dimensions[ 0 ] - 1;
		lastSliceIndex = container.numSlices() - 1;
		
		max = new int[ n ];
		for( int d = 0; d < n; ++d )
			max[ d ] = ( int ) container.max( d );

		reset();
	}

	@Override
	public int getCurrentSliceIndex() { return sliceIndex; }

	@Override
	public T get() { return type; }
	
	@Override
	public PlanarLocalizingCursor< T > copy()
	{
		return new PlanarLocalizingCursor< T >( this );
	}
	
	/**
	 * Note: This test is fragile in a sense that it returns true for elements
	 * after the last element as well.
	 * 
	 * @return false for the last element 
	 */
	@Override
	public boolean hasNext()
	{
		return ( sliceIndex < lastSliceIndex ) || ( index < lastIndex );		
	}
	
	@Override
	public void fwd()
	{
		if ( ++index > lastIndex )
		{
			index = 0;
			++sliceIndex;
			type.updateContainer( this );
		}
		type.updateIndex( index );

		for ( int d = 0; d < n; ++d )
		{
			if ( ++position[ d ] > max[ d ] ) position[ d ] = 0;
			else break;
		}
	}

	@Override
	public void jumpFwd( long steps )
	{
		long newIndex = index + steps;
		if ( newIndex > lastIndex )
		{
			final long s = newIndex / (lastIndex + 1);
			newIndex -= s * (lastIndex + 1);
			sliceIndex += s;
			type.updateContainer( this );
		}
		index = ( int ) newIndex;
		type.updateIndex( index );
		container.indexToGlobalPosition( sliceIndex, index, position );
	}

	@Override
	public void reset()
	{
		position[ 0 ] = -1;
		for ( int d = 1; d < n; d++ )
			position[ d ] = 0;
		
		sliceIndex = 0;
		index = -1;		
		type.updateIndex( -1 );		
		type.updateContainer( this );
	}
}
