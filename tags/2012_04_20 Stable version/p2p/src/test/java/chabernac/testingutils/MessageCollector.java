/**
 * Copyright (c) 2010 Axa Holding Belgium, SA. All rights reserved.
 * This software is the confidential and proprietary information of the AXA Group.
 */
package chabernac.testingutils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import chabernac.protocol.message.MultiPeerMessage;
import chabernac.protocol.message.iMultiPeerMessageListener;

public class MessageCollector implements iMultiPeerMessageListener {
	private List< MultiPeerMessage > myMultiPeerMessages = new ArrayList< MultiPeerMessage >();
	private final CountDownLatch myLatch;

	public MessageCollector(CountDownLatch aLatch){
		myLatch = aLatch;  
	}
	@Override
	public void messageReceived( MultiPeerMessage aMessage ) {
	  myMultiPeerMessages.add(aMessage);
		myLatch.countDown();
	}

	public List<MultiPeerMessage> getMessages(){
		return myMultiPeerMessages;
	}
}
