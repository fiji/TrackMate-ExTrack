package fr.pasteur.iah.extrack.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Jama.Matrix;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Spot;

public class ExTrackUtil
{

	public static final DecimalFormat FORMAT = new DecimalFormat( "0.#####E0" );

	public static final Map< Integer, Matrix > toMatrix( final Model model )
	{
		final Map< Integer, Matrix > Cs = new HashMap<>( model.getTrackModel().nTracks( true ) );
		for ( final Integer trackID : model.getTrackModel().trackIDs( true ) )
		{
			final List< Spot > track = new ArrayList<>( model.getTrackModel().trackSpots( trackID ) );
			track.sort( Spot.frameComparator );

			final Matrix C = new Matrix( track.size(), 2 );
			for ( int r = 0; r < track.size(); r++ )
			{
				final Spot spot = track.get( r );
				C.set( r, 0, spot.getDoublePosition( 0 ) );
				C.set( r, 1, spot.getDoublePosition( 1 ) );
			}
			Cs.put( trackID, C );
		}
		return Cs;
	}
}
