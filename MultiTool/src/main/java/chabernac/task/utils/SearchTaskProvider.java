/**
 * Copyright (c) 2011 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.task.utils;

import java.util.regex.Pattern;

import chabernac.application.ApplicationRefBase;
import chabernac.search.AbstractSearchProvider;
import chabernac.search.SearchProviderException;
import chabernac.task.Task;
import chabernac.task.TaskTools;

public class SearchTaskProvider extends AbstractSearchProvider<Task>{
  private final Task myRootTask;
  private Task myCurrentTask;

  public SearchTaskProvider( Task aRootTask) {
    super();
    if(aRootTask == null) aRootTask = (Task)ApplicationRefBase.getObject( ApplicationRefBase.ROOTTASK );
    myRootTask = aRootTask;
    myCurrentTask = myRootTask;
  }

  @Override
  public Task next(String aSearchTerm, boolean isRegExpr) throws SearchProviderException {
    while((myCurrentTask = nextTask()) != null){
      if(currentTaskMatchesPattern( aSearchTerm, isRegExpr)){
        TaskTools.selectTask( myCurrentTask );
        return myCurrentTask;
      }
    }

    throw new SearchProviderException("No more items match selection");
  }

  @Override
  public Task previous(String aSearchTerm, boolean isRegExpr) throws SearchProviderException {
    while((myCurrentTask = previousTask()) != null){
      if(currentTaskMatchesPattern( aSearchTerm, isRegExpr)){
        TaskTools.selectTask( myCurrentTask );
        return myCurrentTask;
      }
    }

    throw new SearchProviderException("No more items match selection");
  }

  private Task nextTask(){
    if(myCurrentTask == null) myCurrentTask = myRootTask;

    if(myCurrentTask.getChildCount() > 0){
      //return first child
      return (Task)myCurrentTask.getChildAt( 0 );
    } else {
      //go back to parent and take next child
      Task theParent = (Task)myCurrentTask.getParent();
      while(theParent != null){
        Task theNextChild = (Task)theParent.getChildAfter( myCurrentTask );
        if(theNextChild != null) return theNextChild;

        myCurrentTask = myCurrentTask.getParentTask();
        theParent = myCurrentTask.getParentTask();
      }
    }

    return null;
  }

  private Task previousTask(){
    if(myCurrentTask == null) myCurrentTask = myRootTask;

    if(myCurrentTask.getChildCount() > 0){
      //return first child
      return (Task)myCurrentTask.getChildAt( myCurrentTask.getChildCount() - 1 );
    } else {
      //go back to parent and take next child
      Task theParent = (Task)myCurrentTask.getParent();
      while(theParent != null){
        Task theNextChild = (Task)theParent.getChildBefore( myCurrentTask );
        if(theNextChild != null) return theNextChild;

        myCurrentTask = myCurrentTask.getParentTask();
        theParent = myCurrentTask.getParentTask();
      }
    }

    return null;
  }

  private boolean currentTaskMatchesPattern(String aPattern, boolean isRegExpr){
    if(isRegExpr){
      Pattern thePattern = Pattern.compile( aPattern );
      if(thePattern.matcher( myCurrentTask.getName() ).find()) return true;
      if(thePattern.matcher( myCurrentTask.getTestTrackerNr() ).find()) return true;
      if(thePattern.matcher( myCurrentTask.getAugeoCode() ).find()) return true;
      if(thePattern.matcher( Integer.toString( myCurrentTask.getNotesProblemNr() ) ).find()) return true;
      if(thePattern.matcher( myCurrentTask.getDescription() ).find()) return true;
    } else {
      if(matches(myCurrentTask.getName(), aPattern ) ) return true;
      if(matches(myCurrentTask.getTestTrackerNr(), aPattern ) ) return true;
      if(matches(myCurrentTask.getAugeoCode(), aPattern ) ) return true;
      if(matches(Integer.toString(myCurrentTask.getNotesProblemNr()), aPattern ) ) return true;
      if(matches(myCurrentTask.getDescription(),  aPattern ) ) return true;
    }

    return false;

  }

  private boolean matches(String aString, String aSearchTerm){
    if(aString == null) return false;
    return aString.toUpperCase().contains( aSearchTerm.toUpperCase() );
  }


}
