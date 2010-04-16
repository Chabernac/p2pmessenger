package chabernac.GUI.utils;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

public class VectorTableModel extends AbstractTableModel{

	private Vector myVector;
	private ColumnModel[] myColumnModel;

	public VectorTableModel(Vector aVector, ColumnModel[] aColumnModel){
		myVector = aVector;
		myColumnModel = aColumnModel;
	}

	public int getRowCount(){return myVector.size();}
	public int getColumnCount(){return myColumnModel.length;}
	public Object getValueAt(int aRow, int aColumn){
		try{
			return myColumnModel[aColumn].getColumnMethod().invoke(myVector.elementAt(aRow), null);
		}catch(Exception e){
			return e.toString();
		}
	}
	public String getColumnName(int aColumn){
		return myColumnModel[aColumn].getColumnName();
	}

}