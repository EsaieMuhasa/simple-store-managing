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
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.swing.navs.KaliNav;
import com.spiral.simple.store.swing.navs.KaliNavListener;
import com.spiral.simple.store.tools.Config;

/**
 * @author Esaie MUHASA
 *
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1408399668927215883L;
	
	private static MainWindow instance;
	
	private final WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			doClose();
		}
	};
	
	private final KaliNav navigation = new KaliNav();
	private final MainWorkspace workspace = new MainWorkspace();
	private final KaliNavListener navListener = (nav, index) -> {
		workspace.showAt(index);
		setTitle(Config.get("appName")+" / "+workspace.getComponent(index).getName());
	};
	
	public MainWindow(DAOFactory factory) {
		super(Config.get("appName"));
		
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(screen.width - screen.width/4, screen.height - screen.height/4);
		setLocationRelativeTo(null);
		setIconImage(new ImageIcon(Config.getIcon("panier")).getImage());
		
		init();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(windowAdapter);
		
		JPanel content = (JPanel) getContentPane();
		final Box left = Box.createVerticalBox();
		
		left.add(Box.createVerticalGlue());
		left.add(navigation);
		left.add(Box.createVerticalGlue());
		
		content.add(left, BorderLayout.WEST);
		content.add(workspace, BorderLayout.CENTER);
		
		instance = this;
	}
	
	/**
	 * return last instance valid of main window
	 * @return
	 */
	public static MainWindow getLastInstance () {
		return instance;
	}
	
	/**
	 * initialization of navigation items, and 
	 * main container by items menu
	 */
	private void init() {
		final String [] navs = new String[5];
		final JComponent [] containers = new JComponent[5];
        //adding items
        for(int i = 0; i < 5; i++)
        	navs[i] = "icon/item"+String.valueOf(i+1)+".png";
        //==
        
        
        
        for(int i = 0; i < 5; i++) {
        	containers[i] = new JPanel(new BorderLayout());
        	containers[i].add(new JLabel("Container "+i), BorderLayout.CENTER);
        	containers[i].setName("container"+i);
        }
        
        containers[2] = new WorckspaceProduct();
        containers[4] = new ConfigWorkspace();
        
        navigation.setItems(navs);
        navigation.addNavListener(navListener);
        workspace.addElements(containers);
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
