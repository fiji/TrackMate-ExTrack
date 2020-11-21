package fr.pasteur.iah.extrack.compute;

import java.util.Map;
import java.util.function.Consumer;

import org.scijava.Cancelable;

import Jama.Matrix;
import fiji.plugin.trackmate.Logger;

public class ExTrackParameterOptimizer implements Runnable, Cancelable
{

	private final Logger logger;

	private final ExTrackParameters startPoint;

	private final Map< Integer, Matrix > trackMatrices;

	/*
	 * Perform optimization. Optimizer is Powell optimizer updated by Brent.
	 */
	private final ConjugateDirectionSearch optimizer;

	public ExTrackParameterOptimizer(
			final ExTrackParameters startPoint,
			final Map< Integer, Matrix > trackMatrices,
			final Logger logger,
			final Consumer< double[] > valueWatcher )
	{
		this.startPoint = startPoint;
		this.trackMatrices = trackMatrices;
		this.logger = logger;
		this.optimizer = new ConjugateDirectionSearch( logger, valueWatcher );
	}

	@Override
	public void run()
	{
		// TODO We also need to set frameLen and nbSubSteps
		final int frameLen = 5;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		// No doPred for optimization.
		final boolean doPred = false;

		final double[] parameters = startPoint.toArray();
		final double tolfx = 1e-6;
		final double tolx = 1e-6;

		final NegativeLikelihoodFunction fun = new NegativeLikelihoodFunction( trackMatrices, nbSubSteps, doFrame, frameLen, doPred );
		optimizer.optimize(
				fun,
				parameters,
				tolfx, tolx );

		logger.log( "\n\n-------------------------------------------------------------------------\n", Logger.BLUE_COLOR );
		logger.log( String.format( "%30s: %10.5f\n", "localizationError", parameters[ 0 ] ), Logger.BLUE_COLOR );
		logger.log( String.format( "%30s: %10.5f\n", "diffusionLength0", parameters[ 1 ] ), Logger.BLUE_COLOR );
		logger.log( String.format( "%30s: %10.5f\n", "diffusionLength1", parameters[ 2 ] ), Logger.BLUE_COLOR );
		logger.log( String.format( "%30s: %10.5f\n", "F0", parameters[ 3 ] ), Logger.BLUE_COLOR );
		logger.log( String.format( "%30s: %10.5f\n", "probabilityOfUnbinding", parameters[ 4 ] ), Logger.BLUE_COLOR );
	}

	@Override
	public boolean isCanceled()
	{
		return optimizer.isCanceled();
	}

	@Override
	public void cancel( final String reason )
	{
		optimizer.cancel( reason );
	}

	@Override
	public String getCancelReason()
	{
		return optimizer.getCancelReason();
	}

	public ExTrackParameters getParameters()
	{
		return ExTrackParameters.fromArray( optimizer.getCurrentValue() );
	}
}
