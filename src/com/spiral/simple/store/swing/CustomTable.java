/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

import jnafilechooser.api.JnaFileChooser;

/**
 * @author Esaie MUHASA
 *
 */
public class CustomTable extends JTable {
	private static final long serialVersionUID = 1086643646477646234L;
	
	public static final Color 
			GRID_COLOR = new Color(0xAAAAAA),
			BKG_COLOR_1 = new Color(0xCCCCCC),
			BKG_COLOR_2 = new Color(0xFAFAFA),
			ACTIVE_COLOR = new Color(92, 170, 250);
	
	public static final JnaFileChooser XLSX_FILE_CHOOSER = new JnaFileChooser();
	
	static {
		XLSX_FILE_CHOOSER.addFilter("Fichier Excel", "xlsx");;
		XLSX_FILE_CHOOSER.setMultiSelectionEnabled(false);
		XLSX_FILE_CHOOSER.setTitle("Exportation des données au format Excel");
	}
	
	private EmptyBorder padding = new EmptyBorder(5, 5, 5, 5);
	private final CustomTableHeader header = new CustomTableHeader();
    private final CustomTableCellRender cell = new CustomTableCellRender();

	public CustomTable() {
		init();
	}
	
	/**
	 * @param rowData
	 * @param columnNames
	 */
	public CustomTable(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		init();
	}

	/**
	 * @param dm
	 */
	public CustomTable(TableModel dm) {
		super(dm);
		init();
	}
	
	/**
	 * 
	 */
	private void init() {
		
		getTableHeader().setDefaultRenderer(header);
		getTableHeader().setReorderingAllowed(false);
		
		setShowHorizontalLines(true);
		setShowVerticalLines(true);
        setGridColor(GRID_COLOR);
        setRowHeight(40);
        setForeground(Color.WHITE);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        setSelectionBackground(ACTIVE_COLOR);

        setDefaultRenderer(Object.class, cell);
	}
	
	/**
	 * Modification des marges horizotaux pour chaque case du table
	 * @param padding
	 */
	public void setPadding (int padding) {
		this.padding = new EmptyBorder(padding, padding, padding, padding);
		this.repaint();
	}
	
	/**
	 * filtr d'exportation des donnees au format excel
	 * @author Esaie MUHASA
	 */
	public static class FileFilterExcel extends FileFilter {

		@Override
		public boolean accept(File f) {
			if(f.isDirectory())
				return true;
			return f.getName().matches("^(.+)(\\.xlsx)$");
		}

		@Override
		public String getDescription() {
			return "Format excel 2010 ou plus";
		}
		
	}
	
    private class CustomTableHeader extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -2540200968584306187L;

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            com.setForeground(Color.BLACK);
            com.setFont(com.getFont().deriveFont(Font.BOLD, 12));
            setHorizontalAlignment(JLabel.LEFT);
            setBorder(padding);
            return com;
        }
    }
    
    private class CustomTableCellRender extends DefaultTableCellRenderer {
		private static final long serialVersionUID = 8884035252436134429L;
		
		private Image image;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			image = value.getClass() == ImageIcon.class? ((ImageIcon)value).getImage() : null;
			if (isCellSelected(row, column)) {
				setBackground(ACTIVE_COLOR);
			} else {
				if (row % 2 == 0)
					setBackground(BKG_COLOR_1);
				else
					setBackground(BKG_COLOR_2);
			}
			setForeground(Color.BLACK);
			setHorizontalAlignment(JLabel.LEFT);
			setBorder(padding);
			return this;
		}
		
		
		@Override
		protected void paintComponent(Graphics g) {
			if(image == null){
				super.paintComponent(g);
				return;
			}
			
			Graphics2D g2 = (Graphics2D) g.create();
	        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	        int size = 36;
	        int width = getWidth();
	        int height = getHeight();
	        
	        int x = (width - size) / 2;
	        int y = (height - size) / 2;
	        
	        Area area = new Area(new Rectangle(width, height));
	        Ellipse2D.Double oval = new Ellipse2D.Double(x+1, y+1, size-2, size-2);
	        area.subtract(new Area(oval));
	        
	        g2.drawImage(image, x, y, size, size, null);
	        g2.setColor(getBackground());
	        g2.fill(area);
	        
	        g2.dispose();
		}
    }

}
