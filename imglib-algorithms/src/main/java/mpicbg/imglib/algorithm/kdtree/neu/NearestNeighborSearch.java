package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;


public class NearestNeighborSearch< T extends RealLocalizable > 
{
	Node< T > root;
	
	final int n;
	final double[] pos;

	Node< T > bestPoint;
	double bestSquDistance;
	
	public NearestNeighborSearch( Node< T > root )
	{
		n = root.numDimensions();
		pos = new double[ n ];
		this.root = root;
	}
	
	public Node< T > find( RealLocalizable p )
	{
		p.localize( pos );
		bestSquDistance = Double.MAX_VALUE;
		find( root );
		return bestPoint;
	}
	
	public void find( Node< T > current )
	{
		// consider the current node
		final double distance = current.squDistanceTo( pos );
		if ( distance < bestSquDistance )
		{
			bestSquDistance = distance;
			bestPoint = current;
		}
		
		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		Node< T > nearChild = leftIsNearBranch ? current.left : current.right;
		Node< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			find( nearChild );

	    // search the away branch - maybe
		if ( ( axisSquDistance <= bestSquDistance ) && ( awayChild != null ) )
			find( awayChild );
	}
}
