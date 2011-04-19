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

	public K createPosition();

	public void storePosition( final K ref );

	public void setToPosition( final K ref );
}
