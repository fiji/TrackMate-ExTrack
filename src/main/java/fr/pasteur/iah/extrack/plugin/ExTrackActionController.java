package fr.pasteur.iah.extrack.plugin;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import fr.pasteur.iah.extrack.compute.ExTrackParameters;

public class ExTrackActionController
{

	private ExTrackActionPanel gui;

	public ExTrackActionController()
	{
		this.gui = new ExTrackActionPanel();
		final ExTrackParameters params = ExTrackParameters.create().build();
		gui.setManualParameters( params );
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

	public static void main( final String[] args )
	{
		new ExTrackActionController().show();
	}
}
