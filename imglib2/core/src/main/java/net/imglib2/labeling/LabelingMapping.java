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
 * @modifier Christian Dietz, Martin Horn
 *
 */
package net.imglib2.labeling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import net.imglib2.type.numeric.IntegerType;

/**
 * The LabelingMapping maps a set of labelings of a pixel to an index value
 * which can be more compactly stored than the set of labelings. The service it
 * provides is an "intern" function that supplies a canonical object for each
 * set of labelings in a container.
 * 
 * For example, say pixels are labeled with strings and a particular pixel is
 * labeled as belonging to both "Foo" and "Bar" and this is the first label
 * assigned to the container. The caller will ask for the index of { "Foo",
 * "Bar" } and get back the number, "1". LabelingMapping will work faster if the
 * caller first interns { "Foo", "Bar" } and then requests the mapping of the
 * returned object.
 * 
 * @param <L>
 * @param <N>
 */
public class LabelingMapping< L extends Comparable< L >>
{
	final List< L > theEmptyList;

	private int maxNumLabels;

	public LabelingMapping( IntegerType< ? > value )
	{
		maxNumLabels = ( int ) value.getMaxValue();

		List< L > background = Collections.emptyList();
		theEmptyList = intern( background );
	}

	private static class InternedList< L1 extends Comparable< L1 >> implements List< L1 >
	{
		private final List< L1 > value;

		final int index;

		final LabelingMapping< L1 > owner;

		// final LabelingMapping<L1> owner;

		public InternedList( List< L1 > src, int index, LabelingMapping< L1 > owner )
		{
			this.owner = owner;
			this.value = Collections.unmodifiableList( src );
			this.index = index;
		}

		@Override
		public int size()
		{
			return value.size();
		}

		@Override
		public boolean isEmpty()
		{
			return value.isEmpty();
		}

		@Override
		public boolean contains( Object o )
		{
			return value.contains( o );
		}

		@Override
		public Iterator< L1 > iterator()
		{
			return value.iterator();
		}

		@Override
		public Object[] toArray()
		{
			return value.toArray();
		}

		@Override
		public boolean add( L1 e )
		{
			return value.add( e );
		}

		@Override
		public boolean remove( Object o )
		{
			return value.remove( o );
		}

		@Override
		public boolean containsAll( Collection< ? > c )
		{
			return value.containsAll( c );
		}

		@Override
		public boolean addAll( Collection< ? extends L1 > c )
		{
			return value.addAll( c );
		}

		@Override
		public boolean addAll( int index, Collection< ? extends L1 > c )
		{
			return value.addAll( index, c );
		}

		@Override
		public boolean removeAll( Collection< ? > c )
		{
			return value.removeAll( c );
		}

		@Override
		public boolean retainAll( Collection< ? > c )
		{
			return value.retainAll( c );
		}

		@Override
		public void clear()
		{
			value.clear();
		}

		@Override
		public L1 get( int index )
		{
			return value.get( index );
		}

		@Override
		public L1 set( int index, L1 element )
		{
			return value.set( index, element );
		}

		@Override
		public void add( int index, L1 element )
		{
			value.add( index, element );
		}

		@Override
		public L1 remove( int index )
		{
			return value.remove( index );
		}

		@Override
		public int indexOf( Object o )
		{
			return value.indexOf( o );
		}

		@Override
		public int lastIndexOf( Object o )
		{
			return value.lastIndexOf( o );
		}

		@Override
		public ListIterator< L1 > listIterator()
		{
			return value.listIterator();
		}

		@Override
		public ListIterator< L1 > listIterator( int index )
		{
			return value.listIterator( index );
		}

		@Override
		public List< L1 > subList( int fromIndex, int toIndex )
		{
			return value.subList( fromIndex, toIndex );
		}

		@Override
		public < T > T[] toArray( T[] a )
		{
			return value.toArray( a );
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode()
		{
			return value.hashCode();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals( Object obj )
		{
			if ( obj instanceof InternedList )
			{
				@SuppressWarnings( "rawtypes" )
				InternedList iobj = ( InternedList ) obj;
				return value.equals( iobj.value );
			}
			return value.equals( obj );
		}
	}

	protected Map< List< L >, InternedList< L >> internedLists = new HashMap< List< L >, InternedList< L >>();

	protected List< InternedList< L >> listsByIndex = new ArrayList< InternedList< L >>();

	public List< L > emptyList()
	{
		return theEmptyList;
	}

	/**
	 * Return the canonical list for the given list
	 * 
	 * @param src
	 * @return
	 */
	public List< L > intern( List< L > src )
	{
		return internImpl( src );
	}

	private InternedList< L > internImpl( List< L > src )
	{

		InternedList< L > interned;

		if ( src instanceof InternedList )
		{
			interned = ( InternedList< L > ) src;
			if ( interned.owner == this ) { return interned; }
		}
		else
		{
			List< L > copy = new ArrayList< L >( src );
			Collections.sort( copy );
			src = copy;
		}

		interned = internedLists.get( src );

		if ( interned == null )
		{
			int intIndex = listsByIndex.size();

			if ( intIndex >= maxNumLabels )
				throw new AssertionError( String.format( "Too many labels (or types of multiply-labeled pixels): %d maximum", intIndex ) );

			interned = new InternedList< L >( src, intIndex, this );
			listsByIndex.add( interned );
			internedLists.put( src, interned );

		}

		return interned;
	}

	public List< L > intern( L[] src )
	{
		return intern( Arrays.asList( src ) );
	}

	public int indexOf( List< L > key )
	{
		InternedList< L > interned = internImpl( key );
		return interned.index;
	}

	public int indexOf( L[] key )
	{
		return indexOf( intern( key ) );
	}

	public final List< L > listAtIndex( int index )
	{
		return listsByIndex.get( index );
	}

	/**
	 * Returns the number of indexed labeling lists
	 * 
	 * @return
	 */
	public int numLists()
	{
		return listsByIndex.size();
	}

	/**
	 * @return the labels defined in the mapping.
	 */
	public List< L > getLabels()
	{
		HashSet< L > result = new HashSet< L >();
		for ( InternedList< L > instance : listsByIndex )
		{
			for ( L label : instance )
			{
				result.add( label );
			}
		}
		return new ArrayList< L >( result );
	}
}
