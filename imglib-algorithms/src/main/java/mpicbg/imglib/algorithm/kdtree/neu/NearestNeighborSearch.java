package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;


public class NearestNeighborSearch< T extends RealLocalizable > 
{
	Node< T > root;
	
	final int n;
	final float[] pos;
	final float[] tmp;

	Node< T > bestPoint;
	float bestDistance;
	
	public NearestNeighborSearch( Node< T > root )
	{
		n = root.numDimensions();
		pos = new float[n];
		tmp = new float[n];
		this.root = root;
	}
	
	protected static final float squDist( final float[] p1, final float[] p2 )
	{
		float sum = 0;
		for ( int d = 0; d < p1.length; ++d )
		{
			final float diff = p1[d] - p2[d];
			sum += diff*diff; 
		}
		return sum;
	}
	
	public Node< T > find( RealLocalizable p )
	{
		p.localize( pos );
		bestDistance = Float.MAX_VALUE;
		find( root );
		return bestPoint;
	}
	
	public void find( Node< T > current )
	{
		// consider the current node
		current.localize( tmp );
		final float distance = squDist( pos, tmp );
		if ( distance < bestDistance )
		{
			bestDistance = distance;
			bestPoint = current;
		}
		
		final int splitDim = current.getSplitDimension();
		final float axisDiff = pos[ splitDim ] - current.getSplitCoordinate();
		final float axisDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		Node< T > nearChild = leftIsNearBranch ? current.left : current.right;
		Node< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			find( nearChild );

	    // search the away branch - maybe
		if ( ( axisDistance <= bestDistance ) && ( awayChild != null ) )
			find( awayChild );
	}
}
