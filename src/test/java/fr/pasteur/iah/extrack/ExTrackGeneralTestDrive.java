package fr.pasteur.iah.extrack;

import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.trackmate.LoadTrackMatePlugIn_;
import ij.ImageJ;

public class ExTrackGeneralTestDrive
{

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.ROOT );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		ImageJ.main( args );
		new LoadTrackMatePlugIn_().run( "samples/tracks.xml" );
	}

}
