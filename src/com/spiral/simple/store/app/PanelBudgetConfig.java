/**
 * 
 */
package com.spiral.simple.store.app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;

import com.spiral.simple.store.app.form.BudgetRubricForm;
import com.spiral.simple.store.app.form.DistributionConfigForm;
import com.spiral.simple.store.app.models.BudgetRubricTableModel;
import com.spiral.simple.store.beans.DistributionConfig;
import com.spiral.simple.store.beans.DistributionConfigItem;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.dao.DAOException;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.DistributionConfigDao;
import com.spiral.simple.store.dao.DistributionConfigItemDao;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.swing.CaptionnablePanel;
import com.spiral.simple.store.swing.CustomTable;
import com.spiral.simple.store.tools.Config;
import com.spiral.simple.store.tools.UIComponentBuilder;
import com.trimeur.swing.chart.DefaultPieModel;
import com.trimeur.swing.chart.DefaultPiePart;
import com.trimeur.swing.chart.PiePanel;
import com.trimeur.swing.chart.tools.Utility;

/**
 * @author Esaie MUHASA
 *
 */
public class PanelBudgetConfig extends JPanel {
	private static final long serialVersionUID = -1539067905599019447L;
	
	private static final String BASE_NAME = "Configuration du budget";
	
	public static final int TOGGLE_CONFIGURATION_REQUEST_ID = 0xFF00FF;
	private final JTabbedPane container = new JTabbedPane(JTabbedPane.BOTTOM);
	private final CaptionnablePanel captionnablePanel = new CaptionnablePanel(BASE_NAME, container);
	
	private final ChangeListener containerChangeListener = event -> onChangeListener();
	private final BudgetRepartitionPanel repartitionPanel = new BudgetRepartitionPanel();

	public PanelBudgetConfig() {
		super(new BorderLayout());
		init();
		add(captionnablePanel, BorderLayout.CENTER);
		container.addChangeListener(containerChangeListener);
	}
	
	/**
	 * lors du changement de l'element selectionnee
	 */
	private void onChangeListener() {
		int index = container.getSelectedIndex();
		if(index == 0)
			captionnablePanel.setCaption("Liste des rubriques budgétaire");
		else if (index == 1) 
			captionnablePanel.setCaption(BASE_NAME+" / "+repartitionPanel.getSelectedProduct().getName());
	}
	
	/**
	 * initialization of graphics components
	 */
	private void init() {
		container.addTab("Rubriques ", new ImageIcon(Config.getIcon("list")), new BudgetRubricPanel(), "liste des rubriques budgétaire");
		container.addTab("Répartition ", new ImageIcon(Config.getIcon("pie")), repartitionPanel, "Repartition des recettes");
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
	class BudgetRepartitionPanel extends JPanel {
		private static final long serialVersionUID = 8500889567486534049L;
		
		private final DefaultComboBoxModel<DistributionConfig> modelConfig = new DefaultComboBoxModel<>();
		private final DefaultPieModel pieModel = new DefaultPieModel();
		
		private final DefaultListModel<Product> productListModel = new DefaultListModel<>();
		private final JTextField fieldFilter = new JTextField();
		private final JButton btnSearch = new JButton(new ImageIcon(Config.getIcon("view")));
		private final JList<Product> productList = new JList<>(productListModel);
		
		private final JComboBox<DistributionConfig> comboConfig = new JComboBox<>(modelConfig);
		private final JButton btnUpdateConfig = new JButton("Modifier", new ImageIcon(Config.getIcon("edit")));
		private final JButton btnAddConfig = new JButton("Nouvelle configuration", new ImageIcon(Config.getIcon("new")));

		private final JPanel toolContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		private DistributionConfigForm configForm;
		private final JPanel chartContainer = new JPanel(new BorderLayout());//homePanel in cardLayout
		private final PiePanel piePanel = new PiePanel(pieModel, CustomTable.GRID_COLOR);
		
		//workspace manager
		private final CardLayout cardLayout = new CardLayout();
		private final JPanel workspace = new JPanel(cardLayout);
		private final JLabel workspaceTitle = UIComponentBuilder.createH2("");//le titre de l'espace de travai (doit etre le nom du produit selectionner)
		//==
		
		private final ProductDao productDao = DAOFactory.getDao(ProductDao.class);
		private final DistributionConfigDao distributionConfigDao = DAOFactory.getDao(DistributionConfigDao.class);
		private final DistributionConfigItemDao distributionConfigItemDao = DAOFactory.getDao(DistributionConfigItemDao.class);
		
		//listeners to validate/cancel form configuration buttons
		private final ActionListener validateActionListener = event -> {
			if (configForm.allDataIsValid()) {
				configForm.setEnabled(false);
				configForm.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				distributionConfigDao.toggle(TOGGLE_CONFIGURATION_REQUEST_ID, configForm.getConfig());
			} else {
				String message = "Les valeurs entrés dans le champs de text doivent être ecrite au format numérique valide."
						+ "\nS'il s'agit d'une valeur numérique avec virgule, alors utilisez un point (.) à la place.";
				JOptionPane.showMessageDialog(MainWindow.getLastInstance(), message, "Erreur", JOptionPane.ERROR_MESSAGE);
			}
		};
		private final ActionListener cancelActionListener = event -> {
			cardLayout.show(workspace, "homePanel");
			toolContainer.setVisible(true);
			configForm.setConfig(null);
		};
		//==
		
		private final DAOListenerAdapter<DistributionConfig> configListenerAdapter = new DAOListenerAdapter<DistributionConfig>() {
			@Override
			public void onCreate(DistributionConfig... data) {
				configForm.setEnabled(true);
				configForm.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				cancelActionListener.actionPerformed(null);
			}

			@Override
			public void onUpdate(DistributionConfig newState, DistributionConfig oldState) {
				onCreate(newState);
			}

			@Override
			public void onError(int requestId, DAOException exception) {
				if (requestId  == TOGGLE_CONFIGURATION_REQUEST_ID) {
					exception.printStackTrace();
					JOptionPane.showMessageDialog(MainWindow.getLastInstance(), exception.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				}
			}
			
		};	
		
		private final ItemListener comboConfigItemListener = event -> onConfigSelectionChange(event);
		
		public BudgetRepartitionPanel () {
			super(new BorderLayout());
			build();
			captionnablePanel.setCaptionPadding(5);
			captionnablePanel.setCaptionFont(new Font("Arial", Font.PLAIN, 18));
			productList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			productList.addListSelectionListener(event -> onItemSelected(event));
			btnAddConfig.addActionListener(event -> onNewConfigListener());
			workspace.add(chartContainer, "homePanel");
			load();
			
			distributionConfigDao.addBaseListener(configListenerAdapter);
			distributionConfigDao.addErrorListener(configListenerAdapter);
			comboConfig.addItemListener(comboConfigItemListener);
			pieModel.setMax(100);
			pieModel.setSuffix(" %");
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
		 * ecoute du changement de l'element selectionnee dans le liste des produits
		 * + si le formulaire est actelement afficher, on re-affiche le panel de demarage (uniquemet pour les produit qui on aumoin une configuration)
		 * + si le produit n'as aucune configuration, alors on affiche directememt le formulaire d'insertion de la config
		 * @param event
		 */
		private void onItemSelected (ListSelectionEvent event) {
			Product product = getSelectedProduct();
			
			pieModel.setTitle("Répartition de recettes du produit \""+product.getName()+"\"");
			if(container.getSelectedIndex() == 1)
				captionnablePanel.setCaption(BASE_NAME+" / "+product.getName());
			
			modelConfig.removeAllElements();
			pieModel.removeAll();
			comboConfig.setEnabled(false);
			
			if(!distributionConfigDao.checkByProduct(product.getId()))
				return;
			
			//chargement des configurations disponible pour le produit (mis en jour du model du combo-box)
			DistributionConfig [] configs = distributionConfigDao.findByProduct(product.getId());
			for (DistributionConfig config : configs)
				modelConfig.addElement(config);
			
			comboConfig.setEnabled(true);
			//==
			reloadChart(getSelectedConfig());
		}
		
		/**
		 * ecoute le changement de l'element selectionnee dans le model du combo box
		 * des confiurations des repartition des recettes pour le produit actuelement selectionnee.
		 * On recharge le graphique
		 * @param event
		 */
		private void onConfigSelectionChange (ItemEvent event) {
			if(event.getStateChange() != ItemEvent.SELECTED)
				return;
			pieModel.removeAll();
			reloadChart(getSelectedConfig());
		}
		
		/**
		 * force le rechargement du model du graphique
		 * @param config
		 */
		private void reloadChart (DistributionConfig config) {
			if (!distributionConfigItemDao.checkByConfig(config.getId()))
				return;
			pieModel.removeAll();
			DistributionConfigItem [] items = distributionConfigItemDao.findByConfig(config.getId());
			for (int i = 0; i < items.length; i++) {
				DistributionConfigItem item = items[i];
				DefaultPiePart part = new DefaultPiePart(Utility.getColorAt(i), item.getPercent(), item.toString());
				pieModel.addPart(part);
			}
		}
		
		/**
		 * renvoie le produit actuelement selectionnee
		 * @return
		 */
		public Product getSelectedProduct () {
			return productListModel.getElementAt(productList.getSelectedIndex());
		}
		
		/**
		 * renvoie la configuration actuelement selectionnee
		 * @return
		 */
		public DistributionConfig getSelectedConfig () {
			return modelConfig.getElementAt(comboConfig.getSelectedIndex());
		}
		
		/**
		 * demande d'affichage du formulaire de creation d'une nouvelle 
		 * configuration pour le produit actuelement selectionne
		 */
		private void onNewConfigListener () {
			Product product = getSelectedProduct();
			
			if(configForm == null) {
				configForm = new DistributionConfigForm();
				configForm.setCommandActionListener(validateActionListener, cancelActionListener);
				workspace.add(configForm, "configForm");
				cardLayout.addLayoutComponent(configForm, "configForm");
			}
			
			DistributionConfig config = null;
			
			if (distributionConfigDao.checkAvailableByProduct(product.getId())) {
				config = distributionConfigDao.findAvailableByProduct(product.getId());
			} else {
				config = new DistributionConfig();
			}
			
			config.setProduct(product);	
			configForm.setConfig(config);
			cardLayout.show(workspace, "configForm");
			toolContainer.setVisible(false);
		}
		
		/**
		 * building UI components
		 */
		private void build() {
			
			final JPanel 
				left = new JPanel(new BorderLayout(5, 5)),
				paddingLeft = new JPanel(new BorderLayout());
			
			final JPanel pieContainer = new JPanel(new BorderLayout());
			
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
			
			pieContainer.add(workspace, BorderLayout.CENTER);//the workspace component has card layout manager
			pieContainer.add(workspaceTitle, BorderLayout.NORTH);
			
			chartContainer.add(piePanel, BorderLayout.CENTER);
			chartContainer.add(toolContainer, BorderLayout.SOUTH);
			chartContainer.setBorder(BorderFactory.createLineBorder(CustomTable.GRID_COLOR));
			final JPanel pieBorder = new JPanel(new BorderLayout());
			pieBorder.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			pieBorder.add(pieContainer, BorderLayout.CENTER);
			
//			toolContainer.setBorder(UIComponentBuilder.EMPTY_BORDER_5);
			comboConfig.setPreferredSize(new Dimension(250, comboConfig.getPreferredSize().height + 4));
			toolContainer.setBackground(CustomTable.GRID_COLOR);
			toolContainer.setOpaque(true);
			toolContainer.add(comboConfig);
			toolContainer.add(btnAddConfig);
			
			final JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, paddingLeft, pieBorder);
			add(split, BorderLayout.CENTER);
		}
		
	}
}
