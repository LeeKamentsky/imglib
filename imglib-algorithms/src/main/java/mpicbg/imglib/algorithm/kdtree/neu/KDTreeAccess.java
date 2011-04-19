package mpicbg.imglib.algorithm.kdtree.neu;

import mpicbg.imglib.RealLocalizable;
import mpicbg.imglib.Sampler;

public interface KDTreeAccess< T, K extends KDTreeAccess< T, K > > extends RealLocalizable, Sampler< T >
{
	public boolean hasParent();

	public void parent();

	public boolean hasLeft();

	public void left();

	public boolean hasRight();

	public void right();

	/**
	 * Get dimension in which this node splits the tree.
	 * 
	 * @return dimension in which this node splits the tree.
	 */
	public int getSplitDimension();

	/**
	 * Get coordinate in dimension {@link #getSplitDimension()} at which this
	 * node splits the tree. Nodes in the left subtree have coordinates smaller
	 * or equal. Nodes in the right subtree have coordinates larger or equal.
	 * 
	 * @return coordinate at which this node splits the tree.
	 */
	public float getSplitCoordinate();

	@Override
	public K copy();

	public K[] createArray( final int size );

	public void storePosition( final K ref );

	public void setToPosition( final K ref );
}
