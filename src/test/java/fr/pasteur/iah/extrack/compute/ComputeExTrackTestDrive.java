package fr.pasteur.iah.extrack.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import Jama.Matrix;
import fr.pasteur.iah.extrack.numpy.NumPyReader;

public class ComputeExTrackTestDrive
{
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

		final int frameLen = 8;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = false;

		/*
		 * Optimize.
		 */

		final double localizationError = 0.20056507;
		final double diffusionLength0 = 0.01048;
		final double diffusionLength1 = 0.62981;
		final double F0 = 0.06869082094;
		final double probabilityOfUnbinding = 0.849915476;
//			final double localizationError = 0.020056507;
//			final double diffusionLength0 = 0.001048;
//			final double diffusionLength1 = 0.062981;
//			final double F0 = 0.6869082094;
//			final double probabilityOfUnbinding = 0.0849915476;

//		final TrackState trackstate = new TrackState(
//				localizationError,
//				diffusionLength0,
//				diffusionLength1,
//				F0,
//				probabilityOfUnbinding,
//				nbSubSteps,
//				doFrame,
//				frameLen,
//				doPred );

//			for ( final Integer id : tracks.keySet() )
//			{
//				final Matrix track = tracks.get( id );
		//
////				System.out.println( "Track " + id );
////				track.print( 7, 4 );
		//
//				final double sumProba = trackstate.sumLogProbabilities( track );
//				System.out.println( String.format( "%4d -> %12.8f", id, sumProba ) );
//			}

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
