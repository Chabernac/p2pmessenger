package chabernac.test;

import java.rmi.Naming;
import java.util.Random;

import chabernac.messengerservice.MessengerUser;
import chabernac.messengerservice.iMessengerClientService;

public class LocalRegisterStressTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String theServiceURL = "rmi://localhost:1715/MessengerClientService";

		try {
			final iMessengerClientService theClientService = (iMessengerClientService)Naming.lookup(theServiceURL);

			final Object theLatch = new Object();
			final Random theRandom = new Random();
			for(int i=0;i<100;i++){
				final int j = i;
				new Thread(new Runnable(){
					public void run(){
						try {
							synchronized(theLatch){
								theLatch.wait();
							}
							Thread.sleep(Math.abs(theRandom.nextInt() % 5000));
							MessengerUser theUser = new MessengerUser();
							theUser.setFirstName("Ikke" + j);
							theUser.setLastName("Chauliac" + j);
							theUser.setUserName("user" + j);
							theUser.setStatus(MessengerUser.ONLINE);
							theClientService.userChanged(theUser);
							Thread.sleep(10000);
							theClientService.removeUser(theUser);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}).start();
			}
			synchronized (theLatch) {
				theLatch.notifyAll();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
