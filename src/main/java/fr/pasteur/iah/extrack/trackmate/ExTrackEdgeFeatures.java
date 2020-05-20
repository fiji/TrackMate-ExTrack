package fr.pasteur.iah.extrack.trackmate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ImageIcon;

import org.jgrapht.graph.DefaultWeightedEdge;

import fiji.plugin.trackmate.Dimension;
import fiji.plugin.trackmate.Model;
import fiji.plugin.trackmate.features.edges.EdgeAnalyzer;

public class ExTrackEdgeFeatures implements EdgeAnalyzer
{

	public static final String FEATURE_P_STUCK_CHANGE = "EXTRACK_P_STUCK_CHANGE";

	public static final String KEY = "EXTRACK_EDGE_FEATURES";

	static final List< String > FEATURES = new ArrayList<>( 1 );

	static final Map< String, String > FEATURE_SHORT_NAMES = new HashMap<>( 1 );

	static final Map< String, String > FEATURE_NAMES = new HashMap<>( 1 );

	static final Map< String, Dimension > FEATURE_DIMENSIONS = new HashMap<>( 1 );

	static final Map< String, Boolean > IS_INT = new HashMap<>( 1 );

	static final String INFO_TEXT = "<html>A dummy analyzer for ExTrack edge features.</html>";

	static final String NAME = "ExTrack edge features";

	static
	{
		FEATURES.add( FEATURE_P_STUCK_CHANGE );
		FEATURE_SHORT_NAMES.put( FEATURE_P_STUCK_CHANGE, "P stuck change " );
		FEATURE_NAMES.put( FEATURE_P_STUCK_CHANGE, "Probabiity stuck change" );
		FEATURE_DIMENSIONS.put( FEATURE_P_STUCK_CHANGE, Dimension.NONE );
		IS_INT.put( FEATURE_P_STUCK_CHANGE, Boolean.FALSE );
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

	@Override
	public String getInfoText()
	{
		return INFO_TEXT;
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

	@Override
	public ImageIcon getIcon()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public long getProcessingTime()
	{
		return 0;
	}

	@Override
	public void setNumThreads()
	{}

	@Override
	public void setNumThreads( final int numThreads )
	{}

	@Override
	public int getNumThreads()
	{
		return 1;
	}

	@Override
	public void process( final Collection< DefaultWeightedEdge > edges, final Model model )
	{}

	@Override
	public boolean isLocal()
	{
		return false;
	}
}
