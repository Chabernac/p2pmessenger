/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.aspect.log;

import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * http://www.eclipse.org/aspectj/doc/next/quick5.pdf
 */

@Aspect
public class LoggingAspect {
  private long t = -1;
  private long cycle = -1;
  private boolean isDebug = false;

  private Map<Signature, Float> myTimings = new HashMap<Signature, Float>();
  
  @Around("execution(public * chabernac.space.Graphics3D.drawWorld*(..)) && args(aG, aCycle)")
  public void calculateFrameRate(ProceedingJoinPoint aJp, Graphics aG, long aCycle) throws Throwable{
    isDebug = (aCycle >> 8 << 8 == aCycle);

    if(isDebug){
      myTimings.clear();
      aJp.proceed();
      for(Iterator<Signature> i = myTimings.keySet().iterator();i.hasNext();){
        Signature theKey = i.next();
        System.out.println(theKey.toString() + ": " + (1000000000 / myTimings.get(theKey)) + "cps");
      }
      
      
      if(t != -1){
        System.out.println("------------------------------------------------------------------");
        System.out.println(1000 * (aCycle - cycle) / (System.currentTimeMillis() - t) + " fps");
      }
      t = System.currentTimeMillis();
      cycle = aCycle;
    } else {
      aJp.proceed();
    }
  } 

  @Around("execution(* chabernac.space.Graphics3D.draw*(..))")
  public Object log(ProceedingJoinPoint pjp) throws Throwable{
    return logging(pjp);
  }

  @Around("execution(* chabernac.space.Graphics3D.fill*(..))")
  public Object log2(ProceedingJoinPoint pjp) throws Throwable{
    return logging(pjp);
  }
  
  @Around("execution(* chabernac.space.Graphics3D.convert*(..))")
  public Object log3(ProceedingJoinPoint pjp) throws Throwable{
    return logging(pjp);
  }
  
  @Around("execution(* chabernac.space.buffer.Graphics3D2D.*(..))")
  public Object log4(ProceedingJoinPoint pjp) throws Throwable{
    return logging(pjp);
  }
  
  @Around("execution(* chabernac.space.World.*(..))")
  public Object log5(ProceedingJoinPoint pjp) throws Throwable{
    return logging(pjp);
  }
  
  private Object logging(ProceedingJoinPoint aJP) throws Throwable{
    if(!isDebug){
      return aJP.proceed();
    } else {
      long t1 = System.nanoTime();
      Object theObj = aJP.proceed();
      long t2 = System.nanoTime();

      float theTime = 0F;
      Signature theSignature = aJP.getSignature();
      if(myTimings.containsKey(theSignature)){
        theTime = myTimings.get(theSignature);
      }
      theTime += (t2 - t1);
      myTimings.put(aJP.getSignature(), theTime);
      return theObj;
    } 
  }
}
