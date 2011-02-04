/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Stephan Saalfeld
 */
package mpicbg.imglib.algorithm.gauss;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.nearestneighbor.NearestNeighborInterpolatorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory;
import mpicbg.imglib.outofbounds.OutOfBoundsMirrorFactory.Boundary;
import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.util.Util;

public class DownSample<T extends RealType<T>> implements MultiThreaded, OutputAlgorithm<T>, Benchmark
{
	Image<T> input;
	Image<T> downSampled;
	
	float sourceSigma, targetSigma;
	int[] newSize, imgSize;
	float[] scaling;

	String errorMessage = "";
	int numThreads;
	long processingTime;

	public DownSample( final Image<T> image, final int[] newSize, final float sourceSigma, final float targetSigma )
	{
		this.input = image;
		this.newSize = newSize.clone();

		setSourceSigma( sourceSigma );
		setTargetSigma( targetSigma );
		
		if ( input != null )
		{
			this.imgSize = image.getDimensions();

			this.scaling = new float[ image.numDimensions() ];
			for ( int d = 0; d < image.numDimensions(); ++d )
				this.scaling[ d ] = (float)imgSize[ d ] / (float)newSize[ d ];
		}
		else
		{
			this.imgSize = null;
			this.scaling = null;
		}
					         
		setNumThreads();
		this.processingTime = -1;
	}
	
	public DownSample( final Image<T> image, final float downSamplingFactor )
	{
		setInputImage( image );
		
		if ( input != null )
			setDownSamplingFactor( downSamplingFactor );
		
		setSourceSigma( 0.5f );
		setTargetSigma( 0.5f );

		setNumThreads();
		this.processingTime = -1;
	}
	
	public void setSourceSigma( final float sourceSigma ) { this.sourceSigma = sourceSigma; }
	public void setTargetSigma( final float targetSigma ) { this.targetSigma = targetSigma; }
	public void setDownSamplingFactor( final float factor )
	{
		newSize = new int[ input.numDimensions() ];
		scaling = new float[ input.numDimensions() ];
		
		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			newSize[ d ] = Util.round( input.getDimension(d) * factor );
			scaling[ d ] = 1.0f / factor;
		}
	}
	public void setNewSize( final int[] newSize ) { this.newSize = newSize.clone(); }
	public void setInputImage( final Image<T> image )
	{
		this.input = image;
		if ( input != null )
			this.imgSize = image.getDimensions();
		else
			this.imgSize = null;
	}
	
	public float getSourceSigma() { return sourceSigma; }
	public float getTargetSigma() { return targetSigma; }
	public int[] getNewSize() { return newSize.clone(); } 
	public Image<T> getInputImage() { return input; }

	@Override
	public boolean process()
	{
		final long startTime = System.currentTimeMillis();
		
		final int numDimensions = input.numDimensions();
		final double[] sigma = new double[ numDimensions ];
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double s = targetSigma * scaling[ d ]; 
			sigma[ d ] = Math.sqrt( s * s - sourceSigma * sourceSigma );
		}
		
		final GaussianConvolution<T> gauss = new GaussianConvolutionRealType<T>( input, new OutOfBoundsMirrorFactory< T >( Boundary.SINGLE ), sigma );
		gauss.setNumThreads( getNumThreads() );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			errorMessage = "Gaussian Convolution failed: " + gauss.getErrorMessage();
			return false;
		}
		
		final Image<T> gaussConvolved = gauss.getResult();
		downSampled = input.createNewImage( newSize );
		
		final Interpolator<T> interpolator = gaussConvolved.sampler( new NearestNeighborInterpolatorFactory< T >( new OutOfBoundsMirrorFactory< T >( Boundary.SINGLE ) ) );		
		final ImgCursor<T> cursor = downSampled.createLocalizingRasterIterator();
		
		final int[] pos = new int[ numDimensions ];
		final float[] scaledPos = new float[ numDimensions ];		
		final float[] scalingDim = scaling.clone();
		
		while ( cursor.hasNext() )
		{
			cursor.fwd();
			cursor.localize( pos );
			
			for ( int d = 0; d < numDimensions; ++d )
				scaledPos[ d ] = pos[ d ] * scalingDim[ d ];

			interpolator.moveTo( scaledPos );
			cursor.get().set( interpolator.get() );
		}
		
		cursor.close();
		interpolator.close();
		
		gaussConvolved.close();
		
		processingTime = System.currentTimeMillis() - startTime;
		return true;
	}

	@Override
	public boolean checkInput()
	{
		if ( errorMessage.length() > 0 )
		{
			return false;
		}

		if ( input == null )
		{
			errorMessage = "Input image is null";
			return false;
		}
		
		if ( newSize == null )
		{
			errorMessage = "New size of image is null";
			return false;			
		}

		for ( int d = 0; d < input.numDimensions(); ++d )
		{
			if ( newSize[ d ] > imgSize[ d ] )
			{
				errorMessage = "New image supposed to be bigger than input image in dimension " + d + ", " +
							   "this algorithm is only for downsampling (" + newSize[ d ] + " > " + imgSize[ d ] + " )";
				return false;
			}				
		}
		
		return true;
	}

	@Override
	public String getErrorMessage(){ return errorMessage; }

	@Override
	public Image<T> getResult() { return downSampled; }

	@Override
	public long getProcessingTime() { return processingTime; }

	@Override
	public int getNumThreads() { return numThreads; }

	@Override
	public void setNumThreads() { this.numThreads = Runtime.getRuntime().availableProcessors(); }

	@Override
	public void setNumThreads( final int numThreads ) { this.numThreads = numThreads; }
}
