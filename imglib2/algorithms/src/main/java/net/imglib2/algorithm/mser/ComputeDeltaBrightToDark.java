package net.imglib2.algorithm.mser;

import net.imglib2.type.numeric.NumericType;

public final class ComputeDeltaBrightToDark< T extends NumericType< T > > implements ComputeDeltaValue< T >
{
	private final T delta;

	ComputeDeltaBrightToDark( final T delta )
	{
		this.delta = delta;
	}

	@Override
	public T valueMinusDelta( final T value )
	{
		final T valueMinus = value.copy();
		valueMinus.add( delta );
		return valueMinus;
	}
}