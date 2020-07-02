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

			int currentStep = 1;

			final Matrix currBs = getAllBs( nbSubSteps );
			final int currNbBs = currBs.getRowDimension();
			System.out.println( "currBs:" ); // DEBUG
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
			 * LP.
			 */

			final Matrix LP = LT.copy();
			System.out.println( "LP:" ); // DEBUG
			LP.print( 7, 2 ); // DEBUG

			// Repeat LP.

			final Matrix LP2 = repeatLines( LP, nbSubSteps + 1 );
			System.out.println( "LP2:" ); // DEBUG
			LP2.print( 7, 2 ); // DEBUG

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

			/*
			 * Last detection of the track.
			 */

			final Matrix currC = new Matrix( 1, track.getColumnDimension() );
			for ( int c = 0; c < track.getColumnDimension(); c++ )
				currC.set( 0, c, track.get( track.getRowDimension() - 1, c ) );

			System.out.println( "currC:" ); // DEBUG
			currC.print( 7, 2 ); // DEBUG

			/*
			 * Initialize true localization density probability matrices after
			 * displacement.
			 */

			final Matrix[] K = firstLogIntegralDiff( currC, localizationError, currDs3 );
			final Matrix Km = K[ 0 ];
			final Matrix Ks = K[ 1 ];

			System.out.println( "Km:" ); // DEBUG
			Km.print( 7, 2 ); // DEBUG

			System.out.println( "Ks:" ); // DEBUG
			Ks.print( 7, 2 ); // DEBUG

			/*
			 * Increment current step.
			 */

			currentStep++;

			/*
			 * Duplicate Km & Ks.
			 */

			final Matrix Km2 = repeatLines( Km, nbSubSteps + 1 );
			System.out.println( "Km2:" ); // DEBUG
			Km2.print( 7, 2 ); // DEBUG

			final Matrix Ks2 = repeatLines( Ks, nbSubSteps + 1 );
			System.out.println( "Ks2:" ); // DEBUG
			Km2.print( 7, 2 ); // DEBUG

			/*
			 *
			 */

			final int removeStep = 0;

			/*
			 * Big iteration loop.
			 */

			Matrix KmLoop = Km2;
			Matrix KsLoop = Ks2;
			Matrix LPLoop = LP2;

			while ( currentStep < nbLocs - 1 && currentStep < 6 )
			{
				System.out.println( "\n\n____________________________" ); // DEBUG
				System.out.println( "current step: " + currentStep ); // DEBUG

				/*
				 *
				 */

				System.out.println( "len: " + ( currentStep * nbSubSteps + 1 - removeStep ) ); // DEBUG
				final Matrix currBsLoop = getAllBs( currentStep * nbSubSteps + 1 - removeStep );

				System.out.println( "currBsLoop: " + currBsLoop.getRowDimension() + " x " + currBsLoop.getColumnDimension() ); // DEBUG
//				currBsLoop.print( 3, 0 );

				/*
				 * currStatesLoop
				 */

				final Matrix currStatesLoop = new Matrix( currBsLoop.getRowDimension(), nbSubSteps + 1 );
				for ( int r = 0; r < currStatesLoop.getRowDimension(); r++ )
					for ( int c = 0; c < nbSubSteps + 1; c++ )
						currStatesLoop.set( r, c, currBsLoop.get( r, c ) );

				System.out.println( "currStatesLoop: " + currStatesLoop.getRowDimension() + " x " + currStatesLoop.getColumnDimension() ); // DEBUG
//				currStatesLoop.print( 3, 0 );

				/*
				 * Diffusion length matrix - Loop
				 */

				final Matrix currDsLoop = new Matrix( currStatesLoop.getRowDimension(), currStatesLoop.getColumnDimension() );
				for ( int r = 0; r < currDsLoop.getRowDimension(); r++ )
				{
					for ( int c = 0; c < currDsLoop.getColumnDimension(); c++ )
					{
						final double val = currStatesLoop.get( r, c ) == 0. ? diffusionLengths[ 0 ] : diffusionLengths[ 1 ];
						currDsLoop.set( r, c, val );
					}
				}
				System.out.println( "currDsLoop: " + currDsLoop.getRowDimension() + " x " + currDsLoop.getColumnDimension() ); // DEBUG
//				currDsLoop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate currDsLoop.
				 */

				final Matrix currDs2Loop = new Matrix( currDsLoop.getRowDimension(), currDsLoop.getColumnDimension() - 1 );
				for ( int r = 0; r < currDs2Loop.getRowDimension(); r++ )
				{
					for ( int c = 0; c < currDs2Loop.getColumnDimension(); c++ )
					{
						final double val1 = currDsLoop.get( r, c );
						final double val2 = currDsLoop.get( r, c + 1 );
						currDs2Loop.set( r, c, ( val1 + val2 ) / 2. );
					}
				}
				System.out.println( "currDs2Loop: " + currDs2Loop.getRowDimension() + " x " + currDs2Loop.getColumnDimension() ); // DEBUG
//				currDs2Loop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate currDsLoop second time.
				 */

				final Matrix currDs3Loop = new Matrix( currDs2Loop.getRowDimension(), 1 );
				for ( int r = 0; r < currDs2Loop.getRowDimension(); r++ )
				{
					double sumSq = 0.;
					for ( int c = 0; c < currDs2Loop.getColumnDimension(); c++ )
					{
						final double val = currDs2Loop.get( r, c );
						sumSq += val * val;
					}
					final double meanSqRootSumSq = Math.sqrt( sumSq / currDs2Loop.getColumnDimension() );
					currDs3Loop.set( r, 0, meanSqRootSumSq );
				}

				System.out.println( "currDs3Loop: " + currDs3Loop.getRowDimension() + " x " + currDs3Loop.getColumnDimension() );
//				currDs3Loop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate LT.
				 */

				final Matrix LTloop = getTsFromBs( currStatesLoop, TrMat );

				System.out.println( "LTloop: " + LTloop.getRowDimension() + " x " + LTloop.getColumnDimension() );
//				LTloop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate Km - loop.
				 */

				KmLoop = repeatLines( KmLoop, ( int ) Math.pow( 2, nbSubSteps ) );
				System.out.println( "KmLoop: " + KmLoop.getRowDimension() + " x " + KmLoop.getColumnDimension() );
//				KmLoop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate Ks - loop.
				 */

				System.out.println( "KsLoop before: " + ( ( int ) Math.pow( 2, nbSubSteps ) ) + " - " + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
				KsLoop = repeatLines( KsLoop, ( int ) Math.pow( 2, nbSubSteps ) );
				System.out.println( "KsLoop after: " + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
//				KsLoop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate LP - loop.
				 */

				LPLoop = repeatLines( LPLoop, ( int ) Math.pow( 2, nbSubSteps ) );
				System.out.println( "LPLoop: " + LPLoop.getRowDimension() + " x " + LPLoop.getColumnDimension() );
//				LPLoop.print( 7, 2 ); // DEBUG

				/*
				 *
				 */

				final Matrix currCLoop = new Matrix( 1, nbDims );
				for ( int c = 0; c < nbDims; c++ )
				{
					final int r = nbLocs - currentStep;
					currCLoop.set( 0, c, track.get( r, c ) );
				}
				System.out.println( "currCLoop:" + currCLoop.getRowDimension() + " x " + currCLoop.getColumnDimension() );
//				currCLoop.print( 7, 2 ); // DEBUG

				final Matrix[] Kloop = logIntegralDiff( currCLoop, localizationError, currDs3Loop, KmLoop, KsLoop );
				KmLoop = Kloop[ 0 ];
				KsLoop = Kloop[ 1 ];
				final Matrix LC = Kloop[ 2 ];

				System.out.println( "new - KmLoop:" + KmLoop.getRowDimension() + " x " + KmLoop.getColumnDimension() );
//				KmLoop.print( 7, 2 ); // DEBUG
				System.out.println( "new - KsLoop:" + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
//				KsLoop.print( 7, 2 ); // DEBUG
				System.out.println( "new - LC:" + LC.getRowDimension() + " x " + LC.getColumnDimension() );
//				LC.print( 7, 2 ); // DEBUG

				/*
				 * Iterate.
				 */

				currentStep++;
			}

			break; // FIXME
		}

	}

	private static Matrix[] logIntegralDiff(
			final Matrix currCLoop,
			final double localizationError,
			final Matrix currDs3Loop,
			final Matrix KmLoop,
			final Matrix KsLoop )
	{
		final int nbDims = currCLoop.getColumnDimension();

		final Matrix Km = new Matrix( KmLoop.getRowDimension(), KmLoop.getColumnDimension() );
		final Matrix Ks = new Matrix( KsLoop.getRowDimension(), 1 );
		final Matrix LC = new Matrix( KsLoop.getRowDimension(), 1 );

		for ( int r = 0; r < KmLoop.getRowDimension(); r++ )
		{
			for ( int c = 0; c < KmLoop.getColumnDimension(); c++ )
			{
				final double km = KmLoop.get( r, c );
				final double ks = KsLoop.get( r, 0 );
				final double val = ( km * localizationError * localizationError + currCLoop.get( 0, c ) * ks * ks )
						/ ( localizationError * localizationError + ks * ks );
				Km.set( r, c, val );
			}
		}

		for ( int r = 0; r < KsLoop.getRowDimension(); r++ )
		{
			final double ks = KsLoop.get( r, 0 );
			final double cd = currDs3Loop.get( r, 0 );
			final double val = Math.sqrt(
					( cd * cd * localizationError * localizationError
							+ cd * cd * ks * ks
							+ localizationError * localizationError * ks * ks )
							/ ( localizationError * localizationError + ks * ks ) );
			Ks.set( r, 0, val );
		}

		for ( int r = 0; r < LC.getRowDimension(); r++ )
		{
			final double ks = KsLoop.get( r, 0 );
			final double ksOut = Ks.get( r, 0 );
			final double cd = currDs3Loop.get( r, 0 );

			double sumKm = 0.;
			for ( int c = 0; c < KmLoop.getColumnDimension(); c++ )
			{
				final double cc = currCLoop.get( 0, c );
				final double kmOut = Km.get( r, c );
				final double km = KmLoop.get( r, c );
				sumKm += ( kmOut * kmOut / ( 2 * ksOut * ksOut )
						- ( km * km * localizationError * localizationError + ks * ks * cc * cc + ( km - cc ) * ( km - cc ) * cd * cd )
								/ ( 2 * ksOut * ksOut * ( localizationError * localizationError + ks * ks ) ) );
			}

			LC.set( r, 0, sumKm + nbDims * Math.log( 1. /
					( Math.sqrt( 2 * Math.PI * ( localizationError * localizationError + ks * ks ) ) ) ) );

		}

		return new Matrix[] { Km, Ks, LC };
	}

	private static Matrix repeatLines( final Matrix M, final int n )
	{
		final Matrix N = new Matrix( n * M.getRowDimension(), M.getColumnDimension() );
		for ( int c = 0; c < M.getColumnDimension(); c++ )
		{
			for ( int r = 0; r < M.getRowDimension(); r++ )
			{
				final int nr = n * r;
				for ( int inc = 0; inc < n; inc++ )
				{
					final double val = M.get( r, c );
					N.set( nr + inc, c, val );
				}
			}
		}
		return N;
	}

	private static Matrix[] firstLogIntegralDiff( final Matrix currC, final double localizationError, final Matrix currDs3 )
	{
		final Matrix Km = new Matrix( currDs3.getRowDimension(), currC.getColumnDimension() );
		for ( int r = 0; r < currDs3.getRowDimension(); r++ )
			for ( int c = 0; c < currC.getColumnDimension(); c++ )
				Km.set( r, c, currC.get( 0, c ) );

		final Matrix Ks = new Matrix( currDs3.getRowDimension(), 1 );
		for ( int r = 0; r < currDs3.getRowDimension(); r++ )
		{
			final double valCurrDs = currDs3.get( r, 0 );
			final double val = Math.sqrt( localizationError * localizationError + valCurrDs * valCurrDs );
			Ks.set( r, 0, val );
		}

		return new Matrix[] { Km, Ks };
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
