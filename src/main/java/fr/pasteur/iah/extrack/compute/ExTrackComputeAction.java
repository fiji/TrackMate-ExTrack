package fr.pasteur.iah.extrack.compute;

import java.util.Map;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import Jama.Matrix;
import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fr.pasteur.iah.extrack.plugin.ExTrackImporterPanel;
import fr.pasteur.iah.extrack.util.ExTrackUtil;

public class ExTrackComputeAction extends AbstractTMAction
{

	public static final String INFO_TEXT = "Please Francois write this. TODO.";

	public static final String KEY = "COMPUTE_EXTRACK_PROBABILITIES";

	public static final ImageIcon ICON = ExTrackUtil.scaleImage( ExTrackImporterPanel.ICON, 16, 16 );

	public static final String NAME = "Compute ExTrack probabilities";

	@Override
	public void execute( final TrackMate trackmate )
	{
//		final int frameLen = 4;
//		final int nbSubSteps = 2;
		final int frameLen = 5;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = true;

		/*
		 * Optimize.
		 */

//		final double localizationError = 1.;
//		final double diffusionLength0 = 1e-9;
//		final double diffusionLength1 = Math.sqrt( 2. );
//		final double F0 = 2. / 3.;
//		final double probabilityOfUnbinding = 0.1;

//		final double localizationError = 0.20056507;
//		final double diffusionLength0 = 0.01048;
//		final double diffusionLength1 = 0.62981;
//		final double F0 = 0.06869082094;
//		final double probabilityOfUnbinding = 0.849915476;

		final double localizationError = 0.3;
		final double diffusionLength0 = 0.08;
		final double diffusionLength1 = 0.08;
		final double F0 = 0.1;
		final double probabilityOfUnbinding = 0.9;

		execute(
				trackmate,
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

	public void execute(
			final TrackMate trackmate,
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
		execute( ExTrackUtil.toMatrix( trackmate.getModel() ),
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
		final ConjugateDirectionSearch optimizer = new ConjugateDirectionSearch( logger );

		final double[] parameters = new double[] {
				localizationError,
				diffusionLength0,
				diffusionLength1,
				F0,
				probabilityOfUnbinding };
		final double tolfx = 1e-6;
		final double tolx = 1e-6;

		final NegativeLikelihoodFunction fun = new NegativeLikelihoodFunction( Cs, nbSubSteps, doFrame, frameLen, doPred );
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
}
