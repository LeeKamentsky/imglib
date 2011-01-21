package mpicbg.imglib.container.compressed;

import java.util.ArrayDeque;
import java.util.Queue;

import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.container.cell.CellContainer;
import mpicbg.imglib.type.Type;


public class CompressedCellContainer<T extends Type<T>, A extends ArrayDataAccess<A> & Compressable> extends CellContainer<T, A>
{
	final protected Queue<CompressedCell<T,A>> compressQueue;
	final int maxCompressQueueLength;

	public CompressedCellContainer( final ContainerFactory factory, final A creator, final int[] dim, final int[] cellSize, final int entitiesPerPixel, final int maxCompressQueueLength )
	{
		super( factory, creator, dim, cellSize, entitiesPerPixel );
		compressQueue = new ArrayDeque<CompressedCell<T,A>> ();
		this.maxCompressQueueLength = maxCompressQueueLength;
	}
	
	@Override	
	public Cell<T, A> createCellInstance( final A creator, final int cellId, final int[] dim, final int offset[], final int entitiesPerPixel )
	{
		return new CompressedCell<T,A>( creator, cellId, dim, offset, entitiesPerPixel );
	}

	@Override
	public Cell<T, A> getCell( final int cellId ) {
		Cell<T, A> cell = data.get( cellId );
		compressQueue.remove( (CompressedCell<T,A>) cell );
		return cell;
	}
	
	@Override
	public void releaseCell( int cellId )
	{
		((CompressedCell<T,A>) data.get( cellId )).releaseData( this );
	}	

	protected void addCellToCompressQueue( CompressedCell<T, A> cell )
	{
		compressQueue.add( cell );
		if (compressQueue.size() >= maxCompressQueueLength) {
			compressQueue.remove().compressIfUnused();
		}
	}
	
	public void printCompressionInfo()
	{
		long fullSize = 4 * getNumPixels(); // hardcoded to work for FloatType...
		// TODO: Types should be able to provide their size in bytes.
		
		long size = getDataSize();
		System.out.println( "uncompressed: " + fullSize + " bytes" );
		System.out.println( "current size: " + size + " bytes" );
		System.out.println( "current compression ratio: " + ( ( 100.0 * size ) / fullSize ) + "%" );
	}
	
	public long getDataSize()
	{
		long size = 0;
		for ( Cell<T,A> c : data ) {
			CompressedCell<T,A> cell = (CompressedCell<T,A>) c;
			size += cell.getDataSize();
		}
		return size;
	}

}
