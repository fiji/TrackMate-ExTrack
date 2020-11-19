package fr.pasteur.iah.extrack.plugin;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class ExTrackActionPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	public ExTrackActionPanel()
	{
		setLayout( new BorderLayout( 0, 0 ) );

		final JPanel panelBottom = new JPanel();
		panelBottom.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
		add( panelBottom, BorderLayout.SOUTH );
		final GridBagLayout gbl_panelBottom = new GridBagLayout();
		gbl_panelBottom.columnWidths = new int[] { 405, 0 };
		gbl_panelBottom.rowHeights = new int[] { 0, 29, 0 };
		gbl_panelBottom.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panelBottom.rowWeights = new double[] { 0.0, 0.0, Double.MIN_VALUE };
		panelBottom.setLayout( gbl_panelBottom );

		final JLabel lblLog = new JLabel( "     " );
		lblLog.setFont( lblLog.getFont().deriveFont( lblLog.getFont().getSize() - 3f ) );
		final GridBagConstraints gbc_lblLog = new GridBagConstraints();
		gbc_lblLog.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblLog.gridx = 0;
		gbc_lblLog.gridy = 0;
		panelBottom.add( lblLog, gbc_lblLog );

		final JPanel panelButtons = new JPanel();
		final GridBagConstraints gbc_panelButtons = new GridBagConstraints();
		gbc_panelButtons.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelButtons.gridx = 0;
		gbc_panelButtons.gridy = 1;
		panelBottom.add( panelButtons, gbc_panelButtons );
		panelButtons.setLayout( new BoxLayout( panelButtons, BoxLayout.X_AXIS ) );

		final JLabel lblParams = new JLabel( "Parameters:" );
		panelButtons.add( lblParams );
		lblParams.setFont( lblParams.getFont().deriveFont( lblParams.getFont().getSize() - 2f ) );

		final JButton btnSave = new JButton( "Save" );
		panelButtons.add( btnSave );
		btnSave.setFont( lblParams.getFont().deriveFont( lblParams.getFont().getSize() - 2f ) );

		final JButton btnLoad = new JButton( "Load" );
		panelButtons.add( btnLoad );
		btnLoad.setFont( lblParams.getFont().deriveFont( lblParams.getFont().getSize() - 2f ) );

		final Component horizontalGlue = Box.createHorizontalGlue();
		panelButtons.add( horizontalGlue );

		final JButton btnCompute = new JButton( "Compute" );
		panelButtons.add( btnCompute );
		btnCompute.setFont( lblParams.getFont().deriveFont( lblParams.getFont().getSize() - 2f ) );

		final JLabel lblTitle = new JLabel( ExTrackGuiUtil.getIcon(), JLabel.CENTER );
		lblTitle.setFont( getFont().deriveFont( 42 ) );
		add( lblTitle, BorderLayout.NORTH );

		final JTabbedPane mainPane = new JTabbedPane( JTabbedPane.TOP );
		mainPane.setFont( mainPane.getFont().deriveFont( mainPane.getFont().getSize() - 2f ) );
		add( mainPane, BorderLayout.CENTER );

		/*
		 * Manual input panell.
		 */

		final JPanel panelManualInput = new JPanel();
		panelManualInput.setOpaque( false );
		panelManualInput.setBorder( new EmptyBorder( 5, 15, 5, 5 ) );
		mainPane.addTab( "Manual input", null, panelManualInput, null );
		final GridBagLayout gbl_panelManualInput = new GridBagLayout();
		gbl_panelManualInput.columnWidths = new int[] { 0, 0, 0, 0 };
		gbl_panelManualInput.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panelManualInput.columnWeights = new double[] { 0.0, 0.0, 0.0, Double.MIN_VALUE };
		gbl_panelManualInput.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		panelManualInput.setLayout( gbl_panelManualInput );

		final JLabel lblLocError = new JLabel( "Localization error" );
		lblLocError.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblLocError = new GridBagConstraints();
		gbc_lblLocError.anchor = GridBagConstraints.EAST;
		gbc_lblLocError.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblLocError.gridx = 0;
		gbc_lblLocError.gridy = 0;
		panelManualInput.add( lblLocError, gbc_lblLocError );

		final JFormattedTextField ftfLocError = new JFormattedTextField();
		ftfLocError.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfLocError.setMinimumSize( new Dimension( 100, 26 ) );
		ftfLocError.setFont( ftfLocError.getFont().deriveFont( ftfLocError.getFont().getSize() - 2f ) );
		ftfLocError.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfLocError = new GridBagConstraints();
		gbc_ftfLocError.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfLocError.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfLocError.gridx = 1;
		gbc_ftfLocError.gridy = 0;
		panelManualInput.add( ftfLocError, gbc_ftfLocError );

		final JLabel lblUnit1 = new JLabel( "µm" );
		lblUnit1.setFont( lblUnit1.getFont().deriveFont( lblUnit1.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit1 = new GridBagConstraints();
		gbc_lblUnit1.anchor = GridBagConstraints.WEST;
		gbc_lblUnit1.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblUnit1.gridx = 2;
		gbc_lblUnit1.gridy = 0;
		panelManualInput.add( lblUnit1, gbc_lblUnit1 );

		final JLabel lblDiffLength0 = new JLabel( "Diffusion length 0" );
		lblDiffLength0.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblDiffLength0 = new GridBagConstraints();
		gbc_lblDiffLength0.anchor = GridBagConstraints.EAST;
		gbc_lblDiffLength0.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblDiffLength0.gridx = 0;
		gbc_lblDiffLength0.gridy = 1;
		panelManualInput.add( lblDiffLength0, gbc_lblDiffLength0 );

		final JFormattedTextField ftfDiffLength0 = new JFormattedTextField();
		ftfDiffLength0.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfDiffLength0.setMinimumSize( new Dimension( 100, 26 ) );
		ftfDiffLength0.setFont( ftfDiffLength0.getFont().deriveFont( ftfDiffLength0.getFont().getSize() - 2f ) );
		ftfDiffLength0.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfDiffLength0 = new GridBagConstraints();
		gbc_ftfDiffLength0.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfDiffLength0.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfDiffLength0.gridx = 1;
		gbc_ftfDiffLength0.gridy = 1;
		panelManualInput.add( ftfDiffLength0, gbc_ftfDiffLength0 );

		final JLabel lblUnit2 = new JLabel( "µm" );
		lblUnit2.setFont( lblUnit2.getFont().deriveFont( lblUnit2.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit2 = new GridBagConstraints();
		gbc_lblUnit2.anchor = GridBagConstraints.WEST;
		gbc_lblUnit2.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblUnit2.gridx = 2;
		gbc_lblUnit2.gridy = 1;
		panelManualInput.add( lblUnit2, gbc_lblUnit2 );

		final JLabel lblDiffLength1 = new JLabel( "Diffusion length 1" );
		lblDiffLength1.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblDiffLength1 = new GridBagConstraints();
		gbc_lblDiffLength1.anchor = GridBagConstraints.EAST;
		gbc_lblDiffLength1.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblDiffLength1.gridx = 0;
		gbc_lblDiffLength1.gridy = 2;
		panelManualInput.add( lblDiffLength1, gbc_lblDiffLength1 );

		final JFormattedTextField ftfDiffLength1 = new JFormattedTextField();
		ftfDiffLength1.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfDiffLength1.setMinimumSize( new Dimension( 100, 26 ) );
		ftfDiffLength1.setFont( ftfDiffLength1.getFont().deriveFont( ftfDiffLength1.getFont().getSize() - 2f ) );
		ftfDiffLength1.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfDiffLength1 = new GridBagConstraints();
		gbc_ftfDiffLength1.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfDiffLength1.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfDiffLength1.gridx = 1;
		gbc_ftfDiffLength1.gridy = 2;
		panelManualInput.add( ftfDiffLength1, gbc_ftfDiffLength1 );

		final JLabel lblUnit3 = new JLabel( "µm" );
		lblUnit3.setFont( lblUnit3.getFont().deriveFont( lblUnit3.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit3 = new GridBagConstraints();
		gbc_lblUnit3.anchor = GridBagConstraints.WEST;
		gbc_lblUnit3.insets = new Insets( 0, 0, 5, 0 );
		gbc_lblUnit3.gridx = 2;
		gbc_lblUnit3.gridy = 2;
		panelManualInput.add( lblUnit3, gbc_lblUnit3 );

		final JLabel lblMobileFraction = new JLabel( "Mobile fraction" );
		lblMobileFraction.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblMobileFraction = new GridBagConstraints();
		gbc_lblMobileFraction.anchor = GridBagConstraints.EAST;
		gbc_lblMobileFraction.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblMobileFraction.gridx = 0;
		gbc_lblMobileFraction.gridy = 3;
		panelManualInput.add( lblMobileFraction, gbc_lblMobileFraction );

		final JFormattedTextField ftfMobileFraction = new JFormattedTextField();
		ftfMobileFraction.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfMobileFraction.setMinimumSize( new Dimension( 100, 26 ) );
		ftfMobileFraction.setFont( ftfMobileFraction.getFont().deriveFont( ftfMobileFraction.getFont().getSize() - 2f ) );
		ftfMobileFraction.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfMobileFraction = new GridBagConstraints();
		gbc_ftfMobileFraction.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfMobileFraction.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfMobileFraction.gridx = 1;
		gbc_ftfMobileFraction.gridy = 3;
		panelManualInput.add( ftfMobileFraction, gbc_ftfMobileFraction );

		final JLabel lblPU = new JLabel( "Probability of unbinding" );
		lblPU.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblPU = new GridBagConstraints();
		gbc_lblPU.anchor = GridBagConstraints.EAST;
		gbc_lblPU.insets = new Insets( 0, 0, 0, 5 );
		gbc_lblPU.gridx = 0;
		gbc_lblPU.gridy = 4;
		panelManualInput.add( lblPU, gbc_lblPU );

		final JFormattedTextField ftfProbUnbinding = new JFormattedTextField();
		ftfProbUnbinding.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfProbUnbinding.setMinimumSize( new Dimension( 100, 26 ) );
		ftfProbUnbinding.setFont( ftfProbUnbinding.getFont().deriveFont( ftfProbUnbinding.getFont().getSize() - 2f ) );
		ftfProbUnbinding.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfProbUnbinding = new GridBagConstraints();
		gbc_ftfProbUnbinding.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfProbUnbinding.insets = new Insets( 0, 0, 0, 5 );
		gbc_ftfProbUnbinding.gridx = 1;
		gbc_ftfProbUnbinding.gridy = 4;
		panelManualInput.add( ftfProbUnbinding, gbc_ftfProbUnbinding );

		/*
		 * Estimation panel.
		 */

		final JPanel panelMLEstimation = new JPanel();
		panelMLEstimation.setOpaque( false );
		panelMLEstimation.setBorder( new EmptyBorder( 5, 15, 5, 5 ) );
		mainPane.addTab( "Maximum-likelihood estimation", null, panelMLEstimation, null );
		final GridBagLayout gbl_panelMLEstimation = new GridBagLayout();
		gbl_panelMLEstimation.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_panelMLEstimation.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
		gbl_panelMLEstimation.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gbl_panelMLEstimation.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		panelMLEstimation.setLayout( gbl_panelMLEstimation );

		final JLabel lblLocError2 = new JLabel( "Localization error" );
		lblLocError2.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblLocError2 = new GridBagConstraints();
		gbc_lblLocError2.anchor = GridBagConstraints.EAST;
		gbc_lblLocError2.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblLocError2.gridx = 0;
		gbc_lblLocError2.gridy = 0;
		panelMLEstimation.add( lblLocError2, gbc_lblLocError2 );

		final JLabel ftfLocError2 = new JLabel();
		ftfLocError2.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfLocError2.setMinimumSize( new Dimension( 100, 26 ) );
		ftfLocError2.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfLocError2 = new GridBagConstraints();
		gbc_ftfLocError2.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfLocError2.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfLocError2.gridx = 1;
		gbc_ftfLocError2.gridy = 0;
		panelMLEstimation.add( ftfLocError2, gbc_ftfLocError2 );

		final JLabel lblUnit12 = new JLabel( "µm" );
		lblUnit12.setFont( lblUnit12.getFont().deriveFont( lblUnit12.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit12 = new GridBagConstraints();
		gbc_lblUnit12.anchor = GridBagConstraints.WEST;
		gbc_lblUnit12.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblUnit12.gridx = 2;
		gbc_lblUnit12.gridy = 0;
		panelMLEstimation.add( lblUnit12, gbc_lblUnit12 );

		final JLabel lblDiffLength02 = new JLabel( "Diffusion length 0" );
		lblDiffLength02.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblDiffLength02 = new GridBagConstraints();
		gbc_lblDiffLength02.anchor = GridBagConstraints.EAST;
		gbc_lblDiffLength02.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblDiffLength02.gridx = 0;
		gbc_lblDiffLength02.gridy = 1;
		panelMLEstimation.add( lblDiffLength02, gbc_lblDiffLength02 );

		final JLabel ftfDiffLength02 = new JLabel();
		ftfDiffLength02.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfDiffLength02.setMinimumSize( new Dimension( 100, 26 ) );
		ftfDiffLength02.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfDiffLength02 = new GridBagConstraints();
		gbc_ftfDiffLength02.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfDiffLength02.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfDiffLength02.gridx = 1;
		gbc_ftfDiffLength02.gridy = 1;
		panelMLEstimation.add( ftfDiffLength02, gbc_ftfDiffLength02 );

		final JLabel lblUnit22 = new JLabel( "µm" );
		lblUnit22.setFont( lblUnit22.getFont().deriveFont( lblUnit22.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit22 = new GridBagConstraints();
		gbc_lblUnit22.anchor = GridBagConstraints.WEST;
		gbc_lblUnit22.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblUnit22.gridx = 2;
		gbc_lblUnit22.gridy = 1;
		panelMLEstimation.add( lblUnit22, gbc_lblUnit22 );

		final JLabel lblDiffLength12 = new JLabel( "Diffusion length 1" );
		lblDiffLength12.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblDiffLength12 = new GridBagConstraints();
		gbc_lblDiffLength12.anchor = GridBagConstraints.EAST;
		gbc_lblDiffLength12.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblDiffLength12.gridx = 0;
		gbc_lblDiffLength12.gridy = 2;
		panelMLEstimation.add( lblDiffLength12, gbc_lblDiffLength12 );

		final JLabel ftfDiffLength12 = new JLabel();
		ftfDiffLength12.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfDiffLength12.setMinimumSize( new Dimension( 100, 26 ) );
		ftfDiffLength12.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfDiffLength12 = new GridBagConstraints();
		gbc_ftfDiffLength12.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfDiffLength12.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfDiffLength12.gridx = 1;
		gbc_ftfDiffLength12.gridy = 2;
		panelMLEstimation.add( ftfDiffLength12, gbc_ftfDiffLength12 );

		final JLabel lblUnit32 = new JLabel( "µm" );
		lblUnit32.setFont( lblUnit32.getFont().deriveFont( lblUnit32.getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblUnit32 = new GridBagConstraints();
		gbc_lblUnit32.anchor = GridBagConstraints.WEST;
		gbc_lblUnit32.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblUnit32.gridx = 2;
		gbc_lblUnit32.gridy = 2;
		panelMLEstimation.add( lblUnit32, gbc_lblUnit32 );

		final JLabel lblMobileFraction2 = new JLabel( "Mobile fraction" );
		lblMobileFraction2.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblMobileFraction2 = new GridBagConstraints();
		gbc_lblMobileFraction2.anchor = GridBagConstraints.EAST;
		gbc_lblMobileFraction2.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblMobileFraction2.gridx = 0;
		gbc_lblMobileFraction2.gridy = 3;
		panelMLEstimation.add( lblMobileFraction2, gbc_lblMobileFraction2 );

		final JLabel ftfMobileFraction2 = new JLabel();
		ftfMobileFraction2.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfMobileFraction2.setMinimumSize( new Dimension( 100, 26 ) );
		ftfMobileFraction2.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfMobileFraction2 = new GridBagConstraints();
		gbc_ftfMobileFraction2.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfMobileFraction2.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfMobileFraction2.gridx = 1;
		gbc_ftfMobileFraction2.gridy = 3;
		panelMLEstimation.add( ftfMobileFraction2, gbc_ftfMobileFraction2 );

		final JLabel lblPU2 = new JLabel( "Probability of unbinding" );
		lblPU2.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblPU2 = new GridBagConstraints();
		gbc_lblPU2.anchor = GridBagConstraints.EAST;
		gbc_lblPU2.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblPU2.gridx = 0;
		gbc_lblPU2.gridy = 4;
		panelMLEstimation.add( lblPU2, gbc_lblPU2 );

		final JLabel ftfProbUnbinding2 = new JLabel();
		ftfProbUnbinding2.setHorizontalAlignment( SwingConstants.TRAILING );
		ftfProbUnbinding2.setMinimumSize( new Dimension( 100, 26 ) );
		ftfProbUnbinding2.setPreferredSize( new Dimension( 100, 26 ) );
		final GridBagConstraints gbc_ftfProbUnbinding2 = new GridBagConstraints();
		gbc_ftfProbUnbinding2.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfProbUnbinding2.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfProbUnbinding2.gridx = 1;
		gbc_ftfProbUnbinding2.gridy = 4;
		panelMLEstimation.add( ftfProbUnbinding2, gbc_ftfProbUnbinding2 );

		final JPanel panelEstimationButtons = new JPanel();
		panelEstimationButtons.setOpaque( false );
		final GridBagConstraints gbc_panelEstimationButtons = new GridBagConstraints();
		gbc_panelEstimationButtons.anchor = GridBagConstraints.NORTH;
		gbc_panelEstimationButtons.gridwidth = 4;
		gbc_panelEstimationButtons.insets = new Insets( 0, 0, 0, 5 );
		gbc_panelEstimationButtons.fill = GridBagConstraints.HORIZONTAL;
		gbc_panelEstimationButtons.gridx = 0;
		gbc_panelEstimationButtons.gridy = 5;
		panelMLEstimation.add( panelEstimationButtons, gbc_panelEstimationButtons );
		panelEstimationButtons.setLayout( new BoxLayout( panelEstimationButtons, BoxLayout.X_AXIS ) );

		final JButton btnEstimCancel = new JButton( "Cancel" );
		btnEstimCancel.setFont( btnEstimCancel.getFont().deriveFont( btnEstimCancel.getFont().getSize() - 2f ) );
		panelEstimationButtons.add( btnEstimCancel );

		final Component horizontalGlue_1 = Box.createHorizontalGlue();
		panelEstimationButtons.add( horizontalGlue_1 );

		final JButton btnEstimStart = new JButton( "Start estimation" );
		btnEstimStart.setFont( btnEstimStart.getFont().deriveFont( btnEstimStart.getFont().getSize() - 2f ) );
		panelEstimationButtons.add( btnEstimStart );

	}

}
