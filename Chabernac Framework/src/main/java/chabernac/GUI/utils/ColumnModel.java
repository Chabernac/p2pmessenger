package chabernac.GUI.utils;

import java.lang.reflect.Method;


public class ColumnModel{

	private String myColumnName = null;;
	private Method myColumnMethod = null;

	public ColumnModel(String aColumnName, Method aColumnMethod){
		myColumnName = aColumnName;
		myColumnMethod = aColumnMethod;
	}

	public String getColumnName(){
		return myColumnName;
	}

	public Method getColumnMethod(){
		return myColumnMethod;
	}
}