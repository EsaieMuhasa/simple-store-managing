/**
 * 
 */
package com.spiral.simple.store.app;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

import com.spiral.simple.store.dao.DAOConfigException;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.tools.Config;

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
		
		File loggDir = new File(Config.get("workspace")+"logg/");
		File workspaceDir = new File(Config.get("workspace"));
		File productsDir = new File(Config.get("workspace")+"products/");
		
		if(!workspaceDir.isDirectory())
			workspaceDir.mkdirs();
		
		if(!loggDir.isDirectory())
			loggDir.mkdirs();
		
		if(!productsDir.isDirectory())
			productsDir.mkdirs();
		
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
