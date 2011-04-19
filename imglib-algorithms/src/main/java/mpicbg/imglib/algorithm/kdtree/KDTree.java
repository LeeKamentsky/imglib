/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Johannes Schindelin and Stephan Preibisch
 */
package mpicbg.imglib.algorithm.kdtree;

import static mpicbg.imglib.util.Partition.partitionSubList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import mpicbg.imglib.algorithm.kdtree.node.Leaf;
import mpicbg.imglib.algorithm.kdtree.node.Node;
import mpicbg.imglib.algorithm.kdtree.node.NonLeaf;
import mpicbg.imglib.algorithm.kdtree.node.SimpleNode;
import mpicbg.imglib.util.KthElement;

public class KDTree<T extends Leaf<T>>
{
	/*
	 * Use only a subset of at most medianLength semi-randomly picked
	 * values to determine the splitting point.
	 */
	final protected int medianLength;

	final protected int dimension;
	final protected Node<T> root;

	public static boolean debug = false;

	protected ArrayList<T> duplicates = new ArrayList<T>();

	/**
	 * Construct a KDTree from the elements in the given list.
	 *
	 * The elements must implement the interface Leaf.
	 *
	 * The parameter 'leaves' must be a list and cannot be an iterator,
	 * as the median needs to be calculated (or estimated, if the length
	 * is greater than medianLength).
	 */
	public KDTree(final List<T> leaves) {
		this( leaves, 100000 );
	}

	public KDTree(final List<T> leaves, final int maxMedianLength) {
		this.medianLength = maxMedianLength;
		this.dimension = leaves.get( 0 ).getNumDimensions();

		// test that dimensionality is preserved
		int i = 0;
		for (final T leaf : leaves) {
			if (leaf.getNumDimensions() != dimension)
				throw new RuntimeException("Dimensionality of nodes is not preserved, first entry has dimensionality " + dimension + " entry " + i + " has dimensionality " + leaf.getNumDimensions() );

			++i;
		}

//		DummyNode d = new DummyNode();
//		DummyNode e = new DummyNode();
//		doSomething( d, e );
//
//		DummyNode2 d2 = new DummyNode2();
//		DummyNode2 e2 = new DummyNode2();
//		LeafComparator< DummyNode2 > c2 = new LeafComparator< DummyNode2 >( 0 );
//		if ( c2.compare( d2, e2 ) > 0 )
//			System.out.println(""); 

		root = makeNode(leaves, 0, leaves.size()-1, 0);
	}
	
//	public void doSomething( DummyNode d, DummyNode e )
//	{
//		LeafComparator< DummyNode > c = new LeafComparator< DummyNode >( 0 );
//		if ( c.compare( d, e ) > 0 )
//			System.out.println(""); 		
//	}

	public static < T extends Leaf<T> > int partitionSubList( int i, int j, final List< T > values, final int dim )
	{
		final int pivotIndex = j;
		T pivot = values.get( j-- );
		final float pivotDim = pivot.get( dim );

		A: while ( true )
		{
			// move i forward while < pivot (and not at j)
			while ( i <= j )
			{
				T ti = values.get( i );
				if ( ti.get( dim ) >= pivotDim )
					break;
				++i;
			}
			// now [i] is the place where the next value < pivot is to be
			// inserted

			if ( i > j )
				break;

			// move j backward while >= pivot (and not at i)
			while ( true )
			{
				T tj = values.get( j );
				if ( tj.get( dim ) < pivotDim )
				{
					// swap [j] with [i]
					T tmp = values.get( i );
					values.set( i, values.get( j ) );
					values.set( j, tmp );
					++i;
					--j;
					break;
				}
				else if ( j == i )
				{
					break A;
				}
				--j;
			}
		}

		// we are done. put the pivot element here.
		// check whether the element at iLastIndex is <
		if ( i != pivotIndex )
		{
			values.set( pivotIndex, values.get( i ) );
			values.set( i, pivot );
		}
		return i;
	}

	public static < T extends Leaf<T> > void kthElement( int i, int j, final int k, final List< T > values, final int dim )
	{
		while ( true )
		{
			int pivotpos = partitionSubList( i, j, values, dim );
			if ( pivotpos > k )
			{
				// partition lower half
				j = pivotpos - 1;
			}
			else if ( pivotpos < k )
			{
				// partition upper half
				i = pivotpos + 1;
			}
			else
				return;
		}
	}

	protected Node<T> makeNode(final List<T> leaves, final int i, final int j, final int depth) {
		final int length = (j - i) + 1;

		if (length == 0)
			return null;

		if (length == 1)
			return leaves.get( i );

		final int k = (depth % dimension);
		final int medianpos = i + (j - i) / 2;
		kthElement( i, j, medianpos, leaves, k );
//		KthElement.kthElement( i, j, medianpos, leaves, new LeafComparator< T >( k ) );
		final float median = leaves.get( medianpos ).get( k );

		return new NonLeaf<T>(median, dimension, makeNode(leaves, i, medianpos, depth + 1), makeNode(leaves, medianpos+1, j, depth + 1));

//		final List<T> left = leaves.subList( 0, medianpos+1 );
//		final List<T> right = leaves.subList( medianpos+1, length );

		/*
		 * This fails for the following example:
		 *
		 * P1( 1, 1, 0 )
		 * P2( 0, 1, 1 )
		 * P3( 1, 0, 1 )
		 *
		 * k = 0
		 * ( 1; 0; 1 ) median = 1, all are <= 1
		 *
		 * k = 1
		 * ( 1; 1; 0 ) median = 1, all are <= 1
		 *
		 * k = 2
		 * ( 0; 1; 1 ) median = 1, all are <= 1
		 *
		 * That's why added the check for "leaf.get(k) < median"
		 */

		/*
		if (right.size() == 0) {
			if (allIdentical(left)) {
				final T result = leaves.get(0);
				left.remove(0);
				duplicates.addAll(left);
				return result;
			}
			else {
				left.clear();
				right.clear();

				for (int i = 0; i < length; i++) {
					final T leaf = leaves.get(i);
					if (leaf.get(k) < median)
						left.add(leaf);
					else
						right.add(leaf);
				}

			}

		}
		*/

//		return new NonLeaf<T>(median, dimension, makeNode(left, depth + 1), makeNode(right, depth + 1));
	}

	protected boolean allIdentical(final List<T> list) {
		T first = null;
		for (final T leaf : list) {
			if (first == null)
				first = leaf;
			else {
				final T next = leaf;

				for (int j = 0; j < dimension; j++)
					if (next.get(j) != first.get(j))
						return false;
			}
		}
		return true;
	}

	public ArrayList<T> getDuplicates() {
		return duplicates;
	}

	public boolean hasDuplicates() {
		return duplicates.size() > 0;
	}

	protected static class LeafComparator< T extends Leaf< T > > implements Comparator< T >
	{
		final int k;

		public LeafComparator( final int k )
		{
			this.k = k;
		}

		@Override
		public int compare( T o1, T o2 )
		{
			final float d = o1.get(k) - o2.get(k);
			return ( d < 0 ) ? -1 : ( d > 0 ? 1 : 0);
		}
	}

	// TODO: weg damit
	protected float median(final List<T> leaves, final int k) {
		float[] list;
		if (leaves.size() <= medianLength) {
			list = new float[leaves.size()];
			for (int i = 0; i < list.length; i++) {
				T leaf = leaves.get(i);
				list[i] = leaf.get(k);
			}
		}
		else {
			list = new float[medianLength];
			Random random = new Random();
			for (int i = 0; i < list.length; i++) {
				int index = Math.abs(random.nextInt()) % list.length;
				T leaf = leaves.get(index);
				list[i] = leaf.get(k);
			}
		}

		Arrays.sort(list);

		return (list.length & 1) == 1 ? list[list.length / 2] :	(list[list.length / 2] + list[list.length / 2 - 1]) / 2;
	}

	public Node<T> getRoot() {
		return root;
	}

	public int getDimension() {
		return dimension;
	}

	public String toString(Node<T> node, String indent) {
		if (node == null)
			return indent + "null";
		if ( Leaf.class.isInstance(node) )
			return indent + node.toString();
		NonLeaf<T> nonLeaf = (NonLeaf<T>)node;
		return toString(nonLeaf.left, indent + "\t") + "\n"
			+ indent + nonLeaf.coordinate + "\n"
			+ toString(nonLeaf.right, indent + "\t") + "\n";
	}

	public String toString() {
		return toString(root, "");
	}

}
