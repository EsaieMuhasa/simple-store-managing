/**
 * 
 */
package com.spiral.simple.store.app.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.Objects;

import javax.swing.JComponent;

import com.spiral.simple.store.app.models.DBEntityTableModel;
import com.spiral.simple.store.beans.Product;
import com.spiral.simple.store.beans.Stock;
import com.spiral.simple.store.dao.DAOFactory;
import com.spiral.simple.store.dao.DAOListenerAdapter;
import com.spiral.simple.store.dao.ProductDao;
import com.spiral.simple.store.dao.StockDao;
import com.spiral.simple.store.swing.CustomTable;

/**
 * @author Esaie Muhasa
 *
 */
public class StockView extends JComponent {
	private static final long serialVersionUID = 6910705119766475111L;
	
	private static final int VIEW_H = 125; 
	private static final Dimension 
		MAX_SIZE = new Dimension(Short.MAX_VALUE, VIEW_H),
		MIN_SIZE = new Dimension(250, VIEW_H),
		PREF_SIZE = new Dimension(400, VIEW_H);
	
	private final Font 
		TITLE_FONT = new Font("Arial", Font.PLAIN, 16),
		DATE_FONT = new Font("Arial", Font.ITALIC, 14),
		UNIT_PRICE_FONT = new Font("Arial", Font.BOLD, 20);
	
	private static final Color HOVER_COLOR = new Color(0xAAAACC);
	
	//la forme qui perme d'arrondir le images des articles 
	private static final int ROUND_RECT_X = 10, ROUND_RECT_Y = 25, ROUND_RECT_SIZE = 90;
	private static final Area ROUND_PRODUCT_PICTURE = new Area(new Rectangle(ROUND_RECT_X, ROUND_RECT_Y, ROUND_RECT_SIZE, ROUND_RECT_SIZE));
	{
		ROUND_PRODUCT_PICTURE.subtract(new Area(new Ellipse2D.Float(ROUND_RECT_X, ROUND_RECT_Y, ROUND_RECT_SIZE, ROUND_RECT_SIZE)));
	}
	//==
	
	private Stock stock;
	private boolean selected = false;//pour savoie le sotock est selectionner
	private boolean hovered = false;
	
	private final StockDao stockDao = DAOFactory.getDao(StockDao.class);
	private final ProductDao productDao = DAOFactory.getDao(ProductDao.class);
	
	private final StockListenerAdapter stockListenerAdapter = new StockListenerAdapter();
	private final ProductListenerAdapter productListenerAdapter = new ProductListenerAdapter();
	private final ViewMouseListener mouseListener = new ViewMouseListener();

	/**
	 * constructeur par defaut
	 */
	public StockView() {
		super();
		initSize();
		init();
	}
	
	/**
	 * initialisation du stock dont on doit visualiser l'etat
	 * @param stock
	 */
	public StockView (Stock stock) {
		super();
		Objects.requireNonNull(stock);
		this.stock = stock;
		listeningDao();
		initSize();
		init();
	}
	
	/**
	 * ecoute des evenements bas niveau
	 */
	private void init() {
		addMouseListener(mouseListener);
	}
	
	/**
	 * @return the selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * @param selected the selected to set
	 */
	public void setSelected(boolean selected) {
		if(this.selected == selected)
			return;
		
		this.selected = selected;
		repaint();
	}

	/**
	 * initialization of required size component geometry
	 */
	private void initSize() {
		setPreferredSize(PREF_SIZE);
		setMaximumSize(MAX_SIZE);
		setMinimumSize(MIN_SIZE);
	}

	@Override
	protected void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		if(stock == null)
			return;
		
		Graphics2D g = (Graphics2D) graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
		g.setColor(hovered? HOVER_COLOR : CustomTable.GRID_COLOR);
		int left = ROUND_RECT_X *2 + ROUND_RECT_SIZE ;
		
		g.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
		g.drawImage(stock.getProduct().getImage().getImage(), ROUND_RECT_X, ROUND_RECT_Y, ROUND_RECT_SIZE, ROUND_RECT_SIZE, null);
		g.fill(ROUND_PRODUCT_PICTURE);
		
		g.setColor(Color.BLACK);
		g.setFont(TITLE_FONT);
		g.drawString(stock.getProduct().getName(), ROUND_RECT_X + left, ROUND_RECT_Y);
		
		double widthProgress = getWidth() - left - ROUND_RECT_X; // = stock.quantity = 100 %
		double available = (widthProgress / stock.getQuantity()) * stock.getAvailableQuantity();
		
		g.drawRoundRect(left, ROUND_RECT_Y + 10, (int)widthProgress , 10, 10, 10);
		g.fillRoundRect(left, ROUND_RECT_Y + 10, (int)available, 10, 10, 10);

		g.setColor(Color.BLACK);
		String text = Stock.DECIMAL_FORMAT.format(stock.getAvailableQuantity())+" "+stock.getMeasureUnit().getShortName();
		text += " / "+Stock.DECIMAL_FORMAT.format(stock.getQuantity())+" "+stock.getMeasureUnit().getShortName();
		
		g.drawString(text, left + ROUND_RECT_X, ROUND_RECT_Y * 2 + 15);
		g.setFont(UNIT_PRICE_FONT);
		
		text = Stock.DECIMAL_FORMAT.format(stock.getDefaultUnitPrice())+" "+ stock.getSalesCurrency().getShortName() +" / "+stock.getMeasureUnit().getShortName();
		int w = g.getFontMetrics().stringWidth(text) + 30;
		int x = getWidth()/2 - w/2 + left;
		int y = ROUND_RECT_Y * 2 + 20;
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(x, y, w  , 30, 30, 30);
		g.setColor(Color.BLACK);
		g.drawString(text, x + 15, y + 22);
		
		g.setFont(DATE_FONT.deriveFont(Font.BOLD, 12));
		g.setColor(Color.BLUE.darker().darker().darker());
		String date = "Arrivage du "+DBEntityTableModel.DEFAULT_DATE_FORMAT.format(stock.getDate())+", enregistrer en date du "+DBEntityTableModel.DEFAULT_DATE_FORMAT.format(stock.getRecordingDate());
		g.drawString(date, getWidth() - g.getFontMetrics().stringWidth(date) - 15, getHeight() - 5);
		
		if(selected){
			g.setColor(Color.BLUE.darker());
			g.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 10, 10);
		}
	}
	
	/**
	 * utility to subscribe StockView component on DAO interfaces
	 */
	private void listeningDao() {
		stockDao.addBaseListener(stockListenerAdapter);
		productDao.addBaseListener(productListenerAdapter);
	}
	
	/**
	 * unsubscribe StockView component to DAO interfaces
	 */
	public void dispose() {
		stockDao.removeBaseListener(stockListenerAdapter);
		productDao.removeBaseListener(productListenerAdapter);
		removeMouseListener(mouseListener);
	}
	
	/**
	 * @return the stock
	 */
	public Stock getStock() {
		return stock;
	}

	/**
	 * @param stock the stock to set
	 */
	public void setStock (Stock stock) {
		if(this.stock == stock)
			return;
		
		this.stock = stock;
		if (stock == null)
			dispose();
		else
			listeningDao();
	}



	/**
	 * @author Esaie Muhasa
	 * Ecouteur des evenements des chanement du stock
	 */
	private class StockListenerAdapter extends DAOListenerAdapter<Stock> {

		@Override
		public void onUpdate(Stock newState, Stock oldState) {
			if (stock.getId() == oldState.getId()) {
				stock = newState;
				repaint();
			}
		}
	}
	
	/**
	 * @author Esaie Muhasa
	 * adapter class to listening product DAO interface
	 */
	private class ProductListenerAdapter extends DAOListenerAdapter<Product> {

		@Override
		public void onUpdate(Product newState, Product oldState) {
			if(stock.getProduct().getId() == newState.getId()) {
				stock.setProduct(newState);
				repaint();
			}
		}
		
	}
	
	/**
	 * @author Esaie Muhasa
	 * event de la sourie
	 */
	private class ViewMouseListener extends MouseAdapter{

		@Override
		public void mouseEntered(MouseEvent e) {
			hovered = true;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			hovered = false;
			repaint();
		}
		
	}

}
