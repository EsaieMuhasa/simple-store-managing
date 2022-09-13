/**
 * 
 */
package com.spiral.simple.store.tools;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.Objects;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import com.trimeur.swing.chart.tools.Utility;

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
	
	
	/**
	 * renvoie l'equivatent numerique de la date en param
	 * @param date
	 * @return
	 */
	public static double toDateAxisValue (Date date) {
		Date middle = Utility.toMiddleTimestampOfDay(date);
		Date toDay = Utility.toMiddleTimestampOfDay(new Date());
		
		if (middle.getTime() == toDay.getTime())
			return 0;
		
		double value = middle.getTime() - toDay.getTime();
		
		value /= 1000d;
		value /= 60d;
		value /= 60d;
		value /= 24d;
		
//		if (middle.getTime() < toDay.getTime())
//			value *= -1;
		
		return value;
	}
	
	/**
	 * verification dela date l'intervale des deux premier dates
	 * @param min
	 * @param max
	 * @param date
	 * @return
	 */
	public static boolean inIntervale (Date min, Date max, Date date) {
		Objects.requireNonNull(min);
		Objects.requireNonNull(max);
		Objects.requireNonNull(date);
		return (min.getTime() >=  date.getTime() && max.getTime() <= date.getTime());
	}
	
	/**
	 * transforme la valeur en parametre en une date.
	 * la formule de transformation de la valeur en une date est liée aux regles de la classe DateAxis
	 * @param value
	 * @return
	 */
	public static Date fromDateAxisValue (final double value) {
		if(value == 0)
			return new Date();
		double days = value * 1000d * 60d * 60d * 24d;
		long time = (long) (System.currentTimeMillis() + days);
		return new Date(time);
	}
	

}
