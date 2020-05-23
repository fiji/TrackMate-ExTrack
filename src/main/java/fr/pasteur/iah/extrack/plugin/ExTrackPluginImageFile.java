package fr.pasteur.iah.extrack.plugin;

import javax.swing.JFrame;

import org.scijava.util.VersionUtils;

import fr.pasteur.iah.extrack.trackmate.ExTrackImporter;
import ij.ImageJ;
import ij.plugin.PlugIn;

public class ExTrackPluginImageFile implements PlugIn
{

	@Override
	public void run( final String arg )
	{
		final JFrame frame = new JFrame( ExTrackImporter.PLUGIN_NAME + " v" + VersionUtils.getVersion( getClass() ) );
		frame.setIconImage( ExTrackImporterPanel.ICON.getImage() );

		frame.getContentPane().add( new ExTrackImporterPanel(
				ExTrackImporterPanel.lastImagePath,
				ExTrackImporterPanel.lastDataPath,
				ExTrackImporterPanel.lastPizelSize,
				ExTrackImporterPanel.lastRadius,
				ExTrackImporterPanel.lastSpatialUnits,
				ExTrackImporterPanel.lastFrameInterval,
				ExTrackImporterPanel.lastTimeUnits ) );

		frame.pack();
		frame.setVisible( true );
	}

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		ExTrackImporterPanel.lastDataPath = "samples/tracks.npy";
		ExTrackImporterPanel.lastImagePath = "samples/img.tif";
		new ExTrackPluginImageFile().run( "" );
	}
}
