/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

package mpicbg.imglib.container.basictypecontainer.array;

import mpicbg.imglib.container.basictypecontainer.BitAccess;

/**
 * TODO
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class BitArray implements BitAccess, ArrayDataAccess<BitArray>
{
	final static int bitsPerEntity = Integer.SIZE;

	final int numEntities;
	protected int data[];
	
	public BitArray( final int numEntities )
	{
		this.numEntities = numEntities;
		
		final int numElements;
		
		if ( this.numEntities % bitsPerEntity == 0 )
			numElements = this.numEntities / bitsPerEntity;
		else
			numElements = this.numEntities / bitsPerEntity + 1;
			
		this.data = new int[ numElements ];
	}

	@Override
	public void close() { data = null; }

	@Override
	public boolean getValue( final int index ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;

		final int entry = data[ arrayIndex ];		
		final int value = (entry & ( 1 << arrayOffset ) );
		
		return value != 0; 
	}

	@Override
	public void setValue( final int index, final boolean value ) 
	{
		final int arrayIndex = index / bitsPerEntity;
		final int arrayOffset = index % bitsPerEntity;
		
		if ( value )
			data[ arrayIndex ] = data[ arrayIndex ] | ( 1 << arrayOffset );
		else
			data[ arrayIndex ] = data[ arrayIndex ] & ~( 1 << arrayOffset ); 
	}

	@Override
	public int[] getCurrentStorageArray() { return data; }

	@Override
	public BitArray createArray( final int numEntities ) { return new BitArray( numEntities ); }

}
