/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class CaptionnablePanel extends JPanel {

	private static final long serialVersionUID = 2224079186371984152L;
	private final JPanel header = new JPanel(new BorderLayout());
	private final JLabel title = new JLabel();
	private JComponent container;

	/**
	 * 
	 */
	public CaptionnablePanel() {
		super(new BorderLayout());
		init();
	}
	
	public CaptionnablePanel(String title) {
		super(new BorderLayout());
		this.title.setText(title);
		init();
	}
	
	public CaptionnablePanel(String title, JComponent container) {
		super(new BorderLayout());
		this.container = container;
		this.title.setText(title);
		init();
	}
	
	private void init () {
		add(header, BorderLayout.NORTH);
		if(container  != null)
			add(container, BorderLayout.CENTER);
		
		header.add(title, BorderLayout.CENTER);
		header.setBackground(CustomTable.GRID_COLOR);
		setBorder(BorderFactory.createLineBorder(header.getBackground()));
	}
	
	/**
	 * the title setter
	 * @param title
	 */
	public void setCaption(String title) {
		this.title.setText(title);
	}
	
	/**
	 * update padding by caption component
	 * @param padding
	 */
	public void setCaptionPadding (int padding) {
		if(padding <= 0)
			header.setBorder(null);
		else
			header.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
	}
	
	/**
	 * the title font setter
	 * @param font
	 */
	public void setCaptionFont (Font font) {
		title.setFont(font);
	}
	
	/**
	 * the background color for caption and border color of container
	 * @param color
	 */
	public void setCaptionBackground (Color color) {
		header.setBackground(color);
		setBorder(BorderFactory.createLineBorder(color));
	}
	
	/**
	 * the title background color getter
	 * @return
	 */
	public Color getCaptionBackground () {
		return header.getBackground();
	}
	
	/**
	 * the title font getter
	 * @return
	 */
	public Font getCaptioneFont () {
		return title.getFont();
	}
	
	/**
	 * the container to set
	 * @param container
	 */
	public void setContainer(JComponent container) {
		if(this.container != null){
			this.container.setBorder(null);
			remove(this.container);
		}
		
		this.container = container;
		container.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		add(container, BorderLayout.CENTER);
		revalidate();
		repaint();
	}

	/**
	 * @return the header
	 */
	public JPanel getHeader() {
		return header;
	}
}
