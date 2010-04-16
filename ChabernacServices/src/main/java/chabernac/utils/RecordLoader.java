/*
 * Copyright (c) 1998 Anhyp, NV. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Anhyp.
 *
 */

package chabernac.utils;

import org.apache.log4j.Logger;


/**
 *
 *
 * @version v1.0.0      Jul 5, 2005
 *<pre><u><i>Version History</u></i>
 *
 * v1.0.0 Jul 5, 2005 - initial release       - Guy Chauliac
 *
 *</pre>
 *
 * @author <a href="mailto:guy.chauliac@axa.be"> Guy Chauliac </a>
 */
public class RecordLoader  {
  private static Logger logger = Logger.getLogger(RecordLoader.class);
  
  public static Record loadRecord(byte[] aByteArray){
	  try{
		  byte[] theRecordName = new byte[5];
		  System.arraycopy(aByteArray, 0, theRecordName, 0, 5);
			  
		  String theRecordClassName = "chabernac.record." + new String(theRecordName);
		  //Logger.log(RecordLoader.class, "Instantiating record class: " + theRecordClassName);
		  Class theClass = ClassLoader.getSystemClassLoader().loadClass(theRecordClassName);
		  Record theRecord = (Record)theClass.newInstance();
		  theRecord.setContent(aByteArray);
		  return theRecord;
	  }catch(Exception e){
		  logger.error("Could not create record", e);
      e.printStackTrace();
		  return null;
	  }
  }
}
