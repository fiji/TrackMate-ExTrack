package fr.pasteur.iah.extrack.plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fr.pasteur.iah.extrack.compute.ExTrackParameters;
import fr.pasteur.iah.extrack.util.FileChooser;
import fr.pasteur.iah.extrack.util.FileChooser.DialogType;

public class ExTrackActionController
{

	private static final String FILE_EXTENSION = "json";

	private static final FileFilter FILE_FILTER = new FileNameExtensionFilter( "JSon files", FILE_EXTENSION );

	private final ExTrackActionPanel gui;

	private static String selectedFile;

	public ExTrackActionController()
	{
		this.gui = new ExTrackActionPanel();
		final ExTrackParameters params = ExTrackParameters.create().build();
		gui.setManualParameters( params );

		gui.btnSave.addActionListener( e -> save() );
		gui.btnLoad.addActionListener( e -> load() );
	}

	private void load()
	{
		final EverythingDisablerAndReenabler reenabler = new EverythingDisablerAndReenabler(
				SwingUtilities.getWindowAncestor( gui ),
				new Class[] { JLabel.class } );
		reenabler.disable();
		try
		{
			final String dialogTitle = "Load ExTrack parameters from a JSON file";
			final DialogType dialogType = DialogType.LOAD;
			final File chosenFile = FileChooser.chooseFile( gui, selectedFile, FILE_FILTER, dialogTitle, dialogType );
			if ( chosenFile == null )
			{
				gui.log( "Loading aborted." );
				return;
			}

			final Gson gson = new Gson();
			try
			{
				final String content = new String( Files.readAllBytes( Paths.get( chosenFile.getAbsolutePath() ) ) );
				try
				{
					final ExTrackParameters params = gson.fromJson( content, ExTrackParameters.class );
					gui.setManualParameters( params );
					gui.log( "Loaded from " + selectedFile );
					selectedFile = chosenFile.getAbsolutePath();
					gui.log( "Loaded parameters from " + selectedFile );
				}
				catch ( final JsonSyntaxException jse )
				{
					gui.error( "File " + chosenFile + " is not an ExTrack parameter file." );
					jse.printStackTrace();
				}
			}
			catch ( final IOException e )
			{
				gui.error( "Problem reading from file " + chosenFile );
				e.printStackTrace();
			}
		}
		finally
		{
			reenabler.reenable();
		}
	}

	private void save()
	{
		final EverythingDisablerAndReenabler reenabler = new EverythingDisablerAndReenabler(
				SwingUtilities.getWindowAncestor( gui ),
				new Class[] { JLabel.class } );
		reenabler.disable();
		try
		{
			final String dialogTitle = "Save ExTrack parameters to a JSON file";
			final DialogType dialogType = DialogType.SAVE;
			File chosenFile = FileChooser.chooseFile( gui, selectedFile, FILE_FILTER, dialogTitle, dialogType );
			if ( chosenFile == null )
			{
				gui.log( "Saving aborted." );
				return;
			}
			if ( !chosenFile.getAbsolutePath().endsWith( '.' + FILE_EXTENSION ) )
				chosenFile = new File( chosenFile.getAbsolutePath() + '.' + FILE_EXTENSION );

			final ExTrackParameters params = gui.getManualParameters();
			final Gson gson = new GsonBuilder().setPrettyPrinting().create();
			try (FileWriter file = new FileWriter( chosenFile ))
			{
				final String serialized = gson.toJson( params );
				file.write( serialized );
				file.flush();
				selectedFile = chosenFile.getAbsolutePath();
				gui.log( "Saved parameters to " + selectedFile );
			}
			catch ( final IOException e )
			{
				gui.error( "Problem writing to file " + chosenFile );
				e.printStackTrace();
			}
		}
		finally
		{
			reenabler.reenable();
		}
	}

	public void show()
	{
		SwingUtilities.invokeLater( new Runnable()
		{
			@Override
			public void run()
			{

				final JFrame frame = new JFrame();
				frame.getContentPane().add( gui );
				frame.pack();
				frame.setIconImage( ExTrackGuiUtil.ICON.getImage() );
				frame.setLocationRelativeTo( null );
				frame.setVisible( true );
			}
		} );
	}

	/*
	 * Demo.
	 */

	public static void main( final String[] args ) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException
	{
		UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
		new ExTrackActionController().show();
	}
}
