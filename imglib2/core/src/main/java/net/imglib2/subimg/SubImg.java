package net.imglib2.subimg;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;

public class SubImg< T extends Type< T > > extends IterableRandomAccessibleInterval< T > implements Img< T >
{
	private static final < T extends Type< T > > RandomAccessibleInterval< T > getView( Img< T > srcImg, Interval interval, boolean keepDimsWithSizeOne )
	{
		if ( !Util.contains( srcImg, interval ) )
			throw new IllegalArgumentException( "In SubImgs the interval min and max must be inside the dimensions of the SrcImg" );

		final int n = interval.numDimensions();
		final long[] min = new long[ interval.numDimensions() ];
		interval.min( min );
		RandomAccessibleInterval< T > slice = Views.translate( srcImg, min );
		if ( !keepDimsWithSizeOne )
			for ( int d = 0; d < n; ++d )
				if ( interval.dimension( d ) == 1 )
					slice = Views.hyperSlice( slice, d, interval.min( d ) );
		return slice;
	}

	private final Img< T > m_srcImg;

	private final long[] m_origin;

	public SubImg( Img< T > srcImg, Interval interval, boolean keepDimsWithSizeOne )
	{
		super( getView( srcImg, interval, keepDimsWithSizeOne ) );
		m_srcImg = srcImg;
		m_origin = new long[ interval.numDimensions() ];
		interval.min( m_origin );
	}

	public final void getOrigin( long[] origin )
	{
		for ( int d = 0; d < origin.length; d++ )
			origin[ d ] = m_origin[ d ];
	}

	public final long getOrigin( int d )
	{
		return m_origin[ d ];
	}

	/**
	 * @return
	 */
	public Img<T> getImg() {
		return m_srcImg;
	}

	
	// Img implementation
	@Override
	public ImgFactory< T > factory()
	{
		return m_srcImg.factory();
	}

	@Override
	public Img< T > copy()
	{
		Img< T > copy = m_srcImg.factory().create( this, m_srcImg.firstElement() );
		// TODO: copy...
		return copy;
	}
}