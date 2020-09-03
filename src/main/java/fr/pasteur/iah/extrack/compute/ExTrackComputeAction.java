package fr.pasteur.iah.extrack.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
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
		optimizer.step = 0.01;

		final double[] parameters = new double[] {
				localizationError,
				diffusionLengths[ 0 ],
				diffusionLengths[ 1 ],
				Fs[ 0 ],
				probabilityOfUnbinding };
		final double tolfx = 1e-6;
		final double tolx = 1e-6;

		System.out.println( "Starting optmization." );
		optimizer.optimize(
				new NegativeLikelihoodFunction( Cs, nbSubSteps, doFrame, frameLen ),
				parameters,
				tolfx, tolx );
		System.out.println( "Optimization done." );

		System.out.println( String.format( "%30s: %10.5f", "localizationError", parameters[ 0 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "diffusionLengths_0", parameters[ 1 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "diffusionLengths_1", parameters[ 2 ] ) );
		System.out.println( String.format( "%30s: %10.5f", "Fs0", parameters[ 3 ] ) );
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
		 * Python implementation values.
		 */

		final double pyLocalizationError = 0.0201;
		final double pyD0 = 1.225e-5;
		final double dt = 0.02; // s.
		final double pyDiffusionLength0 = Math.sqrt( 2. * pyD0 * dt );
		final double pyD1 = 0.0996;
		final double pyDiffusionLength1 = Math.sqrt( 2. * pyD1 * dt );
		final double pyF0 =  0.6894;
		final double pyProbabilityOfUnbinding = 0.0844;
		final double[] pyValues = new double[] {
				pyLocalizationError,
				pyDiffusionLength0,
				pyDiffusionLength1,
				pyF0,
				pyProbabilityOfUnbinding
		};

		/*
		 * Load data.
		 */

		final String trackFile = "samples/sim_tracks.npy";
		final double[][] data = NumPyReader.readFile( trackFile );
		final Map< Integer, Matrix > tracks = new HashMap<>();

		int idx = 0;
		int trackID = ( int ) data[ 3 ][ 0 ];
		for ( int i = 0; i < data[ 0 ].length; i++ )
		{
			if ( trackID != data[ 3 ][ i ] || i == ( data[ 0 ].length - 1 ) )
			{
				// We changed track id. Backtrack to the start of it.

				final int nRows = i - idx;
				final Matrix cs = new Matrix( nRows, 2 );
				for ( int r = 0; r < nRows; r++ )
				{
					cs.set( r, 0, data[ 0 ][ i - nRows + r ] );
					cs.set( r, 1, data[ 0 ][ i - nRows + r ] );
				}
				tracks.put( Integer.valueOf( trackID ), cs );
				idx = i;
				trackID = ( int ) data[ 3 ][ i ];
			}
		}

		/*
		 * Neg likelihood for the Python implementation.
		 */

		final int frameLen = 10;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = true;
		final NegativeLikelihoodFunction fun = new NegativeLikelihoodFunction( tracks, nbSubSteps, doFrame, frameLen );

		final double pyNegLoLH = fun.evaluate( pyValues );
		System.out.println( "Expected Python implementation NLLH: " + pyNegLoLH );

		/*
		 * Ground truth.
		 */

		final double gtLocalizationError = 0.02;
		final double gtD0 = 1.e-10;
		final double gtDiffusionLength0 = Math.sqrt( 2. * gtD0 * dt );
		final double gtD1 = 0.1;
		final double gtDiffusionLength1 = Math.sqrt( 2. * gtD1 * dt );
		final double gtF0 =  2. / 3.;
		final double gtProbabilityOfUnbinding = 0.1;
		final double[] gtValues = new double[] {
				pyLocalizationError,
				pyDiffusionLength0,
				pyDiffusionLength1,
				pyF0,
				pyProbabilityOfUnbinding
		};

		/*
		 * Check values.
		 */

		final Matrix track = tracks.get( 0 );
		final NegativeLikelihoodFunction funCheck = new NegativeLikelihoodFunction( Collections.singletonMap( 0, track ), nbSubSteps, doFrame, frameLen );
		final double gtNLLH = funCheck.evaluate( gtValues );
		System.out.println( "Expected GT NLLH: " + gtNLLH );


		/*
		 * Optimize.
		 */

//		final double localizationError = 0.03;
//		final double[] diffusionLengths = new double[] { 1e-5, 0.2 };
//		final double[] Fs = new double[] { 0.6, 0.4 };
//		final double probabilityOfUnbinding = 0.15;
//		final double probabilityOfBinding = 0.25;
//
//		new ExTrackComputeAction().execute(
//				tracks,
//				localizationError,
//				diffusionLengths,
//				Fs,
//				probabilityOfUnbinding,
//				probabilityOfBinding,
//				nbSubSteps,
//				doFrame,
//				frameLen,
//				doPred );
	}

}
