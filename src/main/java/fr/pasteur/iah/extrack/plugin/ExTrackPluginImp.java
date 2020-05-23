package fr.pasteur.iah.extrack.plugin;

import javax.swing.JFrame;

import org.scijava.util.VersionUtils;

import fr.pasteur.iah.extrack.trackmate.ExTrackImporter;
import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.plugin.PlugIn;

public class ExTrackPluginImp implements PlugIn
{

	@Override
	public void run( final String arg )
	{
		final ImagePlus imp = WindowManager.getCurrentImage();
		if ( imp == null )
		{
			IJ.error( "Please open an image before running " + ExTrackImporter.PLUGIN_NAME );
			return;
		}

		final JFrame frame = new JFrame( ExTrackImporter.PLUGIN_NAME + " v" + VersionUtils.getVersion( getClass() ) );
		frame.setIconImage( ExTrackImporterPanel.ICON.getImage() );

		frame.getContentPane().add( new ExTrackImporterPanel(
				imp,
				ExTrackImporterPanel.lastDataPath,
				ExTrackImporterPanel.lastRadius ) );

		frame.pack();
		frame.setVisible( true );
	}

	public static void main( final String[] args )
	{
		ImageJ.main( args );
		IJ.openImage( "samples/img.tif" ).show();
		ExTrackImporterPanel.lastDataPath = "samples/tracks.npy";
		new ExTrackPluginImp().run( "" );
	}
}
