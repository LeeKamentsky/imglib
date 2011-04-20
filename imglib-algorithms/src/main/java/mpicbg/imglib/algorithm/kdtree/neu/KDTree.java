package mpicbg.imglib.algorithm.kdtree.neu;

import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import mpicbg.imglib.EuclideanSpace;
import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.util.KthElement;

public class KDTree< T extends RealLocalizable > implements EuclideanSpace //TODO: , IterableRealInterval< T >
{
	/**
	 * the number of dimensions.
	 */
	final protected int n;

	final protected Node< T > root;

	/**
	 * Construct a KDTree from the elements in the given list.
	 */
	public KDTree( final List< T > elements )
	{
		this.n = elements.get( 0 ).numDimensions();

		// test that dimensionality is preserved
		assert( verifyDimensions( elements, n ) );

		if ( elements instanceof java.util.RandomAccess )
			root = makeNode( elements, 0, elements.size() - 1, 0 );
		else
			root = makeNode( elements.listIterator(), elements.listIterator( elements.size() ), 0 );
	}

	protected static <T extends RealLocalizable > boolean verifyDimensions( final List< T > elements, final int n )
	{
		for ( final T element : elements )
			if ( element.numDimensions() != n )
				return false;
		return true;
	}

	public static final class DimComparator< T extends RealLocalizable > implements Comparator< T >
	{
		final int d;
		public DimComparator( int d )
		{
			this.d = d;
		}

		@Override
		public int compare( T o1, T o2 )
		{
			final float diff = o1.getFloatPosition( d ) - o2.getFloatPosition( d );
			return ( diff < 0 ) ? -1 : ( diff > 0 ? 1 : 0);
		}		
	}
	
	protected Node< T > makeNode(final List<T> elements, final int i, final int j, final int d )
	{
		if ( j > i ) {
			final int k = i + (j - i) / 2;
			KthElement.kthElement( i, j, k, elements, new DimComparator< T >( d ) );
	
			final int dChild = ( d + 1 == n ) ? 0 : d + 1;
			return new Node< T >( elements.get( k ), elements.get( k ), d, makeNode( elements, i, k - 1, dChild ), makeNode( elements, k + 1, j, dChild ) );
		}
		else if ( j == i )
		{
			return new Node< T >( elements.get( i ), elements.get( i ), d, null, null );
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

	public String toString( Node< T > node, String indent )
	{
		if ( node == null )
			return "";

		return indent + "- " + node.toString() + "\n"
			+ toString( node.left, indent + "  " )
			+ toString( node.right, indent + "  " );
	}

	public String toString()
	{
		return toString( root, "" );
	}
}
