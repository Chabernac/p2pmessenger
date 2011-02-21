/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.search;


public class SearchDialogTest {
  public static void main(String args[]){
    SearchDialog theDialog = new SearchDialog( null,  new DummySearchProvider(), true );
    theDialog.setVisible( true );
  }
  
  public static class DummySearchProvider extends AbstractSearchProvider {

    public DummySearchProvider( ) {
      super( );
    }

    @Override
    public Object next(String aSearchTerm, boolean isRegExpr) throws SearchProviderException {
      System.out.println("next");
      return null;
    }

    @Override
    public Object previous(String aSearchTerm, boolean isRegExpr) throws SearchProviderException {
      System.out.println("previous");
      return null;
    }

  }
}
