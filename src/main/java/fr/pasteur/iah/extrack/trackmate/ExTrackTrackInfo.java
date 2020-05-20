package fr.pasteur.iah.extrack.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;

public class ExTrackTrackInfo implements TrackAnalyzer
{

	/** The key for this analyzer. */
	public static final String KEY = "ExTrack track info";

	public static final String EXTRACK_TRACKID = "EXTRACK_TRACKID";

	public static final List< String > FEATURES = new ArrayList<>( 1 );

	public static final Map< String, String > FEATURE_NAMES = new HashMap<>( 1 );

	public static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap<>( 1 );

	public static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap<>( 1 );

	public static final Map< String, Boolean > IS_INT = new HashMap<>( 2 );

	static
	{
		FEATURES.add( EXTRACK_TRACKID );
		FEATURE_NAMES.put( EXTRACK_TRACKID, "ExTrack track ID" );
		FEATURE_SHORT_NAMES.put( EXTRACK_TRACKID, "ExTrackID" );
		FEATURE_DIMENSIONS.put( EXTRACK_TRACKID, Dimension.NONE );
		IS_INT.put( EXTRACK_TRACKID, Boolean.TRUE );
	}

	@Override
	public boolean isLocal()
	{
		return true;
	}

	@Override
	public void process( final Collection< Integer > trackIDs, final Model model )
	{}

	@Override
	public long getProcessingTime()
	{
		return 0l;
	}

	@Override
	public String getKey()
	{
		return KEY;
	}

	@Override
	public List< String > getFeatures()
	{
		return FEATURES;
	}

	@Override
	public Map< String, String > getFeatureShortNames()
	{
		return FEATURE_SHORT_NAMES;
	}

	@Override
	public Map< String, String > getFeatureNames()
	{
		return FEATURE_NAMES;
	}

	@Override
	public Map< String, Dimension > getFeatureDimensions()
	{
		return FEATURE_DIMENSIONS;
	}

	/**
	 * Ignored. This analyzer is single-threaded.
	 */
	@Override
	public void setNumThreads()
	{}

	/**
	 * Ignored. This analyzer is single-threaded.
	 */
	@Override
	public void setNumThreads( final int numThreads )
	{}

	/**
	 * Ignore. This analyzer is single-threaded.
	 */
	@Override
	public int getNumThreads()
	{
		return 1;
	}

	@Override
	public String getInfoText()
	{
		return null;
	}

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return KEY;
	}

	@Override
	public Map< String, Boolean > getIsIntFeature()
	{
		return IS_INT;
	}

	@Override
	public boolean isManualFeature()
	{
		return true;
	}
}
