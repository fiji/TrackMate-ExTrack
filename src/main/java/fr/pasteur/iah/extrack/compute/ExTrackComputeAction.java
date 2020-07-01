package fr.pasteur.iah.extrack.compute;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import Jama.Matrix;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackModel;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fiji.plugin.trackmate.io.TmXmlReader;

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

		ExTrackComputer.pCsInterBoundState(
				Cs,
				localizationError,
				diffusionLengths,
				Fs,
				probabilityOfUnbinding,
				probabilityOfBinding,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred);

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
		final File file = new File( "samples/FakeTracks.xml" );
		final TmXmlReader reader = new TmXmlReader( file );
		final Model model = reader.getModel();
		final TrackMate trackmate = new TrackMate( model, new Settings() );



//		ImageJ.main( args );
//		final LoadTrackMatePlugIn_ loader = new LoadTrackMatePlugIn_();
//		loader.run( file.getAbsolutePath() );

		new ExTrackComputeAction().execute( trackmate );
	}

}
