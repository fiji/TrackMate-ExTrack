import os
import time

from java.io import File

from fiji.plugin.trackmate.io import TmXmlReader

from fr.pasteur.iah.extrack import ExTrack
from fr.pasteur.iah.extrack.compute import ExTrackParameters


def process( path ):

	# Load tracks.
	print( "Loading " + path )
	reader =  TmXmlReader(  File( path ) )
	model = reader.getModel()
	if not reader.isReadingOk():
		print( "Problem reading the file:" )
		print( reader.getErrorMessage() )
		print( "Aborting." )
		return
	
	print( "Loading done." )

	# Create an ExTrack object.
	extrack = ExTrack( model )

	# We only estimate parameters if we do not have them already saved.
	parent = File( path ).getParent()
	savefile = File( parent, "extrack-params.json" )
	if savefile.exists():
		print( "\nFound an existing save-file for parameters. Skipping parameter estimation." )
	
	else:
		# Estimate motility parameters.
		print( "\nEstimating motility parameters (can be long)..." )
		# Use the default as starting point.
		startpoint = ExTrackParameters.create() \
					.localizationError( 0.02 ) \
					.diffusionLength0( 0.001 ) \
					.diffusionLength1( 0.1 ) \
					.F0( 0.5 ) \
					.probabilityOfUnbinding( 0.1 ) \
					.nbSubSteps( 1 ) \
					.nFrames( 6 ) \
					.build()
		print( "Using the following as starting point:" )
		print( startpoint.toString() );
	
		start = time.time()
		optimum = extrack.estimateParameters( startpoint )
		end = time.time()
		print( "Estimation done in %.1f seconds.\nFound the following optimum:" % ( end - start ) )
		print( optimum.toString() )
	
		# Save.
		print( "\nSaving the parameters to a JSon file." );
		ExTrack.saveParameters( optimum, savefile.getAbsolutePath() )
		print( "Saved to " + savefile.getAbsolutePath() )

	# Load.
	print( "\nLoading the parameters from a JSon file." )
	loadedparams = ExTrack.loadParameters( savefile.getAbsolutePath() );
	print( "Loaded from " + savefile.getAbsolutePath() )
	print( "Parameters loaded:" )
	print( loadedparams )

	# Predict probabilities.
	print( "\nPredicting diffusive & stuck probabilities..." )
	extrack.computeProbabilities( loadedparams )
	print( "Done." )
	
	# Print probabilities.
	print( "\nContent of model features now:" )
	print( "-----------------------------------------------------------------" )
	print(  "| %-25s | %-15s | %-15s |" % ( "", "P stuck", "P diffusive" ) ) 
	print( "-----------------------------------------------------------------" )
	allspots = model.getSpots()
	frames = allspots.keySet()
	for frame in frames:
		print( "Frame " + str( frame ) + ":" )
		spots = allspots.iterable( frame, True )
		
		for spot in spots:
			print( "| %-25s | %-15.3g | %-15.3g |" % ( spot.getName(), spot.getFeature( "EXTRACK_P_STUCK" ), spot.getFeature( "EXTRACK_P_DIFFUSIVE" ) ) )
			break # Remove to get all spots.
		print( "-----------------------------------------------------------------" )
		break # Remove to get all frames.


if __name__ == "__main__":
	print( 'Current working dir: %s' % os.getcwd() )
	# Path to the TrackMate file containing your tracks.
	path = "scripts/samples/tracks.xml"
	process( path )
