package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;


public class NearestNeighborSearch< T, K extends KDTreeAccess< T, K > > 
{
	K[] treeAccessStack;
	
	final int n;
	final float[] pos;
	final float[] tmp;

	K bestPoint;
	float bestDistance;
	
	public NearestNeighborSearch( K treeAccess )
	{
		final int stackDepth = 32;
		treeAccessStack = treeAccess.createArray( stackDepth );
		bestPoint = treeAccess.copy();
		n = treeAccess.numDimensions();
		pos = new float[n];
		tmp = new float[n];		
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
	
	public K find( RealLocalizable p )
	{
		p.localize( pos );
		bestDistance = Float.MAX_VALUE;
		find( 0 );
		return bestPoint;
	}
	
	public void find( int depth )
	{
		K current = treeAccessStack[ depth ];
		K child = treeAccessStack[ depth + 1 ];
		
		// consider the current node
		current.localize( tmp );
		final float distance = squDist( pos, tmp );
		if ( distance < bestDistance )
		{
			bestDistance = distance;
			bestPoint.setToPosition( current );
		}
		
		final int splitDim = current.getSplitDimension();
		final float axisDiff = pos[ splitDim ] - current.getSplitCoordinate();
		final float axisDistance = axisDiff * axisDiff;
		final boolean leftIsNearBranch = axisDiff < 0;

		// search the near branch
		child.setToPosition( current );
		if( leftIsNearBranch )
		{
			if ( child.hasLeft() )
			{
				child.left();
				find( depth + 1 );
			}
		}
		else
		{
			if ( child.hasRight() )
			{
				child.right();
				find( depth + 1 );
			}
		}
		
	    // search the away branch - maybe
		if ( axisDistance <= bestDistance )
		{
			child.setToPosition( current );
			if( leftIsNearBranch )
			{
				if ( child.hasRight() )
				{
					child.right();
					find( depth + 1 );
				}
			}
			else
			{
				if ( child.hasLeft() )
				{
					child.left();
					find( depth + 1 );
				}
			}
		}
	}
}
