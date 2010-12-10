/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.aspect.log;

import java.awt.Graphics;

import org.aspectj.internal.lang.annotation.ajcDeclareAnnotation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import chabernac.space.Graphics3D;

/**
 * http://www.eclipse.org/aspectj/doc/next/quick5.pdf
 */

@Aspect
public class LoggingAspect {
  private long t = -1;
  private long cycle = -1;
  private boolean isDebug = false;
  
  @Around("execution(* chabernac.space.Graphics3D.draw*(..))")
  public Object log(ProceedingJoinPoint pjp) throws Throwable{
    Graphics3D  theG = (Graphics3D)pjp.getTarget();
    
    
    if(!isDebug){
      return pjp.proceed();
    } else {
      long t1 = System.nanoTime();
      Object theObj = pjp.proceed();
      System.out.println((System.nanoTime() - t1) / 1000 + " micros: " + pjp.getSignature());
      return theObj;
    }
  }
  
  
  @Around("execution(public * chabernac.space.Graphics3D.drawWorld*(..)) && args(aG, aCycle)")
  public void calculateFrameRate(ProceedingJoinPoint aJp, Graphics aG, long aCycle) throws Throwable{
    isDebug = (aCycle >> 8 << 8 == aCycle);
    
    if(isDebug){
      if(t != -1){
        System.out.println(1000 * (aCycle - cycle) / (System.currentTimeMillis() - t) + " fps");
      }
      t = System.currentTimeMillis();
      cycle = aCycle;
    }
    
    aJp.proceed();
  }
  
}
