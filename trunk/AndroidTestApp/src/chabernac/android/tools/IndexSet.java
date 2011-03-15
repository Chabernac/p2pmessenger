/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.android.tools;

import java.util.ArrayList;
import java.util.Collections;

public class IndexSet<T extends Comparable<T>> extends ArrayList<T> {

  private static final long serialVersionUID = -3266273447595020467L;

    public boolean add(T anObject){
      if(contains( anObject )) return false;
      boolean isOk = super.add( anObject );
      Collections.sort(this);
      return isOk;
    }
}
