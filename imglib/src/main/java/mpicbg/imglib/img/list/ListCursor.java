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

import mpicbg.imglib.AbstractCursor;
import mpicbg.imglib.Cursor;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.util.IntervalIndexer;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
final public class ListCursor< T extends Type< T > > extends AbstractCursor< T > implements Cursor< T >
{
	private int i;
	final private int maxNumPixels;
	
	final private ArrayList< T > pixels;
	final private ListImg< T > container;
	
	protected ListCursor( final ListCursor< T > cursor )
	{
		super( cursor.numDimensions() );
		
		container = cursor.container;
		this.pixels = container.pixels;
		this.maxNumPixels = cursor.maxNumPixels;
		
		i = cursor.i;
	}
	
	public ListCursor( final ListImg< T > container )
	{
		super( container.numDimensions() );
		
		this.container = container;
		this.pixels = container.pixels;
		this.maxNumPixels = ( int )container.size() - 1;
		
		reset();
	}
	
	@Override
	public T get() { return pixels.get( i ); }
	
	@Override
	public ListCursor< T > copy()
	{
		return new ListCursor< T >( this );
	}

	@Override
	public boolean hasNext() { return i < maxNumPixels; }

	@Override
	public void jumpFwd( final long steps )  { i += steps; }

	@Override
	public void fwd() { ++i; }

	@Override
	public void reset() { i = -1; }
	
	@Override
	public long getLongPosition( final int dim )
	{
		return IntervalIndexer.indexToPosition( i, container.dim, container.step, dim );
	}

	@Override
	public void localize( final long[] position )
	{
		IntervalIndexer.indexToPosition( i, container.dim, position );
	}
}
