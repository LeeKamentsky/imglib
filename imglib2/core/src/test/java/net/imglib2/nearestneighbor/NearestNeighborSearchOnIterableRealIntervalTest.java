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

package net.imglib2.nearestneighbor;

import net.imglib2.RealCursor;
import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.collection.RealPointSampleList;
import net.imglib2.neighborsearch.KNearestNeighborSearchOnIterableRealInterval;
import net.imglib2.type.numeric.real.DoubleType;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/**
 * TODO
 *
 */
public class NearestNeighborSearchOnIterableRealIntervalTest
{
	final static private RealPointSampleList< DoubleType > realPointSampleList = new RealPointSampleList< DoubleType >( 2 );
	final static private double[][] coordinates = new double[][]{
		{ 0, 0 },
		{ 0, 1 },
		{ 1, 0 },
		{ 1, 1 }
	};
	
	final static private double[] samples = new double[]{
		0, 1, 2, 3
	};
	
	final static private boolean positionEquals(
			final RealLocalizable a,
			final RealLocalizable b )
	{
		final int n = a.numDimensions();
		if ( n != b.numDimensions() )
			return false;
		for ( int d = 0; d < n; ++d )
		{
			if ( a.getDoublePosition( d ) != b.getDoublePosition( d ) )
				return false;
		}
		return true;
	}
	
	@Before
	public void init()
	{
		for ( int i = 0; i < samples.length; ++i )
			realPointSampleList.add( new RealPoint( coordinates[ i ] ), new DoubleType( samples[ i ] ) );
	}
	
	@Test
	public void testKNearestNeighborSearch()
	{
		final RealCursor< DoubleType > cursor = realPointSampleList.cursor();
		final KNearestNeighborSearchOnIterableRealInterval< DoubleType > search1 = new KNearestNeighborSearchOnIterableRealInterval< DoubleType >( realPointSampleList, 1 );
		
		search1.search( new RealPoint( new double[]{ 0.1, 0.2 } ) );
		assertTrue( "Position mismatch ", positionEquals( search1.getPosition( 0 ), new RealPoint( coordinates[ 0 ] ) ) );
		assertTrue( "Sample mismatch ", search1.getSampler( 0 ).get() == cursor.next() );
		
		search1.search( new RealPoint( new double[]{ -1, 20 } ) );
		assertTrue( "Position mismatch ", positionEquals( search1.getPosition( 0 ), new RealPoint( coordinates[ 1 ] ) ) );
		assertTrue( "Sample mismatch ", search1.getSampler( 0 ).get() == cursor.next() );
	}	
}
