/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.spiral.simple.store.swing.navs.KaliNav;
import com.spiral.simple.store.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1408399668927215883L;
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			doClose();
		}
	};
	
	private final KaliNav navigation = new KaliNav();
	
	
	public MainWindow() {
		super(Config.get("appName"));
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screen.width - screen.width/4, screen.height - screen.height/4);
		setLocationRelativeTo(null);
		
		init();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		
		final Box left = Box.createVerticalBox();
		
		left.add(Box.createVerticalGlue());
		left.add(navigation);
		left.add(Box.createVerticalGlue());
		
		getContentPane().add(left, BorderLayout.WEST);
	}
	
	/**
	 * initialization of navigation items, and 
	 * main container by items menu
	 */
	private void init() {
		final String [] navs = new String[5];
        //adding items
        for(int i = 0; i < 5; i++)
        	navs[i] = "icon/item"+String.valueOf(i+1)+".png";
        //==
        
        navigation.setItems(navs);
	}
	
	/**
	 * 
	 */
	private void doClose () {
		System.exit(0);
		int response = JOptionPane.showConfirmDialog(this, "Voulez-vous vraiment quitter ce programme???", "Fermeture du programme", JOptionPane.OK_CANCEL_OPTION);
		if(response == JOptionPane.OK_OPTION)
			System.exit(0);
	}

}
