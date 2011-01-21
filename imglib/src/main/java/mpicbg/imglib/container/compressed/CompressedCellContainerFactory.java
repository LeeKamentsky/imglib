package mpicbg.imglib.container.compressed;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.basictypecontainer.array.BitArray;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.CharArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.container.basictypecontainer.array.LongArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.container.cell.CellContainerFactory;
import mpicbg.imglib.type.Type;

public class CompressedCellContainerFactory extends CellContainerFactory {
	protected int standardMaxCompressQueueLength = 10;

	public CompressedCellContainerFactory() {
		super();
	}

	public CompressedCellContainerFactory(int cellSize) {
		super(cellSize);
	}

	public CompressedCellContainerFactory(int[] cellSize) {
		super(cellSize);
	}

	public CompressedCellContainerFactory(int cellSize, int maxCompressQueueLength) {
		super(cellSize);
		standardMaxCompressQueueLength = maxCompressQueueLength;
	}

	public CompressedCellContainerFactory(int[] cellSize, int maxCompressQueueLength) {
		super(cellSize);
		standardMaxCompressQueueLength = maxCompressQueueLength;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, BitArray> createBitInstance( int[] dimensions, int entitiesPerPixel )
	{
		return null;
	}
	
	@Override
	public <T extends Type<T>> DirectAccessContainer<T, ByteArray> createByteInstance( int[] dimensions, int entitiesPerPixel )
	{
		return null;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, CharArray> createCharInstance(int[] dimensions, int entitiesPerPixel)
	{
		return null;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, DoubleArray> createDoubleInstance(int[] dimensions, int entitiesPerPixel)
	{
		return null;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, CompressedFloatArray> createFloatInstance(int[] dimensions, int entitiesPerPixel)
	{
		dimensions = checkDimensions( dimensions );
		int[] cellSize = checkCellSize( this.cellSize, dimensions );
		
		return new CompressedCellContainer<T, CompressedFloatArray>( this, new CompressedFloatArray( 1 ), dimensions, cellSize, entitiesPerPixel, standardMaxCompressQueueLength );
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, IntArray> createIntInstance(int[] dimensions, int entitiesPerPixel)
	{
		return null;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, LongArray> createLongInstance(int[] dimensions, int entitiesPerPixel)
	{
		return null;
	}

	@Override
	public <T extends Type<T>> DirectAccessContainer<T, ShortArray> createShortInstance(int[] dimensions, int entitiesPerPixel)
	{
		return null;
	}
	
	
}
