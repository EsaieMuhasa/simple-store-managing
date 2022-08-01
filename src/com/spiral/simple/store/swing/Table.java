/**
 * 
 */
package com.spiral.simple.store.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.File;

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
public class Table extends JTable {
	private static final long serialVersionUID = 1086643646477646234L;
	
	public static final Color 
			BORDER_COLOR = new Color(0xAAAAAA),
			BKG_COLOR_1 = new Color(0xCCCCCC),
			BKG_COLOR_2 = new Color(0xFAFAFA),
			ACTIVE_COLOR = new Color(0x1010AA);
	
	public static final JnaFileChooser XLSX_FILE_CHOOSER = new JnaFileChooser();
	
	static {
		XLSX_FILE_CHOOSER.addFilter("Fichier Excel", "xlsx");;
		XLSX_FILE_CHOOSER.setMultiSelectionEnabled(false);
		XLSX_FILE_CHOOSER.setTitle("Exportation des données au format Excel");
	}
	
	private EmptyBorder padding = new EmptyBorder(5, 5, 5, 5);
	private final CustomTableHeader header = new CustomTableHeader();
    private final CustomTableCellRender cell = new CustomTableCellRender();

	public Table() {
		init();
	}
	
	/**
	 * @param rowData
	 * @param columnNames
	 */
	public Table(Object[][] rowData, Object[] columnNames) {
		super(rowData, columnNames);
		init();
	}

	/**
	 * @param dm
	 */
	public Table(TableModel dm) {
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
        setGridColor(BORDER_COLOR);
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
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			Component com = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			if (isCellSelected(row, column)) {
				com.setBackground(ACTIVE_COLOR);
			} else {
				if (row % 2 == 0) {
					com.setBackground(BKG_COLOR_1);
				} else {
					com.setBackground(BKG_COLOR_2);
				}
			}
			com.setForeground(Color.BLACK);
			setHorizontalAlignment(JLabel.LEFT);
			setBorder(padding);
			return com;
		}
    }

}
