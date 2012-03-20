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
 * @author Lee Kamentsky
 * @modified Christian Dietz, Martin Horn
 *
 */
package net.imglib2.labeling;

import java.util.Collection;

import net.imglib2.AbstractInterval;
import net.imglib2.img.AbstractImg;
import net.imglib2.roi.IterableRegionOfInterest;
import net.imglib2.roi.RegionOfInterest;

/**
 * A labeling represents the assignment of zero or more labels to the pixels in
 * a space.
 *
 * @author Lee Kamentsky
 *
 * @param <T>
 *            - the type used to label the pixels, for instance string names for
 *            user-assigned object labels or integers for machine-labeled
 *            images.
 */
public abstract class AbstractLabeling< T extends Comparable< T >> extends AbstractInterval implements Labeling< T >
{

	protected LabelingROIStrategy< T, ? extends Labeling< T >> strategy;

	protected long size;

	protected AbstractLabeling( final long[] size, final LabelingROIStrategyFactory< T > factory )
	{
		super( size );
		this.size = AbstractImg.numElements( size );
		this.strategy = factory.createLabelingROIStrategy( this );
	}

	@Override
	public long size()
	{
		return size;
	}

	/**
	 * Use an alternative strategy for making labeling cursors.
	 *
	 * @param strategy
	 *            - a strategy for making labeling cursors.
	 */
	public void setLabelingCursorStrategy( final LabelingROIStrategy< T, ? extends Labeling< T >> strategy )
	{
		this.strategy = strategy;
	}

	// /* (non-Javadoc)
	// * @see
	// net.imglib2.IterableRealInterval#equalIterationOrder(net.imglib2.IterableRealInterval)
	// */
	// @Override
	// public boolean equalIterationOrder(IterableRealInterval<?> f) {
	// return false;
	// }

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.imglib2.labeling.Labeling#getRegionOfInterest(java.lang.Comparable)
	 */
	@Override
	public RegionOfInterest getRegionOfInterest( final T label )
	{
		return strategy.createRegionOfInterest( label );
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see net.imglib2.labeling.Labeling#getIterableRegionOfInterest(java.lang.
	 * Comparable)
	 */
	@Override
	public IterableRegionOfInterest getIterableRegionOfInterest( final T label )
	{
		return strategy.createIterableRegionOfInterest( label );
	}

	/**
	 * find the coordinates of the bounding box around the given label. The the
	 * minimum extents are inclusive (there will be pixels at the coordinates of
	 * the minimum extents) and the maximum extents are exclusive(all pixels
	 * will have coordinates less than the maximum extents)
	 *
	 * @param label
	 *            - find pixels with this label
	 * @return true if some pixels are labeled, false if none have the label
	 */
	@Override
	public boolean getExtents( final T label, final long[] minExtents, final long[] maxExtents )
	{
		return strategy.getExtents( label, minExtents, maxExtents );
	}

	/**
	 * Find the first pixel in a raster scan of the object with the given label.
	 *
	 * @param label
	 * @param start
	 * @return
	 */
	@Override
	public boolean getRasterStart( final T label, final long[] start )
	{
		return strategy.getRasterStart( label, start );
	}

	/**
	 * Return the area or suitable N-d analog of the labeled object
	 *
	 * @param label
	 *            - label for object in question
	 * @return area in units of pixel / voxel / etc.
	 */
	@Override
	public long getArea( final T label )
	{
		return strategy.getArea( label );
	}

	/**
	 * Find all labels in the space
	 *
	 * @return a collection of the labels.
	 */
	@Override
	public Collection< T > getLabels()
	{
		return strategy.getLabels();
	}

}
