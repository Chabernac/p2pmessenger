/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.tools;

import java.util.HashMap;

public class PropertyMap extends HashMap< String, Object > {
  /**
	 * 
	 */
	private static final long serialVersionUID = 7551356327353644803L;

public Object getProperty(String aProperty, Object aDefault){
    if(!containsKey( aProperty )) return aDefault;
    return get( aProperty );
  }
  
  public void setProperty(String aKey, Object aProperty){
    put(aKey, aProperty);
  }
}
