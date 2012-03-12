package net.imglib2.roi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

/**
 * TODO: make independent of AWT
 *
 * @author Johannes Schindelin
 */
public class GeneralPathRegionOfInterest extends
	AbstractIterableRegionOfInterest
{

	private GeneralPath path;
	private long[] stripes; // for efficiency, these are { xStart, xEnd, y } triplets
	private int index;

	protected GeneralPathRegionOfInterest() {
		super(2);
		path = new GeneralPath();
	}

	// TODO: remove
	public void setGeneralPath(final GeneralPath path) {
		this.path = path;
		this.stripes = null;
	}

	@Override
	protected boolean nextRaster(long[] position, long[] end) {
		ensureStripes();
		if (index >= stripes.length) {
			index = 0;
			return false;
		}
		position[0] = stripes[index];
		end[0] = stripes[index + 1];
		position[1] = end[1] = stripes[index + 2];
new Exception("set " + position[0] + ", " + position[1] + " to " + end[0] + ", " + end[1]).printStackTrace();
		index += 3;
		return true;
	}

	@Override
	protected boolean isMember(double[] position) {
		return path.contains(position[0], position[1]);
	}

	private void ensureStripes() {
		if (stripes != null) return;

		Rectangle2D bounds = path.getBounds2D();
		int left = (int)Math.floor(bounds.getMinX());
		int top = (int)Math.floor(bounds.getMinY());
		int width = (int)(Math.ceil(bounds.getMaxX()) - left);
		int height = (int)(Math.ceil(bounds.getMaxY()) - top);
if (!true) {
	throw new RuntimeException("(" + left + ", " + top + ")+(" + width + ", " + height + ")");
}

		byte[] pixels = new byte[width * height];
		final ColorModel colorModel = new IndexColorModel(1, 2, new byte[] { 0, 1 }, new byte[] { 0, 1 }, new byte[] { 0, 1 });
		final SampleModel sampleModel = new MultiPixelPackedSampleModel(DataBuffer.TYPE_BYTE, width, height, 8); // colorModel.createCompatibleSampleModel(width,  height);
		final DataBuffer dataBuffer = new DataBufferByte(pixels, width * height);
		final WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, null);
		final BufferedImage image = new BufferedImage(colorModel, raster, false, null );
		final GeneralPath transformed = new GeneralPath(path);
		transformed.transform(AffineTransform.getTranslateInstance(-bounds.getMinX(), -bounds.getMinY()));
		Graphics2D g2d = (Graphics2D)image.getGraphics();
		g2d.setColor(Color.WHITE);
		g2d.setStroke(new BasicStroke(0));
		g2d.fill(transformed);

		long[] stripes = new long[3 * width * height / 2]; // avoid re-allocation
		int index = 0;
if (!true) {
	g2d.drawLine(0, 0, 50, 0);
	for (int y = 0; y < height && y < 5; y++) {
		long start = -1;
		for (int x = 0; x < width; x++) {
			boolean inside = pixels[x + width * y] != 0;
//System.err.print(inside ? "#" : ".");
		}
//System.err.println();
	}
} else {
		for (int y = 0; y < height; y++) {
			long start = -1;
			for (int x = 0; x < width; x++) {
				boolean inside = pixels[x + width * y] != 0;
System.err.print(inside ? "#" : ".");
				if (start < 0) {
					if (inside) {
//System.err.println("starting " + x + ", " + y + " (" + (x + left) + ", " + (y + top) + ")");
						start = x;
						stripes[index] = x + left;
						stripes[index + 2] = y + top;
					}
				} else if (!inside) {
					start = -1;
					stripes[index + 1] = x + left;
//System.err.println("ending " + x + " (" + (x + left) + ", " + (y + top) + ")");
					index += 3;
				}
			}
			if (start >= 0) {
				start = -1;
				stripes[index + 1] = width + left;
//System.err.println("ending at " + width + " (" + (width + left) + ", " + (y + top) + ")");
				index += 3;
			}
System.err.println();
		}
}

		this.stripes = new long[index];
		System.arraycopy(stripes, 0, this.stripes, 0, index);
		this.index = 0;
for (int i = 0; i < index; i += 3) System.err.print(" (" + this.stripes[i] + "-" + this.stripes[i + 1] + ", " + this.stripes[i + 2] + ")");
System.err.println();
	}

}
