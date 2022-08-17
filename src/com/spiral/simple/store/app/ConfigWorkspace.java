/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class ConfigWorkspace extends JPanel {
	private static final long serialVersionUID = 4347424585824918024L;
	private final JTabbedPane container = new JTabbedPane(JTabbedPane.RIGHT);

	public ConfigWorkspace() {
		super(new BorderLayout());
		
		init();
		add(container, BorderLayout.CENTER);
		setBorder(UIComponentBuilder.EMPTY_BORDER_5);
	}
	
	/**
	 * initialization of content components by container component
	 */
	private void init() {
		final String titles  [] = {"Dévises", "Unité de mésure", "Budjet"};
		final CaptionnablePanel [] contents = {				
				new CaptionnablePanel("Configuration des devises", new PanelCurrency()),
				new CaptionnablePanel("Configuration des unite de mesure", new PanelMeasureUnit()),
				new CaptionnablePanel("Configuration de la repartition des recette", new PanelBudgetConfig())
		};
		
		for (int i = 0; i < titles.length; i++) {
			contents[i].setCaptionFont(new Font("Arial", Font.PLAIN, 20));
			contents[i].setCaptionPadding(5);
			container.addTab(titles[i], contents[i]);
		}
	}
	
	@Override
	public String getName() {
		return "Configurations";
	}

}
