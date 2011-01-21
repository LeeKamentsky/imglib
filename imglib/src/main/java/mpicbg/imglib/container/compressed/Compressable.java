package mpicbg.imglib.container.compressed;

public interface Compressable {
	public void compress();	
	public void decompress();
	
	// get the current size in bytes of for testing
	public long getDataSize();
}
