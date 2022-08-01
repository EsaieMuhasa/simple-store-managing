/**
 * 
 */
package com.spiral.simple.store.app;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.spiral.simple.store.dao.DAOConfigException;
import com.spiral.simple.store.dao.DAOFactory;

/**
 * @author Esaie MUHASA
 *
 */
public final class Laucher {

	/**
	 * @param args
	 * @throws UnsupportedLookAndFeelException 
	 */
	public static void main (String[] args) throws UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(new NimbusLookAndFeel());
		
		try {
			DAOFactory factory = DAOFactory.getInstance();
			MainWindow main = new MainWindow(factory);
			main.setVisible(true);
		} catch (DAOConfigException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
	}

}
