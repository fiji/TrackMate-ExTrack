package fr.pasteur.iah.extrack.compute;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import Jama.Matrix;

public class ExTrackComputer
{

	private static final DecimalFormat format = new DecimalFormat( "0.#####E0" );

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
			int currNbBs = currBs.getRowDimension();
//			System.out.println( "currBs:" ); // DEBUG
//			currBs.print( 2, 0 ); // DEBUG

			/*
			 * States matrix.
			 */

			final Matrix currStates = currBs.copy();

			/*
			 * Log of transition probabilities.
			 */

			final Matrix LT = getTsFromBs( currStates, TrMat );
//			System.out.println( "LT:" ); // DEBUG
//			LT.print( 7, 2 ); // DEBUG

			/*
			 * LP.
			 */

			final Matrix LP = LT.copy();
			System.out.println( "LP:" ); // DEBUG
			LP.print( 7, 4 ); // DEBUG

			// Repeat LP.

			final Matrix LP2 = repeatLines( LP, nbSubSteps + 1 );
//			System.out.println( "LP2:" ); // DEBUG
//			LP2.print( 7, 2 ); // DEBUG

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
//			System.out.println( "currDs:" ); // DEBUG
//			currDs.print( format, 10 ); // DEBUG

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
//			System.out.println( "currDs2:" ); // DEBUG
//			currDs2.print( format, 7 ); // DEBUG

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
			currDs3.print( format, 7 ); // DEBUG

			/*
			 * Last detection of the track.
			 */

			final Matrix currC = new Matrix( 1, track.getColumnDimension() );
			for ( int c = 0; c < track.getColumnDimension(); c++ )
				currC.set( 0, c, track.get( track.getRowDimension() - 1, c ) );

//			System.out.println( "currC:" ); // DEBUG
//			currC.print( 7, 2 ); // DEBUG

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
			Ks.print( 10, 5 ); // DEBUG

			/*
			 * Increment current step.
			 */

			currentStep++;

			/*
			 * Duplicate Km & Ks.
			 */

			final Matrix Km2 = repeatLines( Km, nbSubSteps + 1 );
//			System.out.println( "Km2:" ); // DEBUG
//			Km2.print( 7, 2 ); // DEBUG

			final Matrix Ks2 = repeatLines( Ks, nbSubSteps + 1 );
//			System.out.println( "Ks2:" ); // DEBUG
//			Km2.print( 7, 2 ); // DEBUG

			/*
			 *
			 */

			int removeStep = 0;

			/*
			 * Big iteration loop.
			 */

			Matrix KmLoop = Km2;
			Matrix KsLoop = Ks2;
			Matrix LPloop = LP2;
			Matrix currStatesLoop = null;

			while ( currentStep < nbLocs - 1 && currentStep < 6 )
			{
				System.out.println( "\n\n____________________________" ); // DEBUG
				System.out.println( "current step: " + currentStep ); // DEBUG

				/*
				 *
				 */

				System.out.println( "len: " + ( currentStep * nbSubSteps + 1 - removeStep ) ); // DEBUG
				Matrix currBsLoop = getAllBs( currentStep * nbSubSteps + 1 - removeStep );

//				System.out.println( "currBsLoop: " + currBsLoop.getRowDimension() + " x " + currBsLoop.getColumnDimension() ); // DEBUG
//				currBsLoop.print( 3, 0 );

				/*
				 * currStatesLoop
				 */

				currStatesLoop = new Matrix( currBsLoop.getRowDimension(), nbSubSteps + 1 );
				for ( int r = 0; r < currStatesLoop.getRowDimension(); r++ )
					for ( int c = 0; c < nbSubSteps + 1; c++ )
						currStatesLoop.set( r, c, currBsLoop.get( r, c ) );

//				System.out.println( "currStatesLoop: " + currStatesLoop.getRowDimension() + " x " + currStatesLoop.getColumnDimension() ); // DEBUG
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
//				System.out.println( "currDsLoop: " + currDsLoop.getRowDimension() + " x " + currDsLoop.getColumnDimension() ); // DEBUG
//				currDsLoop.print( format, 7 ); // DEBUG

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
//				System.out.println( "currDs2Loop: " + currDs2Loop.getRowDimension() + " x " + currDs2Loop.getColumnDimension() ); // DEBUG
//				currDs2Loop.print( format, 7 ); // DEBUG

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
				currDs3Loop.print( format, 7 ); // DEBUG

				/*
				 * Iterate LT.
				 */

				final Matrix LTloop = getTsFromBs( currStatesLoop, TrMat );

//				System.out.println( "LTloop: " + LTloop.getRowDimension() + " x " + LTloop.getColumnDimension() );
//				LTloop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate Km - loop.
				 */

				KmLoop = repeatLines( KmLoop, ( int ) Math.pow( 2, nbSubSteps ) );
//				System.out.println( "KmLoop: " + KmLoop.getRowDimension() + " x " + KmLoop.getColumnDimension() );
//				KmLoop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate Ks - loop.
				 */

//				System.out.println( "KsLoop before: " + ( ( int ) Math.pow( 2, nbSubSteps ) ) + " - " + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
				KsLoop = repeatLines( KsLoop, ( int ) Math.pow( 2, nbSubSteps ) );
//				System.out.println( "KsLoop after: " + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
//				KsLoop.print( 7, 2 ); // DEBUG

				/*
				 * Iterate LP - loop.
				 */

				LPloop = repeatLines( LPloop, ( int ) Math.pow( 2, nbSubSteps ) );
//				System.out.println( "LPLoop: " + LPloop.getRowDimension() + " x " + LPloop.getColumnDimension() );
//				LPloop.print( format, 7 ); // DEBUG

				/*
				 *
				 */

				final Matrix currCLoop = new Matrix( 1, nbDims );
				for ( int c = 0; c < nbDims; c++ )
				{
					final int r = nbLocs - currentStep;
					currCLoop.set( 0, c, track.get( r, c ) );
				}
//				System.out.println( "currCLoop:" + currCLoop.getRowDimension() + " x " + currCLoop.getColumnDimension() );
//				currCLoop.print( 7, 2 ); // DEBUG

				final Matrix[] Kloop = logIntegralDiff( currCLoop, localizationError, currDs3Loop, KmLoop, KsLoop );
				KmLoop = Kloop[ 0 ];
				KsLoop = Kloop[ 1 ];
				final Matrix LC = Kloop[ 2 ];

				System.out.println( "new - KmLoop:" + KmLoop.getRowDimension() + " x " + KmLoop.getColumnDimension() );
				KmLoop.print( format, 7 ); // DEBUG
				System.out.println( "new - KsLoop:" + KsLoop.getRowDimension() + " x " + KsLoop.getColumnDimension() );
				KsLoop.print( format, 7 ); // DEBUG
//				System.out.println( "new - LC:" + LC.getRowDimension() + " x " + LC.getColumnDimension() );
//				LC.print( 7, 2 ); // DEBUG

				/*
				 * Update the value of LP.
				 */

				LPloop = LPloop.plus( LTloop ).plus( LC );

				System.out.println( "LPLoop: " + LPloop.getRowDimension() + " x " + LPloop.getColumnDimension() );
				LPloop.print( format, 7 ); // DEBUG


				/*
				 *
				 */

				if ( doFrame && currentStep < nbLocs - 1 )
				{
					while ( currNbBs >= ( int ) Math.pow( 2, frameLen ) )
					{
						// TODO later: do predictions.

						/*
						 * Update currBs.
						 */

						final Matrix currBsLoopTmp = new Matrix(
								currBsLoop.getRowDimension() / 2,
								currBsLoop.getColumnDimension() - 1 );
						for ( int r = 0; r < currBsLoopTmp.getRowDimension(); r++ )
							for ( int c = 0; c < currBsLoop.getColumnDimension(); c++ )
								currBsLoopTmp.set( r, c, currBsLoop.get( r, c ) );
						currBsLoop = currBsLoopTmp;

						final Matrix[] Kloop2 = fuseTracks(
								KmLoop,
								KsLoop,
								LTloop,
								currBsLoop.getRowDimension() );
						KmLoop = Kloop2[ 0 ];
						KsLoop = Kloop2[ 1 ];
						LPloop = Kloop2[ 2 ];

						currNbBs = currBsLoop.getRowDimension();
						removeStep += 1;
					} // while
				} // if

				/*
				 * Iterate.
				 */

				currentStep++;

			} // while ( currentStep < nbLocs - 1 && currentStep < 6 )

			/*
			 * Compute new value for KsLoop.
			 */

			final Matrix KsLoopTmp = new Matrix( KsLoop.getRowDimension(), 1 );
			for ( int r = 0; r < KsLoop.getRowDimension(); r++ )
			{
				final double ks = KsLoop.get( r, 0 );
				final double val = Math.sqrt( ks * ks + localizationError * localizationError );
				KsLoopTmp.set( r, 0, val );
			}
			KsLoop = KsLoopTmp;

			/*
			 * Compute real value of probability.
			 */

			final Matrix logIntegratedTerm = new Matrix( KsLoop.getRowDimension(), 1 );
			for ( int r = 0; r < KsLoop.getRowDimension(); r++ )
			{
				final double ks = KsLoop.get( r, 0 );
				final double km = KmLoop.get( r, 0 );
				double sumC = 0.;
				for ( int c = 0; c < track.getColumnDimension(); c++ )
				{
					final double t = track.get( 0, c );
					final double dx = ( t - km );
					sumC += dx * dx;
				}
				final double val = Math.log( 2. * Math.PI * ks * ks ) - sumC / ( 2. * ks * ks );
				logIntegratedTerm.set( r, 0, val );
			}

			/*
			 * Log of probabilities to be bound.
			 */

			final Matrix LF = new Matrix( currStatesLoop.getRowDimension(), 1 );
			for ( int r = 0; r < LF.getRowDimension(); r++ )
			{
				final double val = currStatesLoop.get( r, currStatesLoop.getColumnDimension() - 1 ) == 0.
						? Fs[ 0 ]
						: Fs[ 1 ];
				LF.set( r, 0, Math.log( val ) );
			}
			System.out.println( "LF:" ); // DEBUG
			LF.print( 7, 2 ); // DEBUG

			/*
			 * Update LPloop.
			 */

			LPloop = LPloop.plus( logIntegratedTerm ).plus( LF );

			System.out.println( "\nLPloop after update:" ); // DEBUG
			LPloop.print( 7, 2 ); // DEBUG

			final Matrix P = new Matrix( LPloop.getRowDimension(), LPloop.getColumnDimension() );
			for ( int r = 0; r < LPloop.getRowDimension(); r++ )
				for ( int c = 0; c < LPloop.getColumnDimension(); c++ )
					P.set( r, c, Math.exp( LPloop.get( r, c ) ) );

			System.out.println( "\nP:" ); // DEBUG
			P.print( format, 10 ); // DEBUG

		} // loop on tracks.
	}

	private static Matrix[] fuseTracks( final Matrix Km, final Matrix Ks, final Matrix LP, final int currNbBs )
	{
		final int i = currNbBs / 2;

		final Matrix LP0 = LP.getMatrix( 0, i, 0, 0 );
		final Matrix LP1 = LP.getMatrix( i + 1, currNbBs - 1, 0, 0 );

		final Matrix maxLP = new Matrix( LP0.getRowDimension(), 1 );
		for ( int r = 0; r < LP0.getRowDimension(); r++ )
		{
			final double lp0 = LP0.get( r, 0 );
			final double lp1 = LP1.get( r, 0 );
			maxLP.set( r, 0, Math.max( lp0, lp1 ) );
		}

		final Matrix P0 = new Matrix( LP0.getRowDimension(), 1 );
		for ( int r = 0; r < P0.getRowDimension(); r++ )
		{
			final double lp0 = LP0.get( r, 0 );
			final double mlp = maxLP.get( r, 0 );
			final double val = Math.exp( lp0 - mlp );
			P0.set( r, 0, val );
		}

		final Matrix P1 = new Matrix( LP1.getRowDimension(), 1 );
		for ( int r = 0; r < P1.getRowDimension(); r++ )
		{
			final double lp1 = LP1.get( r, 0 );
			final double mlp = maxLP.get( r, 0 );
			final double val = Math.exp( lp1 - mlp );
			P1.set( r, 0, val );
		}

		final Matrix SP = P0.plus( P1 );
		final Matrix A0 = P0.arrayRightDivide( SP );
		final Matrix A1 = P1.arrayRightDivide( SP );

		final Matrix Km0 = Km.getMatrix( 0, i, 0, Km.getColumnDimension() - 1 );
		final Matrix Km1 = Km.getMatrix( i + 1, Km.getRowDimension() - 1, 0, Km.getColumnDimension() - 1 );

		final Matrix Ks0 = Ks.getMatrix( 0, i, 0, Ks.getColumnDimension() - 1 );
		final Matrix Ks1 = Ks.getMatrix( i + 1, Ks.getRowDimension() - 1, 0, Ks.getColumnDimension() - 1 );

		final Matrix KmNew = ( A0.arrayTimes( Km0 ) ).plus( ( A1.arrayTimes( Km1 ) ) );
		final Matrix KsNew = new Matrix( A0.getRowDimension(), A0.getColumnDimension() );
		for ( int r = 0; r < KsNew.getRowDimension(); r++ )
		{
			for ( int c = 0; c < KsNew.getColumnDimension(); c++ )
			{
				final double a0 = A0.get( r, c );
				final double a1 = A1.get( r, c );
				final double ks0 = Ks0.get( r, c );
				final double ks1 = Ks1.get( r, c );
				final double val = Math.sqrt( a0 * ks0 * ks0 + a1 * ks1 * ks1 );
				KsNew.set( r, c, val );
			}
		}

		final Matrix LPNew = new Matrix( LP.getRowDimension(), 1 );
		for ( int r = 0; r < LP.getRowDimension(); r++ )
		{
			final double sp = SP.get( r, 0 );
			final double mlp = maxLP.get( r, 0 );
			final double val = mlp + Math.log( sp );
			LPNew.set( r, 0, val );
		}

		return new Matrix[] { KmNew, KsNew, LPNew };
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

	public static void main( final String[] args )
	{
		final Map< Integer, Matrix > Cs = new HashMap<>();
		final Matrix cs0 = new Matrix( new double[][] {
				{ 0.1, -0.1 },
				{ 0.11, -0.12 },
				{ 0.13, -0.09 },
				{ 0.2, -0.05 },
				{ 0.1, 0.5 } } );
		Cs.put( Integer.valueOf( 0 ), cs0 );
		final double localizationError = 0.03;
		final double[] diffusionLengths = new double[] { 1e-10, 0.05 };
		final double[] Fs = new double[] { 0.6, 0.4 };
		final double probabilityOfUnbinding = 0.1;
		final double probabilityOfBinding = 0.2;
		final int frameLen = 10;
		final int nbSubSteps = 1;
		final boolean doFrame = true;
		final boolean doPred = false;

		pCsInterBoundState(
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

}
