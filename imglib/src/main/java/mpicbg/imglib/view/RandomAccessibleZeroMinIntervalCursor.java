package mpicbg.imglib.view;

import mpicbg.imglib.Cursor;
import mpicbg.imglib.Interval;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.RandomAccessible;
import mpicbg.imglib.iterator.LocalizingZeroMinIntervalIterator;

public final class RandomAccessibleZeroMinIntervalCursor< T > extends LocalizingZeroMinIntervalIterator implements Cursor< T >
{
	final RandomAccess< T > randomAccess;

	public <I extends RandomAccessible< T > & Interval > RandomAccessibleZeroMinIntervalCursor( final I interval )
	{
		super( interval );
		randomAccess = interval.randomAccess();
	}

	protected RandomAccessibleZeroMinIntervalCursor( final RandomAccessibleZeroMinIntervalCursor< T > cursor )
	{
		super( cursor );
		this.randomAccess = cursor.randomAccess.copy();
	}

	@Override
	public T get()
	{
		randomAccess.setPosition( position );
		return randomAccess.get();
	}

	@Override
	public T next()
	{
		fwd();
		return get();
	}

	@Override
	public void remove()
	{
	}

	@Override
	public RandomAccessibleZeroMinIntervalCursor< T > copy()
	{
		return new RandomAccessibleZeroMinIntervalCursor< T >( this );
	}
}
