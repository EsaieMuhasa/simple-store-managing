/**
 * 
 */
package com.spiral.simple.store.app.models;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.table.AbstractTableModel;

import com.spiral.simple.store.beans.DBEntity;
import com.spiral.simple.store.dao.DAOBaseListener;
import com.spiral.simple.store.dao.DAOInterface;


/**
 * @author Esaie MUHASA
 *
 */
public abstract class DBEntityTableModel <T extends DBEntity> extends AbstractTableModel implements DAOBaseListener<T> {
	private static final long serialVersionUID = 6162491854899469995L;
	
	public static final int RPERSIST_EQUEST_ID = 0x77AA77;
	public static final int DELETE_REQUEST_ID = 0x77AA78;
	
	public static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	public static final DateFormat DEFAULT_DATE_TIME_FORMAT = new SimpleDateFormat("dd/MM/yyyy 'à' hh:mm:ss");

	protected List<T> data = new ArrayList<>();
	protected int limit;
	protected int offset;
	protected String title;
	
	protected transient DAOInterface<T> daoInterface;

	/**
	 * construct for initialization of DAT interface
	 * @param daoInterface
	 */
	public DBEntityTableModel(DAOInterface<T> daoInterface) {
		super();
		Objects.requireNonNull(daoInterface, "L'interface du DAO ne doit pars etre null");
		limit = 50;
		this.daoInterface = daoInterface;
		daoInterface.addBaseListener(this);
		reload();
	}
	
	public DBEntityTableModel() {
		super();
		limit = 50;
	}
	
	/**
	 * reload data from database table
	 */
	public synchronized void reload() {
		Objects.requireNonNull(daoInterface, "L'interface du DAO est null. re-definissez la methode reload() ou initialize carrement l'interface du DAO");
		data.clear();
		if(daoInterface != null && daoInterface.checkAll(offset)){
			T [] select =  daoInterface.findAll(limit, offset);
			for (T t : select) 
				data.add(t);
		}
		fireTableDataChanged();
	}
	
	/**
	 * Utility method to execute create/updating method for all data in table model,
	 * and persist data in DAO
	 */
	@SuppressWarnings("unchecked")
	public synchronized void persist() {
		List<T> dataToCreate = new ArrayList<>(),
				dataToUpdate = new ArrayList<>();
		
		for (T t : data) {
			if (t.getId() == null || t.getId().trim().isEmpty())
				dataToCreate.add(t);
			else
				dataToUpdate.add(t);
		}
		
		if (!dataToCreate.isEmpty()){
			if(dataToCreate.size() == 1)
				daoInterface.create(DELETE_REQUEST_ID, dataToCreate.get(0));
			else 
				daoInterface.create(RPERSIST_EQUEST_ID, dataToCreate.toArray(createArray(dataToCreate.size())));
		}
		
		if (!dataToUpdate.isEmpty()){
			if(dataToUpdate.size() == 1)
				daoInterface.update(DELETE_REQUEST_ID, dataToUpdate.get(0));
			else 
				daoInterface.update(RPERSIST_EQUEST_ID, dataToUpdate.toArray(createArray(dataToUpdate.size())));
		}
	}
	
	/**
	 * send request to delete data at index in this table model,
	 * in database
	 * @param index
	 */
	public void deleteAt (int index) {
		daoInterface.delete(DELETE_REQUEST_ID, data.get(index).getId());
	}
	
	/**
	 * utility to create a empty array
	 * @param size
	 * @return
	 */
	protected T [] createArray (int size) {
		throw new RuntimeException("Veillez re-definir la method create array");
	}
	
	/**
	 * @return the limit
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * @param limit the limit to set
	 */
	public void setLimit (int limit) {
		if(limit == this.limit)
			return;
		
		this.limit = limit;
		reload();
	}

	/**
	 * @return the offset
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @param offset the offset to set
	 */
	public void setOffset(int offset) {
		if(offset == this.offset)
			return;
		
		this.offset = offset;
		reload();
	}
	
	/**
	 * modification de l'intervale de selection
	 * @param limit
	 * @param offset
	 */
	public void setInterval (int limit, int offset) {
		if (this.limit == limit && this.offset == offset )
			return;
		
		this.limit = limit;
		this.offset = offset;
		
		reload();
	}
	
	/**
	 * renvoie les comptes tottal des donnees
	 * @return
	 */
	public int getCount () {
		return getRowCount();
	}
	
	/**
	 * Charger la liste suivante des donnees
	 */
	public void next () {
		setOffset(offset + limit);
	}
	
	/**
	 * Charger la liste precedante des donnees
	 */
	public void previous () {
		setOffset(offset - limit);
	}
	
	/**
	 * est-il possible de charger la  liste des donnees suivant???
	 * @return
	 */
	public boolean hasNext() {
		return (getCount() > (offset + limit ));
	}
	
	/**
	 * Est-il possible de lire les donnees precedant
	 * @return
	 */
	public boolean hasPrevious() {
		return (0 <= (offset - limit ));
	}

	@Override
	public boolean isCellEditable (int rowIndex, int columnIndex) {
		return false;
	}
	
	@Override
	public int getRowCount() {
		return data.size();
	}
	
	/**
	 * Renvoie la ligne a l'index en parametre
	 * @param index
	 * @return
	 */
	public T getRow (int index) {
		return this.data.get(index);
	}
	
	/**
	 * Ajout d'une ligne a la fin du tableau
	 * @param row
	 */
	public void addRow (T row) {
		this.data.add(row);
		fireTableRowsInserted(data.size()-1, data.size()-1);	
	}
	
	/**
	 * Ajout d'une suite d'elemenets dans le tableau
	 * @param rows
	 */
	public void addRows (T [] rows) {
		for (T t : rows) {
			data.add(t);
		}
		fireTableRowsInserted( rows.length - data.size() -1, data.size()-1);	
	}
	
	/**
	 * Insersion d'une ligne dans la collection des donnees du model d'un table
	 * @param row
	 * @param index
	 */
	public void addRow (T row, int index) {
		this.data.add(index, row);
		fireTableRowsInserted(index, data.size()-1);
	}
	
	/**
	 * supression d'un ligne du table
	 * @param index
	 */
	public void removeRow (int index) {
		this.data.remove(index);
		if(getRowCount() == 0)
			fireTableDataChanged();
		else 
			fireTableRowsDeleted(index, index);
	}
	
	/**
	 * Mis en jour d'une ligne du model
	 * @param t
	 * @param index
	 */
	public void updateRow (T t, int index) {
		data.set(index, t);
		fireTableRowsUpdated(index, index);
	}
	
	/**
	 * Mise en jour d'une ligne
	 * @param t
	 */
	public void updateRow (T t ) {
		for (int i = 0, count = getRowCount(); i < count; i++) {
			if(data.get(i).getId() == t.getId()) {
				updateRow(t, i);
				return;
			}
		}
	}
	
	/**
	 * force l'intervace graphique de redessiner la linge qui represente cette objet
	 * @param t
	 */
	public void repaintRow (T t) {
		for (int i = 0, count = getRowCount(); i < count; i++) 
			if(data.get(i).getId() == t.getId()) {
				fireTableRowsUpdated(i, i);
				return;
			}
	}
	
	/**
	 * Supression de tout les donnees dans le model
	 */
	public void clear () {
		data.clear();
		fireTableDataChanged();
	}
	
	/**
	 * Renvoie le title du model
	 * @return
	 */
	public String getTitle () {
		return title;
	}
	
	/**
	 * Mis en jour du titre du model
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * renvoie le nombre des colons exportable
	 * @return
	 */
	protected int getExportableColumnCount () {
		return getColumnCount();
	}

	@Override
	public void onCreate(@SuppressWarnings("unchecked") T...ts) {
		addRows(ts);
	}

	@Override
	public void onUpdate(T newStat, T oldStat) {
		if(data.size() <= 1) {
			reload();
			return;
		}
		
		for (int i=0; i < data.size(); i++) {
			T t = data.get(i);
			if(t.getId().equals(newStat.getId())) {
				data.set(i, newStat);
				fireTableRowsUpdated(i, i);
				break;
			}
		}
	}

	@Override
	public void onUpdate(T[] newStat, T[] oldStat) {
		if(data.size() <= 1) {
			reload();
			return;
		}
		
		for (int j = 0; j < newStat.length; j++) {
			for (int i=0; i < data.size(); i++) {
				
				T t = data.get(i);
				if(t.getId().equals(newStat[j].getId())) {
					data.set(i, newStat[j]);
					fireTableRowsUpdated(i, i);
					break;
				}
			}
		}
	}

	@Override
	public void onDelete (@SuppressWarnings("unchecked") T...ts) {
		if(data.size() == 1) {
			data.clear();
			reload();
			fireTableDataChanged();
			return;
		}
		
		for (T e : ts) {
			for (int i=0; i < data.size(); i++) {
				T t = data.get(i);
				if(t.getId().equals(e.getId())) {
					data.remove(i);
					fireTableRowsDeleted(i, i);
					break;
				}
			}
		}
		
	}

}
