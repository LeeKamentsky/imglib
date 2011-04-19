package mpicbg.imglib.transform.integer;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.Positionable;
import mpicbg.imglib.concatenate.Concatenable;
import mpicbg.imglib.concatenate.PreConcatenable;

/**
 * Mixed transform allows to express common integer view transformations such as
 * translation, rotation, rotoinversion, and projection.
 * 
 * <p>
 * It transform a n-dimensional source vector to a m-dimensional target vector,
 * and can be represented as a <em>m+1</em> &times; <em>n+1</em> homogeneous
 * matrix. The mixed transform can be decomposed as follows:
 * <ol>
 * <li>project down (discard some components of the source vector)</li>
 * <li>component permutation</li>
 * <li>component inversion</li>
 * <li>project up (add zero components in the target vector)</li>
 * <li>translation</li>
 * </ol>
 * </p>
 * 
 * <p>
 * The project down and component permutation steps are implemented by the
 * {@link #setComponentMapping(int[]) component mapping}. This is a lookup array
 * that specifies for each target dimension from which source dimension it is
 * taken.
 * <em>Note, that it is not allowed to set this array such that a source component
 * is mapped to several target components!</em>
 * </p>
 * 
 * @author Tobias Pietzsch
 */
public class MixedTransform extends AbstractMixedTransform implements Concatenable< Mixed >, PreConcatenable< Mixed >
{
	/**
	 * dimension of source vector.
	 */
	protected final int numSourceDimensions;

	/**
	 * translation is added to the target vector after applying permutation,
	 * projection, inversion operations.
	 */
	protected final long[] translation;

	/**
	 * for each component of the target vector (before translation). should the
	 * value be taken from a source vector component (false) or should it be
	 * zero (true).
	 */
	protected final boolean[] zero;

	/**
	 * for each component of the target vector (before translation). should the
	 * source vector component be inverted (true).
	 */
	protected final boolean[] invert;

	/**
	 * for each component of the target vector (before translation). from which
	 * source vector component should it be taken.
	 */
	protected final int[] component;

	public MixedTransform( final int sourceDim, final int targetDim )
	{
		super( targetDim );
		this.numSourceDimensions = sourceDim;
		translation = new long[ targetDim ];
		zero = new boolean[ targetDim ];
		invert = new boolean[ targetDim ];
		component = new int[ targetDim ];
		for ( int d = 0; d < targetDim; ++d )
		{
			if ( d < sourceDim )
			{
				component[ d ] = d;
			}
			else
			{
				component[ d ] = 0;
				zero[ d ] = true;
			}
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return numSourceDimensions;
	}

	@Override
	public void getTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			t[ d ] = translation[ d ];
	}

	@Override
	public long getTranslation( final int d )
	{
		assert d <= numTargetDimensions;
		return translation[ d ];
	}

	public void setTranslation( final long[] t )
	{
		assert t.length == numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = t[ d ];
	}

	@Override
	public void getComponentZero( @SuppressWarnings( "hiding" ) final boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			zero[ d ] = this.zero[ d ];
		}
	}

	@Override
	public boolean getComponentZero( final int d )
	{
		assert d <= numTargetDimensions;
		return zero[ d ];
	}

	/**
	 * Set which target dimensions are _not_ taken from source dimensions.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, this will be [false, false, true]
	 * </p>
	 * 
	 * @param zero
	 *            array that says for each component of the target vector
	 *            (before translation) whether the value should be taken from a
	 *            source vector component (false) or should be set to zero
	 *            (true).
	 */
	public void setComponentZero( final boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			this.zero[ d ] = zero[ d ];
		}
	}

	@Override
	public void getComponentMapping( @SuppressWarnings( "hiding" ) final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			component[ d ] = this.component[ d ];
		}
	}

	@Override
	public int getComponentMapping( final int d )
	{
		assert d <= numTargetDimensions;
		return component[ d ];
	}

	/**
	 * Set for each target dimensions from which source dimension it is taken.
	 * 
	 * <p>
	 * For instance, if the transform maps 2D (x,y) coordinates to the first two
	 * components of a 3D (x,y,z) coordinate, this will be [0, 1, x]. Here, x
	 * can be any value because the third target dimension does not correspond
	 * to any source dimension, which can be realized using
	 * {@link #setZero(boolean[])}.
	 * </p>
	 * 
	 * <p>
	 * <em>Note, that it is not allowed to set the {@code component} array such that
	 * a source component is mapped to several target components!</em>
	 * </p>
	 * 
	 * @param component
	 *            array that says for each component of the target vector
	 *            (before translation) from which source vector component it
	 *            should be taken.
	 */
	public void setComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			this.component[ d ] = component[ d ];
		}
	}

	@Override
	public void getComponentInversion( @SuppressWarnings( "hiding" ) final boolean[] invert )
	{
		assert invert.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			invert[ d ] = this.invert[ d ];
		}
	}

	@Override
	public boolean getComponentInversion( final int d )
	{
		assert d <= numTargetDimensions;
		return invert[ d ];
	}

	/**
	 * Set for each target component, whether the source component it is taken
	 * from should be inverted.
	 * 
	 * <p>
	 * For instance, if rotating a 2D (x,y) coordinates by 180 degrees will map
	 * it to (-x,-y). In this case, this will be [true, true].
	 * </p>
	 * 
	 * @param component
	 *            array that says for each component of the target vector
	 *            (before translation) whether the source vector component it is
	 *            taken from should be inverted (true).
	 */
	public void setComponentInversion( final boolean[] invert )
	{
		assert invert.length >= numTargetDimensions;
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			this.invert[ d ] = invert[ d ];
		}
	}

	@Override
	public void apply( long[] source, long[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			target[ d ] = translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source[ component[ d ] ];
				if ( invert[ d ] )
					target[ d ] -= v;
				else
					target[ d ] += v;
			}
		}
	}

	@Override
	public void apply( int[] source, int[] target )
	{
		assert source.length >= numSourceDimensions;
		assert target.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			target[ d ] = ( int ) translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source[ component[ d ] ];
				if ( invert[ d ] )
					target[ d ] -= v;
				else
					target[ d ] += v;
			}
		}
	}

	@Override
	public void apply( Localizable source, Positionable target )
	{
		assert source.numDimensions() >= numSourceDimensions;
		assert target.numDimensions() >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			long pos = translation[ d ];
			if ( !zero[ d ] )
			{
				final long v = source.getLongPosition( component[ d ] );;
				if ( invert[ d ] )
					pos -= v;
				else
					pos += v;
			}
			target.setPosition( pos, d );
		}
	}

	@Override
	public MixedTransform concatenate( Mixed t )
	{
		assert this.numSourceDimensions == t.numTargetDimensions();

		MixedTransform result = new MixedTransform( t.numSourceDimensions(), this.numTargetDimensions );

		for ( int d = 0; d < result.numTargetDimensions; ++d )
		{
			result.translation[ d ] = this.translation[ d ];
			if ( this.zero[ d ] )
			{
				result.zero[ d ] = true;
				result.invert[ d ] = false;
				result.component[ d ] = 0;
			}
			else
			{
				final int c = this.component[ d ];
				final long v = t.getTranslation( c );
				if ( this.invert[ d ] )
					result.translation[ d ] -= v;
				else
					result.translation[ d ] += v;

				if ( t.getComponentZero( c ) )
				{
					result.zero[ d ] = true;
					result.invert[ d ] = false;
					result.component[ d ] = 0;
				}
				else
				{
					result.zero[ d ] = false;
					result.invert[ d ] = ( this.invert[ d ] != t.getComponentInversion( c ) );
					result.component[ d ] = t.getComponentMapping( c );
				}
			}
		}
		return result;
	}

	@Override
	public Class< Mixed > getConcatenableClass()
	{
		return Mixed.class;
	}

	@Override
	public MixedTransform preConcatenate( Mixed t )
	{
		assert t.numSourceDimensions() == this.numTargetDimensions;

		MixedTransform result = new MixedTransform( this.numSourceDimensions, t.numTargetDimensions() );

		for ( int d = 0; d < result.numTargetDimensions; ++d )
		{
			result.translation[ d ] = t.getTranslation( d );
			if ( t.getComponentZero( d ) )
			{
				result.zero[ d ] = true;
				result.invert[ d ] = false;
				result.component[ d ] = 0;
			}
			else
			{
				final int c = t.getComponentMapping( d );
				final long v = this.translation[ c ];
				if ( t.getComponentInversion( d ) )
					result.translation[ d ] -= v;
				else
					result.translation[ d ] += v;

				if ( this.zero[ c ] )
				{
					result.zero[ d ] = true;
					result.invert[ d ] = false;
					result.component[ d ] = 0;
				}
				else
				{
					result.zero[ d ] = false;
					result.invert[ d ] = ( t.getComponentInversion( d ) != this.invert[ c ] );
					result.component[ d ] = this.component[ c ];
				}
			}
		}
		return result;
	}

	@Override
	public Class< Mixed > getPreConcatenableClass()
	{
		return Mixed.class;
	}

	/**
	 * set parameters to <code>transform</code>.
	 * 
	 * @param transform
	 */
	public void set( final Mixed transform )
	{
		assert numSourceDimensions == transform.numSourceDimensions();
		assert numTargetDimensions == transform.numTargetDimensions();

		transform.getTranslation( translation );
		transform.getComponentZero( zero );
		transform.getComponentMapping( component );
		transform.getComponentInversion( invert );
	}

	/**
	 * Get the matrix that transforms homogeneous source points to homogeneous
	 * target points. For testing purposes.
	 */
	@Override
	public double[][] getMatrix()
	{
		double[][] mat = new double[ numTargetDimensions + 1 ][ numSourceDimensions + 1 ];

		mat[ numTargetDimensions ][ numSourceDimensions] = 1;

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			mat[ d ][ numSourceDimensions ] = translation[ d ];
		}

		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			if ( zero[ d ] == false )
			{
				mat[ d ][ component[ d ] ] = invert[ d ] ? -1 : 1 ;
			}
		}

		return mat;
	}

	/**
	 * Check whether the transforms has a full mapping of source to target
	 * components (no source component is discarded).
	 * 
	 * @return whether there is a full mapping of source to target components.
	 */
	public boolean hasFullSourceMapping()
	{
		final boolean[] sourceMapped = new boolean[ numSourceDimensions ];
		for ( int d = 0; d < numTargetDimensions; ++d )
		{
			if ( !zero[ d ] )
			{
				sourceMapped[ component[ d ] ] = true;
			}
		}
		for ( int d = 0; d < numSourceDimensions; ++d )
		{
			if ( !sourceMapped[ d ] ) { return false; }
		}
		return true;
	}
}
