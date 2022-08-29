/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;

import com.spiral.simple.store.app.form.BudgetRubricForm;
import com.spiral.simple.store.app.models.BudgetRubricTableModel;
import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelBudgetConfig extends JPanel {
	private static final long serialVersionUID = -1539067905599019447L;
	
	private final JTabbedPane container = new JTabbedPane(JTabbedPane.BOTTOM);

	public PanelBudgetConfig() {
		super(new BorderLayout());
		init();
		add(container, BorderLayout.CENTER);
	}
	
	/**
	 * initialization of graphics components
	 */
	private void init() {
		container.addTab("Rubriques ", new ImageIcon(Config.getIcon("list")), new BudgetRubricPanel(), "liste des rubriques budgétaire");
		container.addTab("Répartition ", new ImageIcon(Config.getIcon("pie")), new BudgetRepartitionPanel(), "Repartition des recettes");
	}
	
	/**
	 * @author Esaie MUHASA
	 *
	 */
	static class BudgetRubricPanel extends JPanel {
		private static final long serialVersionUID = 8292287071893119734L;
		
		private final JButton btnNewBudgetRubric = new JButton("Nouvelle rubrique", new ImageIcon(Config.getIcon("new")));
		private final JButton btnUpdateBudgetRubric = new JButton("Mise en jour", new ImageIcon(Config.getIcon("edit")));
		private final JButton btnDeleteBudgetRubric = new JButton("Supprimer", new ImageIcon(Config.getIcon("close")));
		
		private final ActionListener btnNewBudgetRubricListener = event -> onShowCreateBudgetRubricForm();
		private final ActionListener btnUpdateBudgetRubricListener = event -> onShowUpdateBudgetRubricForm();
		private final ActionListener btnDeleteBudgetRubricListener = event -> onDeleteBudgetRubric();
		
		private final BudgetRubricTableModel tableModel = new BudgetRubricTableModel();
		private final CustomTable table = new CustomTable(tableModel);
		
		private JDialog budgetRubricDialog;
		private BudgetRubricForm budgetRubicForm;
		
		public BudgetRubricPanel() {
			super(new BorderLayout());
			build();
			btnNewBudgetRubric.addActionListener(btnNewBudgetRubricListener);
			btnUpdateBudgetRubric.addActionListener(btnUpdateBudgetRubricListener);
			btnDeleteBudgetRubric.addActionListener(btnDeleteBudgetRubricListener);
			
			btnUpdateBudgetRubric.setEnabled(false);
			btnDeleteBudgetRubric.setEnabled(false);
		}
		
		/**
		 * initialization of UI components
		 */
		private void build() {
			final JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			final JPanel center  = new JPanel(new BorderLayout());
			
			bottom.add(btnNewBudgetRubric);
			bottom.add(btnUpdateBudgetRubric);
			bottom.add(btnDeleteBudgetRubric);
			bottom.setBackground(CustomTable.GRID_COLOR);
			
			JScrollPane scroll = new JScrollPane(table);
			scroll.setBorder(null);
			center.add(scroll, BorderLayout.CENTER);
			center.setBorder(BorderFactory.createLineBorder(table.getGridColor()));
			
			add(bottom, BorderLayout.SOUTH);
			add(center, BorderLayout.CENTER);
		}
		
		/**
		 * utility method to build and show dialog box container by 
		 * budget row form, to create a new budget row
		 */
		private void onShowCreateBudgetRubricForm() {
			buildBudgetRubricDailog();
			
			budgetRubricDialog.setLocationRelativeTo(MainWindow.getLastInstance());
			budgetRubricDialog.setVisible(true);
		}
		
		/**
		 * utility method to build and show dialog box container by
		 * budget row form, to update information of exist budget row
		 */
		private void onShowUpdateBudgetRubricForm() {
			buildBudgetRubricDailog();
			
			budgetRubricDialog.setLocationRelativeTo(MainWindow.getLastInstance());
			budgetRubricDialog.setVisible(true);
		}

		/**
		 * utility method to delete budget(s) row(s) in database
		 */
		private void onDeleteBudgetRubric() {
		}
		
		
		/**
		 * utility method to build measure unit dialog frame
		 */
		private void buildBudgetRubricDailog () {
			if (budgetRubricDialog == null) {
				budgetRubricDialog = new JDialog(MainWindow.getLastInstance(), "Insértion d'une unite de mésure", true);
				
				budgetRubicForm = new BudgetRubricForm();
				budgetRubicForm.reload();
				
				JPanel content = (JPanel) budgetRubricDialog.getContentPane();
				content.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
				content.add(budgetRubicForm, BorderLayout.CENTER);
				
				budgetRubricDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				budgetRubricDialog.pack();
				budgetRubricDialog.setSize(450, budgetRubricDialog.getHeight() + 120);
				budgetRubricDialog.setResizable(false);
			}
			budgetRubricDialog.setLocationRelativeTo(MainWindow.getLastInstance());
		}
	}
	
	/**
	 * @author Esaie MUHASA
	 * panel to managing recipe repartition
	 */
	static class BudgetRepartitionPanel extends JPanel {
		private static final long serialVersionUID = 8500889567486534049L;
		
		private final DefaultComboBoxModel<DistributionConfig> modelConfig = new DefaultComboBoxModel<>();
		
		private final DefaultListModel<Product> productListModel = new DefaultListModel<>();
		private final JTextField fieldFilter = new JTextField();
		private final JButton btnSearch = new JButton(new ImageIcon(Config.getIcon("view")));
		private final JList<Product> productList = new JList<>(productListModel);
		
		private final JComboBox<DistributionConfig> comboConfig = new JComboBox<>(modelConfig);
		private final JButton btnUpdateConfig = new JButton("Modifier", new ImageIcon(Config.getIcon("edit")));
		private final JButton btnAddConfig = new JButton("Nouvelle configuration", new ImageIcon(Config.getIcon("new")));

		private ProductDao productDao = DAOFactory.getDao(ProductDao.class);

		
		public BudgetRepartitionPanel () {
			super(new BorderLayout());
			build();
			productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			productList.addListSelectionListener(event -> onItemSelected(event));
			load();
		}
		
		/**
		 * loading data.
		 * this method must be called on start application
		 */
		private void load() {
			if(productDao.countAll() != 0) {
				Product [] products = productDao.findAll();
				for (Product product : products)
					productListModel.addElement(product);
				
				
				productList.setSelectedIndex(0);
			}
		}
		
		/**
		 * listening list item selection event
		 * @param event
		 */
		private void onItemSelected (ListSelectionEvent event) {
			Product product = productListModel.getElementAt(productList.getSelectedIndex());
		}
		
		/**
		 * building UI components
		 */
		private void build() {
			
			final JPanel 
				left = new JPanel(new BorderLayout(5, 5)),
				paddingLeft = new JPanel(new BorderLayout());
			
			final JPanel pieContainer = new JPanel(new BorderLayout());
			final JPanel toolContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			
			final Box leftTop = Box.createHorizontalBox();
			final JScrollPane scroll = new JScrollPane(productList);
			
			leftTop.add(fieldFilter);
			leftTop.add(btnSearch);
			
			scroll.setBorder(null);
			left.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
			left.add(scroll, BorderLayout.CENTER);
			left.add(leftTop, BorderLayout.NORTH);
			
			paddingLeft.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			paddingLeft.add(left, BorderLayout.CENTER);
			
			pieContainer.add(toolContainer, BorderLayout.SOUTH);
			
//			toolContainer.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			comboConfig.setPreferredSize(new Dimension(250, comboConfig.getPreferredSize().height + 4));
			toolContainer.setBackground(CustomTable.GRID_COLOR);
			toolContainer.setOpaque(true);
			toolContainer.add(comboConfig);
			toolContainer.add(btnAddConfig);
			
			final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paddingLeft, pieContainer);
			add(split, BorderLayout.CENTER);
		}
		
	}
}
