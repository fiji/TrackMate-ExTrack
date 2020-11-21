package fr.pasteur.iah.extrack.compute;

import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import fiji.plugin.trackmate.Logger;

public class ShowSwingBug
{
	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.ROOT );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		final ExTrackComputeAction action = new ExTrackComputeAction();
		action.setLogger( Logger.DEFAULT_LOGGER );
		action.execute( null );
	}
}
