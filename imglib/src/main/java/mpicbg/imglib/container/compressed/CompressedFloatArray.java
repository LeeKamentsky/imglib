package mpicbg.imglib.container.compressed;

import java.nio.ByteBuffer;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import mpicbg.imglib.container.basictypecontainer.FloatAccess;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;

public class CompressedFloatArray implements FloatAccess, ArrayDataAccess<CompressedFloatArray>, Compressable {
	final int numEntities;

	protected float[] data;
	protected boolean modified;

	protected byte[] compressedData;
	
	protected static final Deflater compresser = new Deflater( 3 );
	protected static final Inflater decompresser = new Inflater();

	public CompressedFloatArray( final int numEntities )
	{
		this.data = null;
		this.numEntities = numEntities;		
		this.compressedData = null;
	}

	protected byte[] dataAsBytes()
	{
		byte[] bytes = new byte[ numEntities * 4 ];
		ByteBuffer.wrap( bytes ).asFloatBuffer().put( data );
		return bytes;
	}
	
	@Override
	public void compress()
	{
		assert data != null : "data == null";
		if ( modified ) {
			byte[] bytes = dataAsBytes();
			data = null;

			final byte[] compressedBytes = new byte[numEntities * 4];
			compresser.reset();
			compresser.setInput( bytes );
			compresser.finish();
			final int compressedDataLength = compresser.deflate( compressedBytes );
			bytes = null;

			compressedData = new byte[compressedDataLength];
			for ( int i = 0; i < compressedDataLength; ++i ) {
				compressedData[i] = compressedBytes[i];
			}
		}
		data = null;
	}

	@Override
	public void decompress()
	{
		if ( data == null ) {
			data = new float[ numEntities ];
		}
		if ( compressedData != null ) {
			try {
				final byte[] bytes = new byte[ numEntities * 4 ];
				decompresser.reset();
			    decompresser.setInput( compressedData );
			    int resultLength = decompresser.inflate( bytes );
			    assert resultLength == numEntities * 4 : "decompressed length does not match data array size";

				ByteBuffer.wrap( bytes ).asFloatBuffer().get( data );
			} catch ( java.util.zip.DataFormatException ex ) {
				throw new RuntimeException( ex );
			}
		}
		modified = false;
	}

	@Override
	public long getDataSize()
	{
		long size = 0;
		if ( data != null ) {
			size += 4 * data.length;
		}
		if ( compressedData != null ) {
			size += compressedData.length;
		}
		return size;
	}

	@Override
	public void close()
	{
		data = null;
	}

	@Override
	public float getValue( final int index )
	{
		return data[ index ];
	}

	@Override
	public void setValue( final int index, final float value )
	{
		data[ index ] = value;
		modified = true;		
	}
	
	@Override
	public CompressedFloatArray createArray( final int numEntities ) { return new CompressedFloatArray( numEntities ); }

	@Override
	public float[] getCurrentStorageArray(){ return data; }
}
