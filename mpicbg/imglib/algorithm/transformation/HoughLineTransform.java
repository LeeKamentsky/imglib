package mpicbg.imglib.algorithm.transformation;

import ij.IJ;
import mpicbg.imglib.algorithm.math.MathLib;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.NumericType;
import mpicbg.imglib.type.numeric.IntType;
import mpicbg.imglib.type.numeric.LongType;


/**
 * A class that extends {@link HoughTransform} to handle Hough Line voting over an edge map.
 * This implementation uses a threshold to determine whether a pixel at a certain point is 
 * an edge or not.  Comparison is strictly-greater-than.  This implementation is fairly dumb
 * in that it does not take gradients into account.  The threshold used is the default value
 * returned by calling the constructor for the {@link Type} of the input {@link Image}. 
 * 
 * Vote space here has two dimensions: rho and theta.  Theta is measured in radians
 * [-pi/2 pi/2), rho is measured in [-rhoMax, rhoMax).
 * 
 * Lines are modeled as
 * 
 * l(t) = | x | = rho * |  cos(theta) | + t * | sin(theta) |
 *        | y |         | -sin(theta) |       | cos(theta) |
 * 
 * In other words, rho represents the signed minimum distance from the image origin to the line,
 * and theta indicates the angle between the row-axis and the minimum offset vector.
 * 
 * For a given point, then, votes are placed along the curve
 * 
 * rho = y * sin(theta) - x * cos(theta)
 * @Override 
 */
public class HoughLineTransform <S extends NumericType<S>, T extends ComparableType<T>> extends HoughTransform<S, T>
{
	public static final int DEFAULT_THETA = 180;
	public final double dTheta;
	public final double dRho;
	private final T threshold;
	private final int nRho;
	private final int nTheta;
	private final double[] rho;
	private final double[] theta;
	
	/**
	 * Calculates a default number of rho bins, which corresponds to a resolution of one pixel.
	 * @param inputImage the {@link Image} in question.
	 * @return default number of rho bins.
	 */
	public static int defaultRho(final Image<?> inputImage)
	{
		return (int)(2 * MathLib.computeLength(inputImage.getDimensions()));
	}
	
	/**
	 * Creates a default {@link HoughLineTransform} with {@IntType} vote space.
	 * @param <T> the {@link Type} of the {@link Image} in question.
	 * @param inputImage the {@link Image} to perform the Hough Line Transform against.
	 * @return a default {@link HoughLineTransform} with {@link IntType} vote space.
	 */
	public static <T extends ComparableType<T>> HoughLineTransform<IntType, T> integerHoughLine(final Image<T> inputImage)
	{
		return new HoughLineTransform<IntType, T>(inputImage, new IntType());
	}
	
	/**
	 * Creates a default {@link HoughLineTransform} with {@link LongType} vote space.
	 * @param <T> the {@link Type} of the {@link Image} in question.
	 * @param inputImage the {@link Image} to perform the Hough Line Transform against.
	 * @return a default {@link HoughLineTransform} with {@link LongType} vote space.
	 * 
	 * Use this for voting against large images, but reasonably small vote space.  If you need a big 
	 * voting space, it would be better to create a {@link HoughLineTransform} instantiated with an
	 * {@link ImageFactory} capable of handling it.
	 */
	public static <T extends ComparableType<T>> HoughLineTransform<LongType, T> longHoughLine(final Image<T> inputImage)
	{
		return new HoughLineTransform<LongType, T>(inputImage, new LongType());
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space.
	 * Defaults are used for rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Image<T> inputImage, final S type)
	{
		this(inputImage, DEFAULT_THETA, type);
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space and theta-resolution.
	 * Rho-resolution is set to the default.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta-resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Image<T> inputImage, final int theta, final S type)
	{
		this(inputImage, defaultRho(inputImage), DEFAULT_THETA, type);
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link Type} of vote space and rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Image<T> inputImage, final int inNRho, final int inNTheta, final S type)
	{
		super(inputImage, new int[]{inNRho, inNTheta}, type);
		//Theta by definition is in [0..pi].
		dTheta = Math.PI / (double)inNTheta;
		/*The furthest a point can be from the origin is the length calculated
		 * from the dimensions of the Image.
		 */
		dRho = 2 * MathLib.computeLength(inputImage.getDimensions()) / (double)inNRho;
		threshold = inputImage.createType();
		nRho = inNRho;
		nTheta = inNTheta;
		theta = new double[inNTheta];
		rho = new double[inNRho];
	}
	
	/**
	 * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
	 * a specific {@link ImageFactory} for the vote space, and 
	 * specific rho- and theta-resolution.
	 * @param inputImage the {@link Image} to operate against.
	 * @param theta the number of bins for theta resolution.
	 * @param type the {@link Type} for the vote space.
	 */
	public HoughLineTransform(final Image<T> inputImage, final ImageFactory<S> factory, final int inNRho, final int inNTheta)
	{
		super(inputImage, new int[]{inNRho, inNTheta}, factory);
		dTheta = Math.PI / (double)inNTheta;
		dRho = 2 * MathLib.computeLength(inputImage.getDimensions()) / (double)inNRho;
		threshold = inputImage.createType();
		nRho = inNRho;
		nTheta = inNTheta;
		theta = new double[inNTheta];
		rho = new double[inNRho];
	}
	
	public void setThreshold(final T inThreshold)
	{
		threshold.set(inThreshold);
	}

	@Override
	public boolean process() {
		final LocalizableCursor<T> imageCursor = getImage().createLocalizableCursor();
		final int[] position = new int[getImage().getDimensions().length];
		final double minTheta = -Math.PI/2;
		final double minRho = -MathLib.computeLength(super.getImage().getDimensions());
		final long sTime = System.currentTimeMillis();
		boolean success;
				
		for (int t = 0; t < nTheta; ++t)
		{
			theta[t] = dTheta * (double)t + minTheta;
		}
		for (int r = 0; r < nRho; ++r)
		{
			rho[r] = dRho * (double)r + minRho;
		}
		
		while (imageCursor.hasNext())
		{
			double fRho;
			int r;
			int[] voteLoc = new int[2];
			
			imageCursor.fwd();
			imageCursor.getPosition(position);
			
			for (int t = 0; t < nTheta; ++t)
			{
				if (imageCursor.getType().compareTo(threshold) > 0)
				{
					fRho = Math.cos(theta[t]) * (double)position[0] + Math.sin(theta[t]) * (double)position[1];
					r = Math.round((float)((fRho - minRho)/ dRho));
					voteLoc[0] = r;
					voteLoc[1] = t;
					try
					{
						super.placeVote(voteLoc);
					}
					catch(Exception e)
					{
						IJ.log("Tried to place vote at " + r + " " + t + " for theta " + theta[t] + ", and rho " + fRho);
						return false;
					}
				}
			}			
		}		
		
		success = super.pickPeaks();
		super.pTime = System.currentTimeMillis() - sTime;
		return success;
	}
	

}
