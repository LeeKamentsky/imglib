
package net.imglib2.roi;

public interface GeneralPathSegmentHandler {

	void lineTo(double x, double y);
	void moveTo(double x, double y);
	void quadTo(double x1, double y1, double x, double y);
	void cubicTo(double x1, double y1, double x2, double y2, double x, double y);
	void close();

}
