package fr.pasteur.iah.extrack.plugin;

import java.awt.Image;

import javax.swing.ImageIcon;

public class ExTrackGuiUtil
{
	public static final ImageIcon ICON = new ImageIcon( ExTrackGuiUtil.class.getResource( "TrackMateExTrack-logo.png" ) );

	public static ImageIcon getIcon()
	{
		final int w = 200;
		final int h = 200;
		return getIcon( w, h );
	}

	public static ImageIcon getIcon( final int w, final int h )
	{

		final Image image = ICON.getImage();
		int nw = ICON.getIconWidth();
		int nh = ICON.getIconHeight();

		if ( ICON.getIconWidth() > w )
		{
			nw = w;
			nh = ( nw * ICON.getIconHeight() ) / ICON.getIconWidth();
		}

		if ( nh > h )
		{
			nh = h;
			nw = ( ICON.getIconWidth() * nh ) / ICON.getIconHeight();
		}
		final Image newimg = image.getScaledInstance( nw, nh, java.awt.Image.SCALE_SMOOTH );
		return new ImageIcon( newimg );
	}

}
