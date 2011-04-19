package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;

public final class NodeTreeAccess< T extends RealLocalizable > implements KDTreeAccess< T, NodeTreeAccess< T > >
{
	protected Node< T > node;
	
	protected final int n;

	public NodeTreeAccess( Node< T > node )
	{
		this.node = node;
		n = node.numDimensions();
	}

	protected NodeTreeAccess( final NodeTreeAccess< T > treeAccess )
	{
		this.node = treeAccess.node;
		this.n = treeAccess.n;
	}

	@Override
	public void localize( float[] position )
	{
		node.localize( position );
	}

	@Override
	public void localize( double[] position )
	{
		node.localize( position );
	}

	@Override
	public float getFloatPosition( int d )
	{
		return node.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( int d )
	{
		return node.getDoublePosition( d );
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

	@Override
	public T get()
	{
		return node.get();
	}

	@Deprecated
	@Override
	public T getType()
	{
		return get();
	}

	@Override
	public boolean hasParent()
	{
		return node.parent != null;
	}

	@Override
	public void parent()
	{
		assert node.parent != null;
		node = node.parent;
	}

	@Override
	public boolean hasLeft()
	{
		return node.left != null;
	}

	@Override
	public void left()
	{
		assert node.left != null;
		node = node.left;
	}

	@Override
	public boolean hasRight()
	{
		return node.right != null;
	}

	@Override
	public void right()
	{
		assert node.right != null;
		node = node.right;
	}

	@Override
	public NodeTreeAccess< T > copy()
	{
		return new NodeTreeAccess< T >( this );
	}

	@Override
	public void storePosition( NodeTreeAccess< T > ref )
	{
		ref.node = this.node;
		
	}

	@Override
	public void setToPosition( NodeTreeAccess< T > ref )
	{
		this.node = ref.node;
	}

	@Override
	public int getSplitDimension()
	{
		return node.getSplitDimension();
	}

	@Override
	public float getSplitCoordinate()
	{
		return node.getSplitCoordinate();
	}

	@Override
	public NodeTreeAccess< T >[] createArray( final int size )
	{
		@SuppressWarnings( "unchecked" )
		NodeTreeAccess< T >[] array = new NodeTreeAccess[ size ];
		for ( int i = 0; i < size; ++i )
			array[ i ] = copy();
		return array;
	}
}
