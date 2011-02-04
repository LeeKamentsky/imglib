/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.image;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import mpicbg.imglib.RandomAccessibleIntegerInterval;
import mpicbg.imglib.Function;
import mpicbg.imglib.IterableRaster;
import mpicbg.imglib.RandomAccessibleRaster;
import mpicbg.imglib.Sampler;
import mpicbg.imglib.SamplerFactory;
import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgFactory;
import mpicbg.imglib.container.ImgCursor;
import mpicbg.imglib.container.ImgSampler;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.array.ArrayLocalizingIterator;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outofbounds.RasterOutOfBoundsFactory;
import mpicbg.imglib.sampler.special.OrthoSliceIterator;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.label.FakeType;
import mpicbg.imglib.util.Util;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class Image< T extends Type< T > > implements
		ImageProperties,
		RandomAccessibleIntegerInterval< T, Image< T >, ImgRandomAccess< T >, ImgCursor< T > >
{
	final protected ArrayList< ImgSampler< T > > rasterSamplers;
	final ImgFactory containerFactory;
	final Img< T > container;
	final ImageFactory< T > imageFactory;
	final T type;

	final static AtomicLong i = new AtomicLong(), j = new AtomicLong();
	protected String name;
	
	final protected float[] calibration;

	/* TODO Should this be in the multi-channel image?  Should that be in the image or not?  Better not! */
	protected Display< T > display;

	private Image( Img< T > container, ImageFactory< T > imageFactory, int dim[], String name )
	{
		if ( name == null || name.length() == 0 )
			this.name = "image" + i.getAndIncrement();
		else
			this.name = name;

		if ( dim == null || dim.length < 1 )
		{
			System.err.print( "Cannot instantiate Image, dimensions are null. Creating a 1D image of size 1." );
			dim = new int[] { 1 };
		}

		for ( int d = 0; d < dim.length; d++ )
		{
			if ( dim[ d ] <= 0 )
			{
				System.err.print( "Warning: Image dimension " + ( d + 1 ) + " does not make sense: size=" + dim[ d ] + ". Replacing it by 1." );
				dim[ d ] = 1;
			}
		}
		this.rasterSamplers = new ArrayList< ImgSampler< T >>();
		this.containerFactory = imageFactory.getContainerFactory();
		this.imageFactory = imageFactory;

		this.type = createType();

		if ( container == null )
			this.container = containerFactory.createContainer( dim, type );
		else
			this.container = container;

		setDefaultDisplay();

		calibration = new float[ getContainer().numDimensions() ];
		for ( int d = 0; d < getContainer().numDimensions(); ++d )
			calibration[ d ] = 1;
	}

	public Image( final Img<T> container, final T type )
	{
		this( container, type, null );
	}

	public Image( final Img<T> container, final T type, final String name )
	{
		this( container, new ImageFactory<T>( type, container.factory() ), container.getDimensions(), name );
	}
	
	protected Image( final ImageFactory<T> imageFactory, final int dim[], final String name )	
	{	
		this ( null, imageFactory, dim, name );		
	}

	/**
	 * Creates a new {@link Image} with the same {@link ImgFactory} and {@link Type} as this one.
	 * @param dimensions - the dimensions of the {@link Image}
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final int[] dimensions, final String newName )
	{
		final Image< T > newImage = imageFactory.createImage( dimensions, newName );
		newImage.setCalibration( getCalibration() );
		return newImage;
	}

	/**
	 * Creates a new {@link Image} with the same {@link ImgFactory} and {@link Type} as this one, the name is given automatically.
	 * @param dimensions - the dimensions of the {@link Image}
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final int[] dimensions ) { return createNewImage( dimensions, null ); }

	/**
	 * Creates a new {@link Image} with the same dimensions, {@link ImgFactory} and {@link Type} as this one as this one.
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage( final String newName ) { return createNewImage( getContainer().getDimensions(), newName); }
	
	/**
	 * Creates a new {@link Image} with the same dimensions, {@link ImgFactory} and {@link Type} as this one, the name is given automatically.
	 * @return - a new empty {@link Image}
	 */
	public Image<T> createNewImage() { return createNewImage( getContainer().getDimensions(), null ); }
	
	public float[] getCalibration() { return calibration.clone(); }
	public float getCalibration( final int dim ) { return calibration[ dim ]; }
	public void setCalibration( final float[] calibration ) 
	{ 
		for ( int d = 0; d < getContainer().numDimensions(); ++d )
			this.calibration[ d ] = calibration[ d ];
	}
	public void setCalibration( final float calibration, final int dim ) { this.calibration[ dim ] = calibration; } 

	/**
	 * Returns the {@link Img} that is used for storing the image data.
	 * @return Container<T,? extends DataAccess> - the typed {@link Img}
	 */
	public Img<T> getContainer() { return container; }
	
	/**
	 * Creates a {@link Type} that the {@link Image} is typed with.
	 * @return T - an instance of {@link Type} for computing
	 */
	public T createType() { return imageFactory.createType(); }
	
	/**
	 * Return a {@link ImgCursor} that will traverse the image's pixel data in a memory-optimized fashion.
	 * @return IterableCursor<T> - the typed {@link ImgCursor}
	 */
	@Override
	public ImgCursor<T> createRasterIterator()
	{
		ImgCursor< T > cursor = container.createRasterIterator( this );
		addRasterSampler( cursor );
		return cursor;	
	}
	
	/**
	 * Return a {@link ImgCursor} that will traverse all image pixels in
	 * access-optimal order and tracks its location at each moving operation.
	 * 
	 * This {@link ImgCursor} is the preferred choice for implementations
	 * that require the iterator's location at each iteration step (e.g. for
	 * rendering a transformed image)
	 *  
	 * @return RasterIterator<T> - the typed {@link ImgCursor}
	 */
	@Override
	public ImgCursor<T> createLocalizingRasterIterator()
	{
		ImgCursor<T> cursor = container.createLocalizingRasterIterator( this );
		addRasterSampler( cursor );
		return cursor;		
	}
	
	/**
	 * Creates a {@link ImgRandomAccess} which is able to move freely
	 * within the {@link Image} without checking for image boundaries.  The
	 * behavior at locations outside of image bounds is not defined.
	 * 
	 * @return - a {@link ImgRandomAccess} that cannot leave the {@link Image}
	 */
	@Override
	public ImgRandomAccess< T > createPositionableRasterSampler()
	{
		ImgRandomAccess<T> cursor = container.createPositionableRasterSampler( this );
		addRasterSampler( cursor );
		return cursor;						
	}
	
	/**
	 * @deprecated Use {@link #createPositionableCursor()} instead.
	 */
	@Deprecated
	public ImgRandomAccess<T> createLocalizableByDimCursor()
	{
		return createPositionableRasterSampler();
	}

	/**
	 * Creates a {@link ImgRandomAccess} which is able to move freely
	 * within and outside of {@link Image} bounds given a
	 * {@link RasterOutOfBoundsFactory} that defines the behavior out of
	 * {@link Image} bounds.
	 * 
	 * @param factory - the {@link RasterOutOfBoundsFactory}
	 * @return - a {@link ImgRandomAccess} that can leave the {@link Image}
	 */
	@Override
	public ImgRandomAccess<T> createPositionableRasterSampler( final RasterOutOfBoundsFactory<T> factory )
	{
		ImgRandomAccess<T> cursor = container.createPositionableRasterSampler( this, factory );
		addRasterSampler( cursor );
		return cursor;								
	}
	
	public OrthoSliceIterator< T > createOrthoSliceIterator( final int x, final int y, final int[] position )
	{
		return container.createOrthoSliceIterator( this, x, y, position );
	}
	
	
	/**
	 * @deprecated Use {@link #createPositionableCursor( OutOfBoundsStrategyFactory<T> )} instead.
	 */
	@Deprecated
	public ImgRandomAccess<T> createLocalizableByDimCursor( final RasterOutOfBoundsFactory<T> factory )
	{
		return createPositionableRasterSampler( factory);
	}

	/**
	 * Creates and {@link Interpolator} on this {@link Image} given a certain {@link InterpolatorFactory}.
	 * @param factory - the {@link InterpolatorFactory} to use
	 * @return {@link Interpolator}
	 * 
	 * @deprecated Use {@link #sampler(SamplerFactory)} instead.
	 */
	@Deprecated
	public < I extends Interpolator< T >> I createInterpolator( final InterpolatorFactory< T, I > factory )
	{
		return factory.create( this );
	}
	
	@Override
	public < S extends Sampler< T > > S sampler( final SamplerFactory< T, S, Image< T > > factory )
	{
		return factory.create( this );
	}
	
	/**
	 * Sets the default {@link Display} of this {@link Image} as defined by the {@link Type}
	 */
	public void setDefaultDisplay() { this.display = type.getDefaultDisplay( this ); }

	/**
	 * Return the current {@link Display} of the {@link Image}
	 * @return - the {@link Display}
	 */
	public Display<T> getDisplay() { return display; }
	
	/**
	 * Sets the {@link Display} associated with this {@link Image}.
	 * @param display - the {@link Display}
	 */
	public void setDisplay( final Display<T> display ) { this.display = display; }
	
	final public synchronized static long createUniqueId() { return j.getAndIncrement(); }
	
	/**
	 * Closes the {@link Image} by closing all {@link ImgSampler}s and the {@link Img}
	 */
	public void close()
	{ 
		closeAllRasterSamplers();
		container.close();
	}
	
	/**
	 * Creates an int array of the same dimensionality as this {@link Image} which can be used for addressing {@link ImgSampler}s. 
	 * @return - empty int[]
	 * 
	 * TODO Do we really need this?  I just replaces
	 * int[] t = new int[ img1.numDimensions() ]; by
	 * int[] t = img1.createPositionArray()
	 *
	 * Saalfeld: remove! ;)  Preibisch: keep (esoteric reasons)
	 */
	public int[] createPositionArray() { return new int[ numDimensions() ]; }
	
	
	@Override
	public int numDimensions() { return getContainer().numDimensions(); }
	@Override
	public int[] getDimensions() { return getContainer().getDimensions(); }
	@Override
	public long numPixels() { return getContainer().numPixels(); }

	@Override
	public String getName() { return name; }

	@Override
	public void setName(String name) { this.name = name; }
	
	@Override
	public String toString()
	{
		return "Image '" + this.getName() + "', dim=" + Util.printCoordinates( getContainer().getDimensions() );
	}
	
	/**
	 * @deprecated Use {@link #size(int[])} instead.
	 * 
	 * @param dimensions
	 */
	@Deprecated
	public void getDimensions( final int[] dimensions )
	{
		size( dimensions );
	}
	
	@Override
	public void size( final int[] dimensions )
	{
		for (int d = 0; d < getContainer().numDimensions(); d++)
			dimensions[d] = getContainer().size( d );
	}

	@Override
	public long size( final int dim ) { return getContainer().size( dim ); }
	
	/**
	 * Clones this {@link Image}, i.e. creates this {@link Image} containing the same content.
	 * No {@link ImgSampler}s will be instantiated and the name will be given automatically.
	 */
	@Override
	public Image<T> copy()
	{
		final Image<T> clone = this.createNewImage();
		
		final ImgCursor<T> c1 = this.createRasterIterator();
		final ImgCursor<T> c2 = clone.createRasterIterator();
		
		while ( c1.hasNext() )
		{
			c1.fwd();
			c2.fwd();
			
			c2.get().set( c1.get() );		
		}
		
		c1.close();
		c2.close();
		
		return clone;
	}

	/**
	 * Returns the {@link ImgFactory} of this {@link Image}.
	 * @return - {@link ImgFactory}
	 */
	public ImgFactory getContainerFactory() { return containerFactory; }
	
	/**
	 * Returns the {@link ImageFactory} of this {@link Image}.
	 * @return - {@link ImageFactory}
	 */
	public ImageFactory<T> getImageFactory() { return imageFactory; }
	
	/**
	 * Remove all cursors
	 */
	public void removeAllRasterSamplers()
	{
		closeAllRasterSamplers();
		rasterSamplers.clear();
	}
	
	/**
	 * Closes all {@link ImgSampler}s operating on this {@link Image}.
	 */
	public void closeAllRasterSamplers()
	{
		final ArrayList< ImgSampler< ? > > copy = new ArrayList< ImgSampler<?> >( rasterSamplers );
		for ( final ImgSampler<?> s : copy )
			s.close();
	}
	
	/**
	 * Put all {@link ImgSampler}s currently instantiated into a {@link Collection}.
	 * 
	 */
	public void getRasterSamplers( final Collection< ImgSampler< T > > collection )
	{
		collection.addAll( rasterSamplers );
	}
	
	/**
	 * Return all {@link ImgSampler}s currently instantiated for this {@link Image} in a new {@link ArrayList}.
	 * @return - {@link ArrayList} containing the {@link ImgSampler}s
	 */
	public ArrayList< ImgSampler< T > > getRasterSamplers(){ return new ArrayList< ImgSampler< T > >( rasterSamplers ); }	
	
	/**
	 * Adds a {@link ImgSampler} to the {@link ArrayList} of instantiated {@link ImgSampler}s.
	 * @param c - new {@link ImgSampler}
	 */
	protected synchronized void addRasterSampler( final ImgSampler<T> c ) { rasterSamplers.add( c );	}
	
	/**
	 * Remove a {@link ImgSampler} from the {@link ArrayList} of instantiated {@link ImgSampler}s.
	 * @param c - {@link ImgSampler} to be removed
	 */
	public synchronized void removeRasterSampler( final ImgSampler<T> c )
	{
		rasterSamplers.remove( c );
	}
	
	/**
	 * Returns the number of {@link ImgSampler}s instantiated on this {@link Image}.
	 * @return - the number of {@link ImgSampler}s
	 */
	public int numRasterSamplers() { return rasterSamplers.size(); }

	@Override
	public Iterator<T> iterator() { return this.createRasterIterator(); }

	public T[] toArray()
	{
		final long numPixels = numPixels();
		
		if ( numPixels > (long)Integer.MAX_VALUE )
			throw new RuntimeException( "Number of pixels in Container too big for a T[] array: " + numPixels + " > " + Integer.MAX_VALUE );
		
		final T[] pixels = createType().createArray1D( (int)numPixels );
		
		final ArrayLocalizingIterator<FakeType> iterator = ArrayLocalizingIterator.createLinearCursor( getDimensions() );
		final ImgRandomAccess<T> positionable = this.createPositionableRasterSampler();
		
		int t = 0;
		while ( iterator.hasNext() )
		{
			iterator.fwd();
			positionable.moveTo( iterator );
			
			pixels[ t++ ] = positionable.get().copy();			
		}
		
		iterator.close();
		positionable.close();
		
		return pixels;
	}	
}
