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

		lowerBound[ 0 ] = 0.005;
		upperBound[ 0 ] = 100.; // um

		/*
		 * 1. diffusionLengths0
		 */

		lowerBound[ 1 ] = 1e-100;
		upperBound[ 1 ] = 10.; // um

		/*
		 * 2. diffusionLengths1
		 */

		lowerBound[ 2 ] = lowerBound[ 1 ];
		upperBound[ 2 ] = upperBound[ 1 ];

		/*
		 * 3. F0.
		 */

		lowerBound[ 3 ] = 0.01;
		upperBound[ 3 ] = 0.99;

		/*
		 * 4. probabilityOfUnbinding
		 */

		lowerBound[ 4 ] = 0.01;
		upperBound[ 4 ] = 0.99;
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
			final Map< Integer, Matrix > tracks,
			final int nbSubSteps,
			final boolean doFrame,
			final int frameLen )
	{
		final double localizationError = params[ 0 ];
		final double diffusionLength0 = params[ 1 ];
		final double diffusionLength1 = params[ 2 ];
		final double F0 = params[ 3 ];
		final double probabilityOfUnbindingContinuous = params[ 4 ];

		final boolean doPred = false;

		final double probabilityOfBindingContinuous = F0 / ( 1 - F0 ) * probabilityOfUnbindingContinuous;
		final double[] diffusionLengths = new double[] { diffusionLength0, diffusionLength1 };
		final double F1 = 1. - F0;
		final double[] Fs = new double[] { F0, F1 };

		double sumLogProbas = 0;
		final TrackState state = new TrackState(
				localizationError,
				diffusionLengths,
				Fs,
				probabilityOfUnbindingContinuous,
				probabilityOfBindingContinuous,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred );
		for ( final Integer trackID : tracks.keySet() )
		{
			final Matrix track = tracks.get( trackID );
			final Matrix probabilities = state.eval( track );

			double sumProba = 0.;
			for ( int r = 0; r < probabilities.getRowDimension(); r++ )
				sumProba += probabilities.get( r, 0 );

			sumLogProbas += Math.log( sumProba );
		}

		return -sumLogProbas;
	}
}
