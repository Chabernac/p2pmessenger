package chabernac.tools;

import java.util.HashMap;
import java.util.Map;

public class SmileyTools {
  private static Map<String, String> SMILEYS = new HashMap<String, String>();
  public static boolean ENABLED = false;
  
  static{
    SMILEYS.put("\\:\\-\\)", "http://serve.mysmiley.net/happy/happy0024.gif");
    SMILEYS.put("\\:\\-\\(", "http://serve.mysmiley.net/sad/sad0024.gif");
  }
  
  public static String replaceSmileys(String aString){
    if(!ENABLED) return aString;
    for(String theKey : SMILEYS.keySet()){
      aString = aString.replaceAll(theKey, SMILEYS.get(theKey));
    }
    
    aString = aString.replaceAll("\\[smiley:(\\p{Alpha}+)(\\d{4})\\]", "http://serve.mysmiley.net/$1/$1$2.gif");
    return aString;
  }
}
