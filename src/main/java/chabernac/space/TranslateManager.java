package chabernac.space;

import java.util.ArrayList;


public abstract class TranslateManager {
	private ArrayList<iTranslatable> myTranslationObjectcountainer = null;
	
	public TranslateManager(){
		myTranslationObjectcountainer = new ArrayList<iTranslatable>();
	}
	
	public void addTranslatable(iTranslatable aTranslatable){
		myTranslationObjectcountainer.add(aTranslatable);
	}
	
	public void removeTranslatable(iTranslatable aTranslatable){
		myTranslationObjectcountainer.remove(aTranslatable);
	}
	
	public void doTranslation(){
		iTranslatable theTranslatable = null;
		for(int i=0;i<myTranslationObjectcountainer.size();i++){
			theTranslatable = myTranslationObjectcountainer.get(i);
			translate(theTranslatable);
		}
	}
	
	protected abstract void translate(iTranslatable aTranslatable);
}
