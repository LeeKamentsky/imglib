package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.Sampler;

public final class Node< T extends RealLocalizable > implements RealLocalizable, Sampler< T >
{
	protected final T value;
	
	protected final int dimension;
	
	protected final float coordinate;
	
	Node< T > parent;
	
	protected final Node< T > left;
	
	protected final Node< T > right;
	
	public Node( T value, int dimension, final Node< T > left, final Node< T > right ) 
	{
		this.value = value;
		this.dimension = dimension;
		this.coordinate = value.getFloatPosition( dimension );
		this.parent = null;
		this.left = left;
		this.right = right;
	}
	
	public int getSplitDimension()
	{
		return dimension;
	}
	
	public float getSplitCoordinate()
	{
		return coordinate;
	}

	@Override
	public int numDimensions()
	{
		return value.numDimensions();
	}

	@Override
	public T get()
	{
		return value;
	}

	@Deprecated
	@Override
	public T getType()
	{
		return get();
	}

	@Override
	public void localize( float[] position )
	{
		value.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		value.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return value.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return value.getDoublePosition( d );
	}
}
