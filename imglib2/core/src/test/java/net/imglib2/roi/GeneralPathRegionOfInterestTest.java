
package net.imglib2.roi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;

import javax.swing.JFrame;
import javax.swing.JPanel;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccessible;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.logic.BitType;

import org.junit.Test;

/**
 * @author Johannes Schindelin
 */

public class GeneralPathRegionOfInterestTest {

	@Test
	public void testIsMemberInCircle() {
		/*
		 * Testing with non-integral center because otherwise the right- and the
		 * bottom-most pixel would be triggering an assertion error.
		 */
		final double x0 = 100.5, y0 = 120.5, radius = 50;
		final AbstractRegionOfInterest roi = makeCircle(x0, y0, radius);

		final double[] coords = new double[2];
		for (coords[0] = 0; coords[0] < x0 + radius + 10; coords[0] += 1) {
			for (coords[1] = 0; coords[1] < y0 + radius + 10; coords[1] += 1) {
				final double distance = getDistance(x0, y0, coords[0], coords[1]);
				if (distance <= radius) assertTrue("(" + coords[0] + ", " + coords[1] +
					") is inside", roi.isMember(coords));
				else assertFalse("(" + coords[0] + ", " + coords[1] + ") is outside",
					roi.isMember(coords));
			}
		}
	}

	@Test
	public void testCircleAsIterator() {
		/*
		 * Testing with non-integral center because otherwise the right- and the
		 * bottom-most pixel would be triggering an assertion error.
		 */
		final double x0 = 10, y0 = 12, radius = 5;
		final IterableRegionOfInterest roi = makeCircle(x0, y0, radius);

		final long width = (int)Math.ceil(x0 + radius + 10);
		final long height = (int)Math.ceil(y0 + radius + 10);

		RandomAccessible<BitType> randomAccessible = new ArrayImgFactory<BitType>().create(new long[] { width, height }, new BitType());
		IterableInterval<BitType> interval = roi.getIterableIntervalOverROI(randomAccessible);
		Cursor<BitType> cursor = interval.localizingCursor();

final double radius2 = radius /* + 0.5 */;
final double x2 = x0 - 0.5;
final double y2 = y0 - 0.5;
		int y = (int)Math.ceil(y2 - radius);
		int x = (int)Math.ceil(x2 - Math.sqrt(radius2 * radius2 - (y - y2) * (y - y2)));
System.err.println("distance: " + getDistance(x, y, x2, y2) + ", " + getDistance(x - 1, y, x2, y2) + ", " + getDistance(94, y, x2, y2) + ", " + getDistance(91, y, x2, y2) + ": " + getDistance(110, 71, x2, y2) + ", " + getDistance(84.0, 73.0, x2, y2));

		// For AWT, we are pretty lenient about boundary pixels...
		double[] coords = new double[2];
		while (cursor.hasNext()) {
			cursor.fwd();
			cursor.localize(coords);
System.err.println("(" + x + ", " + y + ") (" + coords[0] + ", " + coords[1] + ") : " + getDistance(x, y, x2, y2) + " vs " + getDistance(coords[0], coords[1], x2, y2) + " < " + radius2 + " (<" + radius + ")");
			if (Math.abs(x - coords[0]) > 1e-10 || Math.abs(y - coords[1]) > 1e-10) {
				assertTrue("< 1", Math.abs(y - coords[1]) <= 1 + 1e-10);
System.err.println("dist of " + x + ", " + y + ": " + getDistance(x, y, x2, y2) + " > " + (radius2 - 0.1) + "? " + (getDistance(x, y, x2, y2) > radius2 - 0.2));
				y++;
				x = (int)Math.ceil(x0 - Math.sqrt(radius2 * radius2 - (y - y2) * (y - y2)));
				if (Math.abs(x - coords[0]) <= 1 + 1e-10) {
					x = (int)Math.round(coords[0]);
				}
			}
			assertEquals("x", x, coords[0], 1e-10);
			assertEquals("y", y, coords[1], 1e-10);
			x++;
		}
	}

	private double getDistance(final double x0, final double y0,
		final double x1, final double y1)
	{
		final double x = x1 - x0;
		final double y = y1 - y0;
		final double distance = Math.sqrt(x * x + y * y);
		return distance;
	}

	/**
	 * Approximate a circle.
	 *
	 * For a subdivision into n segments, the inner control
	 * points of each cubic Bézier segment should be tangential at a distance of
	 * 4/3 * tan(2*PI/n) to the closest end point.
	 */
	private GeneralPathRegionOfInterest makeCircle(final double x0,
		final double y0, final double radius)
	{
		final GeneralPath path = new GeneralPath();
		final double controlDistance =
			4.0 / 3.0 * Math.tan(1.0 / 4.0 * (Math.PI * 2 / 4)) * radius;

		path.moveTo(x0 + radius, y0);
		path.curveTo(x0 + radius, y0 - controlDistance, x0 + controlDistance, y0 -
			radius, x0, y0 - radius);
		path.curveTo(x0 - controlDistance, y0 - radius, x0 - radius, y0 -
			controlDistance, x0 - radius, y0);
		path.curveTo(x0 - radius, y0 + controlDistance, x0 - controlDistance, y0 +
			radius, x0, y0 + radius);
		path.curveTo(x0 + controlDistance, y0 + radius, x0 + radius, y0 +
			controlDistance, x0 + radius, y0);
		path.closePath();

		final GeneralPathRegionOfInterest roi = new GeneralPathRegionOfInterest();
		roi.setGeneralPath(path);

		return roi;
	}

	@SuppressWarnings("unused") // for debugging
	private void showPath(final GeneralPath path) {
		final JFrame frame = new JFrame("Path " + path);
		final JPanel panel = new JPanel() {

			private static final long serialVersionUID = 5294182503921406779L;

			@Override
			public void paint(final Graphics g) {
				final Graphics2D g2d = (Graphics2D) g;
				g2d.setColor(Color.BLACK);
				g2d.fill(path);
			}

			@Override
			public Dimension getPreferredSize() {
				final Rectangle bounds = path.getBounds();
				return new Dimension(bounds.width + bounds.x, bounds.height + bounds.y);
			}
		};
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
}
