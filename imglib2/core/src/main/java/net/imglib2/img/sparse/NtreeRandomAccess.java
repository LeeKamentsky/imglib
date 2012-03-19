/**
 * Copyright (c) 2009--2012, ImgLib2 developers
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
 * @author Tobias Pietzsch
 */
package net.imglib2.img.sparse;

import net.imglib2.AbstractRandomAccess;
import net.imglib2.img.sparse.NtreeImg.PositionProvider;
import net.imglib2.type.NativeType;

/**
 * @author Tobias Pietzsch
 * 
 */
public final class NtreeRandomAccess< T extends NativeType< T >> extends AbstractRandomAccess< T > implements PositionProvider
{
	private final NtreeImg< T, ? > img;

	private final T type;

	public NtreeRandomAccess( final NtreeImg< T, ? > img )
	{
		super( img.numDimensions() );

		this.img = img;
		this.type = img.createLinkedType();

		for ( int d = 0; d < n; d++ )
			position[ d ] = 0;

		type.updateContainer( this );
	}

	private NtreeRandomAccess( final NtreeRandomAccess< T > randomAccess )
	{
		super( randomAccess.numDimensions() );

		this.img = randomAccess.img;
		this.type = img.createLinkedType();

		for ( int d = 0; d < n; d++ )
			position[ d ] = randomAccess.position[ d ];

		type.updateContainer( this );
	}

	@Override
	public void fwd( final int d )
	{
		++position[ d ];
	}

	@Override
	public void bck( final int d )
	{
		--position[ d ];
	}

	@Override
	public void move( final long distance, final int d )
	{
		position[ d ] += distance;
	}

	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public void setPosition( final long pos, final int d )
	{
		position[ d ] = pos;
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public NtreeRandomAccess< T > copy()
	{
		return new NtreeRandomAccess< T >( this );
	}

	@Override
	public NtreeRandomAccess< T > copyRandomAccess()
	{
		return copy();
	}

	@Override
	public long[] getPosition()
	{
		return position;
	}
}