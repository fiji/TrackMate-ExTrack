package fr.pasteur.iah.extrack.compute;

import java.util.Map;

import Jama.Matrix;

public class ExTrackComputer
{

	/**
	 *
	 * @param Cs
	 * @param localizationError
	 * @param diffusionLengths
	 * @param Fs
	 * @param probabilityOfUnbinding
	 * @param probabilityOfBinding
	 * @param nbSubSteps
	 * @param doFrame
	 * @param frameLen
	 * @param doPred
	 */
	public static void pCsInterBoundState(
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
		final int nbTracks = Cs.size();

		for ( final Integer trackID : Cs.keySet() )
		{
			final Matrix track = Cs.get( trackID );
			final int nbLocs = track.getRowDimension();
			final int nbDims = track.getColumnDimension();

			if ( doPred )
			{
				// TODO
			}

			final Matrix TrMat = new Matrix( 2, 2 );
			TrMat.set( 0, 0, 1 - probabilityOfUnbinding );
			TrMat.set( 0, 1, probabilityOfUnbinding );
			TrMat.set( 1, 0, probabilityOfBinding );
			TrMat.set( 1, 1, 1 - probabilityOfBinding );

			final int currentStep = 1;

			final Matrix currBs = getAllBs( nbSubSteps );
			final int currNbBs = currBs.getRowDimension();
			System.out.println( "currNbBs:" ); // DEBUG
			currBs.print( 2, 0 ); // DEBUG

			/*
			 * States matrix.
			 */

			final Matrix currStates = currBs.copy();

			/*
			 * Log of transition probabilities.
			 */

			final Matrix LT = getTsFromBs( currStates, TrMat );
			System.out.println( "LT:" ); // DEBUG
			LT.print( 7, 2 ); // DEBUG

			/*
			 * Log of probabilities to be bound at the beginning.
			 */

			final Matrix LF = new Matrix( currStates.getRowDimension(), 1 );
			for ( int r = 0; r < LF.getRowDimension(); r++ )
			{
				final double val = currStates.get( r, currStates.getColumnDimension() - 1 ) == 0. ? Fs[ 0 ] : Fs[ 1 ];
				LF.set( r, 0, Math.log( val ) );
			}
			System.out.println( "LF:" ); // DEBUG
			LF.print( 7, 2 ); // DEBUG

			/*
			 * Diffusion length matrix.
			 */

			final Matrix currDs = new Matrix( currStates.getRowDimension(), currStates.getColumnDimension() );
			for ( int r = 0; r < currDs.getRowDimension(); r++ )
			{
				for ( int c = 0; c < currDs.getColumnDimension(); c++ )
				{
					final double val = currStates.get( r, c ) == 0. ? diffusionLengths[ 0 ] : diffusionLengths[ 1 ];
					currDs.set( r, c, val );
				}
			}
			System.out.println( "currDs:" ); // DEBUG
			currDs.print( 7, 2 ); // DEBUG

			/*
			 * Iterate currDs.
			 */

			final Matrix currDs2 = new Matrix( currDs.getRowDimension(), currDs.getColumnDimension() - 1 );
			for ( int r = 0; r < currDs2.getRowDimension(); r++ )
			{
				for ( int c = 0; c < currDs2.getColumnDimension(); c++ )
				{
					final double val1 = currDs.get( r, c );
					final double val2 = currDs.get( r, c + 1 );
					currDs2.set( r, c, ( val1 + val2 ) / 2. );
				}
			}
			System.out.println( "currDs2:" ); // DEBUG
			currDs2.print( 7, 2 ); // DEBUG

			/*
			 * Iterate currDs second time.
			 */

			final Matrix currDs3 = new Matrix( currDs2.getRowDimension(), 1 );
			for ( int r = 0; r < currDs2.getRowDimension(); r++ )
			{
				double sumSq = 0.;
				for ( int c = 0; c < currDs2.getColumnDimension(); c++ )
				{
					final double val = currDs2.get( r, c );
					sumSq += val * val;
				}
				final double meanSqRootSumSq = Math.sqrt( sumSq / currDs2.getColumnDimension() );
				currDs3.set( r, 0, meanSqRootSumSq );
			}
			System.out.println( "currDs3:" ); // DEBUG
			currDs3.print( 7, 2 ); // DEBUG

			break; // FIXME
		}

	}

	private static Matrix getTsFromBs( final Matrix currStates, final Matrix TrMat )
	{
		final Matrix LTtemp = new Matrix( currStates.getRowDimension(), currStates.getColumnDimension() - 1 );
		for ( int r = 0; r < LTtemp.getRowDimension(); r++ )
		{
			for ( int c = 0; c < LTtemp.getColumnDimension(); c++ )
			{
				final int val1 = ( int ) currStates.get( r, c );
				final int val2 = ( int ) currStates.get( r, c + 1 );

				final int stateId = val1 + 2 * val2;
				final double val;
				switch ( stateId )
				{
				case 0:
					val = TrMat.get( 0, 0 );
					break;
				case 1:
					val = TrMat.get( 1, 0 );
					break;
				case 2:
					val = TrMat.get( 0, 1 );
					break;
				case 3:
					val = TrMat.get( 1, 1 );
					break;
				default:
					throw new IllegalStateException( "This state ID should not exist for 2 states: " + stateId );
				}

				LTtemp.set( r, c, val );
			}
		}

		final Matrix LT = new Matrix( currStates.getRowDimension(), 1 );
		for ( int r = 0; r < LT.getRowDimension(); r++ )
		{
			double sum = 0.;
			for ( int c = 0; c < LTtemp.getColumnDimension(); c++ )
				sum += Math.log( LTtemp.get( r, c ) );

			LT.set( r, 0, sum );
		}
		return LT;
	}

	private static Matrix getAllBs( final int nbSubSteps )
	{
		Matrix allBs = new Matrix( 2, 1 );
		allBs.set( 0, 0, 0. );
		allBs.set( 1, 0, 1. );

		for ( int i = 0; i < nbSubSteps; i++ )
		{
			final Matrix nAllBs = new Matrix( 2 * allBs.getRowDimension(), allBs.getColumnDimension() + 1 );

			for ( int r = 0; r < allBs.getRowDimension(); r++ )
			{
				final int nr = 2 * r;
				// 1st column -> set to 0.
				nAllBs.set( nr, 0, 0. );
				nAllBs.set( nr + 1, 0, 1. );

				// Other columns: we copy the previous row.
				for ( int c = 0; c < allBs.getColumnDimension(); c++ )
				{
					nAllBs.set( nr, 1 + c, allBs.get( r, c ) );
					nAllBs.set( nr + 1, 1 + c, allBs.get( r, c ) );
				}
			}
			allBs = nAllBs;
		}

		return allBs;
	}

}
