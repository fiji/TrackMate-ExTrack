package fr.pasteur.iah.extrack.compute;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import Jama.Matrix;
import fiji.plugin.trackmate.Logger;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.TrackMate;
import fr.pasteur.iah.extrack.numpy.NumPyReader;
import fr.pasteur.iah.extrack.util.ExTrackUtil;

public class ComputeExTrackTestDrive
{
	public static void main( final String[] args ) throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		Locale.setDefault( Locale.ROOT );
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );

		/*
		 * Load data.
		 */

		final String trackFile = "samples/tracks.npy";
		final Map< Integer, Matrix > tracks = NumPyReader.readTracks( trackFile );

		/*
		 * Launch GUI.
		 */

		final ExTrackComputeAction action = new ExTrackComputeAction();
		action.setLogger( Logger.DEFAULT_LOGGER );
		final Model model = ExTrackUtil.toModel( tracks );
		final Settings settings = new Settings();
		final TrackMate trackmate = new TrackMate( model , settings  );
		action.execute( trackmate );
	}
}
