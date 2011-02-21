/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.search;


public abstract class AbstractSearchProvider<T> {

  public AbstractSearchProvider( ) {
    super();
  }

  /**
   * return previous item that matches the search criteria or throw exception if no more
   * items are present
   * @return
   */
  public abstract T previous(String aSearchTerm, boolean isRegExpr) throws SearchProviderException;
  
  /**
   * return next item that matches the search criteria or throw exception if no more
   * items are present
   */
  public abstract T next(String aSearchTerm, boolean isRegExpr) throws SearchProviderException;

}
