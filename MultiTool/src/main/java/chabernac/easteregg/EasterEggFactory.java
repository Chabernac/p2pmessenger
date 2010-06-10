package chabernac.easteregg;

import java.util.StringTokenizer;

import chabernac.chat.gui.ChatMediator;

public class EasterEggFactory {
	/*
	public static iPaintable createEasterEgg(ChatMediator aMediator, String anEasterEgg){
		if("matrix".equals(anEasterEgg)){
			return new Matrix(aMediator);
		}
		return new Mystify(aMediator.getMainFrame().getBounds(), 4);
	}
	 */

	public static iEasterEgg createEasterEgg(ChatMediator aMediator, String aParameterlist){
 		StringTokenizer theTokenizer = new StringTokenizer(aParameterlist);

		iEasterEgg theEEgg = new Mystify(aMediator, aMediator.getMainFrame().getBounds(), 4);
		if(theTokenizer.countTokens() >= 1){
			String theEasterEgg = theTokenizer.nextToken();
			if("matrix".equals(theEasterEgg)){
				theEEgg = new Matrix(aMediator);
			}
			if("mystify".equals(theEasterEgg)){
				theEEgg = new Mystify(aMediator, aMediator.getMainFrame().getBounds(), 4);
			}
			if("3d".equals(theEasterEgg)){
				theEEgg = new EasterEgg3d(aMediator);
			}
      if("text".equals(theEasterEgg)){
        theEEgg = new ShowText(aMediator);
      }
			while(theTokenizer.hasMoreTokens()){
				theEEgg.setParameter(theTokenizer.nextToken());
			}
		}
		
		return  theEEgg;
	}
}
