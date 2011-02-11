package mpicbg.imglib.container.cell;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.container.AbstractImgCursor;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

public class CellCursor< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractImgCursor< T > implements CellContainer.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final Cursor< Cell< A > > cursorOnCells;

	protected int lastIndexInCell;

	public CellCursor( final CellContainer< T, A > container )
	{
		super( container.numDimensions() );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.cursor();
		
		reset();
	}

	@Override
	public Cell< A > getCell()
	{
		return cursorOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}

	@Override
	public boolean hasNext()
	{
		return ( type.getIndex() < lastIndexInCell ) || cursorOnCells.hasNext();
	}

	@Override
	public void jumpFwd( long steps )
	{
		long newIndex = type.getIndex() + steps;
		while ( newIndex > lastIndexInCell )
		{
			newIndex -= lastIndexInCell + 1;
			cursorOnCells.fwd();
			lastIndexInCell = ( int )( getCell().size() - 1);
		}
		type.updateIndex( ( int ) newIndex );
		type.updateContainer( this );
	}
	
	@Override
	public void fwd()
	{
		if ( type.getIndex() == lastIndexInCell )
			moveToNextCell();
		type.incIndex();
	}

	@Override
	public void reset()
	{
		cursorOnCells.reset();
		moveToNextCell();
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	@Override
	public long getLongPosition( int dim )
	{
		return getCell().indexToGlobalPosition( type.getIndex(), dim );
	}

	@Override
	public void localize( final long[] position )
	{
		getCell().indexToGlobalPosition( type.getIndex(), position );
	}

	/**
	 * Move cursor right before the first element of the next cell.
	 * Update type and index variables. 
	 */
	private void moveToNextCell()
	{
		cursorOnCells.fwd();
		lastIndexInCell = ( int )( getCell().size() - 1);
		type.updateIndex( -1 );
		type.updateContainer( this );
	}
}
