package chabernac.easteregg;

import java.util.StringTokenizer;

import javax.swing.JFrame;

public class EasterEggFactory {
	/*
	public static iPaintable createEasterEgg(ChatMediator aMediator, String anEasterEgg){
		if("matrix".equals(anEasterEgg)){
			return new Matrix(aMediator);
		}
		return new Mystify(aMediator.getMainFrame().getBounds(), 4);
	}
	 */

	public static iEasterEgg createEasterEgg(JFrame aRootFrame, String aParameterlist){
 		StringTokenizer theTokenizer = new StringTokenizer(aParameterlist);

		iEasterEgg theEEgg = new Mystify(aRootFrame, aRootFrame.getBounds(), 4);
		if(theTokenizer.countTokens() >= 1){
			String theEasterEgg = theTokenizer.nextToken();
			if("matrix".equals(theEasterEgg)){
				theEEgg = new Matrix(aRootFrame);
			}
			if("mystify".equals(theEasterEgg)){
				theEEgg = new Mystify(aRootFrame, aRootFrame.getBounds(), 4);
			}
			if("3d".equals(theEasterEgg)){
				theEEgg = new EasterEgg3d(aRootFrame);
			}
      if("text".equals(theEasterEgg)){
        theEEgg = new ShowText(aRootFrame);
      }
			while(theTokenizer.hasMoreTokens()){
				theEEgg.setParameter(theTokenizer.nextToken());
			}
		}
		
		return  theEEgg;
	}
}
