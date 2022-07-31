package com.spiral.simple.store.swing.navs;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.spiral.simple.store.swing.RoundedPanel;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Esaie MUHASA
 *
 */
public class KaliNav extends JPanel {
	private static final long serialVersionUID = -207710705477272645L;
	
	private final List<KaliNavListener> listeners = new ArrayList<>();
	private final List<KaliNavItem> items = new ArrayList<>();
    private final RoundedPanel panel = new RoundedPanel(new MigLayout("wrap, fill, inset 0", "[center]", "[center]"));
    
    private final ActionListener itemListener = event -> {
    	for (KaliNavItem i : items) 
    		i.setActive(false);
    	
    	KaliNavItem item = (KaliNavItem) event.getSource();
    	item.setActive(true);
    	fireEvent(Integer.parseInt(item.getName()));
    };

    public KaliNav() {
    	initComponents();
    	setOpaque(false);
    }
    
    /**
     * utility to triggered click on item at index in parameter method
     * @param index
     */
    public void doClick (int index) {
    	if(index < items.size())
    		items.get(index).doClick();
    }
    
    /**
     * initialize navigation items
     * @param icons
     */
    public void setItems (String...icons) {
    	for (KaliNavItem item : items) 
    		item.removeActionListener(itemListener);
    	
    	panel.removeAll();
    	panel.add(new JLabel(), "h 20!");
    	for (String icon : icons)
    		addItem(icon);
    	panel.add(new JLabel(), "h 20!");
        
        repaint();
        revalidate();
        items.get(0).setActive(true);//by default we enable first item
        
        repaint();
        revalidate();
        items.get(0).setActive(true);//by default we enable first item
    }

    /**
     * adding new item on navigation panel
     * @param icon
     */
    private void addItem (String icon) {
        KaliNavItem item = new KaliNavItem();
        item.setImage(new ImageIcon(icon).getImage());
        item.addActionListener(itemListener);
        panel.add(item, "w 50!, h 50!");
        items.add(item);
        item.setName(items.indexOf(item)+"");
    }

    /**
     * listing action on navigation item
     * @param event
     */
    public void addNavListener (KaliNavListener event) {
        listeners.add(event);
    }

    /**
     * fire navigation event, on action to item
     * @param index
     */
    private void fireEvent (int index) {
        for (KaliNavListener event : listeners) 
            event.onAction(this, index);
    }

    /**
     * utility method to initialize graphic components
     */
    private void initComponents() {

        panel.setRoundBottomRight(50);
        panel.setRoundTopRight(50);

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        add(panel);
    }

}
