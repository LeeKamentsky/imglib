/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package mpicbg.imglib.algorithm.integral;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.function.Converter;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.NumericType;
import mpicbg.imglib.type.numeric.integer.LongType;

/**
 * Special implementation for long using the basic type to sum up the individual lines. 
 * 
 * @param <R>
 * @author Stephan Preibisch
 */
public class IntegralImageLong< R extends NumericType< R > > extends IntegralImage< R, LongType >
{

	public IntegralImageLong( final Image<R> img, final LongType type, final Converter<R, LongType> converter) 
	{
		super( img, type, converter );
	}
	
	@Override
	protected void integrateLineDim0( final Converter< R, LongType > converter, final LocalizableByDimCursor< R > cursorIn, final LocalizableByDimCursor< LongType > cursorOut, final LongType sum, final LongType tmpVar, final int size )
	{
		// compute the first pixel
		converter.convert( cursorIn.getType(), sum );
		cursorOut.getType().set( sum );
		
		long sum2 = sum.get();

		for ( int i = 2; i < size; ++i )
		{
			cursorIn.fwd( 0 );
			cursorOut.fwd( 0 );

			converter.convert( cursorIn.getType(), tmpVar );
			sum2 += tmpVar.get();
			cursorOut.getType().set( sum2 );
		}		
	}

	@Override
	protected void integrateLine( final int d, final LocalizableByDimCursor< LongType > cursor, final LongType sum, final int size )
	{
		// init sum on first pixel that is not zero
		long sum2 = cursor.getType().get();

		for ( int i = 2; i < size; ++i )
		{
			cursor.fwd( d );

			sum2 += cursor.getType().get();
			cursor.getType().set( sum2 );
		}
	}
}
