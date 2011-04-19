package mpicbg.imglib.algorithm.kdtree.neu;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.util.KthElement;

public class NodeKDTree< T extends RealLocalizable > implements EuclideanSpace //TODO: , IterableRealInterval< T >
{
	/**
	 * the number of dimensions.
	 */
	final protected int n;

	final protected Node< T > root;

	/**
	 * Construct a KDTree from the elements in the given list.
	 * 
	 * The parameter 'leaves' must be a list and cannot be an iterator, as the
	 * median needs to be calculated (or estimated, if the length is greater
	 * than medianLength).
	 */
	public NodeKDTree( final List< T > leaves )
	{
		this.n = leaves.get( 0 ).numDimensions();

		// test that dimensionality is preserved
		assert( verifyDimensions( leaves, n ) );

		if ( leaves instanceof java.util.RandomAccess )
			root = makeNode( leaves, 0, leaves.size() - 1, 0 );
		else
			root = makeNode( leaves.listIterator(), leaves.listIterator( leaves.size() ), 0 );
	}

	protected static <T extends RealLocalizable > boolean verifyDimensions( final List< T > leaves, final int n )
	{
		for ( final T leaf : leaves )
			if ( leaf.numDimensions() != n )
				return false;
		return true;
	}
	
	protected Node< T > makeNode(final List<T> leaves, final int i, final int j, final int d )
	{
		if ( j > i ) {
			final int k = i + (j - i) / 2;
			KthElement.kthElement( i, j, k, leaves, new Comparator< T >() {
				@Override
				public int compare( T o1, T o2 )
				{
					final float diff = o1.getFloatPosition( d ) - o2.getFloatPosition( d );
					return ( diff < 0 ) ? -1 : ( diff > 0 ? 1 : 0);
				}		
			} );
	
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new Node< T >( leaves.get( k ), d, makeNode( leaves, i, k - 1, dChild ), makeNode( leaves, k + 1, j, dChild ) );
		}
		else if ( j == i )
		{
			return new Node< T >( leaves.get( i ), d, null, null );
		}
		else
		{
			return null;
		}
	}

	protected Node< T > makeNode( final ListIterator< T > first, final ListIterator< T > last, final int depth )
	{
		// TODO
		return null;
	}

	public Node< T > getRoot()
	{
		return root;
	}

	@Override
	public int numDimensions()
	{
		return n;
	}

//	public String toString( Node< T > node, String indent )
//	{
//		if ( node == null )
//			return indent + "null";
//		if ( Leaf.class.isInstance( node ) )
//			return indent + node.toString();
//		NonLeaf< T > nonLeaf = ( NonLeaf< T > ) node;
//		return toString( nonLeaf.left, indent + "\t" ) + "\n" + indent + nonLeaf.coordinate + "\n" + toString( nonLeaf.right, indent + "\t" ) + "\n";
//	}
//
//	public String toString()
//	{
//		return toString( root, "" );
//	}
}
