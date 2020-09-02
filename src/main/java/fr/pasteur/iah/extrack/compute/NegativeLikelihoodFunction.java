package fr.pasteur.iah.extrack.compute;

import java.util.Map;

import Jama.Matrix;
import pal.math.MultivariateFunction;

public class NegativeLikelihoodFunction implements MultivariateFunction
{

	private static final int N_ARGS = 5;

	private final Map< Integer, Matrix > Cs;

	private final int nbSubSteps;

	private final boolean doFrame;

	private final int frameLen;

	private final double[] lowerBound;

	private final double[] upperBound;

	public NegativeLikelihoodFunction(
			final Map< Integer, Matrix > Cs,
			final int nbSubSteps,
			final boolean doFrame,
			final int frameLen )
	{
		this.Cs = Cs;
		this.nbSubSteps = nbSubSteps;
		this.doFrame = doFrame;
		this.frameLen = frameLen;
		this.lowerBound = new double[ N_ARGS ];
		this.upperBound = new double[ N_ARGS ];

		/*
		 * 0. localizationError
		 */

		lowerBound[ 0 ] = 0.;
		upperBound[ 0 ] = 100.; // um

		/*
		 * 1. diffusionLengths0
		 */

		lowerBound[ 1 ] = 0.;
		upperBound[ 1 ] = 100.; // um

		/*
		 * 2. diffusionLengths1
		 */

		lowerBound[ 2 ] = lowerBound[ 1 ];
		upperBound[ 2 ] = upperBound[ 1 ];

		/*
		 * 3. F0.
		 */

		lowerBound[ 3 ] = 0.;
		upperBound[ 3 ] = 1.;

		/*
		 * 4. probabilityOfUnbinding
		 */

		lowerBound[ 4 ] = 0.;
		upperBound[ 4 ] = 1.;
	}

	@Override
	public double evaluate( final double[] argument )
	{
		return evalFun( argument, Cs, nbSubSteps, doFrame, frameLen );
	}

	@Override
	public int getNumArguments()
	{
		return N_ARGS;
	}

	@Override
	public double getLowerBound( final int n )
	{
		return lowerBound[ n ];
	}

	@Override
	public double getUpperBound( final int n )
	{
		return upperBound[ n ];
	}

	public static final double evalFun(
			final double[] params,
			final Map< Integer, Matrix > Cs,
			final int nbSubSteps,
			final boolean doFrame,
			final int frameLen )
	{
		final double localizationError = params[ 0 ];
		final double diffusionLengths0 = params[ 1 ];
		final double diffusionLengths1 = params[ 2 ];
		final double F0 = params[ 3 ];
		final double probabilityOfUnbinding = params[ 4 ];

		final boolean doPred = false;
		final double[] diffusionLengths = new double[] { diffusionLengths0, diffusionLengths1 };
		final double F1 = 1 - F0;
		final double[] Fs = new double[] { F0, F1 };
		final double probabilityOfBinding = F0 / ( 1 - F0 ) * probabilityOfUnbinding;

		double sumLogProbas = 0;
		for ( final Integer trackID : Cs.keySet() )
		{
			final Matrix track = Cs.get( trackID );
			final TrackState state = new TrackState(
					track,
					localizationError,
					diffusionLengths,
					Fs,
					probabilityOfUnbinding,
					probabilityOfBinding,
					nbSubSteps,
					doFrame,
					frameLen,
					doPred );

			final Matrix probabilities = state.P;
			double sumProba = 0.;
			for ( int r = 0; r < probabilities.getRowDimension(); r++ )
				sumProba += probabilities.get( r, 0 );

			sumLogProbas += Math.log( sumProba );
		}
		return -sumLogProbas;
	}
}
