package fr.pasteur.iah.extrack.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import Jama.Matrix;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fr.pasteur.iah.extrack.numpy.NumPyReader;
import pal.math.ConjugateDirectionSearch;

public class ExTrackComputeAction extends AbstractTMAction
{

	public static final String INFO_TEXT = "TODO";

	public static final String KEY = "COMPUTE_EXTRACK_PROBABILITIES";

	public static final ImageIcon ICON = null; // ExTrackImporterPanel.ICON;

	public static final String NAME = "Compute ExTrack probabilities";

	@Override
	public void execute( final TrackMate trackmate )
	{	}

	public void execute(
			final Map< Integer, Matrix > Cs,
			final double localizationError,
			final double diffusionLength0,
			final double diffusionLength1,
			final double F0,
			final double probabilityOfUnbinding,
			final int nbSubSteps,
			final boolean doFrame,
			final int frameLen,
			final boolean doPred )
	{

		/*
		 * Perform optimization. Optimizer is Powell optimizer updated by Brent.
		 */
		final ConjugateDirectionSearch optimizer = new ConjugateDirectionSearch();
		optimizer.prin = 2;

		final double[] parameters = new double[] {
				localizationError,
				diffusionLength0,
				diffusionLength1,
				F0,
				probabilityOfUnbinding };
		final double tolfx = 1e-6;
		final double tolx = 1e-6;

		final NegativeLikelihoodFunction fun = new NegativeLikelihoodFunction( Cs, nbSubSteps, doFrame, frameLen );

		System.out.println( "Value of function with start parameters: " + fun.evaluate( parameters ) ); // DEBUG



		System.out.println( "Starting optimization." );
		optimizer.optimize(
				fun,
				parameters,
				tolfx, tolx );
		System.out.println( "Optimization done." );

		System.out.println( String.format( "%30s: %10.5f", "localizationError", parameters[ 0 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "diffusionLength0", parameters[ 1 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "diffusionLength1", parameters[ 2 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "F0", parameters[ 3 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "probabilityOfUnbinding", parameters[ 4 ] ) );
	}

	@Plugin( type = TrackMateActionFactory.class )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public TrackMateAction create( final TrackMateGUIController controller )
		{
			return new ExTrackComputeAction();
		}

		@Override
		public ImageIcon getIcon()
		{
			return ICON;
		}

		@Override
		public String getName()
		{
			return NAME;
		}

	}

	public static void main( final String[] args ) throws FileNotFoundException, IOException
	{

		/*
		 * Load data.
		 */

		final String trackFile = "samples/sim_tracks.npy";
		final Map< Integer, Matrix > tracks = NumPyReader.readTracks( trackFile );

		/*
		 * Neg likelihood for the Python implementation.
		 */

		final int frameLen = 10;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = true;

		/*
		 * Optimize.
		 */

		final double localizationError = 0.03;
		final double diffusionLength0 = 1e-10;
		final double diffusionLength1 = 0.2;
		final double F0 = 0.6;
		final double probabilityOfUnbinding = 0.15;

		new ExTrackComputeAction().execute(
				tracks,
				localizationError,
				diffusionLength0,
				diffusionLength1,
				F0,
				probabilityOfUnbinding,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred );
	}

}
