package mpicbg.imglib.container.compressed;

import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.container.cell.Cell;
import mpicbg.imglib.type.Type;

public class CompressedCell<T extends Type<T>, A extends ArrayDataAccess<A> & Compressable> extends Cell<T, A>
{
	private int numCursors;

	public CompressedCell( A creator, int cellId, int[] dim, int[] offset, int entitiesPerPixel )
	{
		super( creator, cellId, dim, offset, entitiesPerPixel );
		numCursors = 0;
	}

	protected A getData()
	{
		assert numCursors >= 0 : "numCursors < 0";
		if ( numCursors == 0 ) {
			data.decompress ();
		}
		numCursors++;
		return data;
	}

	protected void releaseData( CompressedCellContainer<T, A> container )
	{
		assert numCursors >= 0 : "numCursors < 0";
		numCursors--;
		if ( numCursors == 0 ) {
			container.addCellToCompressQueue (this);
		}
	}

	protected void compressIfUnused ()
	{
		assert numCursors >= 0 : "numCursors < 0";
		if ( numCursors == 0 ) {
			data.compress();
		}
	}
	
	public long getDataSize()
	{
		return data.getDataSize();
	}
}
