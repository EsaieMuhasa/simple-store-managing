/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.spiral.simple.store.app.form.MeasureUnitForm;
import com.spiral.simple.store.app.models.MeasureUnitTableModel;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 * main panel to manage measures unit configurations
 */
public class PanelMeasureUnit extends JPanel{
	private static final long serialVersionUID = 8350504683885669406L;
	
	private final JButton btnNewMeasureUnit = new JButton("Nouvelle unité de mésure", new ImageIcon(Config.getIcon("new")));
	private final JButton btnUpdateMeasureUnit = new JButton("Mise en jour", new ImageIcon(Config.getIcon("edit")));
	private final JButton btnDeleteMeasureUnit = new JButton("Supprimer", new ImageIcon(Config.getIcon("close")));
	
	private final ActionListener btnNewMeasureUnitListener = event -> showCreateMeasureUnitForm();
	private final ActionListener btnUpdateMeasureUnitListener = event -> showUpdateMeasureForm();
	private final ActionListener btnDeleteMeasureUnitListener = event -> deleteMeasureUnit();
	
	private final MeasureUnitTableModel tableModel = new MeasureUnitTableModel();
	private final CustomTable table = new CustomTable(tableModel);
	
	private JDialog measureUnitDialog;
	private MeasureUnitForm measureUnitForm;
	
	public PanelMeasureUnit() {
		super(new BorderLayout());
		init();
		btnNewMeasureUnit.addActionListener(btnNewMeasureUnitListener);
		btnUpdateMeasureUnit.addActionListener(btnUpdateMeasureUnitListener);
		btnDeleteMeasureUnit.addActionListener(btnDeleteMeasureUnitListener);
		
		btnUpdateMeasureUnit.setEnabled(false);
		btnDeleteMeasureUnit.setEnabled(false);
	}
	
	/**
	 * initialization of UI components
	 */
	private void init() {
		final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		final JPanel center  = new JPanel(new BorderLayout());
		
		bottom.add(btnNewMeasureUnit);
		bottom.add(btnUpdateMeasureUnit);
		bottom.add(btnDeleteMeasureUnit);
		bottom.setBackground(CustomTable.GRID_COLOR);
		
		JScrollPane scroll = new JScrollPane(table);
		scroll.setBorder(null);
		center.add(scroll, BorderLayout.CENTER);
		center.setBorder(BorderFactory.createLineBorder(table.getGridColor()));
		
		add(bottom, BorderLayout.SOUTH);
		add(center, BorderLayout.CENTER);
	}
	
	/**
	 * process to perform deletion action
	 */
	private void deleteMeasureUnit () {
		
	}
	
	/**
	 * utility method to show dialog owner of measure unit form
	 * to insert new measure unit in database
	 */
	private void showCreateMeasureUnitForm () {
		buildMeasureUnitDailog();
		
		measureUnitDialog.setVisible(true);
	}
	
	/**
	 * utility method to perform updating operation of measure unit informations
	 */
	private void showUpdateMeasureForm () {
		buildMeasureUnitDailog();
		
		measureUnitDialog.setVisible(true);
	}
	
	/**
	 * utility method to build measure unit dialog frame
	 */
	private void buildMeasureUnitDailog () {
		if (measureUnitDialog == null) {
			measureUnitDialog = new JDialog(MainWindow.getLastInstance(), "Insértion d'une unite de mésure", true);
			
			measureUnitForm = new MeasureUnitForm();
			measureUnitForm.reload();
			
			JPanel content = (JPanel) measureUnitDialog.getContentPane();
			content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			content.add(measureUnitForm, BorderLayout.CENTER);
			
			measureUnitDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			measureUnitDialog.pack();
			measureUnitDialog.setSize(450, measureUnitDialog.getHeight() + 5);
			measureUnitDialog.setResizable(false);
		}
		measureUnitDialog.setLocationRelativeTo(MainWindow.getLastInstance());
	}

}
