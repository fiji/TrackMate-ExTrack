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
package fr.pasteur.iah.extrack.plugin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import fr.pasteur.iah.extrack.compute.ExTrackParameters;

public class ExTrackActionPanel extends JPanel
{

	private static final long serialVersionUID = 1L;

	final JButton btnEstimStart;

	final JButton btnEstimCancel;

	final JButton btnCompute;

	final JButton btnLoad;

	final JButton btnSave;

	private final JLabel lblEstimProbUnbinding;

	private final JLabel lblEstimMobileFraction;

	private final JLabel lblEstimDiffLength1;

	private final JLabel lblEstimDiffLength0;

	private final JLabel lblEstimLocError;

	private final JFormattedTextField ftfProbUnbinding;

	private final JFormattedTextField ftfMobileFraction;

	private final JFormattedTextField ftfDiffLength1;

	private final JFormattedTextField ftfDiffLength0;

	private final JFormattedTextField ftfLocError;

	private final JLabel lblLog;

	private final NumberFormat formatter = new DecimalFormat( "##########.#############" );

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

		lblLog = new JLabel( "     " );
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

		btnSave = new JButton( "Save" );
		panelButtons.add( btnSave );
		btnSave.setFont( btnSave.getFont().deriveFont( btnSave.getFont().getSize() - 2f ) );

		btnLoad = new JButton( "Load" );
		panelButtons.add( btnLoad );
		btnLoad.setFont( btnLoad.getFont().deriveFont( btnLoad.getFont().getSize() - 2f ) );

		panelButtons.add( Box.createHorizontalGlue() );

		btnCompute = new JButton( "Compute" );
		panelButtons.add( btnCompute );
		btnCompute.setFont( btnCompute.getFont().deriveFont( btnCompute.getFont().getSize() - 2f ) );

		final JLabel lblTitle = new JLabel( ExTrackGuiUtil.getIcon(), JLabel.CENTER );
		lblTitle.setFont( getFont().deriveFont( 42 ) );
		add( lblTitle, BorderLayout.NORTH );

		final JTabbedPane mainPane = new JTabbedPane( JTabbedPane.TOP );
		mainPane.setFont( mainPane.getFont().deriveFont( mainPane.getFont().getSize() - 2f ) );
		add( mainPane, BorderLayout.CENTER );

		/*
		 * Manual input panel.
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

		ftfLocError = new JFormattedTextField( formatter );
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

		ftfDiffLength0 = new JFormattedTextField( formatter );
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

		ftfDiffLength1 = new JFormattedTextField( formatter );
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

		ftfMobileFraction = new JFormattedTextField( formatter );
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

		ftfProbUnbinding = new JFormattedTextField( formatter );
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

		lblEstimLocError = new JLabel();
		lblEstimLocError.setHorizontalAlignment( SwingConstants.TRAILING );
		lblEstimLocError.setMinimumSize( new Dimension( 100, 26 ) );
		lblEstimLocError.setPreferredSize( new Dimension( 100, 26 ) );
		lblEstimLocError.setFont( ftfLocError.getFont() );
		final GridBagConstraints gbc_ftfEstimLocError = new GridBagConstraints();
		gbc_ftfEstimLocError.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfEstimLocError.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfEstimLocError.gridx = 1;
		gbc_ftfEstimLocError.gridy = 0;
		panelMLEstimation.add( lblEstimLocError, gbc_ftfEstimLocError );

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

		lblEstimDiffLength0 = new JLabel();
		lblEstimDiffLength0.setHorizontalAlignment( SwingConstants.TRAILING );
		lblEstimDiffLength0.setMinimumSize( new Dimension( 100, 26 ) );
		lblEstimDiffLength0.setPreferredSize( new Dimension( 100, 26 ) );
		lblEstimDiffLength0.setFont( ftfDiffLength0.getFont() );
		final GridBagConstraints gbc_ftfEstimDiffLength0 = new GridBagConstraints();
		gbc_ftfEstimDiffLength0.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfEstimDiffLength0.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfEstimDiffLength0.gridx = 1;
		gbc_ftfEstimDiffLength0.gridy = 1;
		panelMLEstimation.add( lblEstimDiffLength0, gbc_ftfEstimDiffLength0 );

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

		lblEstimDiffLength1 = new JLabel();
		lblEstimDiffLength1.setHorizontalAlignment( SwingConstants.TRAILING );
		lblEstimDiffLength1.setMinimumSize( new Dimension( 100, 26 ) );
		lblEstimDiffLength1.setPreferredSize( new Dimension( 100, 26 ) );
		lblEstimDiffLength1.setFont( ftfDiffLength1.getFont() );
		final GridBagConstraints gbc_ftfEstimDiffLength1 = new GridBagConstraints();
		gbc_ftfEstimDiffLength1.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfEstimDiffLength1.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfEstimDiffLength1.gridx = 1;
		gbc_ftfEstimDiffLength1.gridy = 2;
		panelMLEstimation.add( lblEstimDiffLength1, gbc_ftfEstimDiffLength1 );

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

		lblEstimMobileFraction = new JLabel();
		lblEstimMobileFraction.setHorizontalAlignment( SwingConstants.TRAILING );
		lblEstimMobileFraction.setMinimumSize( new Dimension( 100, 26 ) );
		lblEstimMobileFraction.setPreferredSize( new Dimension( 100, 26 ) );
		lblEstimMobileFraction.setFont( ftfMobileFraction.getFont() );
		final GridBagConstraints gbc_ftfEstimMobileFraction = new GridBagConstraints();
		gbc_ftfEstimMobileFraction.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfEstimMobileFraction.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfEstimMobileFraction.gridx = 1;
		gbc_ftfEstimMobileFraction.gridy = 3;
		panelMLEstimation.add( lblEstimMobileFraction, gbc_ftfEstimMobileFraction );

		final JLabel lblPU2 = new JLabel( "Probability of unbinding" );
		lblPU2.setFont( getFont().deriveFont( getFont().getSize() - 2f ) );
		final GridBagConstraints gbc_lblPU2 = new GridBagConstraints();
		gbc_lblPU2.anchor = GridBagConstraints.EAST;
		gbc_lblPU2.insets = new Insets( 0, 0, 5, 5 );
		gbc_lblPU2.gridx = 0;
		gbc_lblPU2.gridy = 4;
		panelMLEstimation.add( lblPU2, gbc_lblPU2 );

		lblEstimProbUnbinding = new JLabel();
		lblEstimProbUnbinding.setHorizontalAlignment( SwingConstants.TRAILING );
		lblEstimProbUnbinding.setMinimumSize( new Dimension( 100, 26 ) );
		lblEstimProbUnbinding.setPreferredSize( new Dimension( 100, 26 ) );
		lblEstimProbUnbinding.setFont( ftfProbUnbinding.getFont() );
		final GridBagConstraints gbc_ftfEstimProbUnbinding = new GridBagConstraints();
		gbc_ftfEstimProbUnbinding.fill = GridBagConstraints.HORIZONTAL;
		gbc_ftfEstimProbUnbinding.insets = new Insets( 0, 0, 5, 5 );
		gbc_ftfEstimProbUnbinding.gridx = 1;
		gbc_ftfEstimProbUnbinding.gridy = 4;
		panelMLEstimation.add( lblEstimProbUnbinding, gbc_ftfEstimProbUnbinding );

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

		btnEstimCancel = new JButton( "Cancel" );
		btnEstimCancel.setFont( btnEstimCancel.getFont().deriveFont( btnEstimCancel.getFont().getSize() - 2f ) );
		panelEstimationButtons.add( btnEstimCancel );

		panelEstimationButtons.add( Box.createHorizontalGlue() );

		btnEstimStart = new JButton( "Start estimation" );
		btnEstimStart.setFont( btnEstimStart.getFont().deriveFont( btnEstimStart.getFont().getSize() - 2f ) );
		panelEstimationButtons.add( btnEstimStart );

		/*
		 * Set listeners.
		 */

		final ExTrackParameters params = ExTrackParameters.create().build();
		setManualParameters( params );

		final PropertyChangeListener l = new PropertyChangeListener()
		{

			@Override
			public void propertyChange( final PropertyChangeEvent evt )
			{
				setEstimationParameters( getManualParameters() );
			}
		};
		ftfLocError.addPropertyChangeListener( l );
		ftfDiffLength0.addPropertyChangeListener( l );
		ftfDiffLength1.addPropertyChangeListener( l );
		ftfMobileFraction.addPropertyChangeListener( l );
		ftfProbUnbinding.addPropertyChangeListener( l );

		setEstimationParameters( ExTrackParameters.ESTIMATION_START_POINT );
	}

	public void setEstimationParameters( final ExTrackParameters params )
	{
		lblEstimLocError.setText( formatter.format( params.localizationError ) );
		lblEstimDiffLength0.setText( formatter.format( params.diffusionLength0 ) );
		lblEstimDiffLength1.setText( formatter.format( params.diffusionLength1 ) );
		lblEstimMobileFraction.setText( formatter.format( params.F0 ) );
		lblEstimProbUnbinding.setText( formatter.format( params.probabilityOfUnbinding ) );
	}

	public void setManualParameters( final ExTrackParameters params )
	{
		ftfLocError.setValue( Double.valueOf( params.localizationError ) );
		ftfDiffLength0.setValue( Double.valueOf( params.diffusionLength0 ) );
		ftfDiffLength1.setValue( Double.valueOf( params.diffusionLength1 ) );
		ftfMobileFraction.setValue( Double.valueOf( params.F0 ) );
		ftfProbUnbinding.setValue( Double.valueOf( params.probabilityOfUnbinding ) );
	}

	public ExTrackParameters getManualParameters()
	{
		return ExTrackParameters.create()
				.localizationError( ( ( Number ) ftfLocError.getValue() ).doubleValue() )
				.diffusionLength0( ( ( Number ) ftfDiffLength0.getValue() ).doubleValue() )
				.diffusionLength1( ( ( Number ) ftfDiffLength1.getValue() ).doubleValue() )
				.F0( ( ( Number ) ftfMobileFraction.getValue() ).doubleValue() )
				.probabilityOfUnbinding( ( ( Number ) ftfProbUnbinding.getValue() ).doubleValue() )
				.build();
	}

	public void log( final String msg )
	{
		SwingUtilities.invokeLater( () -> {
			lblLog.setForeground( Color.BLACK );
			lblLog.setText( msg );
		} );
	}

	public void error( final String msg )
	{
		SwingUtilities.invokeLater( () -> {
			lblLog.setForeground( Color.RED );
			lblLog.setText( msg );
		} );
	}
}
