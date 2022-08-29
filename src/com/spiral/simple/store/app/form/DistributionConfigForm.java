/**
 * 
 */
package com.spiral.simple.store.app.form;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.spiral.simple.store.beans.BudgetRubric;
import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.DistributionConfigItem;
import com.spiral.simple.store.dao.BudgetRubricDao;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DistributionConfigDao;
import com.spiral.simple.store.dao.DistributionConfigItemDao;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie Muhasa
 *
 */
public class DistributionConfigForm extends JPanel{
	private static final long serialVersionUID = -5844104651787496136L;
	
	private final List<FieldDistributionConfigItem> items = new ArrayList<>();
	private DistributionConfig config;//la configuration encours d'edition
	private final Box container = Box.createVerticalBox();
	
	private final JLabel labelMax = new JLabel();
	private final JLabel labelTitle = new JLabel();
	private final JButton btnValidate = new JButton("Valider", new ImageIcon(Config.getIcon("success")));
	private final JButton btnCancel = new JButton("Annuler", new ImageIcon(Config.getIcon("close")));
	
	private final DistributionConfigDao distributionConfigDao = DAOFactory.getDao(DistributionConfigDao.class);
	private final DistributionConfigItemDao distributionConfigItemDao = DAOFactory.getDao(DistributionConfigItemDao.class);
	private final BudgetRubricDao budgetRubricDao = DAOFactory.getDao(BudgetRubricDao.class);
	
	/**
	 * 
	 */
	public DistributionConfigForm() {
		super(new BorderLayout());
		
		final JPanel 
			bottom = new JPanel(new BorderLayout()),
			btns = new JPanel(),
			top = new JPanel(new BorderLayout()),
			center = new JPanel(new GridLayout(1, 1, 5, 5));
		
		labelMax.setFont(new Font("Arial", Font.PLAIN, 18));
		btns.add(btnValidate);
		btns.add(btnCancel);
		bottom.add(labelMax, BorderLayout.CENTER);
		bottom.add(btns, BorderLayout.EAST);
		bottom.setBackground(CustomTable.GRID_COLOR);
		
		labelTitle.setFont(new Font("Arial", Font.PLAIN, 16));
		top.add(labelTitle, BorderLayout.CENTER);
		top.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
		top.setBackground(CustomTable.GRID_COLOR);
		
		container.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
		JScrollPane scroll = new JScrollPane(container, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		scroll.setBorder(null);
		
		center.add(scroll);
		center.add(new JPanel());// ==> pie chart
		
		add(top, BorderLayout.NORTH);
		add(center, BorderLayout.CENTER);
		add(bottom, BorderLayout.SOUTH);
	}
	
	/**
	 * @return the config
	 */
	public DistributionConfig getConfig() {
		return config;
	}

	/**
	 * @param config the config to set
	 */
	public void setConfig(DistributionConfig config) {
		this.config = config;
		
		for (FieldDistributionConfigItem item : items)
			item.dispose();
		
		container.removeAll();
		reload();
		container.revalidate();
		container.repaint();
		
		if(config != null)
			labelTitle.setText(config.getProduct().getName());
	}
	
	/**
	 * request to reload data associate to current config
	 */
	private void reload() {
		if(config == null)
			return;
		
		BudgetRubric [] rubrics = budgetRubricDao.findAll();
		for (BudgetRubric rubric : rubrics) {
			DistributionConfigItem item = null;

			if ((config.getId() != null && !config.getId().trim().isEmpty())
					&& distributionConfigItemDao.checkByKey(config.getId(), rubric.getId())) {
				item = distributionConfigItemDao.findByKey(config.getId(), rubric.getId());
			} else {
				item = new DistributionConfigItem();
				item.setPercent(100.0d / rubrics.length);
			}
			item.setRubric(rubric);
			item.setOwner(config);
			
			FieldDistributionConfigItem field = new FieldDistributionConfigItem(item);
			items.add(field);
			container.add(field);
			container.add(Box.createVerticalStrut(10));
		}
		
		container.add(Box.createVerticalGlue());
	}


	public static class FieldDistributionConfigItem extends CaptionnablePanel implements CaretListener {
		private static final long serialVersionUID = 2687141923271567849L;
		
		private DistributionConfigItem item;
		private final JTextField field = new JTextField();
		
		/**
		 * construct to initialize distribution field
		 * @param item
		 */
		public FieldDistributionConfigItem(DistributionConfigItem item) {
			super(item.getRubric().getLabel());
			this.item = item;
			buildUI();
			field.addCaretListener(this);
		}
		
		/**
		 * builder of UI components
		 */
		private void buildUI() {
			Box box = Box.createHorizontalBox();
			box.add(field);
			box.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			setContainer(box);
		}

		/**
		 * @return the item
		 */
		public DistributionConfigItem getItem() {
			return item;
		}
		
		/**
		 * if data written in text field has valid number format
		 * true value are returned, otherwise false
		 */
		public boolean isValid () {
			if (!field.getText().trim().isEmpty())
				try {
					Double.parseDouble(field.getText().trim());
					return true;
				} catch (NumberFormatException e) {}
			return false;
		}

		/**
		 * dispose used resource by this component
		 */
		public void dispose() {
			field.removeCaretListener(this);
		}

		@Override
		public void caretUpdate(CaretEvent e) {
			if (isValid())
				item.setPercent(Double.parseDouble(field.getText().trim()));
		}
	}

}
