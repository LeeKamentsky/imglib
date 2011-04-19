package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.Sampler;

public final class Node< T extends RealLocalizable > implements RealLocalizable, Sampler< T >
{
	protected final int n;

	protected final double[] pos;

	protected final T value;
	
	protected final int splitDimension;
	
	protected final Node< T > left;
	
	protected final Node< T > right;
	
	public Node( T value, int dimension, final Node< T > left, final Node< T > right ) 
	{
		this.n = value.numDimensions();
		this.pos = new double[n];
		value.localize( pos );
		this.value = value;
		this.splitDimension = dimension;
		this.left = left;
		this.right = right;
	}
	
	protected Node( final Node< T > node ) 
	{
		this.n = node.n;
		this.pos = node.pos.clone();
		this.value = node.value;
		this.splitDimension = node.splitDimension;
		this.left = node.left;
		this.right = node.right;
	}
	
	public int getSplitDimension()
	{
		return splitDimension;
	}
	
	public double getSplitCoordinate()
	{
		return pos[ splitDimension ];
	}

	@Override
	public int numDimensions()
	{
		return n;
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
		for ( int d = 0; d < n; ++d )
			position[ d ] = ( float ) pos[ d ];
	}

	@Override
	public void localize( double[] position )
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = pos[ d ];
	}

	@Override
	public float getFloatPosition( int d )
	{
		return ( float ) pos[ d ];
	}

	@Override
	public double getDoublePosition( int d )
	{
		return pos[ d ];
	}

	@Override
	public Node< T > copy()
	{
		return new Node< T >( this );
	}
	
	@Override
	public String toString()
	{
		return "node " + getSplitDimension() + " ? " + getSplitCoordinate() + " | " + value;
	}
	
	public float squDistanceTo( final float[] p )
	{
		float sum = 0;
		for ( int d = 0; d < n; ++d ) 
		{
			sum += ( pos[d] - p[d] ) * ( pos[d] - p[d] ); 
		}
		return sum;
	}

	public double squDistanceTo( final double[] p )
	{
		double sum = 0;
		for ( int d = 0; d < n; ++d ) 
		{
			sum += ( pos[d] - p[d] ) * ( pos[d] - p[d] ); 
		}
		return sum;
	}
}
