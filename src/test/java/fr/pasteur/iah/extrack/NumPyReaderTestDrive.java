package fr.pasteur.iah.extrack;

import java.io.IOException;

import fr.pasteur.iah.extrack.numpy.NumPyReader;

public class NumPyReaderTestDrive
{

	public static void main( final String[] args ) throws IOException
	{
		final String file = "samples/tracks.npy";
//		final String file = "samples/params.npy";

		System.out.println( "Reading NumPy file: " + file );
		final long start = System.currentTimeMillis();
		final double[][] out = NumPyReader.readFile( file );
		final long end = System.currentTimeMillis();
		System.out.println( "Read file in " + ( end - start ) + " ms." );
		System.out.println( String.format( "Matrix size: %d x %d ", out[ 0 ].length, out.length ) );

		for ( int r = 0; r < out[ 0 ].length; r++ )
		{
			System.out.println();
			for ( int c = 0; c < out.length - 1; c++ )
				System.out.print( String.format( "%7.2f, ", out[ c ][ r ] ) );

			System.out.print( String.format( "%7.2f ", out[ out.length - 1 ][ r ] ) );
		}
	}
}
