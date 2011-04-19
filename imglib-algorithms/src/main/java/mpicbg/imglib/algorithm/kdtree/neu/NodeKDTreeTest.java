package mpicbg.imglib.algorithm.kdtree.neu;

import java.util.ArrayList;
import java.util.Random;

import mpicbg.imglib.RealPoint;

public class NodeKDTreeTest
{
	
	protected static boolean testNearestNeighbor( final int numDimensions, final int numPoints, final int numTests, final float min, final float max ) 
	{
		final ArrayList< RealPoint > points = new ArrayList< RealPoint >();
		final Random rnd = new Random(435435435);

		final float[] p = new float[numDimensions];
		
		final float size = (max - min);

		for (int i = 0; i < numPoints; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * size + min;

			final RealPoint t = new RealPoint(p);
			points.add(t);
		}

		long start = System.currentTimeMillis();
		final NodeKDTree< RealPoint > kdTree = new NodeKDTree< RealPoint >(points);
		NearestNeighborSearch< RealPoint, NodeTreeAccess< RealPoint > > kd = new NearestNeighborSearch< RealPoint, NodeTreeAccess< RealPoint > >( new NodeTreeAccess< RealPoint >( kdTree.getRoot() ) );		
		final long kdSetupTime = System.currentTimeMillis() - start;
		System.out.println("kdtree setup took: " + (kdSetupTime) + " ms.");

		start = System.currentTimeMillis();
		final ArrayList< RealPoint > testpoints = new ArrayList< RealPoint >();
		for (int i = 0; i < numTests; ++i) {
			for (int d = 0; d < numDimensions; ++d)
				p[d] = rnd.nextFloat() * 2 * size + min - size / 2;

			final RealPoint t = new RealPoint(p);
			testpoints.add(t);
		}
		
//		for ( RealPoint t : testpoints ) {
//			final RealPoint nnKdtree = kd.find( t ).get();
//			final RealPoint nnExhaustive = findNearestNeighborExhaustive(points, t);
//
//			boolean equal = true;
//			for (int d = 0; d < numDimensions; ++d)
//				if( nnKdtree.getFloatPosition( d ) != nnExhaustive.getFloatPosition( d ) )
//					equal = false;
//			if ( ! equal ) {
//				System.out.println("Nearest neighbor to: " + t);
//				System.out.println("KD-Tree says: " + nnKdtree);
//				System.out.println("Exhaustive says: " + nnExhaustive);
//				return false;
//			}
//		}
//		final long compareTime = System.currentTimeMillis() - start;
//		System.out.println("comparison (kdtree <-> exhaustive) search took: " + (compareTime) + " ms.");

		start = System.currentTimeMillis();
		for ( RealPoint t : testpoints ) {
			final RealPoint nnKdtree = kd.find( t ).get();
			nnKdtree.getClass();
		}
		final long kdTime = System.currentTimeMillis() - start;
		System.out.println("kdtree search took: " + (kdTime) + " ms.");
		System.out.println("kdtree all together took: " + (kdSetupTime+kdTime) + " ms.");

//		start = System.currentTimeMillis();
//		for ( RealPoint t : testpoints ) {
//			final RealPoint nnExhaustive = findNearestNeighborExhaustive(points, t);
//			nnExhaustive.getClass();
//		}
//		final long exhaustiveTime = System.currentTimeMillis() - start;
//		System.out.println("exhaustive search took: " + (exhaustiveTime) + " ms.");

		return true;
	}

	private static RealPoint findNearestNeighborExhaustive(final ArrayList< RealPoint > points, final RealPoint t) {
		float minDistance = Float.MAX_VALUE;
		RealPoint nearest = null;

		final int n = t.numDimensions();
		final float[] tpos = new float[ n ];
		final float[] ppos = new float[ n ];
		t.localize( tpos );
		
		for (final RealPoint p : points) {
			p.localize( ppos );
			float dist = 0;
			for ( int i = 0; i < n; ++i )
				dist += (tpos[i] - ppos[i]) * (tpos[i] - ppos[i]);
			if (dist < minDistance) {
				minDistance = dist;
				nearest = p;
			}
		}

		return nearest;
	}
	
	
	
	
	
	
	public static NodeKDTree< RealPoint > buildNodeKDTree( final int numPoints, final int n )
	{
		Random rnd = new Random( 1234 );
		float[] pos = new float[ n ];

		ArrayList< RealPoint > points = new ArrayList< RealPoint >();
		for ( int i = 0; i < numPoints; ++i )
		{
			for ( int d = 0; d < n; ++d )
				pos[ d ] = rnd.nextFloat();
			points.add( new RealPoint( pos ) );
		}
		
		NodeKDTree< RealPoint > tree = new NodeKDTree< RealPoint >( points );
		return tree;
	}
	
	public static void testNN( NodeKDTree< RealPoint > tree )
	{
		NearestNeighborSearch< RealPoint, NodeTreeAccess< RealPoint > > nn = new NearestNeighborSearch< RealPoint, NodeTreeAccess< RealPoint > >( new NodeTreeAccess< RealPoint >( tree.getRoot() ) );
		NodeTreeAccess< RealPoint > result = nn.find( new RealPoint( new float[] {0,0,0} ) );
		System.out.println( result.get() );
	}

	public static void main( String[] args )
	{
//		NodeKDTree< RealPoint > tree = buildNodeKDTree( 7, 3 );
//		System.out.println( tree.toString() );
//		testNN( tree );

		if (testNearestNeighbor(3, 100000, 10000, -5, 5))
			System.out.println("Nearest neighbor test successfull\n");
		if (testNearestNeighbor(3, 100000, 10000, -5, 5))
			System.out.println("Nearest neighbor test successfull\n");
		if (testNearestNeighbor(3, 100000, 10000, -5, 5))
			System.out.println("Nearest neighbor test successfull\n");
		if (testNearestNeighbor(3, 100000, 10000, -5, 5))
			System.out.println("Nearest neighbor test successfull\n");
	}
}
