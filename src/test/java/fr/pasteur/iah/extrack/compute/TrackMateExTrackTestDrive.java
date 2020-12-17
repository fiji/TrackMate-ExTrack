package fr.pasteur.iah.extrack.compute;

import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.trackmate.LoadTrackMatePlugIn_;
import ij.ImageJ;

public class TrackMateExTrackTestDrive
{
	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.US );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

		ImageJ.main( args );
		new LoadTrackMatePlugIn_().run( "samples/tracks.xml" );

	}
}
