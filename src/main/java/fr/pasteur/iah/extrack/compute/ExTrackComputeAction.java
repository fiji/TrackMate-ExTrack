package fr.pasteur.iah.extrack.compute;

import javax.swing.ImageIcon;

import org.scijava.plugin.Plugin;

import fiji.plugin.trackmate.TrackMate;
import fiji.plugin.trackmate.action.AbstractTMAction;
import fiji.plugin.trackmate.action.TrackMateAction;
import fiji.plugin.trackmate.action.TrackMateActionFactory;
import fiji.plugin.trackmate.gui.TrackMateGUIController;
import fr.pasteur.iah.extrack.plugin.ExTrackActionController;
import fr.pasteur.iah.extrack.plugin.ExTrackImporterPanel;
import fr.pasteur.iah.extrack.util.ExTrackUtil;

public class ExTrackComputeAction extends AbstractTMAction
{

	public static final String INFO_TEXT = "Please Francois write this. TODO.";

	public static final String KEY = "COMPUTE_EXTRACK_PROBABILITIES";

	public static final ImageIcon ICON = ExTrackUtil.scaleImage( ExTrackImporterPanel.ICON, 16, 16 );

	public static final String NAME = "Compute ExTrack probabilities";

	@Override
	public void execute( final TrackMate trackmate )
	{
		final ExTrackActionController controller = new ExTrackActionController( trackmate, logger );
		controller.show();
	}

	@Plugin( type = TrackMateActionFactory.class )
	public static class Factory implements TrackMateActionFactory
	{

		@Override
		public String getInfoText()
		{
			return INFO_TEXT;
		}

		@Override
		public String getKey()
		{
			return KEY;
		}

		@Override
		public TrackMateAction create( final TrackMateGUIController controller )
		{
			return new ExTrackComputeAction();
		}

		@Override
		public ImageIcon getIcon()
		{
			return ICON;
		}

		@Override
		public String getName()
		{
			return NAME;
		}

	}
}
