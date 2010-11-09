package chabernac.gui.components;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JTable;

import chabernac.gui.utils.ColumnModel;
import chabernac.gui.utils.TableListener;
import chabernac.gui.utils.VectorTableModel;

public class VectorTable extends JTable{
	private Vector myVector = null;
	private TableListener myTableListener = null;

	public VectorTable(Vector aVector, ColumnModel[] aColumnModel){
		super(new VectorTableModel(aVector, aColumnModel));
		myVector = aVector;
		addListeners();
	}

	private void addListeners(){
		addMouseListener(new MyMouseListener());
	}

	public void setTableListener(TableListener aTableListener){
		myTableListener = aTableListener;
	}

	private class MyMouseListener extends MouseAdapter{
		public void mouseClicked(MouseEvent e){
			if(myTableListener != null){
				myTableListener.rowClicked(myVector.elementAt(getSelectedRow()));
			}
		}
	}
}