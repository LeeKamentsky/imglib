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

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * Invertible transformation from R<sup><em>n</em></sup> to R<sup><em>m</em>
 * </sup>.
 * 
 * <p>
 * Applying the transformation to a <em>n</em>-dimensional
 * <em>source</em> vector yields a <em>m</em>-dimensional
 * <em>target</em> vector.
 * </p>
 * 
 * <p>
 * You can also
 * {@link InvertibleRealTransform#applyInverse(RealPositionable, RealLocalizable)
 * apply the inverse transformation} to a <em>m</em>-dimensional
 * <em>target</em> vector to get the <em>n</em>-dimensional
 * <em>source</em> vector.
 * </p>
 * 
 * @author ImgLib2 developers
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld
 */
public interface InvertibleRealTransform extends RealTransform
{
	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final double[] source, final double[] target );

	/**
	 * Apply the inverse transform to a target vector to obtain a source vector.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final float[] source, final float[] target );

	/**
	 * Apply the inverse transform to a target {@link RealLocalizable} to obtain
	 * a source {@link RealPositionable}.
	 * 
	 * @param source
	 *            set this to the source coordinates.
	 * @param target
	 *            target coordinates.
	 */
	public void applyInverse( final RealPositionable source, final RealLocalizable target );

	/**
	 * Get the inverse transform.
	 * 
	 * @return the inverse transform
	 */
	public InvertibleRealTransform inverse();
}
