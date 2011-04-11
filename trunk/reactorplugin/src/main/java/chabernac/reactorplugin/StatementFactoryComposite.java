/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.reactorplugin;

import java.util.ArrayList;
import java.util.List;

public class StatementFactoryComposite implements iStatementFactory {
  private int myStatementsPerRound = 3;
  private int myCurrentStatementsLeft = myStatementsPerRound;
  
  private List<iStatementFactory> myStatementFactories = new ArrayList<iStatementFactory>( );
  private List<iStatementFactory> myStatementFactoriesLeft = new ArrayList<iStatementFactory>( );
  
  private iStatementFactory myCurrentStatementFactory = null;
  
  public StatementFactoryComposite( int aStatementsPerRound ) {
    super();
    myStatementsPerRound = aStatementsPerRound;
    myCurrentStatementsLeft = aStatementsPerRound;
  }
  
  public void reset(){
    myCurrentStatementsLeft = myStatementsPerRound;
    myStatementFactoriesLeft.clear();
    myStatementFactoriesLeft.addAll( myStatementFactories );
  }

  public boolean hasMoreStatements(){
    return !myStatementFactories.isEmpty();
  }

  @Override
  public AbstractStatement createStatement() {
    if(myStatementFactoriesLeft.isEmpty()) return null;
    
    if(myCurrentStatementFactory == null){
      myCurrentStatementFactory = myStatementFactoriesLeft.get( 0 );
    }
    
    AbstractStatement theStatment = myCurrentStatementFactory.createStatement();
    
    myCurrentStatementsLeft --;
    
    if(myCurrentStatementsLeft <= 0){
      myCurrentStatementsLeft = myStatementsPerRound;
      myStatementFactoriesLeft.remove( 0 );
      myCurrentStatementFactory = null;
    }
    
    return theStatment;
  }
  
  public void addStatmentFactory(iStatementFactory aFactory){
    myStatementFactories.add( aFactory );
  }
  
  

}
