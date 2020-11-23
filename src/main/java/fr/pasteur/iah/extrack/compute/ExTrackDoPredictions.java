package fr.pasteur.iah.extrack.compute;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import Jama.Matrix;
import fiji.plugin.trackmate.FeatureModel;
import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;
import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.TrackModel;
import fr.pasteur.iah.extrack.trackmate.ExTrackEdgeFeatures;
import fr.pasteur.iah.extrack.trackmate.ExTrackProbabilitiesFeature;

public class ExTrackDoPredictions implements Runnable
{

	private final ExTrackParameters params;

	private final Logger logger;

	private final TrackMate trackmate;

	public ExTrackDoPredictions(
			final ExTrackParameters params,
			final TrackMate trackmate,
			final Logger logger )
	{
		this.params = params;
		this.trackmate = trackmate;
		this.logger = logger;
	}

	@Override
	public void run()
	{
		// TODO We also need to set frameLen and nbSubSteps
		final int frameLen = 5;
		// nbSubSteps = 1 for prediction.
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = true;

		final TrackState trackState = new TrackState(
				params.localizationError,
				params.diffusionLength0,
				params.diffusionLength1,
				params.F0,
				params.probabilityOfUnbinding,
				nbSubSteps,
				doFrame,
				frameLen,
				doPred );

		final Model model = trackmate.getModel();
		final TrackModel trackModel = model.getTrackModel();
		final FeatureModel featureModel = model.getFeatureModel();
		
		final int nTracks = trackModel.nTracks( true );
		int index = 0;
		for ( final Integer trackID : trackModel.trackIDs( true ) )
		{
			final List< Spot > track = new ArrayList<>( trackModel.trackSpots( trackID ) );
			track.sort( Spot.frameComparator );

			final Matrix C = new Matrix( track.size(), 2 );
			for ( int r = 0; r < track.size(); r++ )
			{
				final Spot spot = track.get( r );
				C.set( r, 0, spot.getDoublePosition( 0 ) );
				C.set( r, 1, spot.getDoublePosition( 1 ) );
			}

			final Matrix[] matrices = trackState.eval( C );
			final Matrix predictions = matrices[ 1 ];

			for ( int r = 0; r < track.size(); r++ )
			{
				final double stuckProba = predictions.get( r, 0 );
				final double diffusiveProba = predictions.get( r, 1 );

				final Spot spot = track.get( r );
				spot.putFeature( ExTrackProbabilitiesFeature.P_DIFFUSIVE, diffusiveProba );
				spot.putFeature( ExTrackProbabilitiesFeature.P_STUCK, stuckProba );

				final Set< DefaultWeightedEdge > edges = trackModel.edgesOf( spot );
				for ( final DefaultWeightedEdge edge : edges )
				{
					if ( trackModel.getEdgeTarget( edge ).equals( spot ) )
					{
						featureModel.putEdgeFeature( edge, ExTrackEdgeFeatures.P_DIFFUSIVE, diffusiveProba );
						featureModel.putEdgeFeature( edge, ExTrackEdgeFeatures.P_STUCK, stuckProba );
					}
				}
			}
			logger.setProgress( ( double ) ( ++index ) / nTracks );
		}
	}
}
