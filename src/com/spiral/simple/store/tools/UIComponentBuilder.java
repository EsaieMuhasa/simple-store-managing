/**
 * 
 */
package com.spiral.simple.store.tools;

import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

/**
 * @author Esaie MUHASA
 *
 */
public final class UIComponentBuilder {
	
	private static final Font 
			FONT_H1 = new Font("Arial", Font.PLAIN, 20), 
			FONT_H2 = new Font("Arial", Font.PLAIN, 18),
			FONT_H3 = new Font("Arial", Font.PLAIN, 14);
	public static final EmptyBorder EMPTY_BORDER_5 = new EmptyBorder(5, 5, 5, 5);

	private UIComponentBuilder() {}
	
	/**
	 * instantiate a new label and set H1 font size
	 * @param title
	 * @return
	 */
	public static JLabel createH1 (String title) {
		JLabel label = new JLabel(title);
		label.setFont(FONT_H1);
		label.setForeground(Color.BLACK);
		return label;
	}
	
	public static JLabel createH2 (String title) {
		JLabel label = new JLabel(title);
		label.setFont(FONT_H2);
		return label;
	}
	
	/**
	 * @param title
	 * @return
	 */
	public static JLabel createH3 (String title) {
		JLabel label = new JLabel(title);
		label.setFont(FONT_H3);
		return label;
	}
	
	/**
	 * instantiate a image icon and load icon in icons repository
	 * @param name
	 * @return
	 */
	public static ImageIcon loadIcon (String name) {
		ImageIcon icon = new ImageIcon(Config.getIcon(name));
		return icon;
	}
	

}
