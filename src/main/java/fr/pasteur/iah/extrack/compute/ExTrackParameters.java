package fr.pasteur.iah.extrack.compute;

public class ExTrackParameters
{
	public final double localizationError;

	public final double diffusionLength0;

	public final double diffusionLength1;

	public final double F0;

	public final double probabilityOfUnbinding;

	private ExTrackParameters(
			final double localizationError,
			final double diffusionLength0,
			final double diffusionLength1,
			final double F0,
			final double probabilityOfUnbinding )
	{
		this.localizationError = localizationError;
		this.diffusionLength0 = diffusionLength0;
		this.diffusionLength1 = diffusionLength1;
		this.F0 = F0;
		this.probabilityOfUnbinding = probabilityOfUnbinding;
	}

	public double[] toArray()
	{
		return new double[] {
				localizationError,
				diffusionLength0,
				diffusionLength1,
				F0,
				probabilityOfUnbinding };
	}

	public static final Builder create()
	{
		return new Builder();
	}

	public static final ExTrackParameters ESTIMATION_START_POINT = new ExTrackParameters( 0.3, 0.08, 0.08, 0.1, 0.9 );

	public static class Builder
	{
		private double localizationError = 0.3;

		private double diffusionLength0 = 0.08;

		private double diffusionLength1 = 0.08;

		private double F0 = 0.1;

		private double probabilityOfUnbinding = 0.9;

		public Builder localizationError( final double localizationError )
		{
			this.localizationError = localizationError;
			return this;
		}

		public Builder diffusionLength0( final double diffusionLength0 )
		{
			this.diffusionLength0 = diffusionLength0;
			return this;
		}

		public Builder diffusionLength1( final double diffusionLength1 )
		{
			this.diffusionLength1 = diffusionLength1;
			return this;
		}

		public Builder F0( final double F0 )
		{
			this.F0 = F0;
			return this;
		}

		public Builder probabilityOfUnbinding( final double probabilityOfUnbinding )
		{
			this.probabilityOfUnbinding = probabilityOfUnbinding;
			return this;
		}

		public ExTrackParameters build()
		{
			return new ExTrackParameters(
					localizationError,
					diffusionLength0,
					diffusionLength1,
					F0,
					probabilityOfUnbinding );
		}
	}

	public static ExTrackParameters fromArray( final double[] array )
	{
		return ExTrackParameters.create()
				.localizationError( array[ 0 ] )
				.diffusionLength0( array[ 1 ] )
				.diffusionLength1( array[ 2 ] )
				.F0( array[ 3 ] )
				.probabilityOfUnbinding( array[ 4 ] )
				.build();
	}
}
