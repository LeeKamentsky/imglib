package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.nearestneighbor.KNearestNeighborSearch;

public class KNearestNeighborSearchOnKDTree< T extends RealLocalizable > implements KNearestNeighborSearch< T >
{
	protected KDTree< T > tree;
	
	protected final int n;
	protected final double[] pos;

	protected final int k;
	protected Node< T >[] bestPoints;
	protected double[] bestSquDistances;

	@SuppressWarnings( "unchecked" )
	public KNearestNeighborSearchOnKDTree( KDTree< T > tree, final int k )
	{
		this.tree = tree;
		this.n = tree.numDimensions();
		this.pos = new double[ n ];
		this.k = k;
		this.bestPoints = new Node[ k ];
		this.bestSquDistances = new double[ k ];
		for ( int i = 0; i < k; ++i )
			bestSquDistances[ i ] = Double.MAX_VALUE;
	}
	
	
	@Override
	public void search( RealLocalizable reference )
	{
		for ( int i = 0; i < k; ++i )
			bestSquDistances[ i ] = Double.MAX_VALUE;
		searchNode( tree.getRoot() );
	}

	protected void searchNode( Node< T > current )
	{
		// consider the current node
		final double squDistance = current.squDistanceTo( pos );
		if ( squDistance < bestSquDistances[ k - 1 ] )
		{
			int i = k - 1;
            for ( int j = i - 1; i > 0 && squDistance < bestSquDistances[ j ]; --i, --j )
            {
            	bestSquDistances[ i ] = bestSquDistances[ j ];
            	bestPoints[ i ] = bestPoints[ j ];
            }
            bestSquDistances[ i ] = squDistance;
            bestPoints[ i ] = current;
		}
		
		final double axisDiff = pos[ current.getSplitDimension() ] - current.getSplitCoordinate();
		final double axisSquDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		final Node< T > nearChild = leftIsNearBranch ? current.left : current.right;
		final Node< T > awayChild = leftIsNearBranch ? current.right : current.left;
		if ( nearChild != null )
			search( nearChild );

	    // search the away branch - maybe
		if ( ( axisSquDistance <= bestSquDistances[ k - 1 ] ) && ( awayChild != null ) )
			search( awayChild );
	}

	@Override
	public Sampler< T > getSampler( int i )
	{
		return bestPoints[ i ];
	}

	@Override
	public RealLocalizable getPosition( int i )
	{
		return bestPoints[ i ];
	}

	@Override
	public double getSquareDistance( int i )
	{
		return bestSquDistances[ i ];
	}

	@Override
	public double getDistance( int i )
	{
		return Math.sqrt( bestSquDistances[ i ] );
	}
}
