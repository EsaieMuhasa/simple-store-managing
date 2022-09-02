/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.trimeur.swing.chart.ChartPanel;
import com.trimeur.swing.chart.DateAxis;
import com.trimeur.swing.chart.DefaultAxis;
import com.trimeur.swing.chart.DefaultCloudChartModel;

/**
 * @author Esaie Muhasa
 *
 */
public class PanelDashboard extends JPanel{
	private static final long serialVersionUID = 4305735104729283008L;
	
	private final DefaultAxis xAxis = new DateAxis();
	private final DefaultAxis yAxis = new DefaultAxis();
	private final DefaultCloudChartModel chartModel = new DefaultCloudChartModel(xAxis, yAxis);
	
	private final ChartPanel chartPanel = new ChartPanel(chartModel);

	/**
	 * 
	 */
	public PanelDashboard() {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		add(chartPanel, BorderLayout.CENTER);
		
		yAxis.setMeasureUnit(" $");
	}
	
	@Override
	public String getName() {
		return "Tableau de board";
	}

}
