/*-
 * #%L
 * TrackMate interface for the ExTrack track analysis software.
 * %%
 * Copyright (C) 2020 Institut Pasteur.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package fr.pasteur.iah.extrack.trackmate;

import java.io.File;
import java.util.List;

import fiji.plugin.trackmate.Settings;
import fiji.plugin.trackmate.features.edges.EdgeAnalyzer;
import fiji.plugin.trackmate.features.spot.SpotAnalyzerFactory;
import fiji.plugin.trackmate.features.track.TrackAnalyzer;
import fiji.plugin.trackmate.providers.EdgeAnalyzerProvider;
import fiji.plugin.trackmate.providers.SpotAnalyzerProvider;
import fiji.plugin.trackmate.providers.TrackAnalyzerProvider;
import fr.pasteur.iah.extrack.numpy.NumPyReader;
import ij.ImagePlus;

public class ExTrackImporterFromImp extends ExTrackImporter
{

	private final ImagePlus imp;

	public ExTrackImporterFromImp( final ImagePlus imp, final String dataFilePath, final double radius )
	{
		super(
				imp.getTitle(),
				dataFilePath,
				radius,
				imp.getCalibration().getUnit(),
				imp.getCalibration().frameInterval,
				imp.getCalibration().getTimeUnit() );
		this.imp = imp;
	}

	@Override
	public boolean checkInput()
	{
		if ( imp == null )
		{
			errorMessage = "Imageis null.";
			return false;
		}

		final File dataFile = new File( dataFilePath );
		if ( !dataFile.exists() )
		{
			errorMessage = "Data file " + dataFilePath + " does not exist.";
			return false;
		}
		if ( !dataFile.canRead() )
		{
			errorMessage = "Data file " + dataFilePath + " cannot be opened.";
			return false;
		}
		if ( !NumPyReader.isNumPy( dataFilePath ) )
		{
			errorMessage = "Data file " + dataFilePath + " does not seem to be a NumPy file.";
			return false;
		}

		return true;
	}

	@Override
	protected Settings createSettings( final String imageFile )
	{
		final Settings settings = new Settings();
		settings.setFrom( imp );

		// Declare all features.
		final SpotAnalyzerProvider spotAnalyzerProvider = new SpotAnalyzerProvider();
		final List< String > spotAnalyzerKeys = spotAnalyzerProvider.getKeys();
		for ( final String key : spotAnalyzerKeys )
		{
			final SpotAnalyzerFactory< ? > spotFeatureAnalyzer = spotAnalyzerProvider.getFactory( key );
			settings.addSpotAnalyzerFactory( spotFeatureAnalyzer );
		}

		settings.clearEdgeAnalyzers();
		final EdgeAnalyzerProvider edgeAnalyzerProvider = new EdgeAnalyzerProvider();
		final List< String > edgeAnalyzerKeys = edgeAnalyzerProvider.getKeys();
		for ( final String key : edgeAnalyzerKeys )
		{
			final EdgeAnalyzer edgeAnalyzer = edgeAnalyzerProvider.getFactory( key );
			settings.addEdgeAnalyzer( edgeAnalyzer );
		}

		settings.clearTrackAnalyzers();
		final TrackAnalyzerProvider trackAnalyzerProvider = new TrackAnalyzerProvider();
		final List< String > trackAnalyzerKeys = trackAnalyzerProvider.getKeys();
		for ( final String key : trackAnalyzerKeys )
		{
			final TrackAnalyzer trackAnalyzer = trackAnalyzerProvider.getFactory( key );
			settings.addTrackAnalyzer( trackAnalyzer );
		}

		// ExTrack features.
		settings.addSpotAnalyzerFactory( new ExTrackProbabilitiesFeature<>() );
		settings.addEdgeAnalyzer( new ExTrackEdgeFeatures() );
		settings.addTrackAnalyzer( new ExTrackTrackInfo() );

		return settings;
	}

}
