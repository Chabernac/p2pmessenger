/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.utils;

public class FastMath {
  public static float invSqrt(float a) {
    float xhalf = 0.5F * a;
    //convert float to bit representation
    int bitValue = Float.floatToRawIntBits( xhalf );
    //find square for decimal using magic number best quess
    bitValue = 0x5f3759df - (bitValue >> 1);
    
    //turn bitvalue back into decimal float 
    a = Float.intBitsToFloat( bitValue );
    
    //repeat for more accuracy
    a = a * (1.5f - xhalf*a*a);
    a = a * (1.5f - xhalf*a*a);
    
    return a;
  }
}
