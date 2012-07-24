
package chabernac.space;

import java.util.ArrayList;

public class TranslateManagerContainer {
	private ArrayList<TranslateManager> myContainer = null;
	
	public TranslateManagerContainer(){
		myContainer = new ArrayList<TranslateManager>();
	}
	
	public void addTranslateManager(TranslateManager aManager){
		myContainer.add(aManager);
	}
	
	public void removeManager(TranslateManager aManager){
		myContainer.remove(aManager);
	}
	
	public void doTranslation(){
		TranslateManager theManager = null;
		for(int i=0;i<myContainer.size();i++){
			theManager = (TranslateManager)myContainer.get(i);
			theManager.doTranslation();
		}
	}
}
