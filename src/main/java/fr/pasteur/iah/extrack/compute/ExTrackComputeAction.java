package fr.pasteur.iah.extrack.compute;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import Jama.Matrix;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import pal.math.ConjugateDirectionSearch;

public class ExTrackComputeAction extends AbstractTMAction
{

	public static final String INFO_TEXT = "TODO";

	public static final String KEY = "COMPUTE_EXTRACK_PROBABILITIES";

	public static final ImageIcon ICON = null; // ExTrackImporterPanel.ICON;

	public static final String NAME = "Compute ExTrack probabilities";

	@Override
	public void execute( final TrackMate trackmate )
	{

		final Model model = trackmate.getModel();
		final TrackModel trackModel = model.getTrackModel();
		final Set< Integer > trackIDs = trackModel.trackIDs( true );

		// Parameter initialization.
		final double localizationError = 0.02; // µm
		final double[] diffusionLengths = new double[] { 1e-9, 0.1 }; // µm
		final double[] Fs = new double[] { 0.6, 0.4 };
		final double probabilityOfUnbinding = 0.15;
		final double probabilityOfBinding = 0.1;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final int frameLen = 10;
		final boolean doPred = false;

		// Form detection matrices.
		final Map< Integer, Matrix > Cs = new HashMap<>();
		for ( final Integer trackID : trackIDs )
		{

			final Set< Spot > ts = trackModel.trackSpots( trackID );
			final List< Spot > spots = new ArrayList<>( ts );
			spots.sort( Spot.frameComparator );

			final Matrix cs = new Matrix( spots.size(), 3 );
			for ( int i = 0; i < spots.size(); i++ )
			{
				final Spot spot = spots.get( i );
				cs.set( i, 0, spot.getDoublePosition( 0 ) );
				cs.set( i, 1, spot.getDoublePosition( 1 ) );
				cs.set( i, 2, spot.getDoublePosition( 2 ) );
			}
			Cs.put( trackID, cs );
		}

		execute(
				Cs,
				localizationError,
				diffusionLengths,
				Fs,
				probabilityOfUnbinding,
				probabilityOfBinding,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred );
	}

	public void execute(
			final Map< Integer, Matrix > Cs,
			final double localizationError,
			final double[] diffusionLengths,
			final double[] Fs,
			final double probabilityOfUnbinding,
			final double probabilityOfBinding,
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

		final double[] startingPoint = new double[] {
				localizationError,
				diffusionLengths[ 0 ],
				diffusionLengths[ 1 ],
				Fs[ 0 ],
				probabilityOfUnbinding };
		final double tolfx = 1e-6;
		final double tolx = 1e-6;;

		System.out.println( "Starting optmization." );
		optimizer.optimize(
				new NegativeLikelihoodFunction( Cs, nbSubSteps, doFrame, frameLen ),
				startingPoint,
				tolfx, tolx );
		System.out.println( "Optmization done." );
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

	public static void main( final String[] args )
	{
		final Matrix cs0 = new Matrix( new double[][] {
				{ 0.1, -0.1 },
				{ 0.11, -0.12 },
				{ 0.13, -0.09 },
				{ 0.2, -0.05 },
				{ 0.1, 0.5 } } );
		final double localizationError = 0.03;
		final double[] diffusionLengths = new double[] { 1e-10, 0.05 };
		final double[] Fs = new double[] { 0.6, 0.4 };
		final double probabilityOfUnbinding = 0.1;
		final double probabilityOfBinding = 0.2;
		final int frameLen = 3;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = true;

		new ExTrackComputeAction().execute(
				Collections.singletonMap( Integer.valueOf( 0 ), cs0 ),
				localizationError,
				diffusionLengths,
				Fs,
				probabilityOfUnbinding,
				probabilityOfBinding,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred );
	}

}
