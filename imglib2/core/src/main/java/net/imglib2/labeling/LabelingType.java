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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * The LabelingType represents a labeling of a pixel with zero or more labelings
 * of type T. Each labeling names a distinct object in the image space.
 * 
 * @param <L>
 *            the desired type of the pixel labels, for instance Integer to
 *            number objects or String for user-assigned label names
 */
public class LabelingType< L extends Comparable< L >> implements Type< LabelingType< L >>
{
	final protected long[] generation;

	protected final LabelingMapping< L > mapping;

	protected final IntegerType< ? > type;

	/**
	 * Constructor for mirroring state with another labeling
	 * 
	 * @param storage
	 * @param mapping
	 * @param generation
	 */
	protected LabelingType( IntegerType< ? > type, LabelingMapping< L > mapping, long[] generation )
	{
		this.type = type;
		this.mapping = mapping;
		this.generation = generation;
	}

	// this is the constructor if you want it to read from an array
	public LabelingType( IntegerType< ? > type, LabelingMapping< L > mapping )
	{
		this.type = type;
		this.mapping = mapping;
		generation = new long[ 1 ];
	}

	// this is the constructor if you want it to be a variable
	public LabelingType( List< L > value )
	{

		mapping = new LabelingMapping< L >( new IntType() );
		generation = new long[ 1 ];

		this.type = new IntType();
		setLabeling( value );
	}

	@SuppressWarnings( "unchecked" )
	public LabelingType( L value )
	{
		this( Arrays.asList( value ) );
	}

	// this is the constructor if you want it to be a variable
	public LabelingType()
	{
		this( new ArrayList< L >() );
	}

	/**
	 * Get the labels defined at the type's current pixel or
	 * 
	 * @return a list of the labelings at the current location.
	 */
	public final List< L > getLabeling()
	{
		return mapping.listAtIndex( type.getInteger() );
	}

	/**
	 * Set the labeling at the current pixel
	 * 
	 * @param labeling
	 */
	public void setLabeling( List< L > labeling )
	{
		this.type.setInteger( mapping.indexOf( labeling ) );
		synchronized ( generation )
		{
			generation[ 0 ]++;
		}
	}

	public void setLabeling( L[] labeling )
	{
		setLabeling( Arrays.asList( labeling ) );
	}

	/**
	 * Assign a pixel a single label
	 * 
	 * @param label
	 *            - the label to assign
	 */
	public void setLabel( L label )
	{
		List< L > labeling = new ArrayList< L >( 1 );
		labeling.add( label );
		setLabeling( labeling );
	}

	/**
	 * This method returns the canonical object for the given labeling.
	 * SetLabeling will work faster if you pass it the interned object instead
	 * of one created by you.
	 * 
	 * @param labeling
	 * @return
	 */
	public List< L > intern( List< L > labeling )
	{
		return mapping.intern( labeling );
	}

	/**
	 * Return the canonical labeling object representing the single labeling.
	 * SetLabeling will work faster if you use this object.
	 * 
	 * @param label
	 *            - a label for a pixel.
	 * @return - the canonical labeling with the single label.
	 */
	public List< L > intern( L label )
	{
		List< L > labeling = new ArrayList< L >( 1 );
		labeling.add( label );
		return intern( labeling );
	}

	// @Override
	// public void updateContainer(Object o) {
	// b = storage.update(o);
	// }

	@Override
	public LabelingType< L > createVariable()
	{
		return new LabelingType< L >();
	}

	@Override
	public LabelingType< L > copy()
	{
		return new LabelingType< L >( getLabeling() );
	}

	@Override
	public void set( LabelingType< L > c )
	{
		setLabeling( c.getLabeling() );
	}

	@Override
	public String toString()
	{
		return getLabeling().toString();
	}

	/**
	 * Get the labels known by the type
	 * 
	 * @return a list of all labels in the type's associated storage
	 */
	List< L > getLabels()
	{
		return mapping.getLabels();
	}

	/**
	 * The underlying storage has an associated generation which is incremented
	 * every time the storage is modified. For cacheing, it's often convenient
	 * or necessary to know whether the storage has changed to know when the
	 * cache is invalid. The strategy is to save the generation number at the
	 * time of cacheing and invalidate the cache if the number doesn't match.
	 * 
	 * @return the generation of the underlying storage
	 */
	long getGeneration()
	{
		return generation[ 0 ];
	}

	public LabelingMapping< L > getMapping()
	{
		return mapping;
	}

}
