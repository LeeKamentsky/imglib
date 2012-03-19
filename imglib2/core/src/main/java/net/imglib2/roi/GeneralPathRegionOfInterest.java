package net.imglib2.roi;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
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
	AbstractIterableRegionOfInterest implements GeneralPathSegmentHandler
{

	private GeneralPath path;
	private long[] stripes; // for efficiency, these are { xStart, xEnd, y } triplets
	private int index;

	public GeneralPathRegionOfInterest() {
		super(2);
		path = new GeneralPath();
	}

	@Override
	public void moveTo(double x, double y) {
		path.moveTo(x, y);
	}

	@Override
	public void lineTo(double x, double y) {
		path.lineTo(x, y);
	}

	@Override
	public void quadTo(double x1, double y1, double x, double y) {
		path.quadTo(x1, y1, x, y);
	}

	@Override
	public void cubicTo(double x1, double y1, double x2, double y2, double x, double y) {
		path.curveTo(x1, y1, x2, y2, x, y);
	}

	@Override
	public void close() {
		path.closePath();
	}

	public void reset() {
		path.reset();
	}

	// TODO: remove
	public void setGeneralPath(final GeneralPath path) {
		this.path = path;
		this.stripes = null;
	}
	
	// TODO: remove
	public GeneralPath getGeneralPath() {
		return path;
	}

	// TODO: use an Interval
	public void iteratePath(final GeneralPathSegmentHandler handler) {
		final PathIterator iterator = path.getPathIterator(null);
		final double[] coords = new double[6];
		for (;;) {
			int type = iterator.currentSegment(coords);
			switch (type) {
				case PathIterator.SEG_MOVETO:
					handler.moveTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_LINETO:
					handler.lineTo(coords[0], coords[1]);
					break;
				case PathIterator.SEG_QUADTO:
					handler.quadTo(coords[0], coords[1], coords[2], coords[3]);
					break;
				case PathIterator.SEG_CUBICTO:
					handler.cubicTo(coords[0], coords[1], coords[2], coords[3], coords[4], coords[5]);
					break;
				case PathIterator.SEG_CLOSE:
					handler.close();
					break;
				default:
					throw new RuntimeException("Unsupported segment type: " + type);
			}
		}
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
		for (int y = 0; y < height; y++) {
			long start = -1;
			for (int x = 0; x < width; x++) {
				boolean inside = pixels[x + width * y] != 0;
				if (start < 0) {
					if (inside) {
						start = x;
						stripes[index] = x + left;
						stripes[index + 2] = y + top;
					}
				} else if (!inside) {
					start = -1;
					stripes[index + 1] = x + left;
					index += 3;
				}
			}
			if (start >= 0) {
				start = -1;
				stripes[index + 1] = width + left;
				index += 3;
			}
		}

		this.stripes = new long[index];
		System.arraycopy(stripes, 0, this.stripes, 0, index);
		this.index = 0;
	}

}
