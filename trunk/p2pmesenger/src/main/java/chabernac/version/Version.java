/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.version;

public class Version {
  final int myMajor;
  final int myMinor;
  final int myBuild;
  
  public Version(String aVersion){
    String[] theParts = aVersion.split( "\\." );
    if(theParts.length != 3) throw new IllegalArgumentException("Invalid version");
    myMajor = Integer.parseInt( theParts[0] );
    myMinor = Integer.parseInt( theParts[1] );
    myBuild  = Integer.parseInt( theParts[2] );
  }
  
  public Version ( int anMajor , int anMinor , int anBuild ) {
    super();
    myMajor = anMajor;
    myMinor = anMinor;
    myBuild = anBuild;
  }
  
  public String toString(){
    return myMajor + "." + myMinor + "." + myBuild;
  }
  
  /**
   * returns 1 if this version is higher than the given version
   * return -1 if this version is lower than the given version
   * return 0 if the versions are equal  
   * @param anObject
   * @return
   */
  public int compareTo(Object anObject){
    if(!(anObject instanceof Version)) return 0;
    
    Version theVersion = (Version)anObject;
    if(myMajor > theVersion.myMajor) return 1;
    if(myMajor < theVersion.myMajor) return -1;
    
    if(myMinor > theVersion.myMinor) return 1;
    if(myMinor < theVersion.myMinor) return -1;
    
    if(myBuild > theVersion.myBuild) return 1;
    if(myBuild < theVersion.myBuild) return -1;
    
    return 0;
  }
}
