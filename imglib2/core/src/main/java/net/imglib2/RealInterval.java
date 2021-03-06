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

package net.imglib2;

/**
 * <p>{x&isin;R<sup><em>n</em></sup>|<em>min<sub>d</sub></em>&le;<em>x<sub>d</sub></em>&le;<em>max<sub>d</sub></em>;<em>d</em>&isin;{0&hellip;<em>n</em>-1}}</p>
 * 
 * An {@link RealInterval} over the real source domain.  <em>Note</em> that
 * this does <em>not</em> imply that for <em>all</em> coordinates in the
 * {@link RealInterval} function values exist or can be generated.  It only
 * defines where the minimum and maximum source coordinates are.  E.g. an
 * {@link IterableRealInterval} has a limited number of values and a source
 * coordinate for each.  By that, minimum and maximum are defined but the
 * {@link RealInterval} does not define a value for all coordinates in between.
 *
 * @author Stephan Saalfeld
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public interface RealInterval extends EuclideanSpace
{
	/**
	 * 
	 * @param d dimension
	 * @return minimum
	 */
	public double realMin( int d );
	
	/**
	 * Write the minimum of each dimension into double[].
	 * 
	 * @param min
	 */
	public void realMin( double[] min );
	
	/**
	 * Sets a {@link RealPositionable} to the minimum of this {@link Interval}
	 * 
	 * @param min
	 */
	public void realMin( RealPositionable min );
	
	/**
	 * 
	 * @param d dimension
	 * @return maximum
	 */
	public double realMax( final int d );
	
	/**
	 * Write the maximum of each dimension into double[].
	 * 
	 * @param max
	 */
	public void realMax( double[] max );

	/**
	 * Sets a {@link RealPositionable} to the minimum of this {@link Interval}
	 * 
	 * @param min
	 */
	public void realMax( RealPositionable max );
}
