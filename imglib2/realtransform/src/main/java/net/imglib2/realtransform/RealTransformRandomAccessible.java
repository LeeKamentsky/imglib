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

package net.imglib2.realtransform;

import net.imglib2.Interval;
import net.imglib2.Point;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.RealRandomAccessible;

/**
 *
 *
 * @author ImgLib2 developers
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class RealTransformRandomAccessible< T, R extends RealTransform > extends RealTransformRealRandomAccessible< T, R > implements RandomAccessible< T >
{
	/**
	 * {@link RealRandomAccess} that generates its samples from a target
	 * {@link RealRandomAccessible} at coordinates transformed by a
	 * {@link RealTransform}.
	 *
	 */
	public class RealTransformRandomAccess extends Point implements RandomAccess< T >
	{
		final protected Point sourcePosition;
		final protected RealRandomAccess< T > targetAccess;

		protected RealTransformRandomAccess()
		{
			super( transform.numSourceDimensions() );
			sourcePosition = Point.wrap( position );
			this.targetAccess = target.realRandomAccess();
		}

		final protected void apply()
		{
			transform.apply( sourcePosition, targetAccess );
		}

		@Override
		public T get()
		{
			apply();
			return targetAccess.get();
		}

		@Override
		public RealTransformRandomAccess copy()
		{
			return new RealTransformRandomAccess();
		}

		@Override
		public RealTransformRandomAccess copyRandomAccess()
		{
			return copy();
		}
	}

	public RealTransformRandomAccessible( final RealRandomAccessible< T > target, final R transform )
	{
		super( target, transform );
	}

	@Override
	public RealTransformRandomAccess randomAccess()
	{
		return new RealTransformRandomAccess();
	}

	/**
	 * To be overridden for {@link RealTransform} that can estimate the
	 * boundaries of a transferred {@link RealInterval}.
	 */
	@Override
	public RealTransformRandomAccess randomAccess( final Interval interval )
	{
		return randomAccess();
	}
}
